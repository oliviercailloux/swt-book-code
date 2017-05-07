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
import org.eclipse.swt.events.*;

public class CopyAreaTest {

Display display;
Color canvasBackground;
Shell shell;
Canvas canvas1, canvas2;

public static void main(String[] args) {
    new CopyAreaTest().run();
}
void run() {
    display = new Display();
    Color c1 = display.getSystemColor(SWT.COLOR_YELLOW);
    Color c2 = display.getSystemColor(SWT.COLOR_CYAN);
    shell = new Shell(display, SWT.SHELL_TRIM);
    canvas1 = new Canvas(shell, SWT.BORDER);
    canvas1.setBackground(c1);
    canvas1.setBounds(10, 10, 150, 100);
    canvas2 = new Canvas(shell, SWT.BORDER);
    canvas2.setBackground(c2);
    canvas2.addPaintListener(new PaintListener() {
        public void paintControl(PaintEvent e) {
            GC gc = new GC(canvas1);
            Rectangle r = canvas1.getClientArea();
            Image img = new Image(display,
                r.width, r.height);
            gc.copyArea(img, r.x, r.y);
            e.gc.drawImage(img, 20, 20);
            img.dispose();
            gc.dispose();
        }});
    canvas2.setBounds(40, 60, 190, 140);
    canvas2.moveAbove(null);
    shell.setSize(300, 240);
    shell.setText("Copy Area Test");
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    display.dispose();
}}
