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
package part1.ch13;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class CoolBar3 {

static int itemCount;
static CoolItem createItem(
    final CoolBar coolBar,
    int count) {
    final ToolBar toolBar =
        new ToolBar(coolBar, SWT.FLAT | SWT.WRAP);
    for (int i = 0; i < count; i++) {
        ToolItem item = new ToolItem(toolBar, SWT.PUSH);
        item.setText(itemCount++ +"");
    }
    toolBar.pack();
    Point size = toolBar.getSize();
    final CoolItem item =
        new CoolItem(coolBar, SWT.DROP_DOWN);
    item.addListener(SWT.Selection, new Listener() {
        Menu menu = null;
        public void handleEvent(Event e) {
            if (e.detail != SWT.ARROW) return;
            int i = 0;
            ToolItem[] items = toolBar.getItems();
            Rectangle client = toolBar.getClientArea();
            while (i < items.length) {
                Rectangle rect1=items[i].getBounds();
                Rectangle rect2=rect1.intersection(client);
                if (!rect1.equals(rect2)) break;
                i++;
            }
            if (i == items.length) return;
            Shell shell = toolBar.getShell();
            if (menu != null) menu.dispose();
            menu = new Menu(shell, SWT.POP_UP);
            for (int j = i; j < items.length; j++) {
                MenuItem item =new MenuItem(menu,SWT.PUSH);
                item.setText(items[j].getText());
            }
            Point pt = e.display.map(coolBar,null,e.x,e.y);
            menu.setLocation(pt);
            menu.setVisible(true);
        }
    });
    item.setControl(toolBar);
    Point preferred = item.computeSize(size.x, size.y);
    item.setPreferredSize(preferred);
    Rectangle minimum = toolBar.getItems()[0].getBounds();
    item.setMinimumSize(minimum.width, minimum.height);
    return item;
}

public static void main(String[] args) {
    Display display = new Display();
    final Shell shell = new Shell(display);
    CoolBar coolBar = new CoolBar(shell, SWT.NONE);
    createItem(coolBar, 3);
    createItem(coolBar, 2);
    createItem(coolBar, 3);
    createItem(coolBar, 4);
    int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL;
    Text text = new Text(shell, style);
	FormLayout layout = new FormLayout();
	shell.setLayout(layout);
	FormData coolData = new FormData();
	coolData.left = new FormAttachment(0);
	coolData.right = new FormAttachment(100);
	coolData.top = new FormAttachment(0);
	coolBar.setLayoutData(coolData);
	coolBar.addListener (SWT.Resize, new Listener () {
		public void handleEvent(Event event) {
			shell.layout();
		}
	});
	FormData textData = new FormData();
	textData.left = new FormAttachment(0);
	textData.right = new FormAttachment(100);
	textData.top = new FormAttachment(coolBar);
	textData.bottom = new FormAttachment(100);
	text.setLayoutData(textData);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}
