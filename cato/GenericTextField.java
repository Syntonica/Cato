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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class GenericTextField extends JTextField
{
	int type = Constants.FIELD_ALL;
	public int oldCaret = 0;
	boolean check = false;
	String originalEntry = "";

	public GenericTextField(final int type, final boolean check)
	{
		this.type = type;
		this.check = check;
		setFont(Cato.userFont);
		setPreferredSize(new Dimension(200, Cato.compHeight));

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

		getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				if (check)
				{
					SpellCheck.getInstance().runSpellCheck(GenericTextField.this);
				}
			}

			public void removeUpdate(DocumentEvent e)
			{
				if (check)
				{
					SpellCheck.getInstance().runSpellCheck(GenericTextField.this);
				}
			}

			public void changedUpdate(DocumentEvent e)
			{}
		});

		addKeyListener(new KeyAdapter()
		{
			public void keyTyped(final KeyEvent evt)
			{
				if (GenericTextField.this.getText().length() > Constants.textFieldSize)
				{
					evt.consume();
				}
				else
				{
					String key = evt.getKeyChar() + "";
					switch (type)
					{
						case Constants.FIELD_ALL:
							if ("|".contains(key)) evt.consume();
							break;
						case Constants.FIELD_NUMBER:
							if (!"0123456789".contains(key)) evt.consume();
							break;
						case Constants.FIELD_CURRENCY:
							if (!"0123456789-.".contains(key)) evt.consume();
							break;
						case Constants.FIELD_ISBN:
							if (!"0123456789xX".contains(key)) evt.consume();
							break;
						case Constants.FIELD_FILENAME:
							key = key.replaceAll("[^0-9A-Za-z_\\-]", "");
							if (key.equals("")) evt.consume();
							break;
						default:
							break;
					}
				}
			}

			public void keyPressed(final KeyEvent evt)
			{
				if (evt.getModifiers() == Constants.modifierKey)
				{
					return;
				}
				switch (evt.getKeyCode())
				{
					case KeyEvent.VK_ESCAPE:
					{ // escape
						setText(originalEntry);
						setCaretPosition(getText().length());
						evt.consume();
						break;
					}
					case KeyEvent.VK_F5:
					{
						EditFieldView.getInstance().open((JTextComponent) GenericTextField.this);
						evt.consume();
						break;
					}
					default:
						break;
				}
			}
		});

		addFocusListener(new FocusListener()
		{
			public void focusLost(FocusEvent evt)
			{
				if (type == Constants.FIELD_CURRENCY)
				{
					Constants.parseDollars(GenericTextField.this);
				}
				setCaretPosition(0);
			}

			public void focusGained(FocusEvent evt)
			{
					originalEntry = getText();
				if (type == Constants.FIELD_CURRENCY)
				{
					Constants.parseDollars(GenericTextField.this);
					setCaretPosition(getText().length());
				}
			}
		});
	}
}
