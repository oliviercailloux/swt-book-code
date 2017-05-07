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

import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;

public class LoadTransparentImages {

public static final String 
    resourceName = "transparent1.gif";

public static void main(String[] args) {
    final ImageData data = new ImageData(
        LoadTransparentImages.class.
            getResourceAsStream(resourceName));
    final Display display = new Display();
    final Image img = new Image(display, data);
    data.transparentPixel = -1;
    data.alpha = 150;
    final Image img2 = new Image(display, data);
    data.alpha = -1;
    byte[] alphas = new byte[data.width];
    for (int i = 0; i < data.height; ++i) {
        Arrays.fill(alphas, (byte)(255.0*i/data.height));
        data.setAlphas(
            data.x, data.y+i, data.width, alphas, 0);
    }
    final Image img3 = new Image(display, data);
    final Shell shell = new Shell(display, SWT.SHELL_TRIM);
    final Color gray = 
        display.getSystemColor(SWT.COLOR_GRAY);
    shell.addListener(SWT.Paint, new Listener() {
        public void handleEvent(Event event) {
            event.gc.setBackground(gray);
            event.gc.fillRectangle(shell.getClientArea());
            event.gc.drawImage(
                img,
                data.x,data.y,data.width,data.height,
                10,10,data.width,data.height);
            event.gc.drawImage(
                img2,
                data.x,data.y,data.width,data.height,
                20+data.width,10,data.width,data.height);
            event.gc.drawImage(
                img3,
                data.x,data.y,data.width,data.height,
                30+(2*data.width),10,
                    data.width,data.height);
        }
    });
    shell.setText("Load Transparent Images");
    shell.setSize(350, 180);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    img.dispose();
    display.dispose();
}}
