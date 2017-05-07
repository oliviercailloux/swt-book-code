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

import java.io.*;
import java.net.*;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;

/**
 * Loads an image in a background thread.
 * 
 * @author mcq
 */

public class BackgroundLoadImage {

// Controls in the application
Display display;
Shell shell;
Canvas canvas;
Color canvasBackground;
Text text;

// Image loading
ImageLoader loader;
Image[] images = new Image[50];
int lastImageIndex = -1;
boolean loading = false;

/**
 * Launches the application.
 * 
 * @author mcq
 * @param args ignored.
 */
public static void main(String[] args) {
    new BackgroundLoadImage().run();
}

/**
 * Opens a shell and runs the event loop.
 * 
 * @author mcq
 */
void run() {
    display = new Display();
    
    shell = new Shell(display, SWT.SHELL_TRIM);
    GridLayout layout = new GridLayout();
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    layout.numColumns = 1;
    shell.setLayout(layout);
    
    text = new Text(shell, SWT.BORDER | SWT.SINGLE);
    text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    text.addSelectionListener(new SelectionAdapter () {
        public void widgetDefaultSelected(SelectionEvent e) {
            if (!loading) {
                message(null);
                reset();
                loadImages();
            }
        }
    });
    
    canvas = new Canvas(shell, 
        SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE);
    canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
    canvasBackground = canvas.getBackground();
    canvas.addPaintListener(new PaintListener() {
        public void paintControl(PaintEvent e) {
            // Display the images, then erase the rest
            Region theRest = new Region(display);
            e.gc.getClipping(theRest);
            e.gc.setBackground(canvasBackground);
            int x = 0;
            for (int i = 0; i <= lastImageIndex; ++i) {
                if (images[i] != null) {
                    Rectangle r = images[i].getBounds();
                    r.x += x;
                    x += r.width;
                    if (theRest.intersects(r)) {
                        e.gc.fillRectangle(r);
                        e.gc.drawImage(images[i], r.x, r.y);
                        theRest.subtract(r);
                    }
                };
            }
            e.gc.setClipping(theRest);
            e.gc.fillRectangle(canvas.getClientArea());
            theRest.dispose();
        }});
    
    shell.setSize(800, 300);
    message(null); // Set shell title.
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    reset();
    display.dispose();
}

/**
 * Displays the app name, and optionally a message in the shell title. Uses
 * syncExec to allow title to be set from any thread.
 * 
 * @author mcq
 * @param text the text to display
 */
void message(String message) {
    final String title = "Background Load Image";
    final String text = message;
    if (shell.isDisposed()) return;
    display.syncExec(new Runnable() {
        public void run() {
            if (text == null || text.length() == 0)
                shell.setText(title);
            else
                shell.setText(title + " (" + text + ")");
        }});
    
}

/**
 * Answers the amount to shift the idx'th image by when displaying it. Note
 * that, the code assumes that images are always loaded in order. It is the
 * slow but simple version (no state maintained).
 * 
 * @author mcq
 * @param idx the index of the image being loaded
 * @return the x-coordinate to display it at
 */
int shiftAmount(int idx) {
    int x = 0;
    for (int i = 0; i < idx; ++i) {
        x += images[i].getBounds().width;
    }
    return x;
}

/**
 * Sets the entry in the images array for the image at the specified index
 * to an Image created from the parameter. If there was a previous image
 * in that slot dispose of the old one.
 * 
 * @author mcq
 * @param index the index of the image to update
 * @param imageData the new image to display
 */
void updateImage(int index, ImageData imageData) {
    final int idx = index;
    final ImageData id = imageData;
    display.syncExec(new Runnable() {
        public void run() {
            images[idx] = new Image(display, id);
            int x = shiftAmount(idx);
            canvas.redraw(x,0,id.width,id.height,false);
            canvas.update();
        }
    });
}

/**
 * Reads one or more images from the specified file into an array of ImageData
 * instances, then converts the ImageDatas into a matching array of Images.
 * 
 * @author mcq
 * @param fileName the name of the file to read
 * @return true if images could be loaded.
 */
void loadImages() {
    URL url;
    try {
        url = new URL(text.getText().trim());
    } catch (MalformedURLException e1) {
        message("Invalid URL");
        return;
    }
    final InputStream str;
    try {
        str = url.openStream();
    } catch (IOException e2) {
        message("Could not open URL");
        return;
    }
    loading = true;
    loader = new ImageLoader();
    message("loading");
    new Thread(new Runnable() {
        public void run() {
            try {
                ImageData[] answer = loader.load(str);
                if (answer == null) return;
                images = new Image[answer.length];
                lastImageIndex = answer.length - 1;
                for (int i = 0; i <= lastImageIndex; ++i)
                    updateImage(i, answer[i]);
            } catch (Throwable t) {
                message(t.toString());
            } finally {
                loading = false;
                message(null);
            }
       }}).start();
}


/**
 * Dispose of any images that were created.
 * 
 * @author mcq
 */
void reset() {
    loader = null;
    lastImageIndex = -1;
    if (!canvas.isDisposed()) {
        canvas.redraw();
        canvas.update();
    }
    if (images == null) return;
    for (int i = 0; i < images.length; ++i) {
        if (images[i] != null) {
            try {
                Image img = images[i];
                images[i] = null;
                img.dispose();
            } catch (SWTException ex) {
                System.err.println(
                    "Exception while disposing of images"
                    + ex);
            }
        }
    }
}

}
