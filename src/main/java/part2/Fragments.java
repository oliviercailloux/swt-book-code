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

import java.io.InputStream;
import java.util.Hashtable;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 * @author mcq
 *
 * This class is not meant to be executed. It contains all of the code
 * fragments that show up in the Graphics part of the book. This ensures
 * that the code is all syntactically correct.
 */
public class Fragments {

	Display display;
	Shell shell;
	Font textFont;
	Point pt;
    ImageData theImageData;
    Image imageWithTransparency;
    Color backgroundColor;
    InputStream imageSource;
    Image[] images;
    
	public void f1() {
Hashtable h = new Hashtable();
Point p = new Point(100,100);
h.put(p, "get the point?");
p.x = 200;  // BAD BAD BAD: changes hashCode of p.
	}

	public void f2() {
Rectangle rectangle1 = new Rectangle(10, 10, 100, 100);
Rectangle rectangle2 = new Rectangle(100, 100, 100, 100);

Point p = new Point (
	(rectangle1.x + rectangle2.x) / 2, 
	(rectangle1.y + rectangle2.y) / 2);
	}

	public void f3() {
Rectangle rectangle1 = new Rectangle(10, 10, 100, 100);
Rectangle rectangle2 = new Rectangle(100, 100, 100, 100);

if (rectangle1.intersects(rectangle2)) {
	Rectangle intersection =
		rectangle1.intersection(rectangle2); 
			// overlapping area
	Rectangle union =
		rectangle1.union(rectangle2); 
			// smallest area covering both
}
	}

	public void f4() {
RGB rgb = new RGB(19, 255, 100);
System.out.println(rgb.red+" "+ rgb.green+" "+rgb.blue);
	}
	
    public void f5() {
Text text = new Text(shell, SWT.MULTI);
Font textFont = text.getFont();
text.dispose();
    }
    
    public void f6() {
List list = new List(shell, SWT.SINGLE);
RGB foreground = list.getBackground().getRGB();
RGB background = list.getForeground().getRGB();
list.dispose();
    }
    
    public void f7() {
RGB[] rgbs = theImageData.getRGBs();
Color[] colors = new Color[rgbs.length];
if (rgbs != null)
    for (int i=0; i<rgbs.length; ++i)
        colors[i] = new Color(display, rgbs[i]);
    }
    public void f8() {
Rectangle r = imageWithTransparency.getBounds();
Image img = new Image(display, r.width, r.height);
GC gc = new GC(img);
gc.setBackground(backgroundColor);
gc.fillRectangle(r);
gc.drawImage(imageWithTransparency, 0, 0);
gc.copyArea(imageWithTransparency, 0, 0);
gc.dispose();
    }

    public void f9() {
imageWithTransparency = 
    new Image(display, "something.gif");
Button b = new Button(shell, SWT.PUSH);
imageWithTransparency.setBackground(b.getBackground());
b.setImage(imageWithTransparency);
    }
    
}
