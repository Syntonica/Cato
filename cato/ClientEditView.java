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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ClientEditView extends GenericEditView
{
	private JLabel address1Label = new JLabel("Address 1:", SwingConstants.RIGHT);
	private JLabel address2Label = new JLabel("Address 2:", SwingConstants.RIGHT);
	private JLabel address3Label = new JLabel("Address 3:", SwingConstants.RIGHT);
	private JLabel cityLabel = new JLabel("City:", SwingConstants.RIGHT);
	private JLabel clientIDLabel = new JLabel("Client ID:", SwingConstants.RIGHT);
	private JLabel commentsLabel = new JLabel("Comments:", SwingConstants.RIGHT);
	private JLabel contactNameLabel = new JLabel("Contact Name:", SwingConstants.RIGHT);
	private JLabel countryLabel = new JLabel("Country:", SwingConstants.RIGHT);
	private JLabel emailLabel = new JLabel("Email:", SwingConstants.RIGHT);
	private JLabel faxLabel = new JLabel("Fax:", SwingConstants.RIGHT);
	private JLabel nameLabel = new JLabel("Name:", SwingConstants.RIGHT);
	private JLabel phone1Label = new JLabel("Phone 1:", SwingConstants.RIGHT);
	private JLabel phone2Label = new JLabel("Phone 2:", SwingConstants.RIGHT);
	private JLabel postalCodeLabel = new JLabel("Postal Code:", SwingConstants.RIGHT);
	private JLabel stateLabel = new JLabel("State:", SwingConstants.RIGHT);

	private JScrollPane clientEditScrollPane = new JScrollPane();
	private JTextArea commentsTextArea = new GenericTextArea(true);
	private JTextField address1TextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField address2TextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField address3TextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField cityTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField clientIDTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField contactNameTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField countryTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField emailTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField faxTextField = new GenericTextField(Constants.FIELD_ALL, true);
	public  JTextField nameTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField phone1TextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField phone2TextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField postalCodeTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField stateTextField = new GenericTextField(Constants.FIELD_ALL, true);

	public ClientEntry clientEntry;
	public String caller = "";
	private GenericEditButtonPanel buttonPanel = new GenericEditButtonPanel(this);

	private static ClientEditView instance = null;

	public static ClientEditView getInstance()
	{
		if (instance == null)
		{
			instance = new ClientEditView();
		}
		return instance;
	}

	public ClientEditView()
	{
		initComponents();
		Constants.windowNames.put(this, "Client Edit");
		Constants.windowIcons.put(this, "client.png");
	}

	public void setupEntry(int id)
	{
		if (id == -1)
		{
			clientEntry = new ClientEntry();
		}
		else
		{
			clientEntry = new ClientEntry(id);
		}
		clientIDTextField.setText(clientEntry.id);
		nameTextField.setText(clientEntry.name);
		address1TextField.setText(clientEntry.address1);
		address2TextField.setText(clientEntry.address2);
		address3TextField.setText(clientEntry.address3);
		cityTextField.setText(clientEntry.city);
		postalCodeTextField.setText(clientEntry.postalCode);
		phone1TextField.setText(clientEntry.phone1);
		phone2TextField.setText(clientEntry.phone2);
		faxTextField.setText(clientEntry.fax);
		emailTextField.setText(clientEntry.email);
		contactNameTextField.setText(clientEntry.contact);
		stateTextField.setText(clientEntry.state);
		countryTextField.setText(clientEntry.country);
		commentsTextArea.setText(clientEntry.comments);
	}

	public boolean saveEntry()
	{
		String n = nameTextField.getText().trim();
		if (n.equals(""))
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(), "Client name cannot be blank.");
			return false;
		}
		if (clientEntry.id.equals("-1"))
		{
			int i = DBFunctions.getInstance().selectOne("CLI", Constants.CLIENT_NAME, n);
			if (i != -1)
			{
				JOptionPane.showMessageDialog(BooksView.getInstance(), "Client '" + n + "' already exists.");
				return false;
			}
		}
		clientEntry.name = nameTextField.getText();
		clientEntry.address1 = address1TextField.getText();
		clientEntry.address2 = address2TextField.getText();
		clientEntry.address3 = address3TextField.getText();
		clientEntry.city = cityTextField.getText();
		clientEntry.postalCode = postalCodeTextField.getText();
		clientEntry.phone1 = phone1TextField.getText();
		clientEntry.phone2 = phone2TextField.getText();
		clientEntry.fax = faxTextField.getText();
		clientEntry.email = emailTextField.getText();
		clientEntry.contact = contactNameTextField.getText();
		clientEntry.state = stateTextField.getText();
		clientEntry.country = countryTextField.getText();
		clientEntry.comments = commentsTextArea.getText();
		clientEntry.saveClientEntry();
		if (caller.equals("invoice"))
		{
			InvoiceEditView.getInstance().clientListBuild();
			InvoiceEditView.getInstance().clientComboBox.setText(ClientEditView.getInstance().nameTextField.getText());
			InvoiceEditView.getInstance().clientAddressBuild();
		}
		else if (caller.equals("wants"))
		{
			WantEditView.getInstance().clientListBuild();
			WantEditView.getInstance().clientComboBox.setText(ClientEditView.getInstance().nameTextField.getText());
		}
		caller = "";
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
		nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		postalCodeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		clientIDLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		address3Label.setHorizontalAlignment(SwingConstants.RIGHT);
		commentsTextArea.setRows(5);

		Constants.setTabs(commentsTextArea);

		clientEditScrollPane.setViewportView(commentsTextArea);

		JPanel leftPane = new JPanel(new ColumnLayout(0, 0));
		leftPane.add("rx", nameLabel);
		leftPane.add("hwx", nameTextField);
		leftPane.add("rx", address1Label);
		leftPane.add("hwx", address1TextField);
		leftPane.add("rx", address2Label);
		leftPane.add("hwx", address2TextField);
		leftPane.add("rx", address3Label);
		leftPane.add("hwx", address3TextField);
		leftPane.add("rx", cityLabel);
		leftPane.add("hwx", cityTextField);
		leftPane.add("rx", stateLabel);
		leftPane.add("hwx", stateTextField);


		JPanel rightPane = new JPanel(new ColumnLayout(0, 0));
		rightPane.add("rx", phone1Label);
		rightPane.add("hwx", phone1TextField);
		rightPane.add("rx", phone2Label);
		rightPane.add("hwx", phone2TextField);
		rightPane.add("rx", faxLabel);
		rightPane.add("hwx", faxTextField);
		rightPane.add("rx", emailLabel);
		rightPane.add("hwx", emailTextField);
		rightPane.add("rx", contactNameLabel);
		rightPane.add("hwx", contactNameTextField);
		rightPane.add("rx", postalCodeLabel);
		rightPane.add("hwx", postalCodeTextField);
		rightPane.add("rx", countryLabel);
		rightPane.add("hwx", countryTextField);

		JPanel bottomPanel = new JPanel(new ColumnLayout(0, 0));
		bottomPanel.add("thx", leftPane);
		bottomPanel.add("hwx", rightPane);

		setLayout(new ColumnLayout(1, 1));
		add("w", buttonPanel);
		add("hxw", bottomPanel);
		add("wx", commentsLabel);
		add("wxhv", clientEditScrollPane);
	}
}
