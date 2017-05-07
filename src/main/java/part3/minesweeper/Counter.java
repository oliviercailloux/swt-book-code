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

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Steve Northover
 *
 * A Counter is a custom widget that draws an integer with
 * a leading number of zeros.
 * 
 * Thursday June 17, 2004.
 */
public class Counter extends Canvas implements Listener {
	
	int value;
	Image [] images = new Image [10];
	
	/**
	 * The number of digits to draw.
	 */
	static final int DIGITS = 3;

public Counter (Composite parent, int style) {
	super (parent, style);
	/*
	* Load the images and add the listeners.  Don't
	* bother to do any of this work lazily as every
	* image will be needed almost right away.
	*/
	Display display = getDisplay ();
	Class clazz = Counter.class;
	for (int i=0; i<images.length; i++) {
		String name = i + ".gif";
		images [i] = loadImage (display, clazz, name);
	}
	addListener (SWT.Paint, this);
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
	* The preferred size of a counter is defined by the number
	* of digits and the size of a single digit (all digits are
	* the same size).  The wHint and hHint paramters are ignored
	* because the number of digits dictates the size of the counter.
	* The value returned by computeSize() includes the trim (by
	* definition).
	*/
	Rectangle rect = images [0].getBounds ();
	int width = rect.width * DIGITS, height = rect.height;
	Rectangle trim = computeTrim (0, 0, width, height);
	return new Point (trim.width, trim.height);
}

/**
 * Handles an event. Despite the fact that this method is
 * public, it should not be treated as API.
 * 
 * @param event the event
 */
public void handleEvent (Event event) {
	switch (event.type) {
		case SWT.Dispose:
			for (int i=0; i<images.length; i++) {
				if (images [i] != null) images [i].dispose ();
				images [i] = null;
			}
			break;
		case SWT.Paint:
			GC gc = event.gc;
			int x = 0, y = 0;
			String string = String.valueOf (value);
			int zeros = DIGITS - string.length ();
			for (int i=0; i<zeros; i++) {
				gc.drawImage (images [0], x, y);
				x += images [0].getBounds ().width;
			}
			for (int i=0; i<string.length (); i++) {
				int digit = string.charAt (i) - '0';
				Image image = images [digit];
				gc.drawImage (image, x, y);
				x += image.getBounds ().width;
			}
			break;
	}
}

/**
 * Sets the value of the counter.  The counter is redrawn to
 * show the new value.  If the value is negative, it is ignored.
 *
 * @param value the new value of the counter
 */
public void setValue (int value) {
	checkWidget ();
	int newValue = Math.max (0, value);
	if (this.value == newValue) return;
	this.value = newValue;
	redraw ();
}

/**
 * Gets the value of the counter.
 *
 * @return the current value of the counter
 */
public int getValue () {
	checkWidget ();
	return value;
}

}