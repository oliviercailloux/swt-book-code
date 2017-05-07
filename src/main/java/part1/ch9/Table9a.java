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
import org.eclipse.swt.layout.*;

public class Table9a {

static final int COLUMNS = 3, ROWS = 100000, PAGE = 100;
static final String [] [] DATA = new String [ROWS] [COLUMNS];
static {
    for (int i = 0; i < ROWS; i++) {
    for (int j = 0; j < COLUMNS; j++) {
        DATA [i][j] = "Item " + i + "-" + j;
    }
}
}

public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    RowLayout layout = new RowLayout(SWT.VERTICAL);
    layout.fill = true;
    shell.setLayout(layout);
    final Table table = new Table(shell, SWT.BORDER);
    table.setLayoutData(new RowData(400, 400));
    table.setHeaderVisible(true);
    for (int i = 0; i < COLUMNS; i++) {
        TableColumn column=new TableColumn(table,SWT.NONE);
        column.setText("Column " + i);
        column.setWidth(128);
    }
    final ProgressBar progress = new ProgressBar(shell, SWT.NONE);
    progress.setMaximum(ROWS - 1);
    shell.pack();
    shell.open();
 	fillTable(table, progress, PAGE);
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

static void fillTable(
    final Table table,
    final ProgressBar progress,
    final int page) {
    final Display display = table.getDisplay();
    Thread thread = new Thread() {
        public void run() {
            int index = 0;
    		long time = -System.currentTimeMillis();
            while (index < ROWS) {
                if (table.isDisposed()) return;
                final int start = index;
                final int end = Math.min(index + page, ROWS);
                display.syncExec(new Runnable() {
                    public void run() {
                        if (table.isDisposed()) return;
                        for (int i = start; i < end; i++) {
                            TableItem item =
                                new TableItem(table, SWT.NULL);
                            for (int j=0; j<COLUMNS; j++) {
                                item.setText(j, DATA[i][j]);
                            }
                        }
                        int value = end == ROWS ? 0 : end;
                        progress.setSelection(value);
                    }
                });
                index = end;
            }
			time += System.currentTimeMillis();
    		System.out.println(ROWS + " items: " + time + "(ms)");
        }
    };
    thread.start();
}
}