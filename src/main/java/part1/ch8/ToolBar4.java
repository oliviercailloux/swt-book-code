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
package part1.ch8;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class ToolBar4 {

public static void main(String[] args) {
    final Display display = new Display();
    Shell shell = new Shell(display);
    final ToolBar toolBar =
        new ToolBar(shell, SWT.HORIZONTAL);
    final Menu menu = new Menu(shell, SWT.POP_UP);
    for (int i = 0; i < 8; i++) {
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Item " + i);
    }
    final ToolItem item =
        new ToolItem(toolBar, SWT.DROP_DOWN);
    item.setText("Drop Down");
    item.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event event) {
            if (event.detail == SWT.ARROW) {
                Point point = new Point(event.x, event.y);
                point = display.map(toolBar, null, point);
                menu.setLocation(point);
                menu.setVisible(true);
            }
        }
    });
    toolBar.pack();
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}
