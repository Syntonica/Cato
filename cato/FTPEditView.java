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
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class FTPEditView extends GenericEditView
{
	private JComboBox formatComboBox = new JComboBox();
	private JComboBox listComboBox = new JComboBox();
	private JLabel hostnameLabel = new JLabel("Hostname:", SwingConstants.RIGHT);
	private JLabel nameLabel = new JLabel("Name:", SwingConstants.RIGHT);
	private JLabel passwordLabel = new JLabel("Password:", SwingConstants.RIGHT);
	private JLabel uploadFormatLabel = new JLabel("Upload Format:", SwingConstants.RIGHT);
	private JLabel usernameLabel = new JLabel("Username:", SwingConstants.RIGHT);
	private JLabel listLabel = new JLabel("List:", SwingConstants.RIGHT);
	private JPasswordField passwordField = new JPasswordField();
	private JTextField hostnameTextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField nameTextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField usernameTextField = new GenericTextField(Constants.FIELD_ALL, false);
	private GenericEditButtonPanel buttonPanel = new GenericEditButtonPanel(this);

	private FTPEntry ftpEntry;

	private static FTPEditView instance = null;

	public static GenericEditView getInstance()
	{
		if (instance == null)
		{
			instance = new FTPEditView();
		}
		return instance;
	}

	public FTPEditView()
	{
		initComponents();
		Constants.windowNames.put(this, "FTP Edit");
		Constants.windowIcons.put(this, "ftp.png");
	}

	public void setupEntry(int id)
	{
		listComboBox.removeAllItems();
		listComboBox.addItem(SettingsView.getInstance().get("list1"));
		listComboBox.addItem(SettingsView.getInstance().get("list2"));
		listComboBox.addItem(SettingsView.getInstance().get("list3"));
		listComboBox.addItem(SettingsView.getInstance().get("list4"));
		listComboBox.addItem(SettingsView.getInstance().get("list5"));
		if (id == -1)
		{
			ftpEntry = new FTPEntry();
		}
		else
		{
			ftpEntry = new FTPEntry(id);
		}
		nameTextField.setText(ftpEntry.name);
		hostnameTextField.setText(ftpEntry.hostName);
		usernameTextField.setText(ftpEntry.userName);
		passwordField.setText(ftpEntry.password);
		listComboBox.setSelectedItem(ftpEntry.list);
		formatComboBox.setSelectedItem(ftpEntry.format);
	}

	public boolean saveEntry()
	{
		if (nameTextField.getText().trim().equals("") || hostnameTextField.getText().trim().equals("")
				|| passwordField.getPassword().length == 0
				|| ((String) listComboBox.getSelectedItem()).trim().equals("")
				|| usernameTextField.getText().trim().equals(""))
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(), "Fields may not be blank.");
			return false;
		}
		ftpEntry.name = nameTextField.getText();
		ftpEntry.hostName = hostnameTextField.getText();
		ftpEntry.userName = usernameTextField.getText();
		ftpEntry.password = new String(passwordField.getPassword());
		ftpEntry.list = (String) listComboBox.getSelectedItem();
		ftpEntry.format = (String) formatComboBox.getSelectedItem();
		ftpEntry.saveFTPEntry();
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
						nameTextField.requestFocusInWindow();
					}
				});
			}
		});
		formatComboBox.removeAllItems();
		ArrayList<String> macroNames = new ArrayList<String>();
		ArrayList<Integer> sn = DBFunctions.getInstance().selectAll("MAC", Constants.MACRO_NAME, "");
		for (int i : sn)
		{
			macroNames.add(DBFunctions.getInstance().get(i, Constants.MACRO_NAME));
		}
		Collections.sort(macroNames, String.CASE_INSENSITIVE_ORDER);
		formatComboBox.setModel(new DefaultComboBoxModel(macroNames.toArray()));

		listComboBox.setEditable(false);

		setLayout(new ColumnLayout(1, 1));
		add("w", buttonPanel);
		add("rx", nameLabel);
		add("xwh", nameTextField);
		add("rx", hostnameLabel);
		add("xwh", hostnameTextField);
		add("rx", usernameLabel);
		add("xwh", usernameTextField);
		add("rx", passwordLabel);
		add("xwh", passwordField);
		add("rx", listLabel);
		add("xwh", listComboBox);
		add("rx", uploadFormatLabel);
		add("xwh", formatComboBox);
	}
}
