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

import java.util.*;

public class Table4 {

static class MyData {
    String string1, string2;
    public MyData(String string1, String string2) {
        this.string1 = string1;
        this.string2 = string2;
    }
}

static void sort(
    Table table,
    final int column,
    final boolean descend) {

    int count = table.getItemCount();
    MyData[] list = new MyData[count];
    for (int i = 0; i < count; i++) {
        Object data = table.getItem(i).getData();
        list[i] = (MyData) data;
    }
    Arrays.sort(list, new Comparator() {
        public int compare(Object a, Object b) {
            MyData d1 = (MyData) (descend ? b : a);
            MyData d2 = (MyData) (descend ? a : b);
            switch (column) {
                case 0 :
                   return d1.string1.compareTo(d2.string1);
                case 1 :
                   return d1.string2.compareTo(d2.string2);
            }
            return 0;
        }
    });
    for (int i = 0; i < list.length; i++) {
        TableItem item = table.getItem(i);
        item.setText(0, list[i].string1);
        item.setText(1, list[i].string2);
        item.setData(list[i]);
    }
}

static int ROWS = 10000;
public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    final Table table = new Table(shell, SWT.BORDER);
    table.setHeaderVisible(true);
    for (int i = 0; i < 2; i++) {
        TableColumn column =
            new TableColumn(table, SWT.NONE);
        column.setText("Column " + i);
        column.setData(new Boolean(false));
    }
    Random r = new Random();
    table.setRedraw(false);
    for (int i = 0; i < ROWS; i++) {
        TableItem item = new TableItem(table, SWT.NULL);
        MyData data =
            new MyData(
                "A" + r.nextInt(1000),
                "B" + r.nextInt(1000));
        item.setText(0, data.string1);
        item.setText(1, data.string2);
        item.setData(data);
    }
    sort(table, 0, false);
    table.setRedraw(true);
    for (int i = 0; i < table.getColumnCount(); i++) {
        final TableColumn column = table.getColumn(i);
        column.pack();
        column.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                int index = table.indexOf(column);
                if (index != -1) {
                    Boolean b = (Boolean) column.getData();
                    boolean value = b.booleanValue();
                    sort(table, index, !value);
                    column.setData(new Boolean(!value));
                }
            }
        });
    }
    table.setSize(200, 200);
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}