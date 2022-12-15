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

/* Can be set to uneditable if needed
*  Requires a JFrame as a parent, i.e. has a LayeredPane to draw the popup on
*  Makes it no good in tight spaces
*  automatically has mouse hooks into Spellcheck
*  Extends JTextField both for looks and ease of use
*  Not all JComboBox amenities have been implemented
*  Popup version still misbehaves, q.v.
*/

package cato;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class GenericComboBox extends JTextField
{
	final JList jlist = new JList();
	JLayeredPane layeredPane = null;
	JScrollPane scrollPane = new JScrollPane(jlist);
	ArrayList<String> fullArray = new ArrayList<String>();
	ArrayList<String> miniArray = new ArrayList<String>();
	String originalEntry = "";

	public int oldCaret;

	public GenericComboBox()
	{
		this("");
	}

	public GenericComboBox(String name)
	{
		setName(name);
		jlist.setFont(Cato.catoFont);
		jlist.setFixedCellHeight(Cato.textSize + 6);
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlist.setFocusable(false);
		jlist.setBackground(Cato.whiteAndBlack);
		jlist.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				int index = jlist.locationToIndex(e.getPoint());
				if (index > -1)
				{
					jlist.setSelectedIndex(index);
					fillField();
				}
				showPopup(false);
			}
		});

		jlist.addMouseMotionListener(new MouseAdapter()
		{
			public void mouseMoved(MouseEvent e)
			{
				int index = jlist.locationToIndex(e.getPoint());
				if (index > -1)
				{
					jlist.setSelectedIndex(index);
				}
			}
		});

		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVisible(false);
		scrollPane.setFocusable(false);

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
				else if (e.getButton() == MouseEvent.BUTTON1)
				{
					if ((e.getX() > getWidth() - getHeight() + 8) && !popupIsShowing())
					{
						jlist.setListData(fullArray.toArray());
						showPopup(true);
						jlist.setSelectedValue(getText(), true);
						centerLineInScrollPane();
					}
					else
					{
						showPopup(false);
					}
				}
			}
		});

		getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				SpellCheck.getInstance().runSpellCheck(GenericComboBox.this);
			}

			public void removeUpdate(DocumentEvent e)
			{
				SpellCheck.getInstance().runSpellCheck(GenericComboBox.this);
			}

			public void changedUpdate(DocumentEvent e)
			{}
		});

		addKeyListener(new KeyAdapter()
		{
			public void keyTyped(final KeyEvent evt)
			{
				updatePopup((evt.getKeyChar() == KeyEvent.VK_ENTER) || (evt.getKeyChar() == KeyEvent.VK_ESCAPE));
				if ("|".contains(evt.getKeyChar() + "")) evt.consume();
			}
		});

		String cancelName = "cancel";
		InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
		ActionMap actionMap = getActionMap();
		actionMap.put(cancelName, new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				setText(originalEntry);
				setCaretPosition(getText().length());
				showPopup(false);
			}
		});

		String arrowUpName = "arrowUp";
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), arrowUpName);
		actionMap.put(arrowUpName, new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (popupIsShowing())
				{
					int idx = Constants.clamp(jlist.getSelectedIndex() - 1, 0, jlist.getModel().getSize() - 1);
					jlist.setSelectedIndex(idx);
					jlist.ensureIndexIsVisible(idx);
					fillField();
				}
				else
				{
					updatePopup(false);
				}
			}
		});

		String arrowDownName = "arrowDown";
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), arrowDownName);
		actionMap.put(arrowDownName, new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (popupIsShowing())
				{
					int idx = Constants.clamp(jlist.getSelectedIndex() + 1, 0, jlist.getModel().getSize() - 1);
					jlist.setSelectedIndex(idx);
					jlist.ensureIndexIsVisible(idx);
					fillField();
				}
				else
				{
					updatePopup(false);
				}
			}
		});

		String f5 = "f5";
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), f5);
		actionMap.put(f5, new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				EditFieldView.getInstance().open((JTextComponent) GenericComboBox.this);
			}
		});

		String enter = "enter";
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), enter);
		actionMap.put(enter, new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				fillField();
				showPopup(false);
			}
		});

		addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent evt)
			{
				originalEntry = getText();
			}

			public void focusLost(FocusEvent e)
			{
				jlist.setListData(fullArray.toArray());
				setCaretPosition(0);
				showPopup(false);
			}
		});
	}

	public void centerLineInScrollPane()
	{
		JViewport viewport = scrollPane.getViewport();
		if (viewport != null)
		{
			int idx = jlist.getSelectedIndex() < 0 ? 0 : jlist.getSelectedIndex();
			Rectangle r = jlist.getCellBounds(idx, idx);
			if (r != null)
			{
				int extentHeight = viewport.getExtentSize().height;
				int viewHeight = viewport.getViewSize().height;
				int y = Math.max(0, r.y - (extentHeight / 2));
				y = Math.min(y, viewHeight - extentHeight);
				viewport.setViewPosition(new Point(0, y));
			}
		}
	}

	public void paint(Graphics g)
	{ // need notPrinting flag since drawing on Prints is not supported
		super.paint(g);
	//	if (Constants.printing) return;
		int x = getWidth() - getHeight() / 2;
		int xx = (getHeight() - 8) / 2;
		g.setColor(new Color(128, 128, 128));
		g.setXORMode(new Color(255, 255, 255));
		for (int y = 8; y < getHeight() - 8; y++)
		{
			g.drawLine(x - xx + y / 2, y, x + xx - y / 2, y);
		}
	}

	public int getItemCount()
	{
		return jlist.getModel().getSize();
	}

	public Object getItemAt(int idx)
	{
		return jlist.getModel().getElementAt(idx);
	}

	public void setModel(ArrayList<String> array)
	{
		fullArray.clear();
		for (int i = 0; i < array.size(); i++)
		{
			if (!fullArray.contains(array.get(i)))
			{
				fullArray.add(array.get(i));
			}
		}
		Collections.sort(fullArray, String.CASE_INSENSITIVE_ORDER);
		jlist.setListData(fullArray.toArray());
	}

	public ArrayList<String> getModel()
	{
		return fullArray;
	}

	public boolean isValueInList()
	{
		return fullArray.contains(getText());
	}

	public void fillField()
	{
		if (jlist.getSelectedIndex() != -1)
		{
			setText((String) jlist.getSelectedValue());
		}
		setCaretPosition(getText().length());
	}

	public void updatePopup(final boolean enter)
	{ // must ALWAYS be invoked later. ALWAYS.
		if (enter)
		{
			return;
		}
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				String entry1 = getText();
				miniArray.clear();
				if (entry1.equals(""))
				{
					jlist.setListData(fullArray.toArray());
				}
				else
				{
					String entry2 = entry1.toLowerCase();
					for (String s : fullArray)
					{
						if (s.toLowerCase().startsWith(entry2))
						{
							miniArray.add(s);
						}
					}
					jlist.setListData(miniArray.toArray());
				}
				if (jlist.getModel().getSize() > 0)
				{
					showPopup(true);
					if ((miniArray.size() > 0) && miniArray.get(0).toLowerCase().equals(entry1.toLowerCase()))
					{
						jlist.setSelectedValue(miniArray.get(0), false);
					}
				}
				else
				{
					showPopup(false);
				}
			}
		});
	}

	public void showPopup(boolean on)
	{
		if (layeredPane == null)
		{
			JRootPane c = this.getRootPane();
			layeredPane = c.getLayeredPane();
			layeredPane.add(scrollPane, JLayeredPane.POPUP_LAYER);
		}
		if (on)
		{
			jlist.setFixedCellWidth(getWidth() - 2);
			int height = Constants.clamp(jlist.getModel().getSize(), 1, 10);
			int newX = getWidth();
			int newY = jlist.getFixedCellHeight() * height;
			scrollPane.setSize(new Dimension(newX, newY));
			Point newPoint = SwingUtilities.convertPoint(this, 0,  getHeight() - 3, layeredPane);
			if (newPoint.y + newY > layeredPane.getHeight())
			{
				newPoint.y = newPoint.y - newY - getHeight() + 6;
			}
			scrollPane.setLocation(newPoint);
		}
		scrollPane.setVisible(on);
	}

	public boolean popupIsShowing()
	{
		return scrollPane.isVisible();
	}
}
