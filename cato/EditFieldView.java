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
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public class EditFieldView extends GenericEditView
{
	public JTextArea mainTextArea = new GenericTextArea(true);
	public JTextComponent gtf = null;
	private JButton saveButton = new GenericButton("save.png", "Save changes.", null);
	private JButton cancelButton = new GenericButton("cancel.png", "Abandon changes.", null);
	private JButton minusButton = new GenericButton("minus.png", "Make Font Size Smaller", null);
	private JButton plusButton = new GenericButton("plus.png", "Make Font Size Larger", null);
	private int fontSize = 0;
	private JScrollPane mainScrollPane = new JScrollPane();

	private static EditFieldView instance = null;

	public static EditFieldView getInstance()
	{
		if (instance == null)
		{
			instance = new EditFieldView();
		}
		return instance;
	}

	public EditFieldView()
	{
		initComponents();
		Constants.windowNames.put(this, "Edit Field");
		Constants.windowIcons.put(this, "edit.png");
	}

	public void open(JTextComponent c)
	{
		gtf = c;
		mainTextArea.setText(c.getText());
		mainTextArea.setCaretPosition(mainTextArea.getText().length());
		String size = SettingsView.getInstance().get("textSize");
		fontSize = (size.equals("")) ? Cato.textSize + 10 : Integer.parseInt(size);
		int style = Cato.userFont.getStyle();
		mainTextArea.setFont(Cato.userFont.deriveFont(style, fontSize));
		BooksView.getInstance().tabbedPane.openTab(this);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				mainTextArea.requestFocusInWindow();
			}
		});
	}

	public void setupEntry(int id)
	{}

	public void listBuild()
	{}

	public void saveAndExit()
	{ // need this to avoid closeable tab being closed twice when xing tab out
		if (saveEntry()) cancelEntry();
	}

	public boolean saveEntry()
	{
		gtf.setText(mainTextArea.getText());
		return true;
	}

	public void cancelEntry()
	{
		BooksView.getInstance().tabbedPane.closeTab(EditFieldView.this);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				gtf.setCaretPosition(gtf.getText().length());
				gtf.requestFocusInWindow();
				gtf = null;
			}
		});
	}

	public void initComponents()
	{
		mainTextArea.setColumns(20);
		mainTextArea.setRows(5);
		mainTextArea.setFocusCycleRoot(true);
		mainTextArea.setVerifyInputWhenFocusTarget(false);

		mainScrollPane.setViewportView(mainTextArea);

		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				cancelEntry();
			}
		});

		saveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				saveAndExit();
			}
		});

		plusButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				fontSize += 2;
				int style = Cato.userFont.getStyle();
				mainTextArea.setFont(Cato.userFont.deriveFont(style, fontSize));
				SettingsView.getInstance().setNoLog("textSize", Integer.toString(fontSize));
			}
		});

		minusButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				fontSize -= 2;
				if (fontSize < Cato.textSize) fontSize = Cato.textSize;
				int style = Cato.userFont.getStyle();
				mainTextArea.setFont(Cato.userFont.deriveFont(style, fontSize));
				SettingsView.getInstance().setNoLog("textSize", Integer.toString(fontSize));
			}
		});

		setLayout(new ColumnLayout(1, 1));
		add("4x", saveButton);
		add("", cancelButton);
		add("", minusButton);
		add("w", plusButton);
		add("hvwx", mainScrollPane);
	}
}
