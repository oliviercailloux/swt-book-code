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
package part1.ch11;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class Shell4 {

    static final int POINTS = 11;

    public static void main(final String[] args) {
	final Point center = new Point(0, 0);
	final int[] radial = new int[POINTS * 2];
	final Display display = new Display();
	final Color black = display.getSystemColor(SWT.COLOR_BLACK);
	final Shell shell = new Shell(display, SWT.NO_TRIM);
	shell.setBackground(black);
	shell.setSize(200, 200);
	final Rectangle bounds = shell.getClientArea();
	center.x = bounds.x + bounds.width / 2;
	center.y = bounds.y + bounds.height / 2;
	int pos = 0;
	for (int i = 0; i < POINTS; ++i) {
	    final double r = Math.PI * 2 * pos / POINTS;
	    radial[i * 2] = (int) ((1 + Math.cos(r)) * center.x);
	    radial[i * 2 + 1] = (int) ((1 + Math.sin(r)) * center.y);
	    pos = (pos + POINTS / 2) % POINTS;
	}
	final Listener listener = new Listener() {
	    int offsetX = 0, offsetY = 0;

	    public void handleEvent(final Event e) {
		switch (e.type) {
		case SWT.MouseDown:
		    if (e.button == 1) {
			offsetX = e.x;
			offsetY = e.y;
		    }
		    break;
		case SWT.MouseMove:
		    if ((e.stateMask & SWT.BUTTON1) != 0) {
			final Point pt = shell.toDisplay(e.x, e.y);
			pt.x -= offsetX;
			pt.y -= offsetY;
			shell.setLocation(pt);
		    }
		    break;
		case SWT.KeyDown:
		    shell.dispose();
		    break;
		}
	    }
	};
	shell.addListener(SWT.MouseDown, listener);
	shell.addListener(SWT.MouseMove, listener);
	shell.addListener(SWT.KeyDown, listener);
	final Region region = new Region(display);
	region.add(radial);
	shell.setRegion(region);
	shell.open();
	while (!shell.isDisposed()) {
	    if (!display.readAndDispatch()) {
		display.sleep();
	    }
	}
	region.dispose();
	display.dispose();
    }

}
