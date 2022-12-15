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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class NotepadView extends JPanel
{
	public JTextArea mainTextArea = new GenericTextArea(true);
	private JButton openButton = new GenericButton("open.png", "Open a Notepad.", null);
	private JButton newButton = new GenericButton("add.png", "Create a new Notepad.", null);
	private JButton deleteButton = new GenericButton("delete.png", "Delete this Notepad.", null);
	private JButton printButton = new GenericButton("print.png", "Print this Notepad.", null);
	public JLabel textFileNameLabel = new JLabel();
	private JScrollPane mainScrollPane = new JScrollPane();
	private String currentText = "Read Me";

	private static NotepadView instance = null;

	public static NotepadView getInstance()
	{
		if (instance == null)
		{
			instance = new NotepadView();
		}
		return instance;
	}

	public NotepadView()
	{
		initComponents();
		Constants.windowNames.put(this, "Notepad");
		Constants.windowIcons.put(this, "note.png");
	}

	public void showReadMe(String readMe)
	{
		int i = DBFunctions.getInstance().selectOne("NPD", Constants.NOTEPAD_NAME, readMe);
		mainTextArea.setText(DBFunctions.getInstance().get(i, Constants.NOTEPAD_TEXT));
		textFileNameLabel.setText("  File: " + readMe);
		mainTextArea.setCaretPosition(mainTextArea.getText().length());
		currentText = readMe;
	}

	public void openNotepad()
	{
		ScrollablePopupMenu popup = new ScrollablePopupMenu();
		ArrayList<String> noteNames = new ArrayList<String>();
		ArrayList<Integer> sn = DBFunctions.getInstance().selectAll("NPD", Constants.MACRO_NAME, "");
		for (int i : sn)
		{
			noteNames.add(DBFunctions.getInstance().get(i, Constants.NOTEPAD_NAME));
		}
		Collections.sort(noteNames, String.CASE_INSENSITIVE_ORDER);

		popup.setMaximumVisibleRows(20);
		popup.setFocusable(false);
		for (String s : noteNames)
		{
			JMenuItem mi = new JMenuItem(s);
			mi.setBackground(Cato.whiteAndBlack);
			mi.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					saveNotepad();
					showReadMe(ae.getActionCommand());
				}
			});
			popup.add(mi);
		}
		int x = openButton.getX();
		int y = openButton.getY();
		y = y + openButton.getHeight();
		popup.show(openButton, x, y);
	}

	public void saveNotepad()
	{
		int i = DBFunctions.getInstance().selectOne("NPD", Constants.NOTEPAD_NAME, currentText);
		String[] u = { "NPD", currentText, mainTextArea.getText() };
		DBFunctions.getInstance().updateNoLog(i, u);
	}

	public void newNotepad()
	{
		saveNotepad();
		String result = JOptionPane.showInputDialog(BooksView.getInstance(), "Name of new file:");
		if ((result != null) && (result.length() > 0))
		{
			int i = DBFunctions.getInstance().selectOne("NPD", Constants.NOTEPAD_NAME, result);
			if (i == -1)
			{
				String[] ins = { "NPD", result, "" };
				DBFunctions.getInstance().update(-1, ins);
				Constants.writeBlog(ins.toString());
				showReadMe(result);
			}
			else
			{
				JOptionPane.showMessageDialog(BooksView.getInstance(), "Name already in use.");
			}
		}
	}

	public void deleteNotepad()
	{
		if (!currentText.equals("Read Me"))
		{
			if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(BooksView.getInstance(),
					"Delete " + currentText + "?", "Deleting", JOptionPane.OK_CANCEL_OPTION))
			{
				int i = DBFunctions.getInstance().selectOne("NPD", Constants.NOTEPAD_NAME, currentText);
				Constants.writeBlog("Remove Notepad: " + Arrays.toString(DBFunctions.getInstance().getRecord(i)));
				DBFunctions.getInstance().remove(i);
				showReadMe("Read Me");
			}
		}
	}

	public void printNotepad()
	{
		new PrintThis(this);
	}

	public void initComponents()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent e)
			{
				mainTextArea.requestFocusInWindow();
			}
		});

		mainTextArea.setColumns(20);
		mainTextArea.setRows(5);
		mainTextArea.setFocusCycleRoot(true);
		mainTextArea.setVerifyInputWhenFocusTarget(false);
		mainTextArea.setFont(Cato.catoFont);
		mainTextArea.addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent evt)
			{}

			public void focusLost(FocusEvent evt)
			{
				saveNotepad();
			}
		});

		mainScrollPane.setViewportView(mainTextArea);

		textFileNameLabel.setPreferredSize(new Dimension(200, Cato.compHeight));
		textFileNameLabel.setFont(Cato.catoFont);

		openButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				openNotepad();
			}
		});

		newButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				newNotepad();
			}
		});

		deleteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				deleteNotepad();
			}
		});

		textFileNameLabel.setHorizontalAlignment(SwingConstants.LEFT);

		printButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				printNotepad();
			}
		});

		setLayout(new ColumnLayout(1, 1));
		add("5x", openButton);
		add("", newButton);
		add("", printButton);
		add("", deleteButton);
		add("w", textFileNameLabel);
		add("hvwx", mainScrollPane);
	}
}
