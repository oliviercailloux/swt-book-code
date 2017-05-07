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
package part1.ch13;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class Tracker4 {

static final int TOLERANCE = 8;

public static void main(String[] args) {
    Display display = new Display();
    Color blue = display.getSystemColor(SWT.COLOR_BLUE);
    final Shell shell = new Shell(display);
    Composite composite = new Composite(shell, SWT.BORDER);
    composite.setBounds(10, 10, 100, 100);
    composite.setBackground(blue);
    Listener listener = new Listener() {
        Point point = null;
        public void handleEvent(Event event) {
            switch (event.type) {
                case SWT.MouseDown :
                    if (event.button == 1) {
                        point = new Point(event.x,event.y);
                    }
                    break;
                case SWT.MouseMove :
                    if (point == null) break;
                    int x = point.x - event.x;
                    int y = point.y - event.y;
                    if (Math.abs(x) < TOLERANCE
                        && Math.abs(y) < TOLERANCE) break;
                    Control control =(Control) event.widget;
                    move(shell, control, x, y);
                    point = null;
                    break;
            }
        }
    };
    composite.addListener(SWT.MouseDown, listener);
    composite.addListener(SWT.MouseMove, listener);
    shell.setSize(200, 200);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
}

static void move(final Shell shell1, final Control c2, int x, int y) {
    if (!c2.isReparentable()) return;
    final Display display = c2.getDisplay();
    Rectangle rect = shell1.getClientArea();
    final Rectangle r1 = display.map(shell1, null, rect);
    Shell shell2 = c2.getShell();
    rect = c2.getBounds();
    final Rectangle r2 = display.map(shell2, null, rect);
    r2.x -= x;
    r2.y -= y;
    final Tracker tracker = new Tracker(display, SWT.NONE);
    tracker.setRectangles(new Rectangle[] {r2});
    tracker.addListener(SWT.Move, new Listener() {
        public void handleEvent(Event event) {
            boolean inside = r1.intersection(r2).equals(r2);
            tracker.setStippled(!inside);
        }
    });
    if (!tracker.open()) return;
    if (r1.intersection(r2).equals(r2)) {
        if (shell1 != shell2) {
            c2.setVisible(false);
            c2.setParent(shell1);
            shell2.dispose();
        }
        c2.setBounds(display.map(null, shell1, r2));
        if (!c2.getVisible()) c2.setVisible(true);
    } else {
        if (shell1 == shell2) {
            shell2 = new Shell(shell1, SWT.SHELL_TRIM);
            c2.setParent(shell2);
            c2.setLocation(0, 0);
            //WRONG NOT USED
            shell2.addListener(SWT.Move, new Listener() {
                public void handleEvent(Event event) {
                	Shell s2 = (Shell)event.widget;
                	Rectangle r3 = s2.getClientArea();
                	r3 = display.map(s2, null, r3);
                	Rectangle r4 = shell1.getClientArea();
                	r4 = display.map(shell1, null, r4);
                	if (r4.intersection(r3).equals(r3)) {
            			c2.setParent(shell1);
                		r3 = display.map(null, shell1, r3);
                		c2.setBounds(r3);
            			s2.dispose();
                	}
                }
            });
        }
        int width = r2.width, height = r2.height;
        rect = shell2.computeTrim(r2.x, r2.y, width, height);
        shell2.setBounds(rect);
        if (!shell2.getVisible()) shell2.open();
    }
}

}
