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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GenericTextArea extends JTextArea
{
	String originalEntry = "";
	boolean check = false;
	public int oldCaret = 0;

	public GenericTextArea(boolean c)
	{
		this.check = c;
		setFont(Cato.userFont);
		setLineWrap(true);
		setWrapStyleWord(true);

		getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				SpellCheck.getInstance().runSpellCheck(GenericTextArea.this);
			}

			public void removeUpdate(DocumentEvent e)
			{
				SpellCheck.getInstance().runSpellCheck(GenericTextArea.this);
			}

			public void changedUpdate(DocumentEvent e)
			{}
		});

		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if ((e.getButton() == MouseEvent.BUTTON3) || e.isControlDown())
				{
					SpellCheck.getInstance().makeSuggestions(e);
				}
			}
		});

		addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent evt)
			{
				originalEntry = getText();
			}

			public void focusLost(FocusEvent evt)
			{
				setCaretPosition(0);
			}
		});

		addKeyListener(new KeyAdapter()
		{
			public void keyTyped(final KeyEvent evt)
			{
				if (GenericTextArea.this.getText().length() > Constants.textAreaSize)
				{
					evt.consume();
				}
				if (evt.getKeyChar() == '|')
				{
					evt.consume();
				}
			}

			public void keyPressed(final KeyEvent evt)
			{
				if (evt.getModifiers() == Constants.modifierKey) return;
				switch (evt.getKeyCode())
				{
					case KeyEvent.VK_ESCAPE:
					{
						if (EditFieldView.getInstance().mainTextArea == GenericTextArea.this)
						{
							EditFieldView.getInstance().cancelEntry();
						}
						else
						{
							setText(originalEntry);
							setCaretPosition(getText().length());
						}
						evt.consume();
						break;
					}
					case KeyEvent.VK_F5:
					{
						if (EditFieldView.getInstance().mainTextArea == GenericTextArea.this)
						{
							EditFieldView.getInstance().saveAndExit();
						}
						else
						{
							EditFieldView.getInstance().open(GenericTextArea.this);
						}
						evt.consume();
						break;
					}
					default:
						break;
				}
			}
		});
	}
}
