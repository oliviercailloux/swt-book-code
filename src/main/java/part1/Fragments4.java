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

public class Fragments4 {

Display display;
Shell shell;
MessageBox dialog;
Color color;

public void f0_a () {
dialog.setText("Confirm Delete");
dialog.open();
}

public void f0_b () {
//USELESS - MessageBox with no message text
MessageBox dialog = new MessageBox(shell);
dialog.setText("Information");
dialog.open();
}

public void f0_c () {
MessageBox dialog = new MessageBox(shell,
    SWT.ICON_ERROR | SWT.YES | SWT.NO);
}

public void f0_d () {
MessageBox dialog = new MessageBox(shell);
dialog.setText("Information");
dialog.setMessage("Project was deleted.");
dialog.open();
}

public void f0_e () {
int style = SWT.ICON_QUESTION | SWT.YES | SWT.NO;
MessageBox dialog = new MessageBox(shell, style);
dialog.setText("Question");
dialog.setMessage("Delete the Project ?");
if (dialog.open() == SWT.YES) {
    // code to delete the project goes here
}
}

public void f0_f () {
FileDialog dialog = new FileDialog(shell, SWT.SAVE);
dialog.open();
}

public void f0_g () {
FileDialog dialog = new FileDialog(shell, SWT.SAVE);
dialog.setText("Browse for a File");
dialog.setFilterPath(System.getProperty("user.home"));
dialog.open();
}

public void f0_h () {
FileDialog dialog = new FileDialog(shell, SWT.SAVE);
dialog.setText("Browse for a File");
dialog.setFilterPath(System.getProperty("java.home"));
dialog.setFilterExtensions(new String[] {"*.txt", "*.*"});
dialog.open();
}

public void f0_i () {
FileDialog dialog = new FileDialog(shell, SWT.SAVE);
dialog.setText("Browse for a File");
dialog.setFilterPath(System.getProperty("java.home"));
dialog.setFilterExtensions(new String[] {"*.txt", "*.*"});
dialog.setFilterExtensions(
    new String[] {"Text Files(*.txt)", "All Files (*.*)"});
dialog.open();
}

public void f0_j () {
FileDialog dialog = new FileDialog(shell, SWT.MULTI);
dialog.setText("Browse for Files");
dialog.setFilterPath(System.getProperty("user.home"));
if (dialog.open() != null) {
    String path = dialog.getFilterPath();
    String[] names = dialog.getFileNames();
    for (int i = 0; i < names.length; i++) {
        System.out.println(path + names[i]);
    }
}
}

public void f0_k () {
DirectoryDialog dialog = new DirectoryDialog(shell);
dialog.open();
}

public void f0_l () {
DirectoryDialog dialog = new DirectoryDialog(shell);
dialog.setText("Browse for a Directory");
dialog.setMessage("Choose a directory for the install.");
dialog.open();
}

public void f0_m () {
ColorDialog dialog = new ColorDialog(shell);
dialog.open();
}

public void f0_n () {
ColorDialog dialog = new ColorDialog(shell);
dialog.setRGB(color != null ? color.getRGB() : null);
if (dialog.open() != null) {
    if (color != null) color.dispose();
    color = new Color(display, dialog.getRGB());
}
}

public void f0_o () {
FontDialog dialog = new FontDialog(shell);
dialog.open();
}

public void f0 () {
MessageBox dialog = new MessageBox(shell);
dialog.setText("Your application has be restared.");
dialog.open();
}



public void f4 () {
FileDialog dialog = new FileDialog(shell, SWT.SAVE);
dialog.setText("Browse for a File");
dialog.setFilterPath(System.getProperty("java.home"));
dialog.open();
}


}