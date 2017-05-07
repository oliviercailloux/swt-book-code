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
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class Table6 {

static final int COLUMNS = 8, ROWS = 100;
public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    final Table table = new Table(shell, SWT.BORDER);
    table.setLinesVisible(true);
    for (int i = 0; i < COLUMNS; i++) {
        TableColumn column=new TableColumn(table,SWT.NONE);
        column.setText("Column " + i);
    }
    for (int i = 0; i < ROWS; i++) {
        TableItem item = new TableItem(table, SWT.NULL);
        for (int j = 0; j < COLUMNS; j++) {
            item.setText(j, "Item " + i + "-" + j);
        }
    }
    for (int i = 0; i < COLUMNS; i++) {
        TableColumn column = table.getColumn(i);
        column.pack();
    }
    table.addListener(SWT.MouseDown, new Listener () {
    	public void handleEvent(Event event) {
    		Rectangle rect = table.getClientArea();
    		int itemCount = table.getItemCount();
    		int columnCount = table.getColumnCount();
    		int i = table.getTopIndex();
    		while (i < itemCount) {
    			TableItem item = table.getItem(i);
    			for (int j=0; j<columnCount; j++) {
    				Rectangle bounds = item.getBounds(j);
    				if (bounds.y > rect.height) return;
    				if (bounds.contains(event.x, event.y)) {
    					System.out.println (item.getText(j));
    					return;
    				} 
    			}
    			i++;
    		}
    	}
    });
    shell.setSize(300, 300);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}