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
 * CopyOperation performs a file copy operation from
 * a source to a destination.  If the destination
 * does not alreay exist, it is created.
 * 
 * Thursday June 17, 2004.
 */
public class CopyOperation extends FileOperation {

	File dest;
	static final int BUFFER_SIZE = 1024 * 64;
	
/**
 * Constructs a new instance of a file operation.
 * 
 * @param shell the shell where progress is performed
 * @param src the source file
 * @param dest the destination file
 */
public CopyOperation (Shell shell, File src, File dest) {
	super (shell, new File [] {src});
	this.dest = dest;
}

/**
 * Copies a source file to a destination file.  If the
 * source is a file, the it is copied into the destination,
 * which has already been created.  If the source is a
 * folder, the files and folders within the source are
 * copied into the destination.
 * 
 * @param srcFile the source file or folder
 * @param destFile the destionation file or folder
 * @return true if the copy was successful; false otherwise
 */
boolean copy (final File srcFile, final File destFile) {
	if (!check (srcFile, false)) return false;
	if (srcFile.isFile ()) {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream (srcFile);
			out = new FileOutputStream (destFile);
			int count = 0;
			byte [] buffer = new byte [BUFFER_SIZE];
			while ((count = in.read (buffer)) != -1) {
				if (!check (srcFile, false)) return false;
				out.write (buffer, 0, count);
			}
		} catch (Exception exception) {
			error ("Cannot copy " + srcFile + " to " + destFile + " due to: " + exception + ".");
			return false;
		} finally {
			try {
				if (in != null) in.close ();
				if (out != null) out.close ();
			} catch (IOException exception) {
				error ("Cannot copy " + srcFile + " to " + destFile + " due to: " + exception + ".");
				return false;
			}
		}
		return check (srcFile, true);
	}
	File [] files = srcFile.listFiles ();
	if (files == null) return false;
	if (!destFile.mkdirs () && !destFile.exists ()) return false;
	for (int i=0; i<files.length; i++) {
		File file = new File (destFile, files [i].getName ());
		if (!copy (files [i], file)) return false;
	}
	return true;
}

String getText () {
	return FileExplorer.getMessage ("Copying ...");
}

public File [] getLockedFiles () {
	File [] files = super.getLockedFiles ();
	File [] newFiles = new File [files.length + 1];
	System.arraycopy (files, 0, newFiles, 0, files.length);
	newFiles [files.length] = dest;
	return newFiles;
}

String getMessage (File file) {
	return FileExplorer.getMessage ("Copying {0}", new Object [] {file.getAbsolutePath ()});
}

/**
 * Copy the files in the array to the destination.
 * 
 * @param files the files for the operation
 * @return true if the operation was successful; false otherwise
 */
boolean operation (File [] files) {
	for (int i=0; i<files.length; i++) {
		if (!copy (files [i], dest)) return false;
	}
	return true;
}

boolean verify () {
	MessageBox dialog = new MessageBox (shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
	dialog.setMessage ("DEBUG: Confirm copy \"" + files [0] + "\" to \"" + dest + "\"");
	return dialog.open () == SWT.YES;
}
}
