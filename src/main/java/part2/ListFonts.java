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

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;

public class ListFonts {
public static void main(String[] args) {
	final Display display = new Display();
	Set s = new HashSet();
	// Add names of all bitmap fonts.
	FontData[] fds = display.getFontList(null, false);
	for (int i=0; i<fds.length; ++i)
		s.add(fds[i].getName());
	// Add names of all scalable fonts.
	fds = display.getFontList(null, true);
	for (int i=0; i<fds.length; ++i)
		s.add(fds[i].getName());
	// Sort the result and print it.
	String[] answer = new String[s.size()];
	s.toArray(answer);
	Arrays.sort(answer);
	for (int i=0; i<answer.length; ++i) {
		System.out.println(answer[i]);
	}
	display.dispose();
}}
