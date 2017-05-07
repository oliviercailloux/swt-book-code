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

public class TwoTextSizes {

static final String smallText = "Some small text and ";
static final FontData smallFD = 
    new FontData("Times", 14, SWT.NORMAL);
static final String largeText = "Some large text";
static final FontData largeFD = 
    new FontData("Times", 36, SWT.NORMAL);

public static void main(String[] args) {
    final Display display = new Display();
    final Font smallFont = new Font(display, smallFD);
    final Font largeFont = new Font(display, largeFD);
    final Shell shell = new Shell(display);
    shell.addListener(SWT.Paint, new Listener() {
        public void handleEvent(Event event) {
            Rectangle bounds = shell.getClientArea();
            int top = bounds.y;
            int left = bounds.x;
            GC gc = event.gc;
            gc.setFont(largeFont);
            FontMetrics fm = gc.getFontMetrics();
            int largeBaseline = 
                fm.getAscent() + fm.getLeading();
            gc.setFont(smallFont);
            fm = gc.getFontMetrics();
            int smallBaseline = 
                fm.getAscent()+fm.getLeading();
            int yAdvance = largeBaseline - smallBaseline;
            int xAdvance = gc.stringExtent(smallText).x;
            gc.drawString(
                smallText, left, top+yAdvance, true);
            gc.setFont(largeFont);
            gc.drawString(
                largeText, left+xAdvance, top, true);
            gc.setLineStyle(SWT.LINE_DOT);
            gc.drawLine(
                left, largeBaseline, 
                left+bounds.width, largeBaseline);
        }
    });
    shell.setText("Two Text Sizes");
    shell.setSize(400, 400);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    display.dispose();
}}
