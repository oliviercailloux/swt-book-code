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
package part1.ch9;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class Tree3 {

static void traverse(TreeItem item) {
    String string = item.toString();
   	TreeItem temp = item.getParentItem();
    while (temp != null) {
        string = "\t" + string;
        temp = temp.getParentItem();
    }
    System.out.println(string);
    TreeItem[] items = item.getItems();
    for (int i = 0; i < items.length; i++) {
        traverse(items[i]);
    }
}

static void traverse(Tree tree) {
    TreeItem[] items = tree.getItems();
    for (int i = 0; i < items.length; i++) {
        traverse(items[i]);
    }
}

public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    Tree tree = new Tree(shell, SWT.BORDER);
    for (int i = 0; i < 4; i++) {
        TreeItem itemI = new TreeItem(tree, SWT.NULL);
        itemI.setText("Item " + i);
        for (int j = 0; j < 4; j++) {
            TreeItem itemJ = new TreeItem(itemI, SWT.NULL);
            itemJ.setText("Item " + i + " " + j);
            for (int k = 0; k < 4; k++) {
                TreeItem itemK =
                    new TreeItem(itemJ, SWT.NULL);
                itemK.setText(
                    "Item " + i + " " + j + " " + k);
            }
        }
    }
    tree.setSize(200, 200);
    shell.pack();
    shell.open();
    traverse(tree);
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}