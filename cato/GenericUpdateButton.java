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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

final class GenericUpdateButton extends JButton
{
	public GenericUpdateButton(final GenericComboBox cb, final String text, final String tag)
	{
		setIcon(new ImageIcon(getClass().getClassLoader().getResource("resources/pin.png")));
		setFocusable(false);
		setPreferredSize(new Dimension(Cato.compHeight, Cato.compHeight));
		setToolTipText("Update " + text + "s.");
		if (tag.equals("AUT"))
		{
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					BookEditView.getInstance().updateAuthor(cb);
				}
			});
		}
		else if (tag.equals("PUB"))
		{
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					BookEditView.getInstance().updatePublisher();
				}
			});
		}
		else if (tag.equals("CAT"))
		{
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					GenericComboBox cb2 = cb;
					if (BookEditView.getInstance().catalog2ComboBox.getForeground().equals(Color.red))
					{
						cb2 = BookEditView.getInstance().catalog2ComboBox;
					}
					else if (BookEditView.getInstance().catalog3ComboBox.getForeground().equals(Color.red))
					{
						cb2 = BookEditView.getInstance().catalog3ComboBox;
					}
					ListEditView.getInstance().showListEditWindow(cb2, text, tag);
				}
			});
		}
		else
		{
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					ListEditView.getInstance().showListEditWindow(cb, text, tag);
				}
			});
		}
	}
}
