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
package part1.ch14;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class FileDialog1 {

public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    Label label = new Label(shell, SWT.NONE);
    FileDialog dialog = new FileDialog(shell, SWT.OPEN);
    dialog.setText("Browse for a File");
    dialog.setFilterPath(System.getProperty("user.home"));
    dialog.setFilterExtensions(
        new String[] {
            "*.*",
            "*.bmp",
            "*.gif",
            "*.ico",
            "*.jpg",
            "*.png"});
    dialog.setFilterNames(
        new String[] {
            "All Files",
            "BMP (*.bmp)",
            "GIF (*.gif)",
            "ICO (*.ico)",
            "JPEG (*.jpg)",
            "PNG (*.png)" });
    Image image = null;
    String path = dialog.open();
    if (path != null) {
        image = new Image(display, path);
        label.setImage(image);
    }
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    if (image != null) image.dispose();
    display.dispose();
}

}
