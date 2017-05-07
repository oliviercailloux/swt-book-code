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
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.program.*;

import java.util.*;
import java.io.*;
import java.text.*;

/**
 * @author Steve Northover
 *
 * FileExplorer is a simple file system browser.  FileExplorer
 * demonstrates flexible layout, drag and drop, application
 * launching, mutithreading and in-line editing.
 * 
 * Thursday June 17, 2004.
 * 
 */
public class FileExplorer {

	Display display;
	Shell shell;
	ToolBar toolBar;
	Combo combo;
	Tree tree;
	Sash sash;
	Table table;
	Label status;
	Menu editMenu;
	ToolItem deleteItem, comboItem, parentItem, copyItem, cutItem, pasteItem, renameItem;

	FormData statusData;
	TableColumn sortColumn;
	Widget toolBarTarget;
	TreeItem lastSelection;
	Item lastEditItem;
	final Runnable editRunnable = new Runnable () {
		public void run () {
			if (shell.isDisposed ()) return;
			if (lastEditItem != null && !lastEditItem.isDisposed()) {
				rename (lastEditItem);
			}
		}
	};
	FillOperation fillOperation;

	static final int [] TABLE_WIDTHS = new int [] {150, 60, 125, 175};
	static final String [] TABLE_TITLES = new String [] {getMessage ("Name"), getMessage ("Size"), getMessage ("Type"), getMessage ("Modified")};
	static final int NAME_COLUMN = 0;
	static final int SIZE_COLUMN = 1;
	static final int TYPE_COLUMN = 2;
	static final int MODIFIED_COLUMN = 3;
	
	static String APPLICATION_NAME, PROGRAM_NAME;
	static Hashtable ICONS = new Hashtable();
	static Cursor WAIT_CURSOR, BUSY_CURSOR;
	static Image DRIVE_CLOSED, DRIVE_OPEN, FOLDER_OPEN, FOLDER_CLOSED, FILE_ICON;
	static Image PARENT_ICON, REFRESH_ICON, CUT_ICON, COPY_ICON, PASTE_ICON;
	static Image DELETE_ICON, RENAME_ICON, SEARCH_ICON, PRINT_ICON, PROGRAM_ICON;
	static Color BUSY_COLOR;
	static Clipboard CLIPBOARD;
	static final int REFRESH_TIME = 5000;
	static final int TOOLBAR_REFRESH_TIME = 250;

	static final String NEW_FILE = "NewFile.txt";
	static final String NEW_FOLDER = "NewFolder";
	static ResourceBundle resources;
	
static {
	try {
		resources = ResourceBundle.getBundle ("explorer");
	} catch (MissingResourceException e) {}
}

/**
 * Loads a string from a resource file using a key.
 * If the key does not exist, it is used as the result.
 *
 * @param key the name of the string resource
 * @return the string resource
 */
static String getMessage (String key) {
	if (resources == null) return key;
	try {
		return resources.getString (key);
	} catch (MissingResourceException e) {
		return key;
	}			
}

/**
 * Loads a string from a resource file using a key
 * and formats it using MessageFormat.  If the key does
 * not exist, it is used as the argument to be format().
 *
 * @param key the name of the string resource
 * @param args the array of strings to substitute
 * @return the string resource
 */
static String getMessage (String key, Object [] args) {
	return MessageFormat.format (getMessage (key), args);
}

/**
 * Loads an image using a resource name.  A new image is returned
 * every time.  The caller is responsible for disposing of the
 * image when it is no longer needed.
 *
 * @param display the display where the image is be created
 * @param clazz the class that is used to locate the resource
 * @param string the name of the resource
 * @return the new image
 */
static Image loadImage (Display display, Class clazz, String string) {
	InputStream stream = clazz.getResourceAsStream (string);
	if (stream == null) return null;
	Image image = null;
	try {
		image = new Image (display, stream);
	} catch (SWTException ex) {
	} finally {
		try {
			stream.close ();
		} catch (IOException ex) {}
	}
	return image;
}

/**
 * Runs FileExplorer.  The user-interface consists of a single
 * shell, a tool bar, a tree, a table and a status bar. When
 * the user closes the shell, the program ends.
 *
 * @param args the arguments array to the program
 */
public static void main (String [] args) {
	/*
	* Set the application name to the name of this class
	* to allow the program to be configured on X based
	* systems using the resource mechanism.
	*/
	String name = FileExplorer.class.getName ();
	int index = name.lastIndexOf ('.');
	APPLICATION_NAME = name.substring (index + 1);
	PROGRAM_NAME = getMessage (APPLICATION_NAME);
	Display.setAppName (APPLICATION_NAME);
	
	/*
	* Create the Display and acquire the default set of
	* resources used by FileExplorer.  These consist of
	* the images, cursors and colors that will be used
	* in the program.
	*/
	Display display = new Display ();
	createImages (display);
	CLIPBOARD = new Clipboard (display);
	WAIT_CURSOR = new Cursor (display, SWT.CURSOR_WAIT);
	BUSY_CURSOR = new Cursor (display, SWT.CURSOR_APPSTARTING);
	BUSY_COLOR = display.getSystemColor (SWT.COLOR_DARK_GRAY);
	
	/*
	* Create a FileExporer and run the event loop.  The loop
	* exits when the main shell is closed.  It is possible to
	* run multiple instances of FileExplorer by changing the
	* exit condition (but this has not been tested).
	*/
	Shell shell = new FileExplorer ().open (display, args);
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	
	/*
	* If the display is already disposed for any reason.  Don't
	* try to do anything else, just exit.  Resources will be
	* given back when the process exits.
	*/
	if (display.isDisposed ()) System.exit (0);
	
	/*
	* If there are outstanding FileOperations, cancel them and
	* wait for them to stop.
	*/
	do {
		FileOperation [] operations = FileOperation.getOperations ();
		if (operations.length == 0) break;
		for (int i=0; i<operations.length; i++) operations [i].cancel ();
		if (!display.readAndDispatch ()) display.sleep ();
	} while (true);
	
	/* Release resources */
	disposeImages ();
	CLIPBOARD.dispose ();
	WAIT_CURSOR.dispose ();
	BUSY_CURSOR.dispose ();
	display.dispose ();
}

/**
 * Creates all of the default images used within the program.
 *
 * @param display the display where the images are created
 */
static void createImages (Display display) {
	Class clazz = FileExplorer.class;
	DRIVE_CLOSED = loadImage (display, clazz, "drive_closed.gif");
	DRIVE_OPEN = loadImage (display, clazz, "drive_open.gif");
	FOLDER_CLOSED = loadImage (display, clazz, "folder_closed.gif");
	FOLDER_OPEN = loadImage (display, clazz, "folder_open.gif");
	FILE_ICON = loadImage (display, clazz, "file_icon.gif");
	PARENT_ICON = loadImage (display, clazz, "parent_icon.gif");
	REFRESH_ICON = loadImage (display, clazz, "refresh_icon.gif");
	CUT_ICON = loadImage (display, clazz, "cut_icon.gif");
	COPY_ICON =loadImage (display, clazz, "copy_icon.gif");
	PASTE_ICON = loadImage (display, clazz, "paste_icon.gif");
	DELETE_ICON = loadImage (display, clazz, "delete_icon.gif");
	RENAME_ICON = loadImage (display, clazz, "rename_icon.gif");
	SEARCH_ICON = loadImage (display, clazz, "search_icon.gif");
	PRINT_ICON = loadImage (display, clazz, "print_icon.gif");
	PROGRAM_ICON = loadImage (display, clazz, "program_icon.gif");
}

/**
 * Disposes all of the default images used within the program.
 */
static void disposeImages () {
	DRIVE_CLOSED.dispose ();
	DRIVE_OPEN.dispose ();
	FOLDER_CLOSED.dispose ();
	FOLDER_OPEN.dispose ();
	FILE_ICON.dispose ();
	PARENT_ICON.dispose ();	
	REFRESH_ICON.dispose ();	
	CUT_ICON.dispose ();	
	COPY_ICON.dispose ();	
	PASTE_ICON.dispose ();	
	DELETE_ICON.dispose ();	
	RENAME_ICON.dispose ();	
	SEARCH_ICON.dispose ();	
	PRINT_ICON.dispose ();
	PROGRAM_ICON.dispose ();
	Enumeration e = ICONS.elements ();
	while (e.hasMoreElements ()) {
		((Image) e.nextElement()).dispose ();
	}
}

/**
 * Opens an "about" dialog. 
 */
void about () {
	/*
	* Create the dialog shell and add the listeners.
	*/
	final Shell dialog = new Shell (shell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
	dialog.setText (getMessage ("About {0}", new Object [] {APPLICATION_NAME}));
	Label label = new Label (dialog, SWT.NONE);
	label.setImage (PROGRAM_ICON);
	Label message = new Label (dialog, SWT.WRAP);
	String string =
		"{0} is a simple file browser that demonstrates flexible layout, drag and drop," +
		"application launching, " + "mutithreading and in-line editing.";
	message.setText (getMessage (string, new Object [] {APPLICATION_NAME}));
	Label separator = new Label (dialog, SWT.SEPARATOR | SWT.HORIZONTAL);
	Button button = new Button (dialog, SWT.PUSH);
	button.setText (getMessage ("OK"));
	button.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			dialog.close ();
		}
	});
	
	/*
	* Create the layout for the shell and pack it.
	*/
	Monitor monitor = shell.getMonitor ();
	int width = monitor.getClientArea ().width / 5;
	FormLayout layout = new FormLayout ();
	layout.spacing = 8;
	layout.marginWidth = layout.marginHeight = 10;
	dialog.setLayout (layout);
	FormData messageData = new FormData ();
	messageData.width = width;
	messageData.left = new FormAttachment (label);
	messageData.right = new FormAttachment (100);
	messageData.top = new FormAttachment (0);
	messageData.bottom = new FormAttachment (separator);
	message.setLayoutData (messageData);
	FormData separatorData = new FormData ();
	separatorData.left = new FormAttachment (0);
	separatorData.right = new FormAttachment (100);
	separatorData.bottom = new FormAttachment (button);
	separator.setLayoutData (separatorData);
	FormData buttonData = new FormData ();
	buttonData.right = new FormAttachment (100);
	buttonData.bottom = new FormAttachment (100);
	button.setLayoutData (buttonData);
	dialog.pack ();
	
	/*
	* Center the dialog within the parent shell and open it,
	* ensuring that the dialog is visible on the montitor.
	*/
	Rectangle rect = shell.getClientArea ();
	rect = display.map (shell, null, rect);
	Rectangle bounds = dialog.getBounds ();
	int x = Math.max (0, (rect.width - bounds.width) / 2);
	int y = Math.max (0, (rect.height - bounds.height) / 2);
	Rectangle monitorRect = monitor.getClientArea ();
	x = Math.min (rect.x + x, monitorRect.x + monitorRect.width - bounds.width);
	y = Math.min (rect.y + y, monitorRect.y + monitorRect.height - bounds.height);
	dialog.setLocation (x, y);
	dialog.open ();	
}

/**
 * Returns true if one file is an ancestor of another.
 *
 * @param file1 the first file
 * @param file2 the second file
 * @return true if file1 is an ancestor of file2; false othewise
 */
boolean isAncestor (File file1, File file2) {
	while (file2 != null) {
		if (file2.equals (file1)) return true;
		file2 = file2.getParentFile ();
	}
	return false;
}

/**
 * Returns true if a file is busy.  A busy file is one that
 * is being copied or deleted or is contained in a directory
 * that is being copied or deleted.
 *
 * @param file the file that could be busy
 * @return true if file is busy; false othewise
 */
boolean isBusy (File file) {
	FileOperation [] operations = FileOperation.getOperations ();
	for (int i=0; i<operations.length; i++) {
		FileOperation operation = operations [i];
		if (!(operations [i] instanceof FillOperation)) {
			File [] files = operation.getLockedFiles ();
			for (int j=0; j<files.length; j++) {
				if (isAncestor (files [j], file)) return true;
			}
		}
	}
	return false;
}

/**
 * Checks to see whether data to paste is available.
 * 
 * @return true if the item should be enabled; false otherwise
 */
boolean isPasteAvailable () {
	/*
	* Bug in GTK and Motif.  Every once in a while, the clipboard
	* indicates that there are no available files, even when some
	* files have been copied to the clipboard.  This causes the
	* paste icon to flash.  The fix is to disable the this feature
	* on these platforms.
	*/
	String platform = SWT.getPlatform ();
	if ("gtk".equals (platform) || "motif".equals (platform)) return true;
	int index = 0;
	boolean pasteEnabled = false;
	TransferData [] transfers = CLIPBOARD.getAvailableTypes ();
	while (index < transfers.length) {
		if (FileTransfer.getInstance ().isSupportedType (transfers [index])) {
			pasteEnabled = true;
			break;
		}
		index++;
	}
	return pasteEnabled;
}

/**
 * Returns true if a file can be copied into a folder.
 * If the folder is a file, then a file cannot be copied into
 * it.  When any child or ancestor of the file is being deleted,
 * the file cannot be copied because it is about to be deleted.
 * If a folder is being deleted, a file cannot be copied into it.
 * If the file is the destination of a copy operation, then
 * the folder cannot be copied.
 *
 * @param file the source file
 * @param folder the destination folder
 * @return true if file can be copied; false othewise
 */
boolean checkCopy (File file, File folder) {
	if (folder.isFile ()) return false;
	FileOperation [] operations = FileOperation.getOperations ();
	for (int i=0; i<operations.length; i++) {
		FileOperation operation = operations [i];
		if (operations [i] instanceof DeleteOperation) {
			File [] files = operation.getLockedFiles ();
			for (int j=0; j<files.length; j++) {
				if (isAncestor (file, files [j])) return false;
				if (isAncestor (files [j], file)) return false;
				if (isAncestor (files [j], folder)) return false;
			}
		}
		if (operations [i] instanceof CopyOperation) {
			File [] files = operation.getLockedFiles ();
			File dest = files [files.length - 1];
			if (isAncestor (file, dest)) return false;
			if (isAncestor (dest, file)) return false;
		}
	}
	return true;
}

/**
 * Returns true if a file can be deleted.  If any child or
 * ancestor of the file is being copied or deleted, then the
 * file cannot be deleted.
 *
 * @param file the source file
 * @return true if file can be deleted; false othewise
 */
boolean checkDelete (File file) {
	FileOperation [] operations = FileOperation.getOperations ();
	for (int i=0; i<operations.length; i++) {
		FileOperation operation = operations [i];
		if (!(operations [i] instanceof FillOperation)) {
			File [] files = operation.getLockedFiles ();
			for (int j=0; j<files.length; j++) {
				if (isAncestor (file, files [j])) return false;
				if (isAncestor (files [j], file)) return false;
			}
		}
	}
	return true;
}

/**
 * Returns true if a file can be renamed.  If any child or
 * ancestor of the file is being copied or deleted, then the
 * file cannot be renamed.
 *
 * @param file the source file
 * @return true if file can be deleted; false othewise
 */
boolean checkRename (File file) {
	/* If a file can be deleted, it can be renamed */
	return checkDelete (file);
}

/**
 * Performs the combo selection action.  When the combo
 * is selected, the tree is searched for a directory that
 * matches the selected string.  If a match is found, the
 * tree is selected and the table is filled.
 *
 * @param string the selected text in the combo
 */
void comboSelected (String string) {
	File file = new File (string);
	TreeItem [] items = tree.getItems ();
	for (int i=0; i<items.length; i++) {
		if (file.equals (items [i].getData ())) {
			setCursor (WAIT_CURSOR);
			tree.setSelection (new TreeItem [] {items [i]});
			treeSelected (items [i], false);
			if (shell.isDisposed ()) return;
			setCursor (null);
			return;
		}
	}
}

/**
 * Compares two files.  Directories are placed before files.
 * The comparison is based on different file attributes such
 * as name and size.  Attributes are selected using an index
 * that matches a column in the table.
 *
 * @param file1 the first file
 * @param file2 the second file
 * @param index the index of the table column
 * @return the result of the compare (-1, 0, or 1)
 */
int compareFiles (File file1, File file2, int index) {
	if (file1.isDirectory ()) {
		if (!file2.isDirectory ()) return -1;
	} else {
	 	if (file2.isDirectory ()) return 1;
	}
	switch (index) {
		case NAME_COLUMN: return file1.compareTo (file2);
		case SIZE_COLUMN:
			long length1 = file1.length (), length2 = file2.length ();
			return length1 == length2 ? 0 : length1 < length2 ? -1 : 1;
		case MODIFIED_COLUMN:
			long modified1 = file1.lastModified (), modified2 = file2.lastModified ();
			return modified1 == modified2 ? 0 : modified1 < modified2 ? -1 : 1;
	}
	return 0;
}

/**
 * Confirms the copy operation with the user
 * 
 * @param files the source files to be copied
 * @param folder the destination folder
 * @param move indicates a move operation
 * @param move indicates the copy is part of a move operation
 */
boolean confirmCopy (File [] files, File folder, boolean move) {
	if (folder.isFile ()) {
		error (getMessage ("Destination is a file.  It must be a folder."));
		return false;
	}
	for (int i=0; i<files.length; i++) {
		if (!checkCopy (files [i], folder)) {
			error (getMessage ("\"{0}\" is busy.", new Object [] {files [i]}));
			return false;
		}
		if (move) {
			if (folder.equals (files [i].getParentFile ())) {
				error (getMessage ("Destination and source are the same."));
				return false;
			}
		}
		if (isAncestor (files [i], folder)) {	
			error (getMessage ("Destination is a subfolder or the same folder as the source."));
			return false;
		}
	}
	return true;
}

/**
 * Confirms the delete operation with the user.
 * 
 * @param files the files to be deleted
 * @return true if the operation should proceed; false otherwise
 */
boolean confirmDelete (File [] files) {
	String string = null;
	switch (files.length) {
		case 0: return false;
		case 1: {
			string = getMessage ("Delete \"{0}\"?",  new Object [] {files [0]});
			break;
		}
		default: {
			String names = "";
			for (int i=0; i<files.length; i++) {
				names += " \"" + files [i] + "\"" + (i == files.length - 1 ? "" : ",");
				if (names.length () > 128) {
					names = names.substring (0, 128) + "...";
					break;
				}
			}
			Integer count = new Integer (files.length);
			string = getMessage ("Delete {0} object(s): {1}?", new Object [] {count, names});
			break;
		}
	}
	if (question (string, SWT.YES | SWT.NO) != SWT.YES) return false;
	for (int i=0; i<files.length; i++) {
		if (!checkDelete (files [i])) {
			error (getMessage ("\"{0}\" is busy.", new Object [] {files [i]}));
			return false;
		}
	}
	return true;
}

/**
 * Copies the selected files to the clipboard.
 */
void copy () {
	Item [] items = getSelectedItems ();
	if (items.length == 0) return;
	String [] names = new String [items.length];
	for (int i=0; i<items.length; i++) {
		File file = (File) items [i].getData ();
		names [i] = file.getAbsolutePath ();
	}
	CLIPBOARD.setContents (new Object [] {names}, new Transfer [] {FileTransfer.getInstance ()});		
}

/**
 * Copies files into a folder.  The copy operation can fail
 * for a number of reasons.  If this happens, an error message
 * is issued.
 *
 * @param files the source files to be copied
 * @param folder the destination folder
 * @param move indicates the copy is part of a move operation
 * @return true if the operation was successful; false otherwise
 */
boolean copyFiles (File [] files, File folder, boolean move) {
	if (!confirmCopy (files, folder, move)) return false;
	boolean success = true;
	for (int i=0; i<files.length; i++) {
		File oldFile = files [i];
		File newFile = new File (folder.getPath (), oldFile.getName ());
		if (newFile.exists ()) {
			//TODO - merge files rather than prompting the user and creating a new file
			String string = getMessage ("The file or folder \"{0}\" exists, create a new file or folder?", new Object [] {newFile});
			switch (question (string, SWT.YES | SWT.NO | SWT.CANCEL)) {
				case SWT.YES:
					newFile = createFile (folder, oldFile.getName (), oldFile.isFile ());
					if (newFile.exists ()) {
						information (getMessage ("Created the file or folder \"{0}\".", new Object [] {newFile}));
					} else {
						error (getMessage ("Failed to create \"{0}\".", new Object [] {newFile}));
						newFile = null;
					}
					break;
				case SWT.NO:
					newFile = null;
					break;
				case SWT.CANCEL:
					return false;
			}
		}
		if (newFile != null) {
			final CopyOperation operation = new CopyOperation (shell, oldFile, newFile);
			operation.longExec (new Runnable () {
				public void run () {
					display.asyncExec (new Runnable () {
						public void run () {
							if (shell.isDisposed ()) return;
							setCursor (BUSY_CURSOR);
							refreshAll (true, false);
						}
					});
					operation.doneExec (new Runnable () {
						public void run () {
							display.asyncExec (new Runnable () {
								public void run () {
									if (shell.isDisposed ()) return;
									refreshAll (true, false);
									if (shell.isDisposed ()) return;
									setCursor (null);
								}
							});
						}
					});
				}
			});
			
			/*
			* When a drag and drop operation takes too long, on some platforms
			* a drag end is posted into the message queue.  This means that
			* during a long move operation, when a message loop is running to
			* wait for the delete, it is delivered early.  The fix is to avoid
			* forking a thread for move operations on these platforms.
			* 
			* NOTE:  Most applications are not as fancy as FileExplorer and
			* do not run an event loop during drag and drop.  By the time you
			* read this, the problem may be fixed in the platform or worked
			* around in SWT.
			*/
			boolean fork = true;
			if (move) {
				String platform = SWT.getPlatform ();
				if ("gtk".equals (platform) || "motif".equals (platform)) {
					fork = false;
				}
			}
			if (operation.run (fork)) {
				refreshAll (true, false);
			} else {
				if (move) {
					/* Add a pending delete operation to lock the directory */
					shell.setCursor (BUSY_CURSOR);
					DeleteOperation pending = new DeleteOperation (shell, new File [] {oldFile});	
					while (!shell.isDisposed () && operation.isRunning ()) {
						if (!display.readAndDispatch ()) display.sleep ();
					}
					pending.cancel ();
					if (shell.isDisposed ()) return false;
					setCursor (null);
					if (operation.isCancelled ()) return false;
				}
			}
			success = success && operation.isSuccessful ();
		}
	}
	return success;
}

/**
 * Creates a drag source for a control.  The control will
 * act as a drag source for FileTransfers allowing files
 * to be copied and moved.
 *
 * @param control the drag source control
 */
void createDragSource (final Control control) {
	DragSource source = new DragSource (control, DND.DROP_COPY | DND.DROP_MOVE);
	source.setTransfer (new Transfer [] {FileTransfer.getInstance ()});
	source.addDragListener (new DragSourceAdapter () {
		File [] files = null;
		Item [] selection = null;
		public void dragStart (DragSourceEvent event) {
			lastEditItem = null;
			toolBarTarget = control;
			display.timerExec (-1, editRunnable);
			/*
			* Do not allow two drags to happen at the same time.
			* When the drag is a move operation, it is not possible
			* to determine which drag ended and delete the data.
			*/
			if (selection != null) {
				event.doit = false;
			} else {
				selection = getSelectedItems ();
				event.doit = selection.length > 0;
				if (!event.doit) selection = null;
			}
		}
		public void dragSetData (DragSourceEvent event) {
			files = new File [selection.length];
			String [] names = new String [selection.length];
			for (int i=0; i<selection.length; i++) {
				files [i] = (File) selection [i].getData();
				names [i] = files [i].getAbsolutePath ();
			}
			event.data = names;
		}
		public void dragFinished (DragSourceEvent event) {
			if (files != null) {
				switch (event.detail) {
					case DND.DROP_MOVE:
						if (event.doit) {
							DeleteOperation operation = new DeleteOperation (shell, files);
							if (!operation.run (true)) {
								setCursor (BUSY_CURSOR);
								while (!shell.isDisposed () && operation.isRunning ()) {
									if (!display.readAndDispatch ()) display.sleep ();
								}
								if (shell.isDisposed ()) return;
								setCursor (null);
							}
						}
						//FALL THROUGH
					default:
						refreshAll (true, false);
						break;
				}
			}
			files = null;
			selection = null;
		}
	});
}

/**
 * Creates a drop target for a control.  The control will
 * act as a drop target for FileTransfers allowing files
 * to be copied and moved.
 *
 * @param control the drop target control
 */
void createDropTarget (final Control control) {
	DropTarget target = new DropTarget (control, DND.DROP_MOVE | DND.DROP_COPY);
	target.setTransfer (new Transfer[] {FileTransfer.getInstance()});
	target.addDropListener (new DropTargetAdapter () {
		int detail = DND.DROP_NONE;
		public void dragEnter (DropTargetEvent event) {
			detail = event.detail;
			dragOver (event);
		}
		public void dragOperationChanged (DropTargetEvent event) {
			detail = event.detail;
			dragOver (event);
		}
		public void dragOver (DropTargetEvent event) {
			Item item = (Item) event.item;
			File file = item != null ? (File) item.getData () : null;
			event.detail = file == null || file.isFile () ? DND.DROP_NONE : detail;
			event.feedback |= DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
		}
		public void dragLeave (DropTargetEvent event) {
			detail = DND.DROP_NONE;
		}
		public void drop (DropTargetEvent event) {
			Item item = (Item) event.item;
			File folder = item != null ? (File) item.getData () : null;
			String [] names = (String[]) event.data;
			File [] files = new File [names.length];
			for (int i=0; i<names.length; i++) {
				files [i] = new File (names [i]);
			}
			if (!copyFiles (expandRoots (files), folder, event.detail == DND.DROP_MOVE)) {
				event.detail = DND.DROP_NONE;
			}
		}
	});
}

/**
 * Creates the edit menu items for a menu.  The edit menu
 * appears on the menu bar and as the context menu for the
 * tree and table.
 *
 * @param editMenu the edit menu
 * @param isPopup the edit menu is a popup menu
 * @param isTree the edit menu is for a tree conrol
 */
void createEditItems (final Menu editMenu, final boolean isPopup, final boolean isTree) {
	final MenuItem cutItem = new MenuItem (editMenu, SWT.PUSH);
	cutItem.setText (getMessage ("Cut\tCtrl+X"));
	cutItem.setImage (CUT_ICON);
	cutItem.setAccelerator (SWT.MOD1 + 'X');
	cutItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			cut ();
		}
	});
	final MenuItem copyItem = new MenuItem (editMenu, SWT.PUSH);
	copyItem.setText (getMessage ("Copy\tCtrl+C"));
	copyItem.setImage (COPY_ICON);
	copyItem.setAccelerator (SWT.MOD1 + 'C');
	copyItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			copy ();
		}
	});
	final MenuItem pasteItem = new MenuItem (editMenu, SWT.PUSH);
	pasteItem.setText (getMessage ("Paste\tCtrl+V"));	
	pasteItem.setAccelerator (SWT.MOD1 + 'V');
	pasteItem.setImage (PASTE_ICON);
	pasteItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			paste ();
		}
	});
	new MenuItem (editMenu, SWT.SEPARATOR);
	final MenuItem deleteItem = new MenuItem (editMenu, SWT.PUSH);
	deleteItem.setText (getMessage ("&Delete\tDelete"));
	deleteItem.setImage (DELETE_ICON);
	deleteItem.setAccelerator (SWT.DEL);
	deleteItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			delete ();
		}
	});
	final MenuItem renameItem = new MenuItem (editMenu, SWT.PUSH);
	renameItem.setText (getMessage ("&Rename\tCtrl+R"));
	renameItem.setAccelerator (SWT.MOD1 + 'R');
	renameItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			rename ();
		}
	});
	final MenuItem selectAllItem;
	if (!isTree) {
		new MenuItem (editMenu, SWT.SEPARATOR);
		selectAllItem = new MenuItem (editMenu, SWT.PUSH);
		selectAllItem.setText (getMessage ("&Select All\tCtrl+A"));
		selectAllItem.setAccelerator (SWT.MOD1 + 'A');
		selectAllItem.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent event) {
				selectAll ();
			}
		});
	} else {
		selectAllItem = null;
	}
	editMenu.addMenuListener (new MenuAdapter () {
		public void menuShown (MenuEvent event) {
			Item [] items = getSelectedItems ();
			boolean enabled = items.length > 0;
			cutItem.setEnabled (enabled);
			copyItem.setEnabled (enabled);
			pasteItem.setEnabled (isPasteAvailable ());
			deleteItem.setEnabled (enabled);
			if (enabled && items.length == 1) {
				File file = (File) items [0].getData ();
				enabled = checkRename (file);
			}
			renameItem.setEnabled (enabled);
			if (selectAllItem != null) {
				selectAllItem.setEnabled (table.getItemCount () > 0);
			}
			if (isPopup) display.timerExec (-1, editRunnable);
		}
	});
}

/**
 * Creates the edit menu for the menu bar.  The edit menu
 * is created as a drop down menu and attached to the edit
 * menu cascade item in the menu bar.
 *
 * @param editItem the menu bar cascade item
 */
void createEditMenu (MenuItem editItem) {
	editMenu = new Menu (shell, SWT.DROP_DOWN);
	editItem.setMenu (editMenu);
	createEditItems (editMenu, false, false);
}

/**
 * Creates a file or folder.  The current selection in the tree
 * determines the parent directory for the file.  A new file and
 * table item is created and an in-line edit operation is started.
 *
 * @param isFile indicates that a file should be created rather than a folder
 */
void createFile (final boolean isFile) {
	final TreeItem [] selection = tree.getSelection ();
	if (selection.length != 1) return;
	File root = (File) selection [0].getData ();
	String name = isFile ? NEW_FILE : NEW_FOLDER;
	final File file = createFile (root, name, isFile);
	if (file == null) return;
	final TableItem item = createTableItem (file);
	table.setSelection (new TableItem [] {item});
	table.setFocus ();
	/*
	* Bug in GTK.  Run this later to ensure that the table has
	* scrolled to show the selection.  By the time you read this,
	* this problem may be fixed.  In any case, queing the create
	* operation is not harmful on all platforms.
	* 
	* NOTE: This particular problem is part of GTK and needs to
	* be worked around by all native GTK applications.
	*/
	display.asyncExec (new Runnable () {
		public void run () {
			if (item.isDisposed ()) return;
			if (!isFile) {
				TreeItem [] items = selection [0].getItems ();
				if (items.length == 0) {
					new TreeItem (selection [0], SWT.NONE);
				} else {
					if (items [0].getData () != null) {
						createTreeItem (selection [0], items, file);
					}
				}
			}
			rename (item);
		}
	});
}

/**
 * Creates a file or folder on the file system.  If the
 * operation fail because a file of the same name exists,
 * the operation is repeated with a new name derived from
 * the old one.  If the file cannot be created, null is
 * returned.
 *
 * @param folder the parent folder for the new file
 * @param name the name of the file or folder
 * @param isFile create a file or folder 
 * @return the new file or folder (or null)
 */
File createFile (File folder, String name, boolean isFile) {
	int index = 0;
	File file = null;
	while (index < 1024) {
		String prefix = "";
		switch (index) {
			case 0: prefix = ""; break;
			case 1: prefix = "Copy of "; break;
			default:
				prefix = "Copy (" + index + ") of ";
				break;
		}
		file = new File (folder.getPath (), prefix + name);
		try {
			if (isFile) {
				if (file.createNewFile ()) break;
			} else {
				if (file.mkdir ()) break;
			}
		} catch (IOException ex) {
			return null;
		}
		index++;
	}
	return file;
}

/**
 * Creates the file menu for the menu bar.  The file menu
 * is created as a drop down menu and attached to the file
 * menu cascade item in the menu bar.
 *
 * @param fileItem the menu bar cascade item
 */
void createFileMenu (MenuItem fileItem) {
	Menu fileMenu = new Menu (shell, SWT.DROP_DOWN);
	fileItem.setMenu (fileMenu);
	MenuItem newItem = new MenuItem (fileMenu, SWT.CASCADE);
	newItem.setText (getMessage ("&New"));	
	Menu newMenu = new Menu (shell, SWT.DROP_DOWN);
	newItem.setMenu (newMenu);
	MenuItem createFolderItem = new MenuItem (newMenu, SWT.PUSH);
	createFolderItem.setText (getMessage ("Fo&lder\tCtrl+F"));
	createFolderItem.setImage (FOLDER_CLOSED);
	createFolderItem.setAccelerator (SWT.MOD1 + 'F');
	createFolderItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			createFile (false);
		}
	});
	MenuItem createFileItem = new MenuItem (newMenu, SWT.PUSH);
	createFileItem.setText (getMessage ("&File\tCtrl+L"));
	createFileItem.setImage (FILE_ICON);
	createFileItem.setAccelerator (SWT.MOD1 + 'L');
	createFileItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			createFile (true);
		}
	});
	new MenuItem (fileMenu, SWT.SEPARATOR);
	final MenuItem refreshItem = new MenuItem (fileMenu, SWT.PUSH);
	refreshItem.setText (getMessage ("&Refresh\tF5"));
	refreshItem.setAccelerator (SWT.F5);
	refreshItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			refreshAll (false, true);
		}
	});
	MenuItem exitItem = new MenuItem (fileMenu, SWT.PUSH);
	exitItem.setText (getMessage ("E&xit"));
	exitItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			shell.close ();
		}
	});
}

/**
 * Creates the help menu for the menu bar.  The help menu
 * is created as a drop down menu and attached to the help
 * menu cascade item in the menu bar.
 *
 * @param helpItem the menu bar cascade item
 */
void createHelpMenu (MenuItem helpItem) {
	Menu helpMenu = new Menu (shell, SWT.DROP_DOWN);
	helpItem.setMenu (helpMenu);
	MenuItem aboutItem = new MenuItem (helpMenu, SWT.PUSH);
	aboutItem.setText (getMessage ("About {0}", new Object [] {APPLICATION_NAME}));
	aboutItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			about ();
		}
	});
}

/**
 * Creates the layout for the shell.  A FormLayout is used
 * to place the tool bar, tree, sash, table and status line.
 * A selection listener is added to the sash to resize the
 * controls dynamically.
 */
void createLayout () {
	FormLayout layout = new FormLayout ();
	shell.setLayout (layout);
	FormData toolData = new FormData ();
	toolData.top = new FormAttachment (0);
	toolData.left = new FormAttachment (0);
	toolData.right = new FormAttachment (100);
	
	/*
	* Make the combo fit in the toolbar.  The initial
	* size was computed and assigned when the tool bar
	* was created.  Normally, the initial size of a
	* control is zero.  Use the current height as the
	* offset for the bottom attachment causing it to be
	* placed at an absolute location.
	*/
	int offset = toolBar.getSize().y;
	toolData.bottom = new FormAttachment (0, offset);
	
	toolBar.setLayoutData (toolData);
	FormData treeData = new FormData ();
	treeData.top = new FormAttachment (toolBar);
	treeData.bottom = new FormAttachment (status, -3);
	treeData.left = new FormAttachment (0);
	treeData.right = new FormAttachment (sash);
	tree.setLayoutData (treeData);
	final FormData sashData = new FormData ();
	sashData.left = new FormAttachment (30);
	sashData.top = new FormAttachment (toolBar);
	sashData.bottom = new FormAttachment (status, -3);
	sash.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			Rectangle sashRect = sash.getBounds ();
			Rectangle shellRect = shell.getClientArea ();
			int right = shellRect.width - sashRect.width - 128;
			event.x = Math.max (Math.min (event.x, right), 40);
			if (event.detail != SWT.DRAG)  {
				sashData.left = new FormAttachment (0, event.x);
				shell.layout ();
			}
		}
	});
	sash.setLayoutData (sashData);
	FormData tableData = new FormData ();
	tableData.top = new FormAttachment (toolBar);
	tableData.bottom = new FormAttachment (status, -3);
	tableData.left = new FormAttachment (sash);
	tableData.right = new FormAttachment (100);
	table.setLayoutData (tableData);
	statusData = new FormData ();
	statusData.bottom = new FormAttachment (100, -3);
	statusData.left = new FormAttachment (0, 3);
	statusData.right = new FormAttachment (100, -3);
	status.setLayoutData (statusData);
}

/**
 * Creates the menu bar and install it into the shell.  The
 * file, edit, view and help items are created and their
 * associated drop down menus are created.
 */
void createMenuBar () {
	Menu menuBar = new Menu (shell, SWT.BAR);
	shell.setMenuBar (menuBar);
	MenuItem fileItem = new MenuItem (menuBar, SWT.CASCADE);
	fileItem.setText (getMessage ("&File"));
	createFileMenu (fileItem);
	MenuItem editItem = new MenuItem (menuBar, SWT.CASCADE);
	editItem.setText (getMessage ("&Edit"));
	createEditMenu (editItem);
	MenuItem viewItem = new MenuItem (menuBar, SWT.CASCADE);
	viewItem.setText (getMessage ("&View"));
	createViewMenu (viewItem);
	MenuItem helpItem = new MenuItem (menuBar, SWT.CASCADE);
	helpItem.setText (getMessage ("&Help"));
	createHelpMenu (helpItem);
}

/**
 * Creates the sash.
 */
void createSash () {
	sash = new Sash (shell, SWT.VERTICAL);
}

/**
 * Creates the shell.
 */
void createShell () {
	shell = new Shell (display);
	shell.setText (PROGRAM_NAME);
	shell.setImage (PROGRAM_ICON);
	/*
	* Use a shell closed listener to stop the shell from closing when
	* there are outstanding FileOperations.  The user can optionally
	* cancel the close operation.
	*/
	shell.addShellListener (new ShellAdapter () {
		public void shellClosed (ShellEvent event) {
			int count = 0;
			FileOperation [] operations = FileOperation.getOperations ();
			for (int i=0; i<operations.length; i++) {
				if (operations [i].isRunning ()) {
					if (!(operations [i] instanceof FillOperation)) count++;
				}
			}
			if (count > 0) {
				String string = getMessage ("There are {0} operation(s) running.  Exit and stop the operation(s)?", new Object [] {new Integer (count)});
				event.doit = question (string, SWT.YES | SWT.NO) == SWT.YES;
			}
		}
	});
}

/**
 * Creates the status line.  The status line is a label control.
 */
void createStatus () {
	status = new Label (shell, SWT.NONE);
}

/**
 * Creates the table.  A table is used to show indiviual files and
 * folders within a directory.  The table is created with columns for
 * the file and folder names, size, type and modified date.  The table
 * is initially sorted in ascending order by name.
 */
void createTable () {
	table = new Table (shell, SWT.BORDER | SWT.MULTI);
	table.setHeaderVisible (true);
	
	/* Create the columns */
	for (int i=0; i<TABLE_TITLES.length; ++i) {
		final TableColumn column = new TableColumn (table, i == SIZE_COLUMN ? SWT.RIGHT : SWT.LEFT);
		column.setText (TABLE_TITLES [i]);
		column.setWidth (TABLE_WIDTHS [i]);
		column.setData (new Boolean (true));
		column.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent event) {
				tableColumnSelected (column);
			}
		});
	}
	sortColumn = table.getColumn (NAME_COLUMN);
	
	/*
	* Add a selection listener to show the statistics associated with the
	* selected items on the status line.  When the user reselects an item,
	* queue an in-line edit session to allow the name of the folder or
	* directory to be changed.
	*/
	table.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			TableItem [] items = table.getSelection ();
			tableSelected (items);
			TableItem item = items.length == 1 ? items [0] : null;
			if (item != null && item == lastEditItem) {
				File file = (File) item.getData ();
				if (checkRename (file)) {
					int time = display.getDoubleClickTime () * 3 / 2;
					display.timerExec (time, editRunnable);
				}
			} else {
				lastEditItem = item;
			}
		}
		public void widgetDefaultSelected (SelectionEvent event) {
			display.timerExec (-1, editRunnable);
			tableDefaultSelected (table.getSelection ());
		}
	});
	table.addFocusListener (new FocusAdapter () {
		public void focusGained (FocusEvent event) {
			toolBarTarget = event.widget;
		}
		public void focusLost (FocusEvent event) {
			display.timerExec (-1, editRunnable);
		}
	});
	table.addKeyListener (new KeyAdapter () {
		public void keyPressed (KeyEvent event) {
			if (event.character == SWT.DEL) delete ();
		}
	});
}

/**
 * Creates a table item for a file.  When a table item is created, it
 * is initialized to show the name, size, type and modified date for
 * the file.
 * 
 * @param file the file to provide data for the item
 * @return the new table item
 */
public TableItem createTableItem (File file) {
	if (shell.isDisposed ()) return null;
	TableItem item = new TableItem (table, SWT.NULL);
	item.setData (file);
	String name = file.getName ();
	item.setText (NAME_COLUMN, name);
	if (file.isFile ()) {
		long size = Math.max (1, (file.length () + 512) / 1024);
		item.setText (SIZE_COLUMN, getMessage ("{0} KB", new Object [] {new Long (size)}));
		setFileImage (item);
	} else {
		item.setText (TYPE_COLUMN, getMessage ("Folder"));
		item.setImage (FOLDER_CLOSED);
	}
	DateFormat dateFormat = DateFormat.getDateTimeInstance (DateFormat.SHORT, DateFormat.MEDIUM);
	String date = dateFormat.format (new Date (file.lastModified ()));
	item.setText (MODIFIED_COLUMN, date);
	return item;
}

/**
 * Creates and assigns the popup menu for the table.
 */
void createTableMenu () {
	Menu menu = new Menu (shell, SWT.POP_UP);
	createEditItems (menu, true, false);
	table.setMenu (menu);
}

/**
 * Creates the text control used for in-line editing.  The new
 * control is initialized with the text from the item.
 * 
 * @param editor the control editor that wraps the text control
 * @param parent the parent control (either a table or tree)
 * @param item the item to edit (either a table or tree item)
 * @param bounds the bounds of the item
 * @return a new text control
 */
Text createText (final ControlEditor editor, final Composite parent, final Item item, final Rectangle bounds) {
	/*
	* Create a composite to surround the text control with a thin
	* black line which is actually the background color of the
	* composite except on the Macintosh where this looks wrong.
	*/
	final Composite composite = new Composite (parent, SWT.NONE);
	final FillLayout layout = new FillLayout ();
	if (!("carbon".equals (SWT.getPlatform ()))) {
		layout.marginWidth = layout.marginHeight = 1;
		composite.setBackground (display.getSystemColor (SWT.COLOR_BLACK));
	}
	composite.setLayout (layout);
	final Text text = new Text (composite, SWT.NONE);
	
	/*
	* Add the listeners to the text control that enable in-line editing.
	* A verify listener builds the new contents of the control before it
	* is entered into the control in order to measure it.  If the control
	* needs to be larger to show the text, the control editor is resized.
	* A focus listener cancels the in-line edit operation and accepts
	* the new string.  A travers listener overrides the Escape and Return
	* keys to either accept or cancel the in-line edit operation.
	*/
	text.addVerifyListener (new VerifyListener () {
		public void verifyText (VerifyEvent event) {
			String oldText = text.getText ();
			String leftText = oldText.substring (0, event.start);
			String rightText = oldText.substring (event.end, oldText.length ());
			GC gc = new GC (text);
			Point size = gc.textExtent (leftText + event.text + rightText);
			gc.dispose ();
			size = text.computeSize (size.x, SWT.DEFAULT);
			Rectangle rect = parent.getClientArea ();
			editor.minimumWidth = Math.max (size.x, bounds.width) + (layout.marginWidth * 2);
			int left = bounds.x, right = rect.x + rect.width;
			editor.minimumWidth = Math.min (editor.minimumWidth, right - left);
			editor.minimumHeight = size.y + (layout.marginHeight * 2);
			editor.layout ();
		}
	});
	text.addFocusListener (new FocusAdapter () {
		public void focusLost (FocusEvent event) {
			item.setText (text.getText ());
			composite.dispose ();
			editor.dispose ();
		}
	});
	text.addTraverseListener (new TraverseListener () {
		public void keyTraversed (TraverseEvent event) {
			switch (event.detail) {
				case SWT.TRAVERSE_RETURN:
					item.setText (text.getText ());
					//FALL THROUGH
				case SWT.TRAVERSE_ESCAPE:
					composite.dispose ();
					editor.dispose ();
					event.doit = false;
			}
		}
	});
	
	/*
	* Setting the text after the verify listener is added ensures
	* that the initial size of the in-line edit control is correct.
	* Verify listeners run whenever the text is modified.
	*/
	text.setText (item.getText ());
	text.selectAll ();
	return text;
}

/**
 * Creates the timer that is used to refersh the table and
 * the tree.  This timer runs all the time but only does
 * work when there is a long operation.
 */
void createRefreshTimer () {
	display.timerExec (REFRESH_TIME, new Runnable () {
		public void run () {
			if (shell.isDisposed ()) return;
			Shell [] shells = shell.getShells ();
			if (shells.length > 0) refreshAll (true, false);
			display.timerExec (REFRESH_TIME, this);
		}
	});
}

/**
 * Creates the tool bar.  The tool bar contains icons to allow
 * the user to cut, copy and paste files.
 */
void createToolBar () {
	toolBar = new ToolBar (shell, SWT.FLAT | SWT.SHADOW_OUT);
	combo = new Combo (toolBar, SWT.DROP_DOWN | SWT.READ_ONLY);
	combo.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			comboSelected (combo.getText ());
		}
	});
	comboItem = new ToolItem (toolBar, SWT.SEPARATOR);
	comboItem.setControl (combo);
	new ToolItem (toolBar, SWT.SEPARATOR);
	parentItem = new ToolItem (toolBar, SWT.PUSH);
	parentItem.setImage (PARENT_ICON);
	parentItem.setToolTipText (getMessage("Parent"));
	parentItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			traverseParent ();
		}
	});
	parentItem.setEnabled (false);
	final ToolItem refreshItem = new ToolItem (toolBar, SWT.PUSH);
	refreshItem.setImage (REFRESH_ICON);
	refreshItem.setToolTipText (getMessage("Refresh"));
	refreshItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			refresh ();
		}
	});
	new ToolItem (toolBar, SWT.SEPARATOR);
	cutItem = new ToolItem (toolBar, SWT.PUSH);
	cutItem.setImage (CUT_ICON);
	cutItem.setToolTipText (getMessage("Cut"));
	cutItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			cut ();
		}
	});
	copyItem = new ToolItem (toolBar, SWT.PUSH);
	copyItem.setImage (COPY_ICON);
	copyItem.setToolTipText (getMessage("Copy"));
	copyItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			copy ();
		}
	});
	pasteItem = new ToolItem (toolBar, SWT.PUSH);
	pasteItem.setImage (PASTE_ICON);
	pasteItem.setToolTipText (getMessage("Paste"));
	pasteItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			paste ();
		}
	});
	new ToolItem (toolBar, SWT.SEPARATOR);
	deleteItem = new ToolItem (toolBar, SWT.PUSH);
	deleteItem.setImage (DELETE_ICON);
	deleteItem.setToolTipText (getMessage("Delete"));
	deleteItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			delete ();
		}
	});
	renameItem = new ToolItem (toolBar, SWT.PUSH);
	renameItem.setImage (RENAME_ICON);
	renameItem.setToolTipText (getMessage("Rename"));
	renameItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			rename ();
		}
	});
	ToolItem searchItem = new ToolItem (toolBar, SWT.PUSH);
	searchItem.setImage (SEARCH_ICON);
	searchItem.setToolTipText (getMessage("Search"));
	searchItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			search ();
		}
	});
	ToolItem printItem = new ToolItem (toolBar, SWT.PUSH);
	printItem.setImage (PRINT_ICON);
	printItem.setToolTipText (getMessage("Print"));
	printItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			print ();
		}
	});
	
	/*
	* Force the tool bar to be tall enough to show the combo.
	* This value is used later when creating the layout for
	* the shell.  Setting the size of a control that is going
	* to be layed out is pointless because the layout just
	* ignores it.
	*/
	Point p1 = combo.computeSize (SWT.DEFAULT, SWT.DEFAULT, true);
	Point p2 = toolBar.computeSize (SWT.DEFAULT, SWT.DEFAULT, true);
	toolBar.setSize (p1.x, Math.max (p1.y, p2.y));
}

/**
 * Creates the tool bar timer.  This timer is used to enable and
 * disable the cut, copy, paste, delete and rename tool items.
 * The enabled or disabled state of the items depends on the
 * contents of the clipboard which can be changed by other programs
 * on the desktop.  Since there is no notification when an item is
 * placed on the clipboard, polling using a timer is the only option.
 */
void createToolBarTimer () {
	display.timerExec (TOOLBAR_REFRESH_TIME, new Runnable () {
		public void run () {
			if (shell.isDisposed ()) return;
			updateToolBar ();
			display.timerExec (TOOLBAR_REFRESH_TIME, this);
		}
	});
}

/**
 * Creates a tree item for a file.  If the parent tree item is null,
 * the new tree item is created as root in the tree.  Otherwise, the
 * new item is a child of the parent item.  Tree items are sorted.
 * If the sibling items array is not null, the new item is created
 * at a sorted position within the tree.  Otherwise, the item is added
 * to the end of its siblings.  When a tree item is created, it is
 * initialized to show the folder name and appropriate folder icon.
 * 
 * @param parent the parent item (or null)
 * @param items the sibling items array (or null)
 * @param the file to provide data for the item
 * @return the new tree item
 */
TreeItem createTreeItem (TreeItem parent, TreeItem [] items, File file) {
	int index = 0;
	if (items != null) {
		while (index < items.length) {
			File oldFile = (File) items [index].getData ();
			if (oldFile != null) {
				if (compareFiles (file, oldFile, NAME_COLUMN) < 0) {
					break;
				}
			}
			index++;
		}
	}
	TreeItem item = null;
	if (parent == null) {
		if (items == null) {
			item = new TreeItem (tree, SWT.NONE);
		} else {
			item = new TreeItem (tree, SWT.NONE, index);
		}
	} else {
		if (items == null) {
			item = new TreeItem (parent, SWT.NONE);
		} else {
			item = new TreeItem (parent, SWT.NONE, index);
		}
	}
	item.setText (parent == null ? file.toString () : file.getName ());
	item.setImage (parent == null ? DRIVE_CLOSED : FOLDER_CLOSED);
	item.setForeground (isBusy (file) ? BUSY_COLOR : null);
	item.setData (file);
	return item;
}

/**
 * Creates the tree.  The tree is used to show the directory structure
 * of the file system.  The roots of the file system are the roots of
 * the tree.
 */
void createTree () {
	
	/*
	* Create the tree and initialize the roots.  Because it is expensive
	* to compute every node in the tree, the tree items are initialized 
	* lazily.  This is achieved by adding the children of an item in an
	* expand listener.  To start with, it is assumed that the roots will
	* all have children so a dummy tree item is created for each root.
	*/
	tree = new Tree (shell, SWT.BORDER);
	File [] roots = File.listRoots ();
	for (int i=0; i<roots.length; i++) {
		TreeItem root = createTreeItem (null, null, roots [i]);
		new TreeItem (root, SWT.NONE);
		combo.add (root.getText ());
	}
	
	/* Add the expand and collapse listener */
	tree.addTreeListener (new TreeListener () {
		public void treeExpanded (TreeEvent event) {
			TreeItem root = (TreeItem) event.item;
			if (root != null) FileExplorer.this.treeExpanded (root);
		}
		public void treeCollapsed (TreeEvent event) {
			TreeItem root = (TreeItem) event.item;
			if (root != null) FileExplorer.this.treeCollapsed (root);
		}
	});
	
	/*
	* Add a selection listener to fill the table with the files when
	* a directory is selected.  When the user reselects an item, start
	* an in-line edit session to allow the name of the directory to be
	* changed.
	*/
	tree.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			TreeItem [] items = tree.getSelection ();
			TreeItem item = items.length == 1 ? items [0] : null;
			if (item == null) return;
			setCursor (WAIT_CURSOR);
			treeSelected (item, false);
			if (shell.isDisposed ()) return;
			if (item != null && item == lastEditItem) {
				File file = (File) item.getData ();
				if (checkRename (file)) {
					int time = display.getDoubleClickTime () * 3 / 2;
					display.timerExec (time, editRunnable);
				}
			} else {
				lastEditItem = item;
			}
			setCursor (null);
		}
		public void widgetDefaultSelected (SelectionEvent event) {
			display.timerExec (-1, editRunnable);
		}
	});
	tree.addFocusListener (new FocusAdapter () {
		public void focusGained (FocusEvent event) {
			toolBarTarget = event.widget;
		}
		public void focusLost (FocusEvent event) {
			display.timerExec (-1, editRunnable);
		}
	});
	tree.addKeyListener (new KeyAdapter () {
		public void keyPressed (KeyEvent event) {
			if (event.character == SWT.DEL) delete ();
		}
	});
}

/**
 * Creates and assigns the popup menu for the tree.
 */
void createTreeMenu () {
	Menu menu = new Menu (shell, SWT.POP_UP);
	createEditItems (menu, true, true);
	tree.setMenu (menu);
}

/**
 * Creates the view menu for the menu bar.  The view menu
 * is created as a drop down menu and attached to the view
 * menu cascade item in the menu bar.
 *
 * @param viewItem the menu bar cascade item
 */
void createViewMenu (MenuItem viewItem) {
	Menu viewMenu = new Menu (shell, SWT.DROP_DOWN);
	viewItem.setMenu (viewMenu);
	final MenuItem toolItem = new MenuItem (viewMenu, SWT.CHECK);
	toolItem.setText (getMessage ("Toolbar"));
	toolItem.setSelection (true);
	toolItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			if (toolItem.getSelection ()) {
				FormData treeData = (FormData) tree.getLayoutData ();
				treeData.top = new FormAttachment (toolBar);
				FormData sashData = (FormData) sash.getLayoutData ();
				sashData.top = new FormAttachment (toolBar);
				FormData tableData = (FormData) table.getLayoutData ();
				tableData.top = new FormAttachment (toolBar);
			} else {
				FormData treeData = (FormData) tree.getLayoutData ();
				treeData.top = new FormAttachment (0);
				FormData sashData = (FormData) sash.getLayoutData ();
				sashData.top = new FormAttachment (0);
				FormData tableData = (FormData) table.getLayoutData ();
				tableData.top = new FormAttachment (0);
			}
			toolBar.setVisible (toolItem.getSelection ());
			shell.layout ();
		}
	});
	final MenuItem statusItem = new MenuItem (viewMenu, SWT.CHECK);
	statusItem.setText (getMessage ("Status"));
	statusItem.setSelection (true);
	statusItem.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent event) {
			if (statusItem.getSelection ()) {
				FormData treeData = (FormData) tree.getLayoutData ();
				treeData.bottom = new FormAttachment (status, -3);
				FormData sashData = (FormData) sash.getLayoutData ();
				sashData.bottom = new FormAttachment (status, -3);
				FormData tableData = (FormData) table.getLayoutData ();
				tableData.bottom = new FormAttachment (status, -3);
			} else {
				FormData treeData = (FormData) tree.getLayoutData ();
				treeData.bottom = new FormAttachment (100);
				FormData sashData = (FormData) sash.getLayoutData ();
				sashData.bottom = new FormAttachment (100);
				FormData tableData = (FormData) table.getLayoutData ();
				tableData.bottom = new FormAttachment (100);
			}
			status.setVisible (statusItem.getSelection ());
			shell.layout ();
		}
	});
}

/**
 * Perform a cut operation.  This operation is not implemented.
 */
void cut () {
	information ("This operation has not been implemented");
}

/**
 * Performs a delete operation.  The current selection in the tree or table
 * is deleted, depending on the target of the operation.  The user is prompted
 * before the operation takes place.  While the operation is performed, a busy
 * cursor is displayed.
 */
void delete () {
	final Item [] items = getSelectedItems ();
	File [] files = new File [items.length];
	for (int i=0; i<items.length; i++) {
		files [i] = (File) items [i].getData ();
	}
	if (!confirmDelete (files)) return;
	final DeleteOperation operation = new DeleteOperation (shell, files);
	operation.longExec (new Runnable () {
		public void run () {
			display.asyncExec (new Runnable () {
				public void run () {
					if (shell.isDisposed ()) return;
					setCursor (BUSY_CURSOR);
					refreshAll (true, false);
				}
			});
			operation.doneExec (new Runnable () {
				public void run () {
					display.asyncExec (new Runnable () {
						public void run () {
							if (shell.isDisposed ()) return;
							refreshAll (true, false);
							if (shell.isDisposed ()) return;
							setCursor (null);
						}
					});
				}
			});
		}
	});
	if (operation.run (true)) {
		if (operation.isSuccessful ()) {
			for (int i=items.length-1; i>=0; --i) {
				items [i].dispose ();
			}
		} else {
			refreshAll (true, false);
		}
	}
}

/**
 * Checks to see whether a file that is contained in the list of
 * files is a root.  If so, expand the root into the list of files
 * or folders that are in the root.
 * 
 * @param files an array of files to check for roots
 * @return a new array of files that does not contain roots
 */
File [] expandRoots (File [] files) {
	if (files == null) return new File [0];
	File [] result = new File [files.length];
	int index = 0;
	while (index < files.length) {
		File file = files [index];
		if (file.getName ().length () == 0) {
			File [] list = expandRoots (file.listFiles ());
			if (list == null) list = new File [0];
			File [] newResult = new File [result.length + list.length - 1];
			System.arraycopy (result, 0, newResult, 0, index);
			System.arraycopy (list, 0, newResult, index, list.length);
			result = newResult;
		} else {
			result [index] = file;
		}
		index++;
	}
	return result;
}

/**
 * Issues an error message to the user.
 * 
 * @param message the message to be displayed
 */
void error (String message) {
	MessageBox dialog = new MessageBox (shell, SWT.ICON_ERROR | SWT.OK);
	dialog.setText (shell.getText ());
	dialog.setMessage (message);
	dialog.open ();
}

/**
 * Gets the selected items.  Depending on the current tool bar target,
 * the selection in either the tree or table is queried.  The tool bars
 * and menus in FileExplorer are someone non-standard because the
 * operations that they invoke do not always map on to the same control.
 * For example, when the user has selected the tree, a delete operation
 * deletes items from the tree.  When the table is selected, table items
 * are deleted.  This means it is necessary to track the target of tool
 * bar and menu operations rather than performing operations using the
 * particular control.
 * 
 * @param message the error message to be displayed
 */
Item [] getSelectedItems () {
	if (toolBarTarget == tree) return tree.getSelection ();
	if (toolBarTarget == table) return table.getSelection ();
	return new Item [0];
}

/**
 * Gets the main shell for the FileExplorer.
 * 
 * @return the main shell
 */
public Shell getShell () {
	return shell;
}

/**
 * Informs the user that something has happened.
 * 
 * @param message the message to be displayed
 */
void information (String message) {
	MessageBox dialog = new MessageBox (shell, SWT.ICON_INFORMATION | SWT.OK);
	dialog.setText (shell.getText ());
	dialog.setMessage (message);
	dialog.open ();
}

/**
 * Creates and opens the main shell for the FileExplorer.
 * 
 * @param display the display that contains the shell
 * @param args the program arguments
 * @return the new shell
 */
Shell open (Display display, String [] args) {
	this.display = display;
	createShell ();
	createMenuBar ();
	createToolBar ();
	createTree ();
	createTreeMenu ();
	createDragSource (tree);
	createDropTarget (tree);
	createSash ();
	createTable ();
	createTableMenu ();
	createDragSource (table);
	createDropTarget (table);
	createStatus ();
	createLayout ();
	createToolBarTimer ();
	createRefreshTimer ();
	setInitialState ();
	shell.open ();
	return shell;
}

/**
 * Perform a paste operation.  This content of the clipboard is queried
 * for a list of files to be pasted.  These files are copied to the
 * destination directory which is always the current seleciton in the tree.
 */
void paste () {
	String [] names = (String []) CLIPBOARD.getContents (FileTransfer.getInstance ());
	if (names == null) {
		information (getMessage ("Nothing to paste."));
		return;
	}
	TreeItem [] selection = tree.getSelection ();
	if (selection.length != 1) return;
	File [] files = new File [names.length];
	for (int i=0; i<names.length; i++) {
		files [i] = new File (names [i]);
	}
	File file = ((File) selection [0].getData ());
	copyFiles (expandRoots (files), file, false);
}

/**
 * Performs a print operation.  This operation is not implemented.
 */
void print () {
	information ("This operation has not been implemented");
}

/**
 * Performs a rename operation.  The current selection in the tree or table
 * is renamed, depending on the target of the operation  If the tree is the
 * the target of a rename operation, the folder corresponding to the selected
 * tree item is renamed.  If the table is the target, the file or folder
 * corresponding to the selected table item is renamed.
 */
void rename () {
	Item [] items = getSelectedItems ();
	if (items.length == 0) return;
	rename (items [0]);
}

/**
 * Renames a tree or table item.  A temporary in-line text control is
 * created and positioned on top of the tree or table item.  While the
 * in-line edit session is in progress, accelerators are disabled.
 * 
 * @param editor the control editor that wraps the text control
 * @param composite the control (either a table or tree)
 * @param item the item to rename (either a table or tree item)
 * @param bounds the bounds of the item
 * @return true if the operation was successful; false otherwise
 */
boolean rename (ControlEditor editor, Composite composite, Item item, Rectangle bounds) {
	File file = (File) item.getData ();
	if (isBusy (file)) {
		error (getMessage ("\"{0}\" is busy.", new Object [] {file}));
		return false;
	}
	editor.horizontalAlignment = SWT.LEFT;
	editor.grabVertical = true;
	Text text = createText (editor, composite, item, bounds);
	editor.setEditor (text.getParent ());
	String oldText = item.getText ();
	toolBar.setEnabled (false);
	Menu menuBar = shell.getMenuBar ();
	setAcceleratorsEnabled (menuBar, false);
	text.setFocus ();
	while (!text.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	if (shell.isDisposed ()) return false;
	toolBar.setEnabled (true);
	setAcceleratorsEnabled (menuBar, true);
	composite.setFocus ();
	if (item.isDisposed ()) return false;
	boolean renamed = false;
	String newText = item.getText ();
	if (!oldText.equals (newText)) {
		File oldFile = (File) item.getData ();
		File newFile = new File (oldFile.getParent(), newText);
		if (oldFile.renameTo (newFile)) {
			renamed = true;
			item.setData (newFile);
		} else {
			item.setText (oldText);
			error (getMessage ("Failed to rename \"{0}\".", new Object []{file}));
		}
	}
	return renamed;
}

/**
 * Renames a tree or table item.
 *
 * @param item the item to rename (either a table or tree item)
 */
void rename (Item item) {
	if (item instanceof TreeItem) {
		rename ((TreeItem) item);
	} else {
		if (item instanceof TableItem) {
			rename ((TableItem) item);
		}
	}
}

/**
 * Renames a table item.  A new TableEditor is created for the item
 * and the table is scrolled to show the item to the user.  If the
 * rename is sucessful, the image associated with the file is reset
 * because the file extension may have changed.  If the item was a
 * folder, the tree is updated to display the new folder name.
 *
 * @param item the table item to rename
 */
void rename (TableItem item) {
	TableEditor editor = new TableEditor (table);
	editor.setItem (item);
	editor.setColumn (0);
	table.showSelection ();
	if (rename (editor, table, item, item.getBounds (0))) {
		File file = (File) item.getData ();
		if (file.isFile ()) setFileImage (item);
		TreeItem [] items = tree.getSelection ();
		for (int i=0; i<items.length; i++) {
			refreshTree (items [i], false);
		}
	}
}
/**
 * Rename a tree item.  A new TableEditor is created for the item
 * and the tree is scrolled to show the item to the user.
 *
 * @param item the tree item to rename
 */
void rename (TreeItem item) {
	TreeEditor editor = new TreeEditor (tree);
	editor.setItem (item);
	tree.showSelection ();
	if (rename (editor, tree, item, item.getBounds ())) {
		TreeItem [] selection = tree.getSelection ();
		for (int i=0; i<selection.length; i++) {
			treeSelected (selection [i], true);
			if (shell.isDisposed ()) return;
		}
	}
}

/**
 * Asks the user a question.
 * 
 * @param message the message to be displayed
 * @param style the style of the message box
 * @return one of the constants from style
 */
int question (String message, int style) {
	MessageBox dialog = new MessageBox (shell, SWT.ICON_QUESTION | style);
	dialog.setText (shell.getText ());
	dialog.setMessage (message);
	return dialog.open ();
}

/**
 * Performs a refresh operation.  The file system is queried and the
 * contents of the tree and table are recomputed.  Even if nothing
 * has changed, both controls are redrawn to give the user a visual
 * cue that the operation was performed.
 */
void refresh () {
	refreshAll (false, true);
	if (shell.isDisposed ()) return;
	tree.redraw ();
	table.redraw ();
}

/**
 * Refreshes the tree and the table from the file system.  If nothing
 * has changed, neither control is redrawn.
 * 
 * @param merge indicates that table items should be merged rather than deleted
 * @param unexpand indicates that tree items have been expanded should be recomputed
 */
void refreshAll (boolean merge, boolean unexpand) {
	//TODO - roots of the file system are not refreshed
	TreeItem [] items = tree.getItems ();
	for (int i=0; i<items.length; i++) {
		refreshTree (items [i], unexpand);
	}
	TreeItem [] selection = tree.getSelection ();
	if (selection.length == 0) {
		table.removeAll ();
	} else {
		for (int i=0; i<selection.length; i++) {
			treeSelected (selection [i], merge);
			if (shell.isDisposed ()) return;
		}
	}
}

/**
 * Refreshes the tree starting at a node.  If nothing has changed,
 * the tree is not redrawn.
 * 
 * @param root the root node of the subtree to be refreshed
 * @param unexpand indicates that items have been expanded should be recomputed
 */
boolean refreshTree (TreeItem root, boolean unexpand) {
	//TODO - tree items are not sorted when refreshed
	File file = (File) root.getData ();
	root.setForeground (isBusy (file) ? BUSY_COLOR : null);
	TreeItem [] items = root.getItems ();
	if (items.length > 0) {
		if (items [0].getData () == null) return false;
		if (unexpand && !root.getExpanded ()) {
			for (int i=0; i<items.length; i++) {
				items [i].dispose ();
			}
			new TreeItem (root, SWT.NULL);
			return false;
		}
	}
	File [] files = file.listFiles ();
	if (files == null) return false;
	sort (files, NAME_COLUMN, true);
	for (int i=0; i<files.length; i++) {
		if (files [i].isFile () || !files [i].exists ()) {
			files [i] = null;
		}
	}
	boolean modified = false;
	for (int i=0; i<items.length; i++) {
		TreeItem item = items [i];
		File oldFile = (File) item.getData ();
		if (oldFile != null) {
			int index = 0;
			while (index < files.length) {
				File newFile = files [index];
				if (newFile != null) {
					if (newFile.equals (oldFile)) break;
				}
				index++;
			}
			if (index == files.length) {
				modified = true;
				item.dispose ();
			} else {
				if (refreshTree (item, unexpand)) {
					modified = true;
				}
			}
		}
	}
	items = root.getItems ();
	for (int i=0; i<files.length; i++) {
		File newFile = files [i];
		if (newFile != null) {
			int index = 0;
			while (index < items.length) {
				TreeItem item = items [index];	
				File oldFile = (File) item.getData ();	
				if (oldFile != null) {
					if (newFile.equals (oldFile)) break;
				}
				index++;
			}
			if (index == items.length) {
				modified = true;
				TreeItem item = createTreeItem (root, items, files [i]);
				new TreeItem (item, SWT.NONE);
			}
		}
	}
	return modified;
}

/**
 * Performs a print search.  This operation is not implemented.
 */
void search () {
	information ("This operation has not been implemented");
}

/**
 * Performs a select all operation.  All items in the table are
 * selected and focus is assigned to the table.
 */
void selectAll () {
	table.selectAll ();
	tableSelected (table.getItems ());
	table.setFocus ();
}

/**
 * Disables or enables accelerators for a menu.  The entire
 * menu hierarchy is traversed and accelerators are added
 * or removed.
 * 
 * @param menu the menu to be traversed
 * @param enabled the new enabled state for the accelerators
 */
void setAcceleratorsEnabled (Menu menu, boolean enabled) {
	MenuItem [] items = menu.getItems ();
	for (int i=0; i<items.length; i++) {
		MenuItem item = items [i];
		if (enabled) {
			item.setAccelerator (((Integer)item.getData ()).intValue ());
		} else {
			item.setData (new Integer (item.getAccelerator ()));
			item.setAccelerator (0);
		}
		if (item.getMenu () != null) {
			setAcceleratorsEnabled (item.getMenu (), enabled);
		}
	}
}

/**
 * Sets the cursor for the FileExplorer.  If a background operation is
 * running and the cursor is to be cleared, the SWT.APP_STARTING cursor
 * is set instead.
 * 
 * @param cursor the new cursor (or null)
 */
void setCursor (Cursor cursor) {
	if (cursor == null && FileOperation.getOperationCount () > 0) {
		cursor = BUSY_CURSOR;
	}
	shell.setCursor (cursor);
}

/**
 * Sets the file image for a table item.  The image for each table item
 * is different, depending on the file extension.  If their is no program
 * associated with the file, an generic file icon is used.
 * 
 * @param item the table item for the image
 */
void setFileImage (TableItem item) {
	Image image = null;
	String name = item.getText ();
	int index = name.lastIndexOf ('.');
	if (index != -1) {
		String extension = name.substring (index);
		Program program = Program.findProgram (extension);
		if (program != null) {
			item.setText (TYPE_COLUMN, program.getName ());
			image = (Image) ICONS.get (extension);
			if (image == null) {
				ImageData data = program.getImageData ();
				if (data != null) {
					Rectangle rect = FILE_ICON.getBounds ();
					data = data.scaledTo (rect.width, rect.height);
					image = new Image (display, data);
					if (image != null) ICONS.put (extension, image);
				}
			}
		}
	}
	if (image == null) image = FILE_ICON;
	item.setImage (image);
}

void setInitialState () {
	updateEditMenu ();
	updateToolBar ();
	
	/*
	* Set the width of the combo to match the width of
	* the tree.  In order to determine the width, it is
	* necessary to layout the shell.
	*/
	shell.layout ();
	comboItem.setWidth (tree.getSize ().x);
	
	/*
	* When a tree does not have a selection and it gets
	* focus, on some platforms it selects the first item
	* in the tree.  Normally, this isn't a problem, but on
	* Windows, the first item in the tree is the A: drive
	* which almost never has a disk in it.  This causes an
	* error message.  The fix is to select the C: drive
	* on Windows.
	*/
	if ("win32".equals (SWT.getPlatform ())) {
		int index = 0;
		TreeItem [] items = tree.getItems ();
		while (index <items.length) {
			String drive = items [index].getText ().toUpperCase ();
			if (drive.indexOf ("C:") != -1) {
				tree.setSelection (new TreeItem [] {items [index]});
				treeSelected (items [index], false);
				if (shell.isDisposed ()) return;
				break;
			}
			index++;
		}
	}
	
	/* Put the intial focus in the tree */	
	tree.setFocus ();
}

/**
 * Sorts an array of files.
 * 
 * @param files the array of files to be sorted
 * @param index the index of the table column
 * @param ascend the sorting order (either ascending or descending)
 */
void sort (final File [] files, final int index, final boolean ascend) {
	Comparator comparator = new Comparator () {
		public int compare (Object object1, Object object2) {
			if (ascend) {
				return compareFiles ((File) object1, (File) object2, index);
			} else {
				return compareFiles ((File) object2, (File) object1, index);
			}
		}
	};
	Arrays.sort (files, comparator);
}

/**
 * Performs the table column selected action.  When a table
 * column is selected, the table is sorted based on the column.
 * If the same column is reselected, the sorting order is inverted.
 * 
 * @param column the table column that was selected
 */
void tableColumnSelected (TableColumn column) {
	//TODO - use a sort icon to show the sorting order
	int index = table.indexOf (column);
	if (index == TYPE_COLUMN) return;
	setCursor (WAIT_CURSOR);
	Boolean value = (Boolean) column.getData ();
	if (column == sortColumn) {
		value = new Boolean (!value.booleanValue ());
		column.setData (value);
	}
	sortColumn = column;
	TreeItem [] selection = tree.getSelection ();
	for (int i=0; i<selection.length; i++) {
		treeSelected (selection [i], false);
		if (shell.isDisposed ()) return;
	}
	setCursor (null);
}

/**
 * Performs the table default selection action  When a table is
 * default selected, the programs that are associated with the
 * table items are invoked.
 * 
 * @param items the selected items in the table
 */
void tableDefaultSelected (TableItem [] items) {
	for (int i=0; i<items.length; i++) {
		File file = (File) items [i].getData ();
		if (file.isDirectory()) {
			TreeItem [] selection = tree.getSelection ();
			if (selection.length != 1) break;
			setCursor (WAIT_CURSOR);
			selection [0].setExpanded (true);
			if (selection [0].getExpanded ()) treeExpanded (selection [0]);
			TreeItem [] treeItems = selection [0].getItems ();
			for (int j=0; j<treeItems.length; j++) {
				if (file.equals (treeItems [j].getData ())) {
					tree.setSelection (new TreeItem [] {treeItems [j]});
					treeSelected (treeItems [j], false);
					if (shell.isDisposed ()) return;
				}
			}
			setCursor (null);
		} else {
			String name = file.getAbsolutePath ();
			if (!Program.launch (name)) {
				error (getMessage ("Could not launch \"{0}\".", new Object [] {name}));
			}
		}
	}
}

/**
 * Perform the table selection action.  When the table is selected,
 * the status line is updated to reflect the selected items.
 * 
 * @param items the selected items
 */
void tableSelected (TableItem [] items) {
	long total = 0;
	for (int i=0; i<items.length; i++) {
		File file = (File) items [i].getData ();
		total += Math.max (1, (file.length () + 512) / 1024);
	}
	status.setText (getMessage ("{0} objects(s), {1} KB", new Object [] {new Integer (items.length), new Long (total)}));	
	updateEditMenu ();
	updateToolBar ();
}

/**
 * Perform traverse to parent action.  The parent item of the current
 * selection in the tree is selected and the table is filled with the
 * files that are in the parent directory.
 *
 */
void traverseParent () {
	TreeItem [] items = tree.getSelection ();
	if (items.length != 1) return;
	setCursor (WAIT_CURSOR);
	TreeItem parentItem = items [0].getParentItem ();
	if (parentItem != null) {
		tree.setSelection (new TreeItem [] {parentItem});
		treeSelected (parentItem, false);
		if (shell.isDisposed ()) return;
	}
	setCursor (null);
}

/**
 * Perform the tree collapsed action.  When a tree item is collapsed,
 * a closed folder icon is displayed.
 * 
 * @param root the item that was collapsed
 */
void treeCollapsed (TreeItem root) {
	root.setImage (root.getParentItem () == null ? DRIVE_CLOSED : FOLDER_CLOSED);
}

/**
 * Perform the tree expanded action.  When a tree item is expanded,
 * an open folder icon is displayed.
 * 
 * @param root the item that was expanded
 */
void treeExpanded (TreeItem root) { 
	/*
	* If the item has never been expanded, the child items are created.
	* If any of these new items are directories, dummy item is created
	* for each new item so that lazy initialization can continue.
	*/
	boolean hasFolder = false;
	int index = 0;
	TreeItem [] items = root.getItems ();
	while (index < items.length) {
		if (items [index].getData () != null) {
			break;
		}
		items [index].dispose ();
		index++;
	}
	if (index == items.length) {
		File file = (File) root.getData ();
		File [] files = file.listFiles ();
		if (files == null) return;
		sort (files, NAME_COLUMN, true);
		for (int i= 0; i<files.length; i++) {
			if (files [i].isDirectory()) {
				hasFolder = true;
				TreeItem item = createTreeItem (root, null, files [i]);
				new TreeItem (item, SWT.NONE);
			}
		}
	} else {
		hasFolder = true;
		for (int i=0; i<items.length; i++) {
			TreeItem item = items [i];
			File file = (File) items [i].getData ();
			if (file != null) {
				item.setForeground (isBusy (file) ? BUSY_COLOR : null);
			}
		}
	}
	if (hasFolder) {
		root.setImage (root.getParentItem () == null ? DRIVE_OPEN : FOLDER_OPEN);
	}
}

/**
 * Perform a tree selection operation.  When the tree is selected, the table is
 * filled with the contents of the associated directory.  It is possible for this
 * operation to fail in which case, the original selection is restored.
 * 
 * @param root the tree item that was selected
 * @param merge indicates that table items should be merged rather than deleted
 */
void treeSelected (TreeItem root, boolean merge) {
	File file = (File) root.getData ();
	File [] files = file.listFiles ();
	if (files == null) {
		error (getMessage ("Cannot get the contents of \"{0}\".", new Object [] {file}));
		tree.deselectAll ();
		if (lastSelection != null && lastSelection.isDisposed ()) lastSelection = null;
		if (lastSelection != null) {
			tree.setSelection (new TreeItem [] {lastSelection});
			File folder = (File) lastSelection.getData ();
			while (folder.getParentFile () != null) {
				folder = folder.getParentFile ();
			}
			combo.setText (folder.toString ());
		}
		return;
	}
	if (fillOperation != null) {
		FillOperation operation = fillOperation;
		operation.cancel ();
		while (!shell.isDisposed () && operation.isRunning ()) {
			if (!display.readAndDispatch()) display.sleep ();
		}
		if (shell.isDisposed ()) return;
		if (fillOperation != null) return;
	}
	lastSelection = root;
	boolean ascend = ((Boolean) sortColumn.getData()).booleanValue ();
	sort (files, table.indexOf (sortColumn), ascend);
	int index = 0;
	while (index < files.length) {
		if (files [index].isDirectory()) break;
		index++;
	}
	if (index == files.length) {
		TreeItem [] items = root.getItems ();
		for (int i=0; i<items.length; i++)  {
			items [i].dispose();
		}
	}
	int itemHeight = table.getItemHeight ();
	Rectangle rect = table.getClientArea ();
	int visibleCount = (rect.height + itemHeight - 1) / itemHeight;
	visibleCount = Math.min (files.length, visibleCount);
	int i = 0;
	if (merge) {
		TableItem [] items = table.getItems ();
		while (i < items.length && i < files.length) {
			if (!items [i].getData ().equals (files [i])) break;
			i++;
		}
		int count = items.length - i;
		if (i < visibleCount && count > 4) table.setRedraw (false);
		table.remove (i, items.length - 1);
		if (i < visibleCount && count > 4) table.setRedraw (true);
	}
	if (i == 0) table.removeAll ();
	while (i < visibleCount && i < files.length) {
		createTableItem (files [i]);
		i++;
	}
	if (i < files.length) {
		fillOperation = new FillOperation (this, file, files, i, visibleCount);
		fillOperation.doneExec (new Runnable () {
			public void run () {
				display.syncExec (new Runnable () {
					public void run () {
						if (shell.isDisposed ()) return;
						setCursor (null);
						updateEditMenu ();
						updateToolBar ();
					}
				});
				fillOperation = null;
			}
		});
		fillOperation.run (true);
	}
	parentItem.setEnabled (file.getParentFile () != null);
	File folder = file;
	while (folder.getParentFile () != null) {
		folder = folder.getParentFile ();
	}
	combo.setText (folder.toString ());
	status.setText (getMessage ("{0} object(s)", new Object [] {new Integer (files.length)}));
	updateEditMenu ();
	updateToolBar ();
}

/**
 * Updates the enabled and disabled state of the items on the edit menu
 * that appears on the menu bar.
 */
void updateEditMenu () {
	editMenu.notifyListeners (SWT.Show, null);
}

/**
 * Updates the enabled and disabled state of item on the tool bar.
 */
void updateToolBar () {
	Item [] items = getSelectedItems ();
	boolean enabled = items.length > 0;
	cutItem.setEnabled (enabled);
	copyItem.setEnabled (enabled);
	pasteItem.setEnabled (isPasteAvailable ());
	deleteItem.setEnabled (enabled);
	if (enabled && items.length == 1) {
		File file = (File) items [0].getData ();
		enabled = checkRename (file);
	}
	renameItem.setEnabled (enabled);
}

}