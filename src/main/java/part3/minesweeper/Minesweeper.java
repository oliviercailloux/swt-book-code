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
package part3.minesweeper;
 
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

import java.util.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Steve Northover
 *
 * Minesweeper is a simple implementation of the classic computer
 * game of the same name.
 * 
 * Thursday June 17, 2004.
 */
public class Minesweeper {

	static final int ROWS = 12;
	static final int COLUMNS = 12;
	static ResourceBundle resources;
	static {
		try {
			resources = ResourceBundle.getBundle ("minesweeper");
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
 * Runs Minesweeper.  The user-interface consists of a single
 * shell, a counter, a button, a timer and a game board.  The
 * counter is used to show the remaining number of mines.  The
 * button resets the game while the timer shows elapsed time.
 * The board implements the minesweeper game but knows nothing
 * about the other controls.  When the user closes the shell,
 * the program ends.
 *
 * @param args the arguments array to the program
 */
public static void main (String [] args) {
	final Display display = new Display ();
	Shell shell = new Shell (display, SWT.DIALOG_TRIM | SWT.MIN);
	shell.setText (getMessage ("Minesweeper"));
	
	/* Load the images */
	Class clazz = Minesweeper.class;
	final Image happy = loadImage (display, clazz, "happy.gif");
	final Image surprise = loadImage (display, clazz, "surprise.gif");
	final Image sad = loadImage (display, clazz, "sad.gif");
	final Image smile = loadImage (display, clazz, "smile.gif");
	
	/* Create and configure the counter, the button, timer and the board */
	final Counter counter = new Counter (shell, SWT.NONE);
	final Button button = new Button (shell, SWT.PUSH);
	button.setImage (smile);
	final Timer timer = new Timer (shell, SWT.NONE);
	final Board board = new Board (shell, SWT.NONE);
	board.reset (ROWS + COLUMNS, ROWS, COLUMNS);
	int count = board.getMineCount () - board.getGuessCount ();
	counter.setValue (count);
	
	/* Position the controls */
	GridLayout layout = new GridLayout (3, false);
	layout.verticalSpacing = 4;
	layout.marginWidth = layout.marginHeight = 2;
	shell.setLayout (layout);
	counter.setLayoutData (new GridData (SWT.CENTER, SWT.CENTER, true, false));
	button.setLayoutData (new GridData (SWT.CENTER, SWT.CENTER, true, false));
	timer.setLayoutData (new GridData (SWT.CENTER, SWT.CENTER, true, false));
	GridData boardData = new GridData ();
	boardData.horizontalSpan = 3;
	board.setLayoutData (boardData);
	
	/*
	* The selection listener for the button resets the smile
	* image, the counter, the timer and the board.  Rather
	* than reset the timer and wait for it to draw the zero,
	* it is stopped and set to zero.
	*/
	button.addListener (SWT.Selection, new Listener () {
		public void handleEvent (Event event) {
			button.setImage (smile);
			board.reset (ROWS + COLUMNS, ROWS, COLUMNS);
			int count = board.getMineCount () - board.getGuessCount ();
			counter.setValue (count);
			timer.stop ();
			timer.setValue (0);
		}
	});
	
	/*
	* The mouse listener for the board are used to set and
	* clear the smile image when the mouse is pressed and
	* released respectively.  In addition, when the mouse is
	* released, the game may be over if the board is either
	* swept or exploded.  If the game is over, the sad or
	* happy image is displayed, the timer is stopped and the
	* board is revealed.  Otherwise, the game continues and
	* the counter is updated to show the remaining possible
	* mines.
	*/
	Listener mouseListener = new Listener () {
		public void handleEvent (Event event) {
			switch (event.type) {
				case SWT.MouseDown:
					button.setImage (surprise);
					break;
				case SWT.MouseUp:
					if (board.isSwept () || board.isExploded ()) {
						button.setImage (board.isExploded () ? sad : happy);
						timer.stop ();
						board.reveal ();
						return;
					}
					button.setImage (smile);
					Rectangle rect = board.getClientArea ();
					if (rect.contains (event.x, event.y)) {
						int count = board.getMineCount () - board.getGuessCount ();
						counter.setValue (count);
						if (timer.isRunning ()) return;
						timer.reset ();
						timer.start ();
					}
					break;
			}
		}
	};
	board.addListener (SWT.MouseDown, mouseListener);
	board.addListener (SWT.MouseUp, mouseListener);
	
	/*
	* Add iconify and deiconify listeners to start and
	* stop the timer when the shell is minimized and
	* restored (useful when the boss is coming).
	*/
	Listener shellListener = new Listener () {
		public void handleEvent (Event event) {
			switch (event.type) {
				case SWT.Iconify:
					timer.stop ();
					break;
				case SWT.Deiconify:
					timer.start ();
					break;
			}
		}
	};
	shell.addListener (SWT.Iconify, shellListener);
	shell.addListener (SWT.Deiconify, shellListener);

	/* Open the shell and run the event loop */
	shell.pack ();
	shell.open ();
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	
	/* Dispose resources */
	happy.dispose ();
	surprise.dispose ();
	sad.dispose ();
	smile.dispose ();
	display.dispose ();
}

}