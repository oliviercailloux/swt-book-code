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

// This version uses getFontMetrics() to decide how
// much vertical space to leave.

public class DrawText2 {

static final String string = 
    "&Hello,\tWorld\nfrom\tOttawa";

public static void main(String[] args) {
    final Display display = new Display();
    final Color white = 
        display.getSystemColor(SWT.COLOR_WHITE);
    final Color gray = 
        display.getSystemColor(SWT.COLOR_GRAY);
    final Color black = 
        display.getSystemColor(SWT.COLOR_BLACK);
    final Shell shell = new Shell(display);
    shell.addListener(SWT.Paint, new Listener() {
        public void handleEvent(Event event) {
            Rectangle bounds = shell.getClientArea();
            int top = bounds.y;
            int left = bounds.x;
            GC gc = event.gc;
            int yAdvance = gc.getFontMetrics().getHeight();
            gc.setBackground(gray);
            gc.fillRectangle(bounds);
            gc.setBackground(white);
            gc.setForeground(black);
            gc.drawString(
                string, left, top, false);
            gc.drawString(
                string, left, top + yAdvance, true);
            gc.drawText(
                string, left, top + (2 * yAdvance), false);
            gc.drawText(
                string, left, top + (4 * yAdvance), true);
            gc.drawText(
                string, left, top + (6 * yAdvance),
                SWT.DRAW_DELIMITER | SWT.DRAW_TAB | 
                SWT.DRAW_MNEMONIC);
            gc.drawText(
                string, left, top + (8 * yAdvance),
                SWT.DRAW_MNEMONIC);
        }
    });
    shell.setText("Draw Text 2");
    shell.setSize(400, 400);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    display.dispose();
}}
