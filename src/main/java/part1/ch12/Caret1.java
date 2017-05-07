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
package part1.ch12;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class Caret1 {

static int ibeam = 0;
static String text = "0123456789";

public static void main(String[] args) {
    Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    final Caret caret = new Caret(shell, SWT.NONE);
    GC gc = new GC(shell);
    Point size = gc.stringExtent("");
    caret.setSize(1, size.y);
    gc.dispose();
    shell.addListener(SWT.Paint, new Listener() {
        public void handleEvent(Event e) {
            e.gc.drawString(text, 0, 0);
        }
    });
    shell.addListener(SWT.KeyDown, new Listener() {
        public void handleEvent(Event e) {
            int length = text.length();
            if (e.character == 0) {
                switch (e.keyCode) {
                    case SWT.HOME: ibeam = 0; break;
                    case SWT.END: ibeam = length; break;
                    case SWT.ARROW_LEFT: --ibeam; break;
                    case SWT.ARROW_RIGHT: ibeam++; break;
                }
                ibeam = Math.min(Math.max(0,ibeam),length);
            }
            String left = text.substring(0, ibeam);
            String right = text.substring(ibeam, length);
            switch (e.character) {
                case SWT.CR:
                case SWT.LF:
                case SWT.TAB: break;
                case SWT.BS:
                    ibeam = Math.max(0, ibeam - 1);
                    left = text.substring(0, ibeam);
                    break;
                case SWT.DEL:
                    int pos =
                        Math.min(ibeam + 1, length);
                    right = text.substring(pos, length);
                    break;
                default:
                    if (e.character < ' ') break;
                    ibeam = Math.min(length + 1,ibeam + 1);
                    left = left + e.character;
            }
            GC gc = new GC(shell);
            Point size = gc.stringExtent(left);
            caret.setLocation(size.x, 0);
            gc.dispose();
            if (e.character != 0) {
                text = left + right;
                shell.redraw();
            }
        }
    });
    shell.addListener(SWT.MouseDown, new Listener() {
        public void handleEvent(Event e) {
            GC gc = new GC(shell);
            int length = text.length();
            int width = 0, lastWidth = 0;
            ibeam = 0;
            while (ibeam <= length) {
                lastWidth = width;
                String string = text.substring(0, ibeam);
                width = gc.stringExtent(string).x;
                if (width >= e.x)  break;
                ibeam++;
            }
            int offset = (width - lastWidth) / 2;
            if (e.x >= lastWidth + offset) {
                caret.setLocation(width, 0);
            } else {
                --ibeam;
                caret.setLocation(lastWidth, 0);
            }
            ibeam = Math.min(Math.max(0, ibeam), length);
            gc.dispose();
        }
    });
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}
