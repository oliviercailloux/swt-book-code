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

import java.io.*;

public class Tree7 {
	
public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setText("Lazy Tree");
    Tree tree = new Tree(shell, SWT.BORDER);

    /* Initialize the roots of the tree */
    File[] roots = File.listRoots();
    for (int i = 0; i < roots.length; i++) {
        TreeItem root = new TreeItem(tree, SWT.NULL);
        root.setText(roots[i].toString());
        root.setData(roots[i]);

        /* Use a dummy item to force the '+' */
        new TreeItem(root, SWT.NULL);
    }

    /* Use SWT.Expand to lazily fill the tree */
    tree.addListener(SWT.Expand, new Listener() {
        public void handleEvent(Event event) {

            /*
            * If the item has not contain a
            * dummy node, return. A dummy item
            * is a single child of the root that
            * does not have any application data.
            */
            TreeItem root = (TreeItem) event.item;
            TreeItem[] items = root.getItems();
            if (items.length != 1) return;
            if (items[0].getData() != null) return;
            items[0].dispose();

            /* Create the item children */
            File file = (File) root.getData();
            File[] files = file.listFiles();
            if (files == null) return;
            for (int i = 0; i < files.length; i++) {
                TreeItem item =new TreeItem(root,SWT.NULL);
                item.setText(files[i].getName());
                item.setData(files[i]);

                /* Use a dummy item to force the '+' */
                if (files[i].isDirectory()) {
                    new TreeItem(item, SWT.NULL);
                }
            }
        }
    });

    /* Set size of the tree and open the shell */
    tree.setSize(300, 300);
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}
}
