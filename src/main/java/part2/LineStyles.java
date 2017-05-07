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
package part2;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;

public class LineStyles {
public static void main(String[] args) {
    final Display display = new Display();
    final Shell shell = new Shell(display);
    shell.addListener(SWT.Paint, new Listener() {
        public void handleEvent(Event event) {
            GC gc = event.gc;
            gc.setLineWidth(10);
            gc.setLineStyle(SWT.LINE_SOLID);
            gc.drawLine(10, 10, 200, 10);
            gc.setLineStyle(SWT.LINE_DASH);
            gc.drawLine(10, 30, 200, 30);
            gc.setLineStyle(SWT.LINE_DOT);
            gc.drawLine(10, 50, 200, 50);
            gc.setLineStyle(SWT.LINE_DASHDOT);
            gc.drawLine(10, 70, 200, 70);
            gc.setLineStyle(SWT.LINE_DASHDOTDOT);
            gc.drawLine(10, 90, 200, 90);
        }});
    shell.setText("Line Styles");
    shell.setSize(250, 150);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    display.dispose();
}
}
