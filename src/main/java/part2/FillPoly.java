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

public class FillPoly {

static final int points = 11;

public static void main(String[] args) {
    final Point center = new Point(0,0);
    final int[] radial = new int[points*2];
    final Display display = new Display();
    final Color white = 
        display.getSystemColor(SWT.COLOR_WHITE);
    final Shell shell = new Shell(display);
    shell.addListener(SWT.Resize, new Listener() {
        public void handleEvent(Event event) {
            Rectangle bounds = shell.getClientArea();
            center.x = bounds.x + bounds.width/2;
            center.y = bounds.y + bounds.height/2;
            int pos = 0;
            for (int i = 0; i < points; ++i) {
                double r = Math.PI*2 * pos/points;
                radial[i*2] = (int)
                    ((1+Math.cos(r))*center.x);
                radial[i*2+1] = (int)
                    ((1+Math.sin(r))*center.y);
                pos = (pos + points/2) % points;
            }
        }});
    shell.addListener(SWT.Paint, new Listener() {
        public void handleEvent(Event event) {
        	event.gc.setBackground(white);
            event.gc.fillPolygon(radial);
            event.gc.drawPolygon(radial);
        }});
    shell.setText("Fill Polygon");
    shell.setSize(400, 400);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    display.dispose();
}}
