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
package part2;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;

public class GetSystemFont {
public static void main(String[] args) {
	Display display = new Display();
	System.out.println("System Font is:");
	printFontData(display.getSystemFont().getFontData());
	System.out.println("Font in GC on Display is:");
	GC gc = new GC(display);
	printFontData(gc.getFont().getFontData());
	gc.dispose();
	display.dispose();
}
private static void printFontData(FontData[] fds) {
	String style;
	for (int i=0; i<fds.length; ++i) {
		FontData fd = fds[i];
		switch (fd.getStyle()) {
			case SWT.NORMAL: style = "NORMAL"; break;
			case SWT.BOLD: style = "BOLD"; break;
			case SWT.ITALIC: style = "ITALIC"; break;
			case (SWT.BOLD|SWT.ITALIC):
				style = "BOLD|ITALIC"; break;
			default: 
				style = "STYLE("+fd.getStyle()+")";
		}
		System.out.println(
			"    FontData[" + i + "] " + fd.getName() + 
			", " + fd.getHeight() +
			", " + style);
	}
}}
