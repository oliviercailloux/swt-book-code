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
package part1;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class Fragments5 {

Table table;
FontMetrics fm;

public void f0 () {
	GridData data1 = new GridData(GridData.FILL_BOTH);
	GridData data2 = 
	    new GridData(SWT.FILL, SWT.FILL, true, true);
	GridData data3 = new GridData();
	data3.horizontalAlignment = SWT.FILL;
	data3.verticalAlignment = SWT.FILL;
	data3.grabExcessHorizontalSpace = true;
	data3.grabExcessVerticalSpace = true;
}

public void f1 () {
int height =
    5 * table.getItemHeight() + table.getHeaderHeight();
int width = 20 * fm.getAverageCharWidth();
table.setLayoutData(new RowData(width, height));
}
}