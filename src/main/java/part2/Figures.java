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

public class Figures {
public static void main(String[] args) {
    final Display display = new Display();
    final Color allOnes = 
        new Color(display, 255, 255, 255);
    final Shell shell = new Shell(display);
    shell.addListener(SWT.Paint, new Listener() {
        public void handleEvent(Event event) {
            GC gc = event.gc;
            gc.drawRectangle(10, 10, 200, 100);
            gc.drawRectangle(
                new Rectangle (20, 20, 180, 80));
            gc.drawRoundRectangle(30, 30, 160, 60, 30, 20);
            gc.drawFocus(40, 40, 140, 40);
            gc.drawOval(50, 50, 120, 20);
            gc.setLineWidth(30);
            gc.setForeground(allOnes);
            gc.setXORMode(true);
            gc.drawArc(10, 80, 200, 180, 45, 90);
            gc.setXORMode(false);
        }});
    shell.setText("Figures");
    shell.setSize(250, 150);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    allOnes.dispose();
    display.dispose();
}}
