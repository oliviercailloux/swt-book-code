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
 * DeleteOperation performs a file delete operation.
 * If the file is a folder, then all files within the
 * folder are deleted as well as the folder.
 * 
 * Thursday June 17, 2004.
 */
public class DeleteOperation extends FileOperation  {

/**
 * Constructs a new instance of a delete operation.
 * 
 * @param shell the parent shell for the verify dialog
 * @param files an array of files for the operation
 */
public DeleteOperation (Shell shell, File [] files) {
	super (shell, files);
}

int computeTotal (File [] files) {
	/* Include the files that have already been deleted */
	return count + countFiles (files);
}

/**
 * Deletes a file or folder.
 * 
 * @param file
 * @return true if the operation was successful; false otherwise
 */
boolean delete (File file) {
	if (!check (file, false)) return false;
	if (file.isDirectory ()) {
		File [] files = file.listFiles ();
		for (int i=files.length-1; i>=0; --i) {
			if (!delete (files [i])) return false;
		}
	} else {
		if (!check (file, true)) return false;
	}
	return file.delete ();
}

String getText () {
	return FileExplorer.getMessage ("Deleting ...");
}

String getMessage (File file) {
	return FileExplorer.getMessage ("Deleting {0}", new Object [] {file.getAbsolutePath ()});
}

/**
 * Delete the files in the array.
 * 
 * @param files the files for the operation
 * @return true if the operation was successful; false otherwise
 */
boolean operation (File [] files) {
	for (int i=0; i<files.length; i++) {
		if (!delete (files [i])) return false;
	}
	return true;
}

boolean verify () {
	MessageBox dialog = new MessageBox (shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
	String names = "";
	for (int i=0; i<files.length; i++) {
		names += " \"" + files [i] + "\"" + (i == files.length - 1 ? "" : ",");
	}
	dialog.setMessage ("DEBUG: Confirm delete " + files.length + " file(s): " + names + "?"); //bad nls
	return dialog.open () == SWT.YES;
}

}