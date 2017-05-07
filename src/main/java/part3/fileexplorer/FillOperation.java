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

import java.io.*;

/**
 * @author Steve Northover
 *
 * FillOperation fills a table with entries that represent
 * files in a folder.  Filling a table is always considered
 * to be a long running operation.
 * 
 * Thursday June 17, 2004.
 */
public class FillOperation extends FileOperation {
	
	FileExplorer explorer;
	int start, page;
	File [] files;

/**
 * Constructs a new instance to fill a table within an explorer.
 * The table is filled, a page at a time from the array of files.
 * The larger the page size, the more table items are created in
 * the user interface thread.  If this number is too large, the
 * user interface becomes slow.  If it is too small, the fill
 * operation takes longer than it should.  A good first estimate
 * for the page size is the number of visible items in the table.
 * 
 * @param explorer the explorer that contains the table
 * @param file the source folder
 * @param files the array of files within the folder
 * @param start the start index within the array
 * @param page the paging count
 */
public FillOperation (FileExplorer explorer, File file, File [] files, int start, int page) {
	super (explorer.getShell (), new File [] {file});
	this.explorer = explorer;
	this.files = files;
	this.start = start;
	this.page = page;
}

int countFiles (File [] files) {
	return files.length;
}

/**
 * Creates a table item in the explorer.
 * 
 * @param file the file for the table item
 * @return true if the item was created; false otherwise
 */
boolean createTableItem (File file) {
	if (!check (file, true)) return false;
	return explorer.createTableItem (file) != null;
}

String getMessage (File file) {
	return FileExplorer.getMessage ("Filling ...");
}

String getText () {
	return FileExplorer.getMessage ("Filling {0}", new Object [] {files [0].getAbsolutePath ()});
}

/**
 * Fill the table with items that represent files.
 * 
 * @param files the files for the operation
 * @return true if the operation was successful; false otherwise
 */
boolean operation (File [] file) {
	/* Force the fill operation to always be long running */
	isLong = true;
	synchronized (this) {
		notifyAll ();
	}
	if (longRunnable != null) longRunnable.run ();
	int index = start;
	final boolean [] quit = new boolean [1];
	while (index < files.length) {
		if (quit [0]) break;
		final int start = index;
		final int end = Math.min (files.length, start + page);
		display.syncExec (new Runnable () {
			public void run () {
				int count = start;
				while (count < end) {
					if (!createTableItem (files [count])) break;
					count++;
				}
				quit [0] = count < end;
			}
		});
		index = end;
	}
	return !quit [0];
}

}
