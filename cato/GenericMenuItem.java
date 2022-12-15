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

import java.awt.event.InputEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

final class GenericMenuItem extends JMenuItem
{
	public GenericMenuItem(String text, int key, boolean shift, String ic)
	{
		setText(text);
		setIcon(new ImageIcon(getClass().getClassLoader().getResource("resources/" + ic)));
		setFont(Cato.catoFont);
		setBorder(new EmptyBorder(TOP, LEFT, BOTTOM, RIGHT));
		if (shift)
		{
			setAccelerator(KeyStroke.getKeyStroke(key, Constants.modifierKey | InputEvent.SHIFT_MASK));
		}
		else
		{
			setAccelerator(KeyStroke.getKeyStroke(key, Constants.modifierKey));
		}
	}
}