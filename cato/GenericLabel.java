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

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

final class GenericLabel extends JLabel
{
	public GenericLabel(String text)
	{
		setText(text);
		setName(text.substring(0, text.length() - 1));
		setFont(Cato.catoFont);
		setForeground(Cato.naturalColor);
		setFocusable(false);
		setMinimumSize(new Dimension(70, Cato.compHeight));
		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				JLabel l = (JLabel) evt.getComponent();
				if (l.getForeground().equals(Cato.naturalColor))
				{
					l.setForeground(Cato.hiliteColor);
					if (l.getName().equals("Catalog"))
					{
						BookEditView.getInstance().catalog2Label.setForeground(Cato.hiliteColor);
						BookEditView.getInstance().catalog3Label.setForeground(Cato.hiliteColor);
					}
				}
				else
				{
					l.setForeground(Cato.naturalColor);
					if (l.getName().equals("Catalog"))
					{
						BookEditView.getInstance().catalog2Label.setForeground(Cato.naturalColor);
						BookEditView.getInstance().catalog3Label.setForeground(Cato.naturalColor);
					}
				}
			}
		});
	}
}