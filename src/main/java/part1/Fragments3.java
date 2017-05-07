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

public class Fragments3 {

Composite parent;
Widget widget;
Text text;

int wrap;
Button button, button1, button2, button3;
static final int WRAP_NONE = 0;
static final int WRAP_WINDOW = 1;
static final int WRAP_RULER = 2;

Image cutImage, copyImage, pasteImage;

MenuItem item;
Slider slider;
Canvas canvas;
int originX, originY, newOriginX, newOriginY, destX, destY;
int width, height;

FontMetrics fm;
List list;
Table table;
int rows, columns;

Shell shell;

public void f1 () {
Label label = new Label(parent, SWT.NONE);
label.setText("User Name:");
}

public void f2 () {
Label label = new Label(shell, SWT.WRAP);
label.setText("This is a label with text that wraps.");
Point size = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
label.setSize(size.x / 2, 200);
}

public void f3 () {
Label separator =
    new Label(parent, SWT.SEPARATOR | SWT.SHADOW_OUT);
}

public void f4 () {
Button button = new Button(parent, SWT.PUSH);
button.setText("Ok");
}

public void f5 () {
Button button = new Button(parent, SWT.CHECK);
button.setText("Overwrite when typing");
button.setSelection(true);
}

public void f6 () {
Button button1 = new Button(parent, SWT.RADIO);
button1.setText("Don't wrap");
button1.setSelection(true);
Button button2 = new Button(parent, SWT.RADIO);
button2.setText("Wrap to window");
Button button3 = new Button(parent, SWT.RADIO);
button3.setText("Wrap to ruler");
}

public void f7 () {
button1.setSelection(wrap == WRAP_NONE);
button2.setSelection(wrap == WRAP_WINDOW);
button3.setSelection(wrap == WRAP_RULER);
}

public void f8 () {
Button button = new Button(parent, SWT.TOGGLE);
button.setText("Play");
button.setSelection(true);
}

public void f9 () {
Button button = new Button(parent, SWT.ARROW);
button.setAlignment(SWT.RIGHT);
}

public void f10 () {
button.addListener(SWT.Selection, new Listener() {
    public void handleEvent(Event event) {
        System.out.println("Ok Pressed");
    }
});
}

public void f10a() {
Listener listener = new Listener() {
	public void handleEvent(Event event) {
		Button button = (Button) event.widget;
		if (!button.getSelection()) return;
		System.out.println(
			"Arriving " + button.getText());
	}
};
Button land = new Button(shell, SWT.RADIO);
land.setText("By Land");
land.addListener(SWT.Selection, listener);
Button sea = new Button(shell, SWT.RADIO);
sea.setText("By Sea");
sea.addListener(SWT.Selection, listener);
sea.setSelection(true);
}

public void f11 () {
widget.setData("Picard");
if ("Picard".equals(widget.getData())) {
    System.out.println("Found the captain!");
}
}

public void f12 () {
widget.setData("Android", "Data");
widget.setData("Captain", "Picard");
if ("Picard".equals(widget.getData("Captain"))) {
    System.out.println("Found the captain again!");
}
}

public void f13 () {
Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
text.setText("Texan");
}

public void f14 () {
int style =
    SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL;
Text text = new Text(parent, style);
}

public void f14a () {

}

public void f15 () {
int style = SWT.SINGLE | SWT.BORDER | SWT.PASSWORD;
Text text = new Text(parent, style);
text.setText("fred54"); //this text won't be displayed
}

public void f16 () {
String lf = Text.DELIMITER;
text.setText("Line 1" + lf + "Line 2" + lf + "Line 3" );
}

public void f17 () {
String string = "Line 1\nLine 2\nLine 3\nLine 4";
text.setText(string);
string = text.getText();
}

public void f18 () {
text.setText("Hello There Fred!");
text.setSelection(6, 11);
}

public void f19 () {
text.setText("Hello There Fred!");
text.setSelection(6, 11);
text.insert("New Text");
}

public void f20 () {
text.append("New Text");
text.setSelection(text.getCharCount());
text.insert("More new text");
text.setSelection(text.getCharCount());
}

public void f21() {
int height = rows * list.getItemHeight();
int width = columns * fm.getAverageCharWidth();
list.setSize(list.computeSize(width, height));
}

public void f22() {
int height =
    rows * table.getItemHeight() + table.getHeaderHeight();
int width = columns * fm.getAverageCharWidth();
table.setSize(table.computeSize(width, height));
}

public void f23() {
table.addListener(SWT.MouseDown, new Listener() {
    public void handleEvent(Event event) {
        Rectangle rect = table.getClientArea();
        int itemCount = table.getItemCount();
        int columnCount = table.getColumnCount();
        int i = table.getTopIndex();
        while (i < itemCount) {
            TableItem item = table.getItem(i);
            for (int j = 0; j < columnCount; j++) {
                Rectangle bounds = item.getBounds(j);
                if (bounds.y > rect.height) return;
                if (bounds.contains(event.x, event.y)) {
                    System.out.println(item.getText(j));
                    return;
                }
            }
            i++;
        }
    }
});
}

public void f50 () {
// FAILS - copies bits using new origin, then damages
originX = newOriginX;
originY = newOriginY;
canvas.scroll(destX, destY, 0, 0, width, height, false);
}

public void f51 () {
//WORKS - copies bits at the old origin, then damages
canvas.scroll(destX, destY, 0, 0, width, height, false);
originX = newOriginX;
originY = newOriginY;
}

public void f98 () {
//fails when minimum is 0 and maximim is 100
slider.setMinimum(120);
slider.setMaximum(140);
}

public void f99 () {
//works when minimum is 0 and maximum is 100
slider.setMaximum(140);
slider.setMinimum(120);
}

public void f100 () {
ToolBar toolBar = new ToolBar(parent, SWT.HORIZONTAL);
ToolItem cutItem = new ToolItem(toolBar, SWT.PUSH);
cutItem.setImage(cutImage);
ToolItem copyItem = new ToolItem(toolBar, SWT.PUSH);
copyItem.setImage(copyImage);
ToolItem pasteItem = new ToolItem(toolBar, SWT.PUSH);
pasteItem.setImage(pasteImage);
}

public void f200 () {
item.setText("Select All\tCtrl+A");
item.setAccelerator(SWT.CTRL + 'A');
item.addListener(SWT.Selection, new Listener() {
    public void handleEvent(Event e) {
        System.out.println("The item was selected.");
    }
});
}

public void f201 () {
item.addListener(SWT.Selection, new Listener() {
	public void handleEvent(Event event) {
		System.out.println("Menu item selected");
}
});
}

}