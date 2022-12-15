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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

public class GenericEditButtonPanel extends JPanel
{
	public GenericEditButtonPanel(final GenericEditView editView)
	{
		Action saveAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (editView.saveEntry()) BooksView.getInstance().tabbedPane.closeTab(editView);
			}
		};

		Action cancelAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				editView.cancelEntry();
			}
		};

		Action printAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e) // printPage
			{
				new PrintThis(editView);
			}
		};

		JButton saveButton = new GenericButton("save.png", "Save this entry.", saveAction);
		JButton cancelButton = new GenericButton("cancel.png", "Cancel all changes to this entry.", cancelAction);
		JButton printButton = new GenericButton("print.png", "Print this page.", printAction);

		this.setLayout(new ColumnLayout(0, 0));
		add("3x", saveButton);
		add("", cancelButton);
		add("w", printButton);
	}
}
