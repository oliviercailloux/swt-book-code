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
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class Menu2 {

public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    FormLayout layout = new FormLayout();
    shell.setLayout(layout);
    final Label label = new Label(shell, SWT.BORDER);
    Listener armListener = new Listener() {
        public void handleEvent(Event event) {
            MenuItem item = (MenuItem) event.widget;
            label.setText(item.getText());
        }
    };
    Listener showListener = new Listener() {
        public void handleEvent(Event event) {
            Menu menu = (Menu) event.widget;
            MenuItem item = menu.getParentItem();
            if (item != null) {
                label.setText(item.getText());
            }
        }
    };
    Listener hideListener = new Listener() {
        public void handleEvent(Event event) {
            label.setText("");
        }
    };
    FormData labelData = new FormData();
    labelData.left = new FormAttachment(0);
    labelData.right = new FormAttachment(100);
    labelData.bottom = new FormAttachment(100);
    label.setLayoutData(labelData);
    Menu menuBar = new Menu(shell, SWT.BAR);
    shell.setMenuBar(menuBar);
    MenuItem fileItem = new MenuItem(menuBar, SWT.CASCADE);
    fileItem.setText("File");
    fileItem.addListener(SWT.Arm, armListener);
    MenuItem editItem = new MenuItem(menuBar, SWT.CASCADE);
    editItem.setText("Edit");
    editItem.addListener(SWT.Arm, armListener);
    Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
    fileMenu.addListener(SWT.Hide, hideListener);
    fileMenu.addListener(SWT.Show, showListener);
    fileItem.setMenu(fileMenu);
    String[] fileStrings = { "New", "Close", "Exit" };
    for (int i = 0; i < fileStrings.length; i++) {
        MenuItem item = new MenuItem(fileMenu, SWT.PUSH);
        item.setText(fileStrings[i]);
        item.addListener(SWT.Arm, armListener);
    }
    Menu editMenu = new Menu(shell, SWT.DROP_DOWN);
    editMenu.addListener(SWT.Hide, hideListener);
    editMenu.addListener(SWT.Show, showListener);
    String[] editStrings = { "Cut", "Copy", "Paste" };
    editItem.setMenu(editMenu);
    for (int i = 0; i < editStrings.length; i++) {
        MenuItem item = new MenuItem(editMenu, SWT.PUSH);
        item.setText(editStrings[i]);
        item.addListener(SWT.Arm, armListener);
    }
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}
