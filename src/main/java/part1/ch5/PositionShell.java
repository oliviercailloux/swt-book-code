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
package part1.ch5;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class PositionShell {

public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setSize(200, 200);
    Point pt = display.getCursorLocation();
    Point size = shell.getSize();
    Monitor [] monitors = display.getMonitors();
    for (int i= 0; i<monitors.length; i++) {
    	if (monitors [i].getBounds().contains(pt)) {
    	   Rectangle rect = monitors [i].getClientArea();
		   pt.x =
		        Math.max(
		            rect.x,
		            Math.min(
		                Math.max(pt.x, rect.x),
		                rect.x + rect.width - size.x));
		   pt.y =
		        Math.max(
		            rect.y,
		            Math.min(
		                Math.max(pt.y, rect.y),
		                rect.y + rect.height - size.y));
    	   break;
    	}
    }
    shell.setLocation(pt);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}
