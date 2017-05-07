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

import java.util.Random;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Steve Northover
 *
 * A Board a custom widget used to display (and manage) a
 * Minesweeper game board.  Rather than defining a class to
 * represent a square, the implementation uses two dimensional
 * arrays to track the state of each square.  An interesting
 * implementation detail is that the state of the board is
 * captured by the images representing the game.  For example,
 * rather than remembering whether a mine has exploded, the fact
 * that any square contains the exploded icon is used to capture
 * the state.  When a flag icon is added to the "guesses" array,
 * the number of guesses implicitly increases because the image
 * is present in the array.  This approach works well to minimize
 * duplicate state at the cost of computing the state when
 * necessary (which is minimal).
 * 
 * Thursday June 17, 2004.
 */
public class Board extends Canvas implements Listener {
	
	int rows, columns;
	Image [][] images;
	Image [][] guesses;
	boolean [] [] selected;
	boolean mouseDown;
	int lastX, lastY;
	
	/* The resources used by the board */
	Image mine, explosion, question, flag, wrong;
	Image [] minesAway = new Image [8];
	
	/**
	 * The inset that surrounds a square on all sides.
	 */
	static final int INSET = 1;

public Board (Composite parent, int style) {
	super (parent, style);
	/*
	* Acquire the resources that are needed to draw the board and add
	* all of the listeners.  It's a good idea to do this work in the
	* constructor rather than using lazy initialization (which really
	* only makes sense when a computation is expensive and may not be
	* needed).
	*/
	Display display = getDisplay ();
	Class clazz = Board.class;
	mine = loadImage (display, clazz, "mine.gif");
	explosion = loadImage (display, clazz, "explosion.gif");
	question = loadImage (display, clazz, "question.gif");
	flag = loadImage (display, clazz, "flag.gif");
	wrong = loadImage (display, clazz, "wrong.gif");
	for (int i=0; i<minesAway.length; i++) {
		String name = (i + 1) + "mines.gif";
		minesAway [i] = loadImage (display, clazz, name);
	}
	
	/*
	* Rather than use an inner class for each listener or a single listener
	* that contained a case statement, class Board implements the Listener
	* interface.  This makes sense when Board is considered as an implementation
	* class for Minesweeper but not if Board were to provide a general purpose
	* API.  In the later case, the handleEvent() method (an implementation detail
	* of class Board) would be public despite the fact that is not API.
	*/
	addListener (SWT.Paint, this);
	addListener (SWT.MouseDown, this);
	addListener (SWT.MouseMove, this);
	addListener (SWT.MouseUp, this);
	addListener (SWT.Dispose, this);
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

public Point computeSize (int wHint, int hHint, boolean changed) {
	checkWidget ();
	/*
	* The preferred size of a board is computed using the size
	* of one square and the number of rows and columns.  The
	* wHint and hHint paramters are ignored because the number
	* of rows and columns dictates the size of the board.  The
	* value returned by computeSize() includes the trim (by
	* definition).
	*/
	Point size = getBoxSize ();
	int width = columns * size.x, height = rows * size.y;
	Rectangle trim = computeTrim (0, 0, width, height);
	return new Point (trim.width, trim.height);
}

/**
 * Disposes of the board.  Any resources that were acquired
 * by the board are released here.
 * 
 * @param event the dispose event
 */
void dispose (Event event) {
	/*
	* Dispose of every image but do not dispose of colors.
	* The board uses system colors to draw squares and these
	* are disposed of when the display goes away.
	*/
	for (int i=0; i<minesAway.length; i++) {
		if (minesAway [i] != null) minesAway [i].dispose ();
		minesAway [i] = null;
	}
	if (mine != null) mine.dispose ();
	if (explosion != null) explosion.dispose ();
	if (question != null) question.dispose ();
	if (flag != null) flag.dispose ();
	if (wrong != null) wrong.dispose ();
	mine = explosion = question = flag = wrong = null;
}

/**
 * Draws a single box.  The box may appear to be pressed or not pressed
 * and the content of the box is not filled.
 *
 * @param gc the gc that is used to do the drawing
 * @param x the x location of the box
 * @param y the y location of the box
 * @param width the width of the box
 * @param height the height of the box
 * @param pressed the pressed state of the box
 */
void drawBox (GC gc, int x, int y, int width, int height, boolean pressed) {
	/*
	* Get the system colors used to draw the box from the display
	* every time rather than caching them.  This ensures that the
	* board will draw properly when the theme changes.
	*/
	Display display = getDisplay ();
	Color highlight = display.getSystemColor (SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
	Color light = display.getSystemColor (SWT.COLOR_WIDGET_LIGHT_SHADOW);
	Color normal = display.getSystemColor (SWT.COLOR_WIDGET_NORMAL_SHADOW);
	Color dark = display.getSystemColor (SWT.COLOR_WIDGET_DARK_SHADOW);
	
	/*
	* Boxes are drawn using drawLine() (rather than drawRectangle()) so
	* that the shadows around the box can be fine tuned.  The width and
	* height paramters will always be the size of the size of a square.
	*/
	gc.setForeground (pressed ? dark : highlight);
	gc.drawLine (x, y, x + width - 1, y);
	gc.drawLine (x, y, x, y + height - 1);
	gc.setForeground (pressed ? normal : light);
	gc.drawLine (x + 1, y + 1, x + width - 2, y + 1);
	gc.drawLine (x + 1, y + 1, x + 1, y + height - 2);
	gc.setForeground (pressed ? light : normal);
	gc.drawLine (x + 1, y + height - 2, x + width - 2, y + height - 2);
	gc.drawLine (x + width - 2, y + height - 2, x + width - 2, y + 1);
	gc.setForeground (pressed ? highlight : dark);
	gc.drawLine (x, y + height - 1, x + width - 1, y + height - 1);
	gc.drawLine (x + width - 1, y + height - 1, x + width - 1, y);
}

/**
 * Draws a single item.  Items are really just the squares of the board.
 * A square can be pressed or not pressed and can contain an image.  The
 * image is centered withing the square.  If the square has not been selected,
 * then a box is not drawn around the square and the image.  Otherwise, only
 * the image is drawn.
 *
 * @param gc the gc that is used to draw the item
 * @param i the row of the item
 * @param j the column of the item
 * @param width the width of the item
 * @param height the height of the item
 * @param pressed the pressed state of the item
 */
void drawItem (GC gc, int i, int j, int width, int height, boolean pressed) {
	if (!(0 <= i && i < rows)) return;
	if (!(0 <= j && j < columns)) return;
	Image image = null;
	int x = i * width, y = j * height;
	gc.fillRectangle (x, y, width, height);
	if (selected [i][j]) {
		image = images [i][j];
	} else {
		drawBox (gc, x, y, width, height, pressed);
		image = guesses [i][j];
	}
	if (image != null) {
		Rectangle rect = image.getBounds ();
		int offsetX = Math.max (1, (width - rect.width) / 2);
		int offsetY = Math.max (1, (height - rect.height) / 2);
		gc.drawImage (image, x + offsetX, y + offsetY);
	}
}

/**
 * Gets the guess count.  The guess count is the number of squares
 * that the user believes contain a mine.  Guesses appear as a flag
 * icon in a square.  When the user clears a guess, a question mark
 * icon is drawn but it does not count as a guess.
 * 
 * @return the number of guesses
 */
public int getGuessCount () {
	checkWidget ();
	/*
	* The guess count is by definition the number of flag icons
	* on the board.  This value is computed rather than stored.
	*/
	int count = 0;
	for (int i=0; i<rows; i++) {
		for (int j=0; j<columns; j++) {
			if (guesses [i][j] == flag) count++;
		}
	}
	return count;
}

/**
 * Gets the size of a box.  This is the size of a single
 * square on the board.
 * 
 * @return a point describing the size of a box
 */
Point getBoxSize () {
	/*
	* The size of a square is calculated using the mine icon.
	* The assumption here is that all of the icons on the board
	* are the same size.  This value is computed rather than stored.
	*/
	Rectangle rect = mine.getBounds ();
	int width = rect.width + INSET * 2;
	int height = rect.height + INSET * 2;
	return new Point (width, height);
}

/**
 * Gets the mine count.  The mine count is the number of squares
 * that contain a bomb.
 * 
 * @return the number of mines
 */
public int getMineCount () {
	checkWidget ();
	/*
	* The mine count is by definition the number of mine icons
	* on the board.  This value is computed rather than stored.
	*/
	int count = 0;
	for (int i=0; i<rows; i++) {
		for (int j=0; j<columns; j++) {
			if (images [i][j] == mine) count++;
		}
	}
	return count;
}

/**
 * Determines if the board is exploded.  The game is over when
 * a mine has exploded or the board has been swept clean of
 * mines.
 * 
 * @return true if the board has exploded; false otherwise
 */
public boolean isExploded () {
	checkWidget ();
	/*
	* The board is exploded when any square contains an explosion
	* icon.  This value is computed rather than stored.
	*/
	for (int i=0; i<rows; i++) {
		for (int j=0; j<columns; j++) {
			if (images [i][j] == explosion) {
				return true;
			}
		}
	}
	return false;
}

/**
 * Determines if the board is swept.  The game is over when
 * a mine has exploded or the board has been swept clean of
 * mines.
 * 
 * @return true if the board has exploded; false otherwise
 */
public boolean isSwept () {

//	MCQ:
//	Is the test is wrong?  You had to unclick to
//	end the game once.

	checkWidget ();
	/*
	* The board is swept when every square has been taken into
	* acount.  A square is either selected or contains a mine.
	* When the sum of the selected and mined squares equals the
	* total number of squares in the board, the board has been
	* swept clean of mines.
	*/
	int mineCount = 0, selectedCount = 0;
	for (int i=0; i<rows; i++) {
		for (int j=0; j<columns; j++) {
			if (selected [i][j]) selectedCount++;
			if (images [i][j] == mine) mineCount++;
		}
	}
	return rows * columns == selectedCount + mineCount;
}

/**
 * Process a paint event.  Draw the squares one at a time.
 * Because drawing squares is fast, it is not necessary to
 * perform computations to reduce the amount that is drawn.
 * For example, if drawing the squares was slow or the board
 * was very large, only those squares that intersected with
 * the paint rectangle or region would need to be drawn.
 * 
 * @param event the paint event
 */
void paint (Event event) {
	Point size = getBoxSize ();
	for (int i=0; i<rows; i++) {
		for (int j=0; j<columns; j++) {
			drawItem (event.gc, i, j, size.x, size.y, false);
		}
	}
}

/**
 * Process a mouse down event.  When the mouse is pressed, the
 * board is "hit tested" to determine the row and column of the
 * square where the user pressed.  If the user clicked on a flag,
 * the square is safe and no action is taked.  Flags are used to
 * mark mines and protect the user from clicking by accident on
 * a suspected mine.  Pressing the mouse draws the square using
 * the "pressed in" look for a box.
 * 
 * @param event the mouse down event
 */
void mouseDown (Event event) {
	if (event.button != 1) return;
	Point size = getBoxSize ();
	int width = size.x, height = size.y;
	int i = event.x / width;
	int j = event.y / height;
	if (0 <= i && i < rows) {
		if (0 <= j && j < columns) {
			if (guesses [i][j] == flag) return;
		}
	}
	mouseDown = true;
	lastX = event.x / width * width;
	lastY = event.y / height * height;
	GC gc = new GC (this);
	drawItem (gc, i, j, width, height, true);
	gc.dispose ();
}

/**
 * Process a mouse move event.  When the mouse is moved, the square
 * that contains the mouse is drawn using the "pressed in" look.
 * The board is "hit tested" to determine the row and column of the
 * square.  When the mouse moves to a new square, the new square is
 * drawn.  To avoid flashing when the mouse moves within the same
 * square, the x and y coordinates of the last square that was drawn
 * are used.  When the mouse moves outside of the board, the last
 * square is drawn "pressed out".
 * 
 * @param event the mouse move event
 */
void mouseMove (Event event) {
	if (!mouseDown) return;
	Point size = getBoxSize ();
	int width = size.x, height = size.y;
	int x = event.x / width * width;
	int y = event.y / height * height;
	if (x == lastX && y == lastY) return;
	GC gc = new GC (this);
	if (lastX != -1 && lastY != -1) {
		drawItem (gc, lastX / width, lastY / height, width, height, false);
		lastX = lastY = -1;
	}
	if (event.x < 0 || event.x > width * rows) x = -1;
	if (event.y < 0 || event.y > height * columns) y = -1;
	if (x != -1 && y != -1) {
		drawItem (gc, x / width, y / height, width, height, true);
		lastX = x;
		lastY = y;
	}
	gc.dispose ();
}

/**
 * Process a mouse up event.  When the mouse is released, the board
 * performs an action.  When button 1 is released inside the board,
 * the square under the mouse is revealed.  When button 3 is released,
 * the square is toggled to a flag icon, then a question icon and then
 * restored to an unselected square.
 * 
 * @param event the mouse move event
 */
void mouseUp (Event event) {
	Point size = getBoxSize ();
	int width = size.x, height = size.y;
	/*
	* Button 3 was released, so change the icon to a flag.
	* If the icon is already a flag, change it to a question.
	* If the icon is a question, clear the icon.  The square
	* is redrawn to show the new icon.
	*/
	if (!mouseDown) {
		if (event.button != 3) return;
		int i = event.x / width;
		int j = event.y / height;
		if (0 <= i && i < rows) {
			if (0 <= j && j < columns) {
				if (!selected [i][j]) {
					if (guesses [i][j] == null) {
						guesses [i][j] = flag;
					} else {
						if (guesses [i][j] == flag) {
							guesses [i][j] = question;
						} else {
							if (guesses [i][j] == question) {
								guesses [i][j] = null;
							}
						}
					}
					GC gc = new GC (this);
					drawItem (gc, i, j, width, height, false);
					gc.dispose ();
				}
			}
		}
		return;
	}
	/*
	* Button 1 was released, so reveal the square.  If the square
	* is empty, then it is selected.  Otherwise, if it is a mine,
	* the icon is changed to an explosion and the board is disabled
	* to stop further mouse clicks.  Clicking on a mine exposes the
	* location of the other mines to show the user how many guesses
	* were correct.  This is achieved by marking the squares that
	* contain mines and flag icons as selected and then drawing them.
	* Regarless of whether the square was a mine, it is marked as
	* selected and drawn.  The game is also over when the board
	* is swept and the board is disabled accordingly.
	*/
	GC gc = new GC (this);
	if (lastX != -1 && lastY != -1) {
		int i = lastX / width;
		int j = lastY / height;
		if (0 <= i && i < rows) {
			if (0 <= j && j < columns) {
				if (images [i][j] == null) {
					select (gc, i, j, width, height);
				} else {
					if (images [i][j] == mine) {
						images [i][j] = explosion;
						setEnabled (false);
						for (int k=0; k<rows; k++) {
							for (int l=0; l<columns; l++) {
								if (images [k][l] == mine) {
									selected [k][l] = true;
									drawItem (gc, k, l, width, height, false);
								} else {
									if (guesses [k][l] == flag) {
										selected [k][l] = true;
										images [k][l] = wrong;
										drawItem (gc, k, l, width, height, false);
									}
								}
							}
						}
					}
					selected [i][j] = true;
					drawItem (gc, i, j, width, height, false);
				}
				if (isSwept ()) setEnabled (false);
			}
		}
	}
	gc.dispose ();
	mouseDown = false;
}

/**
 * Handles an event. Despite the fact that this method is
 * public, it should not be treated as API.
 * 
 * @param event the event
 */
public void handleEvent (Event event) {
	switch (event.type) {
		case SWT.Dispose: dispose (event); break;
		case SWT.Paint: paint (event); break;
		case SWT.MouseDown: mouseDown (event); break;
		case SWT.MouseMove: mouseMove (event); break;
		case SWT.MouseUp: mouseUp (event); break;
	}
}

/**
 * Resets the board.  The current contents of the board is
 * cleared and new squares are created.  Mines are placed
 * randomly on the board.
 * 
 * @param mineCount the number of mines for the board
 * @param rows the number of rows in the board
 * @param columns the number of columns in the board
 */
public void reset (int mineCount, int rows, int columns) {
	checkWidget ();
	this.rows = rows;
	this.columns = columns;
	setEnabled (true);
	lastX = lastY = -1;
	images = new Image [rows][columns];
	guesses = new Image [rows][columns];
	selected = new boolean [rows] [columns];
	Random random = new Random ();
	int count = 0;
	while (count < mineCount) {
		int i = random.nextInt (rows);
		int j = random.nextInt (columns);
		if (images [i][j] == null) {
			images [i][j] = mine;
			count++;
		}
	}
	/*
	* The "awayCount" is computed for each square of the board
	* that does not contain a mine by determining how many mines
	* touch the square.  The adjacent squares are tested for mines
	* and the "awayCount" is incremented when a mine is found.
	* Finally, the "awayCount" is used as an index into the
	* "minesAway" array that contain the images for the numbers
	* one to eight.  The "minesAway" image is assigned to the
	* square.
	*/
	for (int i=0; i<rows; i++) {
		for (int j=0; j<columns; j++) {
			if (images [i][j] == null) {
				int awayCount = 0;
				for (int k=i-1; k<i+2; k++) {
					for (int l=j-1; l<j+2; l++) {
						if (0 <= k && k < rows) {
							if (0 <= l && l < columns) {
								if (images [k][l] == mine) awayCount++;
							}
						}
					}
				}
				if (awayCount != 0) images [i][j] = minesAway [awayCount-1];
			}
		}
	}
	redraw ();	
}

/**
 * Reveal the board.  Mark all the squares as selected and
 * redraw the board.
 */
public void reveal () {
	checkWidget ();
	for (int i=0; i<rows; i++) {
		for (int j=0; j<columns; j++) {
			selected [i][j] = true;
		}
	}
	redraw ();
}

/**
 * Select a square on the board.  Recursively mark all
 * squares selected that are not already selected and
 * do not contain a mine or a flag icon.
 * 
 * @param gc the gc used to draw the selected square(s)
 * @param i the row of the square
 * @param j the column of the square
 * @param width the width of the square
 * @param height the height of the square
 */
void select (GC gc, int i, int j, int width, int height) {	
	if (0 <= i && i < rows) {
		if (0 <= j && j < columns) {
			if (selected [i][j]) return;
			if (images [i][j] == mine) return;
			if (guesses [i][j] == flag) return;
			selected [i][j] = true;
			drawItem (gc, i, j, width, height, false);
			if (images [i][j] == null) {
				
//				MCQ:
//				Should it not be sufficient to mark only
//				four squares (left, right, top, bottom)
//				because the recursive call will eventually
//				hit the corner cases?

				select (gc, i + 1, j, width, height);
				select (gc, i + 1, j + 1, width, height);
				select (gc, i, j + 1, width, height);
				select (gc, i - 1, j + 1, width, height);
				select (gc, i - 1, j, width, height);
				select (gc, i - 1, j - 1, width, height);
				select (gc, i, j - 1, width, height);
				select (gc, i + 1, j - 1, width, height);
			}
		}
	}
}

}