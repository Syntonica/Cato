/*
Copyright 2023 K.J.Donaldson

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package cato;

import java.awt.Font;
import javax.swing.JDialog;
import javax.swing.JLabel;

// Need the WaitBox since the Busy cursor only functions on certain parts of
// the GUI due to its Frankenstein nature...
public class WaitBox extends JDialog
{
	WaitBox()
	{
		setUndecorated(true);
		setSize(250, 50);
		setLayout(new ColumnLayout());
		JLabel wait = new JLabel("...please wait...");
		wait.setForeground(Cato.hiliteColor);
		wait.setHorizontalAlignment(JLabel.CENTER);
		wait.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		add("hvwx", wait);
		setLocationRelativeTo(BooksView.getInstance());
		setVisible(true);
	}
}