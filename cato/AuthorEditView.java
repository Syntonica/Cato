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

public class AuthorEditView extends GenericEditView
{
	private static AuthorEditView instance = null;
	public AuthorEntry authorEntry;
	private JLabel nameLabel = new JLabel("Name:", SwingConstants.RIGHT);
	public  JTextField nameTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JLabel realNameLabel = new JLabel("Real Name:", SwingConstants.RIGHT);
	private JTextField realNameTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JLabel biographyLabel = new JLabel("Biography:", SwingConstants.RIGHT);
	private JTextArea biographyTextArea = new GenericTextArea(true);
	private JScrollPane authorEditScrollPane = new JScrollPane(biographyTextArea);
	private GenericEditButtonPanel buttonPanel = new GenericEditButtonPanel(this);

	public static AuthorEditView getInstance()
	{
		if (instance == null)
		{
			instance = new AuthorEditView();
		}
		return instance;
	}

	public AuthorEditView()
	{
		initComponents();
		Constants.windowNames.put(this, "Author Edit");
		Constants.windowIcons.put(this, "author.png");
	}

	public void setupEntry(int id)
	{
		if (id == -1)
		{
			authorEntry = new AuthorEntry();
		}
		else
		{
			authorEntry = new AuthorEntry(id);
		}
		nameTextField.setText(authorEntry.name);
		realNameTextField.setText(authorEntry.realName);
		biographyTextArea.setText(authorEntry.biography);
		biographyTextArea.setCaretPosition(0);
	}

	public boolean saveEntry()
	{
		if (nameTextField.getText().trim().equals(""))
		{
			JOptionPane.showMessageDialog(BookEditView.getInstance(), "Author name cannot be blank.");
			return false;
		}
		authorEntry.name = nameTextField.getText().trim();
		authorEntry.realName = realNameTextField.getText().trim();
		authorEntry.biography = biographyTextArea.getText().trim();
		authorEntry.saveAuthorEntry();
		BookEditView.getInstance().updateAuthors();
		return true;
	}

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
						nameTextField.requestFocusInWindow();
					}
				});
			}
		});
		Constants.setTabs(biographyTextArea);
		biographyTextArea.setFont(Cato.catoFont);

		setLayout(new ColumnLayout(1, 1));
		add("w", buttonPanel);
		add("rx", nameLabel);
		add("hwx", nameTextField);
		add("rx", realNameLabel);
		add("hwx", realNameTextField);
		add("rtx", biographyLabel);
		add("hvwx", authorEditScrollPane);
	}
}
