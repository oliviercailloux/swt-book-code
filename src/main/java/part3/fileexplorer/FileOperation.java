/*
 * Copyright (c) 2004 Steve Northover and Mike Wilson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 */
package part3.fileexplorer;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

import java.io.*;

/**
 * @author Steve Northover
 *
 * FileOperation is the root of all file operations that
 * are performed by a FileExplorer.  Subclasses implement
 * operations such as copy and delete for files.  The
 * operation runs in either the user interface thread or
 * a background thread.
 * 
 * Thursday June 17, 2004.
 */

public abstract class FileOperation  {
	
	Display display;
	Shell shell;
	ProgressDialog dialog;
	Thread thread;
	
	long start, end;
	int count, total, expectedTime = 500;
	boolean running, success, cancel, isLong;
	Runnable doneRunnable, longRunnable;
	
	File [] files = new File [0];
	static FileOperation [] Operations = new FileOperation [0];
	static boolean DEBUG = false;

/**
 * Constructs a new instance of a file operation.
 * 
 * @param shell the shell where progress is performed
 * @param files an array of files for the operation
 */
public FileOperation (Shell shell, File [] files) {
	display = shell.getDisplay ();
	this.shell = shell;
	this.files = files;
	register (this);
}

/**
 * Deregisters a file operation.  All known file operations are
 * tracked to determine when there are outstanding operations.
 * 
 * @param operation the file operation to be deregistered
 */
static synchronized void deregister (FileOperation operation) {
	int index = 0;
	while (index < Operations.length) {
		if (operation.equals (Operations [index])) break;
		index++;
	}
	if (index < Operations.length) {
		FileOperation [] newOperations = new FileOperation [Operations.length - 1];
		System.arraycopy (Operations, 0, newOperations, 0, index);
		System.arraycopy (Operations, index + 1, newOperations, index, Operations.length - index - 1);
		Operations = newOperations;
	}
}

/**
 * Returns the number of outstanding file operations.
 * 
 * @return all outstanding file operations
 */
public static synchronized int getOperationCount () {
	return Operations.length;
}

/**
 * Returns an array of all outstanding file operations.  A copy
 * is returned so that modifying the array does nothing.
 * 
 * @return all outstanding file operations
 */
public static synchronized FileOperation [] getOperations () {
	FileOperation [] newOperations = new FileOperation [Operations.length];
	System.arraycopy (Operations, 0, newOperations, 0, Operations.length);
	return newOperations;
}

/**
 * Registers a file operation.  All known file operations are
 * tracked to determine when there are outstanding operations.
 * 
 * @param operation the file operation to be registered
 */
static synchronized void register (FileOperation operation) {
	FileOperation [] newOperations = new FileOperation [Operations.length + 1];
	System.arraycopy (Operations, 0, newOperations, 0, Operations.length);
	newOperations [Operations.length] = operation;
	Operations = newOperations;
}

/**
 * Checks to see whether the operation should become long running
 * and updates the count of files that have been processed.  If the
 * operation should be stopped for any reason, false is returned.
 * Normally this happens when the user cancells the operation.
 * 
 * @param file the current file
 * @param increment indicates that the count should be incremented
 * @return true if the operation should continue; otherwise false
 */
boolean check (final File file, boolean increment) {
	if (DEBUG && !(this instanceof FillOperation)) {
		if (increment) {
			try {Thread.sleep (100);} catch (Throwable th) {}
		}
	}
	end = System.currentTimeMillis ();
	if (cancel) return false;
	if (increment) count++;
	if (isLong) {
		showProgress (file);
	} else {
		if (isLong = end - start > expectedTime) {
			synchronized (this) {
				notifyAll ();
			}
			if (longRunnable != null) longRunnable.run ();
			total = computeTotal (files);
			if (cancel) return false;
			startProgress ();
		}
	}
	return true;
}

/**
 * Counts the number of files in the array as well
 * as any files that belong to subfolders of directories
 * in the array.
 * 
 * @param files the array of files or folders
 * @return the total number of files
 */
int countFiles (File [] files) {
	if (files == null) return 0;
	int total = 0;
	for (int i=0; i<files.length; i++) {
		if (files [i].isFile ()) total++;
		total += countFiles (files [i].listFiles ());
	}
	return total;
}

/**
 * Cancels the operation.
 */
public void cancel () {
	cancel = true;
	if (!running) deregister (this);
	display.wake ();
}

/**
 * Assigns the runnable that is executed when the operaion is done.
 * 
 * @param doneRunnable the runnable to exec when the operation is done
 */
public void doneExec (Runnable doneRunnable) {
	this.doneRunnable = doneRunnable;
}

/**
 * Issues an error message to the user.
 * 
 * @param message the message to be displayed
 */
void error (final String message) {
	display.asyncExec (new Runnable () {
		public void run () {
			if (shell.isDisposed ()) return;
			MessageBox dialog = new MessageBox (shell, SWT.ICON_ERROR | SWT.OK);
			dialog.setText (shell.getText ());
			dialog.setMessage (message);
			dialog.open ();
		}
	});
}

/**
 * Gets the expected time for the operation.  If an operation
 * takes longer than the expected time, it becomes long running
 * and executes in parallel with the user interface thread.
 * 
 * @return the expected time for the operation
 */
public int getExpectedTime () {
	return expectedTime;
}

/**
 * Returns the array of files for the operation.
 * 
 * @return the array of files
 */
public File [] getFiles () {
	return files;
}

/**
 * Returns the array of files that are considered to be locked
 * by the operation.  Typically, this array is identical to the
 * array of files for the operation.
 * 
 * @return the array of locked files
 */
public File [] getLockedFiles () {
	return files;
}

/**
 * Returns a short message that describes the current file.
 * This string is used in the user interface to inform the
 * user that a particuar file is currently being processed
 * by the operation.
 * 
 * @param the file that is being processed
 * @return the message that describes the file
 */
abstract String getMessage (File file);

/**
 * Returns the name of the operation.  This string is used in
 * the user interface as the title of the operation, typically
 * appearing as the title of a shell.
 * 
 * @return the name of the operation
 */
abstract String getText ();

/**
 * Computes the total number of files to be processed.
 * By default, this is the files in the array as well
 * as any files that belong to subfolders.
 * 
 * @param files the array of files or folders
 * @return the total number of files
 */
int computeTotal (File [] files) {
	return countFiles (files);
}

/**
 * Returns the total time that the operation took to
 * perform.
 * 
 * @return the total time
 */
public long getTotalTime () {
	return end - start;
}

/**
 * Returns the cancelled state of the operation.
 * 
 * @return true if the operation was cancelled; false otherwise
 */
public boolean isCancelled () {
	return cancel;
}

/**
 * Returns the running state of the operation.
 * 
 * @return true if the operation is running; false otherwise
 */
public boolean isRunning () {
	return running;
}

/**
 * Returns the success state of the operation.
 * 
 * @return true if the operation was successful; false otherwise
 */
public boolean isSuccessful () {
	return success;
}

/**
 * Assigns the runnable that is executed when the operaion is becomes
 * long running.  If an operation takes longer than the expected time,
 * it becomes long running and executes in parallel with the user interface
 * thread.
 * 
 * @param longRunnable the runnable to exec when the operation is long
 */
public void longExec (Runnable longRunnable) {
	this.longRunnable = longRunnable;
}

/**
 * Perform the work for the operation.  Typically, operations iterate
 * through the array of files performing the work for each file and
 * checking to see whether the operation should continue.
 * 
 * @param files the files for the operation
 * @return true if the operation was successful; false otherwise
 */
abstract boolean operation (File [] files);

/**
 * Runs the operation.  If the operation takes longer than the
 * expected time, the operation goes long and executes in parallel
 * with the user interface thread.  If the operation completed
 * within the expected time, true is returned.
 * 
 * @param fork indicates that a new thread will be created
 * @return true if the operation completed; false otherwise
 */
public boolean run (boolean fork) {
	if (DEBUG && !verify ()) {
		deregister (this);
		return false;
	}
	if (!running) {
		if (fork) {
			thread = new Thread () {
				public void run () {
					runOperation ();
				}
			};
			synchronized (this) {
				thread.start ();
				try {
					wait ();
				} catch (InterruptedException e) {
					//TODO - thread interrupted
				}
			}
		} else {
			thread = display.getThread ();
			runOperation ();
		}
	}
	return !isLong;
}

/**
 * Runs the operation.
 */
void runOperation () {
	running = true;
	start = System.currentTimeMillis ();
	success = operation (files);
	end = System.currentTimeMillis ();
	stopProgress ();
	deregister (this);
	synchronized (this) {
		notifyAll ();
	}
	if (doneRunnable != null) {
		doneRunnable.run ();
	}
	running = false;
}

/**
 * Sets the expected time for the operation.  If an operation
 * takes longer than the expected time, it becomes long running
 * and executes in parallel with the user interface thread.
 * 
 * @param expectedTime the new expected time for the operation
 */
public void setExpectedTime (int expectedTime) {
	this.expectedTime = expectedTime;
}

/**
 * Shows the current progress for the operation.
 * 
 * @param file the current file
 */
void showProgress (final File file) {
	if (dialog != null) {
		display.syncExec (new Runnable () {
			public void run () {
				if (shell.isDisposed ()) return;
				dialog.setMessage (getMessage (file));
				dialog.setCount (count);
				/*
				* Force the dialog to draw right away
				* when running in the user interface
				* thread because the event loop is not
				* running.
				*/
				if (thread == display.getThread ()) {
					display.update ();
				}
			}
		});
	}
}

/**
 * Starts showing progress for the operation.
 */
void startProgress () {
	display.syncExec (new Runnable () {
		public void run () {
			if (shell.isDisposed ()) return;
			dialog = new ProgressDialog (shell, SWT.MODELESS);
			dialog.setText (getText ());
			dialog.setTotal (total);
			dialog.setCount (count);
			/*
			* Allow the user to cancel operations that are
			* not running in the user interface thread.
			* Otherwises, the cancel button will draw but
			* because the event loop is not running, the
			* user can't click on it.
			*/
			if (thread != display.getThread ()) {
				dialog.cancelExec (new Runnable () {
					public void run () {
						cancel ();
					}
				});
			}
			dialog.open ();
		}
	});	
}

/**
 * Stops showing progress for the operation.
 */
void stopProgress () {
	if (dialog != null) {
		display.asyncExec (new Runnable () {
			public void run () {
				if (shell.isDisposed ()) return;
				dialog.close ();
				dialog = null;
			}
		});
	}
}

/**
 * Verify that the operation should start.  This method
 * is used for debugging to allow a last minute check
 * before an operation starts.
 * 
 * @return true if the operation should start; false otherwise
 */
boolean verify () {
	return true;
}

}