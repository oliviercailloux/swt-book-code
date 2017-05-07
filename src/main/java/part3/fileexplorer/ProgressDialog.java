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
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;

/**
 * @author Steve Northover
 *
 * ProgressDialog is a simple dialog that shows the progress of
 * a long running operation.  Although it is possible to run the
 * operation in the user interface thread and use this dialog, 
 * it is generally not useful.  One reason for this is that the
 * dialog includes a cancel button that the user will not respond
 * to events when the user interface thread is busy running the
 * long operation rather than running the event loop.  Despite the
 * fact that the dialog is generally updated from a background thread,
 * it must be created from the user interface thread and its methods
 * must be called from this thread.  This means that callers from other
 * threads must use either syncExec() or asyncExec(), just like any
 * other widget or dialog in SWT.  This is a design point:  Rather
 * than decide in advance how the dialog is intended to be used and
 * guess wrong, threading is left up to the caller.  Threading is
 * a tricky area in any program and it helps to be explict which
 * classes are multi-threaded and which ones are not.
 */
public class ProgressDialog extends Dialog {

	Shell shell;
	Label label;
	Button button;
	ProgressBar progress;
	
	String message = "";
	int count, total = 100;
	boolean cancel;
	Runnable cancelRunnable;

/**
 * Constructs a new instance of this class given its parent
 * and a style value describing its behavior and appearance.
 *
 * @param parent a shell which will be the parent of the new instance
 * @param style the style of dialog to construct
 *
 */
public ProgressDialog (Shell parent, int style) {
	super(parent, style);
}

/**
 * Constructs a new instance of this class given only its parent.
 *
 * @param parent a shell which will be the parent of the new instance
 */
public ProgressDialog (Shell parent) {
	this(parent, SWT.PRIMARY_MODAL); 
}

/**
 * Loads a string from a resource file using a key.
 * If the key does not exist, it is used as the result.
 *
 * @param key the name of the string resource
 * @return the string resource
 */
static String getMessage (String key) {
	return FileExplorer.getMessage (key);
}

/**
 * Open the dialog.  This method is used for testing only.
 * 
 * @param args the arguments to the program
 */
public static void main (String [] args) {
	final Display display = new Display ();
	final Shell shell = new Shell();
	shell.open();
	Thread thread = new Thread () {
		ProgressDialog dialog;
		public void run () {
			display.asyncExec (new Runnable () {
				public void run () {
					if (shell.isDisposed ()) return;
					dialog = new ProgressDialog (shell);
					dialog.setMessage ("This is a really long message that might wrap when there is lots of text.");
					dialog.open ();
				}
			});
			for (int i=0; i<100; i++) {
				if (shell.isDisposed ()) return;
				try {
					Thread.sleep (100);
				} catch (Throwable th) {}
				final int count = i;
				display.syncExec (new Runnable () {
					public void run () {
						if (shell.isDisposed ()) return;
						dialog.setCount (count);
					}
				});
			}
			display.syncExec (new Runnable () {
				public void run () {
					if (shell.isDisposed ()) return;
					dialog.close ();
				}
			});
		}
	};
	thread.start ();
	while (!shell.isDisposed() && thread.isAlive ()) {
		if (!display.readAndDispatch()) display.sleep();
	}
	display.dispose();
}

/**
 * Assigns the runnable that is executed when the dialog is cancelled.
 * 
 * @param cancelRunnable the runnable to exec when the dialog is cancelled
 */
public void cancelExec (Runnable cancelRunnable) {
	this.cancelRunnable = cancelRunnable;
	if (button != null) {
		button.setEnabled (cancelRunnable != null);
	}
}

/**
 * Closes the dialog.
 */
public void close () {
	/*
	* Don't call close().  When a shell is disabled, it will
	* not close because the user could not have closed it
	* by clicking on the close button.
	*/
	if (shell != null) shell.dispose ();
}

/**
 * Gets the current amount of work that has been done.  This is
 * a percentage of the total work that the dialog represents.
 * 
 * @return the amount of work completed
 */
public int getCount () {
	return count;
}

/**
 * Gets the message to be displayed in the dialog.  This is
 * a string that desctribes the state of the operations that
 * this dialog represents.
 * 
 * @return the message that is displayed 
 */
public String getMessage () {
	return message;
}

/**
 * Gets the total amount of work to be done.  This is used with
 * the current work to display a percentage to indicate the current
 * state of the operation that this dialog represents.
 * 
 * @return the total amount of work
 */

public int getTotal () {
	return total;
}

/**
 * Opens the dialog.
 * 
 * @return true if the user cancelled the dialog; false otherwise
 */
public boolean open () {
	int modal = SWT.PRIMARY_MODAL | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL;
	int style = SWT.SHELL_TRIM | (getStyle() & modal);
	Display display = getParent ().getDisplay ();
	shell = new Shell (getParent (), style);
	shell.addDisposeListener (new DisposeListener () {
		public void widgetDisposed (DisposeEvent event) {
			shell = null;
			label = null;
			button = null;
			progress = null;
		}
	});
	shell.setText (getText());
	progress = new ProgressBar (shell, SWT.NONE);
	progress.setSelection (count);
	progress.setMaximum (total);
	label = new Label (shell, SWT.WRAP);
	label.setText (message);
	button = new Button (shell, SWT.PUSH);
	button.setText (getMessage ("Cancel"));
	button.setEnabled (cancelRunnable != null);
	button.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			cancel = true;
			button.setEnabled (false);
			if (cancelRunnable != null) cancelRunnable.run ();
			shell.dispose ();
		}
	});
	FormLayout layout = new FormLayout ();
	layout.spacing = 3;
	layout.marginWidth = layout.marginHeight = 6;
	shell.setLayout (layout);
	FormData progressData = new FormData ();
	progressData.left = new FormAttachment (0);
	progressData.right = new FormAttachment (100);
	progress.setLayoutData (progressData);
	FormData labelData = new FormData ();
	labelData.width = 300;
	labelData.left = new FormAttachment (0);
	labelData.right = new FormAttachment (button);
	labelData.top = new FormAttachment (progress);
	labelData.bottom = new FormAttachment (100);
	label.setLayoutData (labelData);
	FormData buttonData = new FormData ();
	buttonData.right = new FormAttachment (100);
	buttonData.top = new FormAttachment (progress);
	button.setLayoutData (buttonData);
	shell.pack ();
	if ((getStyle () & modal) != 0) {
		Rectangle rect1 = getParent ().getClientArea ();
		rect1 = display.map (getParent (), null, rect1);
		Rectangle rect2 = shell.getBounds ();
		int x = Math.max (0, (rect1.width - rect2.width) / 2);
		int y = Math.max (0, (rect1.height - rect2.height) / 2);
		Rectangle rect3 = shell.getMonitor ().getClientArea ();
		x = Math.min (rect1.x + x, rect3.x + rect3.width - rect2.width);
		y = Math.min (rect1.y + y, rect3.y + rect3.height - rect2.height);
		shell.setLocation (x, y);
	}
	shell.open();
	if ((getStyle () & modal) != 0) {
		while (shell != null && !shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display = null;
	}
	return cancel;
}

/**
 * Sets the current amount of work that has been done.  This is
 * a percentage of the total work that the dialog represents.
 * 
 * @param count the new amount of work completed
 */
public void setCount (int count) {
	this.count = count;
	if (progress != null) progress.setSelection (count);
}

/**
 * Sets the message to be displayed in the dialog.  This is
 * a string that desctribes the state of the operations that
 * this dialog represents.
 * 
 * @param message the new message to be displayed 
 */
public void setMessage (String message) {
	this.message = message;
	if (label != null) {
		label.setText (message);
		// TODO - Only resize the shell when the user has not resized it
		Point size = shell.getSize ();
		Point newSize = shell.computeSize (SWT.DEFAULT, SWT.DEFAULT);
		if (newSize.x > size.x || newSize.y > size.y) shell.setSize (newSize);
	}
}

/**
 * Sets the total amount of work to be done.  This is used with
 * the current work to display a percentage to indicate the current
 * state of the operation that this dialog represents.
 * 
 * @param total the new total amount of work
 */
public void setTotal (int total) {
	this.total = total;
	if (progress != null) progress.setMaximum (total);
}

}