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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MacroEditView extends GenericEditView
{
	private JLabel nameLabel = new JLabel("Macro Name:", SwingConstants.RIGHT);
	private JLabel textLabel = new JLabel("Macro Text:", SwingConstants.RIGHT);
	private JTextArea macroTextArea = new GenericTextArea(false);
	private JScrollPane jScrollPane1 = new JScrollPane(macroTextArea);
	private JTextField macroNameTextField = new GenericTextField(Constants.FIELD_ALL, false);
	private GenericEditButtonPanel buttonPanel = new GenericEditButtonPanel(this);

	private MacroEntry macroEntry;

	private static MacroEditView instance = null;

	public static MacroEditView getInstance()
	{
		if (instance == null)
		{
			instance = new MacroEditView();
		}
		return instance;
	}

	public MacroEditView()
	{
		initComponents();
		Constants.windowNames.put(this, "Macro Edit");
		Constants.windowIcons.put(this, "macro.png");
	}

	public void setupEntry(int id)
	{
		// if id <> -1 retrieve data and enter into fields
		if (id == -1)
		{
			macroEntry = new MacroEntry();
		}
		else
		{
			macroEntry = new MacroEntry(id);
		}
		macroNameTextField.setText(macroEntry.name);
		macroTextArea.setText(macroEntry.text);
		macroNameTextField.setCaretPosition(0);
		macroTextArea.setCaretPosition(0);
	}

	public boolean saveEntry()
	{
		if (macroNameTextField.getText().equals(""))
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(), "Macro name must not be blank.");
			return false;
		}
		macroEntry.name = macroNameTextField.getText();
		macroEntry.text = macroTextArea.getText();
		macroEntry.saveMacroEntry();
		return true;
	}

	public void listBuild()
	{}

	protected void initComponents()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent e)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						macroNameTextField.requestFocusInWindow();
					}
				});
			}
		});
		macroTextArea.setTabSize(3);
		macroTextArea.setFont(Cato.catoFont);

		setLayout(new ColumnLayout(1, 1));
		add("w", buttonPanel);
		add("xr", nameLabel);
		add("hwx", macroNameTextField);
		add("trx", textLabel);
		add("hvwx", jScrollPane1);
	}
}
