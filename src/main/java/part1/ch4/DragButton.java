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
package part1.ch4;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class DragButton {
	
public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    Composite composite = new Composite(shell, SWT.NULL);
    composite.setEnabled(false);
    final Button button = new Button(composite, SWT.PUSH);
    button.setText("Drag Me");
    button.pack();
    Listener listener = new Listener() {
        Point offset = null;
        public void handleEvent(Event e) {
            switch (e.type) {
                case SWT.MouseDown:
                    Rectangle rect = button.getBounds();
                    if (!rect.contains(e.x, e.y)) break;
                    Point pt = button.getLocation();
                    offset = new Point(e.x - pt.x, e.y - pt.y);
                    break;
                case SWT.MouseMove:
                    if (offset == null) break;
                    button.setLocation(
                        e.x - offset.x,
                        e.y - offset.y);
                    break;
                case SWT.MouseUp:
                    offset = null;
                    break;
            }
        }
    };
    shell.addListener(SWT.MouseDown, listener);
    shell.addListener(SWT.MouseUp, listener);
    shell.addListener(SWT.MouseMove, listener);
    shell.setSize(300, 300);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}
}