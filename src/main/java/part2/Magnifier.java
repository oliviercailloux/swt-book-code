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

public class Magnifier {

public static final String resourceName = "Camel.JPG";

public static void main(String[] args) {
    final ImageData imgData = new ImageData(
        Magnifier.class.getResourceAsStream(resourceName));
    final Display display = new Display();
    final Image img = new Image(display, imgData);
    final Color white = 
        display.getSystemColor(SWT.COLOR_WHITE);
    final Shell shell = new Shell(display, 
        SWT.SHELL_TRIM | SWT.NO_BACKGROUND);
    shell.addListener(SWT.Move, new Listener() {
        public void handleEvent(Event event) {
            shell.redraw();
            shell.update();
        }});
    shell.addListener(SWT.Paint, new Listener() {
        public void handleEvent(Event event) {
            Rectangle r = shell.getClientArea();
            Point p = display.map(shell, null, r.x, r.y);
            r = new Rectangle(
                -p.x + 50, -p.y + 50,
                imgData.width * 2, imgData.height * 2);
            event.gc.drawImage(
                img,
                0, 0, imgData.width, imgData.height,
                r.x, r.y, r.width, r.height);
            Region rgn = new Region();
            rgn.add(shell.getClientArea());
            rgn.subtract(r);
            event.gc.setClipping(rgn);
            event.gc.fillRectangle(shell.getClientArea());
            rgn.dispose();
        }});
    shell.setText("Magnifier");
    shell.setBounds(400, 300, 300, 200);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    img.dispose();
    display.dispose();
}}
