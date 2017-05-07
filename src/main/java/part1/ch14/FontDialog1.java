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

public class FontDialog1 {

public static void main(String[] args) {
    final Font[] font = new Font[1];
    final Color[] color = new Color[1];
    final Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new RowLayout());
    int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL;
    final Text text = new Text(shell, style);
    text.setLayoutData(new RowData(100, 200));
    text.setText("Sample Text");
    Button button = new Button(shell, SWT.PUSH);
    button.setText("Set Font");
    final FontDialog dialog = new FontDialog(shell);
    dialog.setText("Choose a Font");
    button.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event event) {
            if (dialog.open() == null) return;
            if (font[0] != null) font[0].dispose();
            FontData[] list = dialog.getFontList();
            font[0] = new Font(display, list);
            text.setFont(font[0]);
            RGB rgb = dialog.getRGB();
            if (rgb != null) {
                if (color[0] != null) color[0].dispose();
                color[0] = new Color(display, rgb);
                text.setForeground(color[0]);
            }
        }
    });
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    if (font[0] != null) font[0].dispose();
    if (color[0] != null) color[0].dispose();
    display.dispose();
}

}
