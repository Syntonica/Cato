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

/* settings available to the user:

"address1", "address2", "address3", "autoInc", "autoIncNumber", "city",
"closeTab", "country", "currency", "dateFormat", "email", "fax", "internetImage",
"isbnKey", "isbnUrl", "List 1", "List 2", "List 3", "List 4", "List 5",
"localImage", "monthlyCost1", "monthlyCost2", "monthlyCost3", "monthlyCost4",
"monthlyCost5", "paymentMethods", "percentage1", "percentage2", "percentage3",
"phone", "postalCode", "priceUrl", "publisherInfo", "showToolTips", "state",
"tax1", "tax2", "tax3", "taxID", "user1", "user2", "vendorName"

*/

package cato;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.*;

public class SettingsView extends JPanel
{
	private Action saveAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			saveEntry();
			BooksView.getInstance().tabbedPane.closeTab(SettingsView.this);
		}
	};

	private Action cancelAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			BooksView.getInstance().tabbedPane.closeTab(SettingsView.this);
		}
	};

	private Action importAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			importSettings();
		}
	};

	private Action exportAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			exportSettings();
		}
	};

	private static SettingsView instance = null;
	private JLabel address1Label = new JLabel("Address 1:");
	private JLabel address2Label = new JLabel("Address 2:");
	private JLabel autoIncNumberLabel = new JLabel("Next Book ID:");
	private JLabel cityLabel = new JLabel("City:");
	private JLabel countryLabel = new JLabel("Country:");
	private JLabel dummyLabel = new JLabel("");
	private JLabel emailLabel = new JLabel("Email:");
	private JLabel faxLabel = new JLabel("Fax:");
	private JLabel field1Label = new JLabel("Field 1:");
	private JLabel field2Label = new JLabel("Field 2:");
	private JLabel field3Label = new JLabel("Field 3:");
	private JLabel field4Label = new JLabel("Field 4:");
	private JLabel field5Label = new JLabel("Field 5:");
	private JLabel localImageLabel = new JLabel("Local Images:");
	private JLabel internetImageLabel = new JLabel("Internet Images:");
	private JLabel isbnAccessKeyLabel = new JLabel("ISBN Access Key:");
	private JLabel isbnCheckLabel = new JLabel("ISBN Lookup URL");
	private JLabel address3Label = new JLabel("Address 3:");
	private JLabel monthlyCostLabel = new JLabel("Monthly Cost:");
	private JLabel nameLabel = new JLabel("Name:");
	private JLabel percentageLabel = new JLabel("% :");
	private JLabel phoneLabel = new JLabel("Phone:");
	private JLabel postalCodeLabel = new JLabel("Postal Code:");
	private JLabel priceCheckURLLabel = new JLabel("Price Check URL:");
	private JLabel stateLabel = new JLabel("State:");
	private JLabel taxIDLabel = new JLabel("Tax ID:");
	private JLabel typeLabel = new JLabel("Type:");
	private JLabel userField1Label = new JLabel("User 1:");
	private JLabel userField2Label = new JLabel("User 2:");
	private JLabel listLabel = new JLabel("List Name:");
	private JPanel advancedPanel = new JPanel();
	private JPanel basicPanel = new JPanel();
	private JPanel booksellerDataPane = new JPanel();
	private JPanel formatsPanel = new JPanel();
	private JPanel miscPane = new JPanel();
	private JPanel paymentMethodsPanel = new JPanel();
	private JPanel taxesPanel = new JPanel();
	private JPanel userDefinedFieldsPane = new JPanel();
	private JPanel userNamedFieldsPanel = new JPanel();
	private JPanel listsPanel = new JPanel();
	private JScrollPane paymentMethodsScrollPanel = new JScrollPane();
	private JTabbedPane settingsTabbedPane = new JTabbedPane();
	private JButton pathSelectButton = new JButton();
	private JButton saveButton = new GenericButton("save.png", "Save SettingsView.", saveAction);
	private JButton cancelButton = new GenericButton("cancel.png", "Discard changes.", cancelAction);
	private JButton importButton = new GenericButton("down.png", "Import SettingsView.", importAction);
	private JButton exportButton = new GenericButton("up.png", "Export SettingsView.", exportAction);

	private JTextArea paymentMethodsTextArea = new GenericTextArea(false);
	private JTextField address1TextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField address2TextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField address3TextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField autoIncNumberTextField = new GenericTextField(Constants.FIELD_NUMBER, false);
	private JTextField cityTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField countryTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField emailTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField faxTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField localImageTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField internetImageTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField isbnAccessKeyTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField monthlyCost1TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	private JTextField monthlyCost2TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	private JTextField monthlyCost3TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	private JTextField monthlyCost4TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	private JTextField monthlyCost5TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	private JTextField nameTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField percentage1TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	private JTextField percentage2TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	private JTextField percentage3TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	private JTextField phoneTextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField postalCodeTextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField stateTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField taxIDTextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField type1TextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField type2TextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField type3TextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField userField1TextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField userField2TextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField list1TextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField list2TextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField list3TextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField list4TextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField list5TextField = new GenericTextField(Constants.FIELD_ALL, false);

	private JCheckBox autoIncrementCheckBox = new JCheckBox("Auto Increment");
	private JCheckBox toolTipCheckBox = new JCheckBox("Show All ToolTips");
	private JCheckBox publisherInfoCheckBox = new JCheckBox("Show Publisher Info as ToolTip");
	private JCheckBox closeTabCheckBox = new JCheckBox("Clicking X on Tab Saves Before Closing");

	private JComboBox currencyComboBox = new JComboBox(
			new DefaultComboBoxModel(new String[] { "$", "US$", "A$", "C$", "£", "€" }));
	private JComboBox dateComboBox = new JComboBox(new DefaultComboBoxModel(
			new String[] { "MM-dd-yyyy", "MM/dd/yyyy", "dd-MM-yyyy", "dd/MM/yyyy", "yyyy-MM-dd", "yyyy/MM/dd" }));
	public JComboBox isbnURLComboBox = new JComboBox();
	public JComboBox priceURLComboBox = new JComboBox();

	public static SettingsView getInstance()
	{
		if (instance == null)
		{
			instance = new SettingsView();
		}
		return instance;
	}

	public SettingsView()
	{
		initComponents();
		Constants.windowNames.put(this, "Settings");
		Constants.windowIcons.put(this, "settings.png");
	}

	/**
	 * **************************************************************** Setters and
	 * Getters
	 ******************************************************************
	 */
	public void populateFields()
	{
		nameTextField.setText(get("vendorName"));
		address1TextField.setText(get("address1"));
		address2TextField.setText(get("address2"));
		address3TextField.setText(get("address3"));
		cityTextField.setText(get("city"));
		stateTextField.setText(get("state"));
		countryTextField.setText(get("country"));
		postalCodeTextField.setText(get("postalCode"));
		phoneTextField.setText(get("phone"));
		faxTextField.setText(get("fax"));
		emailTextField.setText(get("email"));
		taxIDTextField.setText(get("taxID"));
		if (get("list1").equals(""))
		{
			list1TextField.setText("List 1");
		}
		else
		{
			list1TextField.setText(get("list1"));
		}
		if (get("list2").equals(""))
		{
			list2TextField.setText("List 2");
		}
		else
		{
			list2TextField.setText(get("list2"));
		}
		if (get("list3").equals(""))
		{
			list3TextField.setText("List 3");
		}
		else
		{
			list3TextField.setText(get("list3"));
		}
		if (get("list4").equals(""))
		{
			list4TextField.setText("List 4");
		}
		else
		{
			list4TextField.setText(get("list4"));
		}
		if (get("list5").equals(""))
		{
			list5TextField.setText("List 5");
		}
		else
		{
			list5TextField.setText(get("list5"));
		}
		monthlyCost1TextField.setText(get("monthlyCost1"));
		monthlyCost2TextField.setText(get("monthlyCost2"));
		monthlyCost3TextField.setText(get("monthlyCost3"));
		monthlyCost4TextField.setText(get("monthlyCost4"));
		monthlyCost5TextField.setText(get("monthlyCost5"));
		if (get("user1").equals(""))
		{
			userField1TextField.setText("User 1");
		}
		else
		{
			userField1TextField.setText(get("user1"));
		}
		if (get("user2").equals(""))
		{
			userField2TextField.setText("User 2");
		}
		else
		{
			userField2TextField.setText(get("user2"));
		}
		if (get("tax1").equals(""))
		{
			type1TextField.setText("Tax 1");
		}
		else
		{
			type1TextField.setText(get("tax1"));
		}
		if (get("tax2").equals(""))
		{
			type2TextField.setText("Tax 2");
		}
		else
		{
			type2TextField.setText(get("tax2"));
		}
		if (get("tax3").equals(""))
		{
			type3TextField.setText("Tax 3");
		}
		else
		{
			type3TextField.setText(get("tax3"));
		}
		percentage1TextField.setText(get("percentage1"));
		percentage2TextField.setText(get("percentage2"));
		percentage3TextField.setText(get("percentage3"));
		dateComboBox.setSelectedItem(get("dateFormat"));
		currencyComboBox.setSelectedItem(get("currency"));
		autoIncrementCheckBox.setSelected(Boolean.parseBoolean(get("autoInc")));
		autoIncNumberTextField.setText(get("autoIncNumber"));
		toolTipCheckBox.setSelected(Boolean.parseBoolean(get("showToolTips")));
		publisherInfoCheckBox.setSelected(Boolean.parseBoolean(get("publisherInfo")));
		closeTabCheckBox.setSelected(Boolean.parseBoolean(get("closeTab")));
		paymentMethodsTextArea.setText(get("paymentMethods"));
		localImageTextField.setText(get("localImage"));
		internetImageTextField.setText(get("internetImage"));
		isbnAccessKeyTextField.setText(get("isbnKey"));
		loadUrls();
		priceURLComboBox.setSelectedItem(get("priceUrl"));
		isbnURLComboBox.setSelectedItem(get("isbnUrl"));
	}

	public void loadUrls()
	{
		priceURLComboBox.removeAllItems();
		isbnURLComboBox.removeAllItems();
		ArrayList<Integer> i = DBFunctions.getInstance().selectAll("PLU", 0, "");
		for (int j : i)
		{
			priceURLComboBox.addItem(DBFunctions.getInstance().get(j, Constants.SETTING_KEY));
		}
		i = DBFunctions.getInstance().selectAll("ISN", 0, "");
		for (int j : i)
		{
			isbnURLComboBox.addItem(DBFunctions.getInstance().get(j, Constants.SETTING_KEY));
		}
	}

	public void saveEntry()
	{
		nameTextField.setText(nameTextField.getText().trim());
		int i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "vendorName");
		DBFunctions.getInstance().update(i, new String[] { "SET", "vendorName", nameTextField.getText() });

		address1TextField.setText(address1TextField.getText().trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "address1");
		DBFunctions.getInstance().update(i, new String[] { "SET", "address1", address1TextField.getText() });

		address2TextField.setText(address2TextField.getText().trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "address2");
		DBFunctions.getInstance().update(i, new String[] { "SET", "address2", address2TextField.getText() });

		address3TextField.setText(address3TextField.getText().trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "address3");
		DBFunctions.getInstance().update(i, new String[] { "SET", "address3", address3TextField.getText() });

		cityTextField.setText(cityTextField.getText().trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "city");
		DBFunctions.getInstance().update(i, new String[] { "SET", "city", cityTextField.getText() });

		stateTextField.setText(stateTextField.getText().trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "state");
		DBFunctions.getInstance().update(i, new String[] { "SET", "state", stateTextField.getText() });

		countryTextField.setText(countryTextField.getText().trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "country");
		DBFunctions.getInstance().update(i, new String[] { "SET", "country", countryTextField.getText() });

		postalCodeTextField.setText(postalCodeTextField.getText().trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "postalCode");
		DBFunctions.getInstance().update(i, new String[] { "SET", "postalCode", postalCodeTextField.getText() });

		phoneTextField.setText(phoneTextField.getText().trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "phone");
		DBFunctions.getInstance().update(i, new String[] { "SET", "phone", phoneTextField.getText() });

		faxTextField.setText(faxTextField.getText().trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "fax");
		DBFunctions.getInstance().update(i, new String[] { "SET", "fax", faxTextField.getText() });

		emailTextField.setText(emailTextField.getText().trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "email");
		DBFunctions.getInstance().update(i, new String[] { "SET", "email", emailTextField.getText() });

		taxIDTextField.setText(taxIDTextField.getText().trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "taxID");
		DBFunctions.getInstance().update(i, new String[] { "SET", "taxID", taxIDTextField.getText() });

		if (list1TextField.getText().trim().length() == 0)
		{
			list1TextField.setText("List 1");
		}
		else
		{
			list1TextField.setText(list1TextField.getText().trim());
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "list1");
		DBFunctions.getInstance().update(i, new String[] { "SET", "list1", list1TextField.getText() });

		if (list2TextField.getText().trim().length() == 0)
		{
			list2TextField.setText("List 2");
		}
		else
		{
			list2TextField.setText(list2TextField.getText().trim());
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "list2");
		DBFunctions.getInstance().update(i, new String[] { "SET", "list2", list2TextField.getText() });

		if (list3TextField.getText().trim().length() == 0)
		{
			list3TextField.setText("List 3");
		}
		else
		{
			list3TextField.setText(list3TextField.getText().trim());
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "list3");
		DBFunctions.getInstance().update(i, new String[] { "SET", "list3", list3TextField.getText() });

		if (list4TextField.getText().trim().length() == 0)
		{
			list4TextField.setText("List 4");
		}
		else
		{
			list4TextField.setText(list4TextField.getText().trim());
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "list4");
		DBFunctions.getInstance().update(i, new String[] { "SET", "list4", list4TextField.getText() });

		if (list5TextField.getText().trim().length() == 0)
		{
			list5TextField.setText("List 5");
		}
		else
		{
			list5TextField.setText(list5TextField.getText().trim());
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "list5");
		DBFunctions.getInstance().update(i, new String[] { "SET", "list5", list5TextField.getText() });

		if (monthlyCost1TextField.getText().length() == 0)
		{
			monthlyCost1TextField.setText("0.00");
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "monthlyCost1");
		DBFunctions.getInstance().update(i, new String[] { "SET", "monthlyCost1", monthlyCost1TextField.getText() });

		if (monthlyCost2TextField.getText().length() == 0)
		{
			monthlyCost2TextField.setText("0.00");
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "monthlyCost2");
		DBFunctions.getInstance().update(i, new String[] { "SET", "monthlyCost2", monthlyCost2TextField.getText() });

		if (monthlyCost3TextField.getText().length() == 0)
		{
			monthlyCost3TextField.setText("0.00");
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "monthlyCost3");
		DBFunctions.getInstance().update(i, new String[] { "SET", "monthlyCost3", monthlyCost3TextField.getText() });

		if (monthlyCost4TextField.getText().length() == 0)
		{
			monthlyCost4TextField.setText("0.00");
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "monthlyCost4");
		DBFunctions.getInstance().update(i, new String[] { "SET", "monthlyCost4", monthlyCost4TextField.getText() });

		if (monthlyCost5TextField.getText().length() == 0)
		{
			monthlyCost5TextField.setText("0.00");
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "monthlyCost5");
		DBFunctions.getInstance().update(i, new String[] { "SET", "monthlyCost5", monthlyCost5TextField.getText() });

		if (userField1TextField.getText().trim().length() == 0)
		{
			userField1TextField.setText("User 1");
		}
		else
		{
			userField1TextField.setText(userField1TextField.getText().trim());
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "user1");
		DBFunctions.getInstance().update(i, new String[] { "SET", "user1", userField1TextField.getText() });

		if (userField2TextField.getText().trim().length() == 0)
		{
			userField2TextField.setText("User 2");
		}
		else
		{
			userField2TextField.setText(userField2TextField.getText().trim());
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "user2");
		DBFunctions.getInstance().update(i, new String[] { "SET", "user2", userField2TextField.getText() });

		if (type1TextField.getText().trim().length() == 0)
		{
			type1TextField.setText("Tax 1");
		}
		else
		{
			type1TextField.setText(type1TextField.getText().trim());
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "tax1");
		DBFunctions.getInstance().update(i, new String[] { "SET", "tax1", type1TextField.getText() });

		if (type2TextField.getText().trim().length() == 0)
		{
			type2TextField.setText("Tax 2");
		}
		else
		{
			type2TextField.setText(type2TextField.getText().trim());
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "tax2");
		DBFunctions.getInstance().update(i, new String[] { "SET", "tax2", type2TextField.getText() });

		if (type3TextField.getText().trim().length() == 0)
		{
			type3TextField.setText("Tax 3");
		}
		else
		{
			type3TextField.setText(type3TextField.getText().trim());
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "tax3");
		DBFunctions.getInstance().update(i, new String[] { "SET", "tax3", type3TextField.getText() });

		if (percentage1TextField.getText().length() == 0)
		{
			percentage1TextField.setText("0");
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "percentage1");
		DBFunctions.getInstance().update(i, new String[] { "SET", "percentage1", percentage1TextField.getText() });

		if (percentage2TextField.getText().length() == 0)
		{
			percentage2TextField.setText("0");
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "percentage2");
		DBFunctions.getInstance().update(i, new String[] { "SET", "percentage2", percentage2TextField.getText() });

		if (percentage3TextField.getText().length() == 0)
		{
			percentage3TextField.setText("0");
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "percentage3");
		DBFunctions.getInstance().update(i, new String[] { "SET", "percentage3", percentage3TextField.getText() });

		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "dateFormat");
		DBFunctions.getInstance().update(i, new String[] { "SET", "dateFormat", (String) dateComboBox.getSelectedItem() });

		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "currency");
		DBFunctions.getInstance().update(i, new String[] { "SET", "currency", (String) currencyComboBox.getSelectedItem() });

		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "autoInc");
		DBFunctions.getInstance().update(i, new String[] { "SET", "autoInc", Boolean.toString(autoIncrementCheckBox.isSelected()) });

		if (autoIncNumberTextField.getText().trim().length() == 0)
		{
			autoIncNumberTextField.setText("0");
		}
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "autoIncNumber");
		DBFunctions.getInstance().update(i, new String[] { "SET", "autoIncNumber", autoIncNumberTextField.getText() });

		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "publisherInfo");
		DBFunctions.getInstance().update(i,
				new String[] { "SET", "publisherInfo", Boolean.toString(publisherInfoCheckBox.isSelected()) });

		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "closeTab");
		DBFunctions.getInstance().update(i, new String[] { "SET", "closeTab", Boolean.toString(closeTabCheckBox.isSelected()) });

		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "showToolTips");
		DBFunctions.getInstance().update(i, new String[] { "SET", "showToolTips", Boolean.toString(toolTipCheckBox.isSelected()) });
		if (toolTipCheckBox.isSelected())
		{
			ToolTipManager.sharedInstance().setEnabled(true);
		}
		else
		{
			ToolTipManager.sharedInstance().setEnabled(false);
		}

		/*
		 * paymentMethodsTextArea.setText(paymentMethodsTextArea.getText().trim()); i =
		 * DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "paymentMethods");
		 * DBFunctions.getInstance().update(i, new String[]{"SET", "paymentMethods",
		 * paymentMethodsTextArea.getText()});
		 */
		localImageTextField.setText(localImageTextField.getText().trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "localImage");
		DBFunctions.getInstance().update(i, new String[] { "SET", "localImage", localImageTextField.getText() });

		internetImageTextField.setText(internetImageTextField.getText().trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "internetImage");
		DBFunctions.getInstance().update(i, new String[] { "SET", "internetImage", internetImageTextField.getText() });

		priceURLComboBox.setSelectedItem(((String) priceURLComboBox.getSelectedItem()).trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "priceUrl");
		DBFunctions.getInstance().update(i, new String[] { "SET", "priceUrl", (String) priceURLComboBox.getSelectedItem() });

		isbnURLComboBox.setSelectedItem(((String) isbnURLComboBox.getSelectedItem()).trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "isbnUrl");
		DBFunctions.getInstance().update(i, new String[] { "SET", "isbnUrl", (String) isbnURLComboBox.getSelectedItem() });

		isbnAccessKeyTextField.setText(isbnAccessKeyTextField.getText().trim());
		i = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, "isbnKey");
		DBFunctions.getInstance().update(i, new String[] { "SET", "isbnKey", isbnAccessKeyTextField.getText() });
	}

	public String get(String s)
	{
		int idx = DBFunctions.getInstance().selectOne("SET", 1, s);
		if (idx < 0)
		{
			return "";
		}
		return DBFunctions.getInstance().get(idx, Constants.SETTING_VALUE);
	}

	public void set(String s, String v)
	{
		int idx = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, s);
		DBFunctions.getInstance().update(idx, new String[] { "SET", s, v });
	}

	public void setNoLog(String s, String v)
	{
		int idx = DBFunctions.getInstance().selectOne("SET", Constants.SETTING_KEY, s);
		DBFunctions.getInstance().updateNoLog(idx, new String[] { "SET", s, v });
	}

	private void exportSettings()
	{
		DBFunctions.getInstance().exportList("Settings", "SET");
	}

	private void importSettings()
	{
		DBFunctions.getInstance().importList("Settings", "SET", Constants.NO_MERGEABLE);
		populateFields();
	}

	// **************** GUI Here *****************
	protected void initComponents()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent e)
			{
				populateFields();
				settingsTabbedPane.requestFocusInWindow();
			}
		});

		booksellerDataPane.setLayout(new ColumnLayout(0, 0));
		booksellerDataPane.add("rx", nameLabel);
		booksellerDataPane.add("hwx", nameTextField);
		booksellerDataPane.add("rx", address1Label);
		booksellerDataPane.add("hwx", address1TextField);
		booksellerDataPane.add("rx", address2Label);
		booksellerDataPane.add("hwx", address2TextField);
		booksellerDataPane.add("rx", address3Label);
		booksellerDataPane.add("hwx", address3TextField);
		booksellerDataPane.add("rx", cityLabel);
		booksellerDataPane.add("hwx", cityTextField);
		booksellerDataPane.add("rx", stateLabel);
		booksellerDataPane.add("hwx", stateTextField);
		booksellerDataPane.add("rx", countryLabel);
		booksellerDataPane.add("hwx", countryTextField);
		booksellerDataPane.add("rx", postalCodeLabel);
		booksellerDataPane.add("hwx", postalCodeTextField);
		booksellerDataPane.add("rx", phoneLabel);
		booksellerDataPane.add("hwx", phoneTextField);
		booksellerDataPane.add("rx", faxLabel);
		booksellerDataPane.add("hwx", faxTextField);
		booksellerDataPane.add("rx", emailLabel);
		booksellerDataPane.add("hwx", emailTextField);
		booksellerDataPane.add("rx", taxIDLabel);
		booksellerDataPane.add("hwx", taxIDTextField);

		settingsTabbedPane.addTab("Bookseller Data", booksellerDataPane);

		listsPanel.setBorder(BorderFactory.createTitledBorder("Lists"));
		listsPanel.setLayout(new ColumnLayout(0, 0));
		listsPanel.add("x", dummyLabel);
		listsPanel.add("cx", listLabel);
		listsPanel.add("cwx", monthlyCostLabel);
		listsPanel.add("x", field1Label);
		listsPanel.add("hx", list1TextField);
		listsPanel.add("hwx", monthlyCost1TextField);
		listsPanel.add("x", field2Label);
		listsPanel.add("hx", list2TextField);
		listsPanel.add("hwx", monthlyCost2TextField);
		listsPanel.add("x", field3Label);
		listsPanel.add("hx", list3TextField);
		listsPanel.add("hwx", monthlyCost3TextField);
		listsPanel.add("x", field4Label);
		listsPanel.add("hx", list4TextField);
		listsPanel.add("hwx", monthlyCost4TextField);
		listsPanel.add("x", field5Label);
		listsPanel.add("hx", list5TextField);
		listsPanel.add("hwx", monthlyCost5TextField);

		userNamedFieldsPanel.setBorder(BorderFactory.createTitledBorder("User Named Fields"));
		userNamedFieldsPanel.setLayout(new ColumnLayout(0, 0));
		userNamedFieldsPanel.add("rx", userField1Label);
		userNamedFieldsPanel.add("hxw", userField1TextField);
		userNamedFieldsPanel.add("rx", userField2Label);
		userNamedFieldsPanel.add("hxw", userField2TextField);

		percentage1TextField.setPreferredSize(new Dimension(75, 25));
		percentage2TextField.setPreferredSize(new Dimension(75, 25));
		percentage3TextField.setPreferredSize(new Dimension(75, 25));
		taxesPanel.setBorder(BorderFactory.createTitledBorder("Taxes"));
		taxesPanel.setLayout(new ColumnLayout(0, 0));
		taxesPanel.add("cx", typeLabel);
		taxesPanel.add("cwx", percentageLabel);
		taxesPanel.add("hx", type1TextField);
		taxesPanel.add("wx", percentage1TextField);
		taxesPanel.add("hx", type2TextField);
		taxesPanel.add("wx", percentage2TextField);
		taxesPanel.add("hx", type3TextField);
		taxesPanel.add("wx", percentage3TextField);

		formatsPanel.setBorder(BorderFactory.createTitledBorder("Formats"));
		formatsPanel.setLayout(new ColumnLayout(0, 0));
		formatsPanel.add("hwx", dateComboBox);
		formatsPanel.add("hwx", currencyComboBox);

		userDefinedFieldsPane.setLayout(new ColumnLayout(0, 0));
		userDefinedFieldsPane.add("hvx", listsPanel);
		userDefinedFieldsPane.add("hvxw", userNamedFieldsPanel);
		userDefinedFieldsPane.add("hvx", taxesPanel);
		userDefinedFieldsPane.add("hvxw", formatsPanel);

		settingsTabbedPane.addTab("User-Defined Fields", userDefinedFieldsPane);

		basicPanel.setBorder(BorderFactory.createTitledBorder("Basic"));

		pathSelectButton.setText("...");
		pathSelectButton.setPreferredSize(new Dimension(25, 25));
		pathSelectButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				JFileChooser fc = new JFileChooser();
				Constants.setFileChooserFont(fc.getComponents());
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setAcceptAllFileFilterUsed(false);
				if (fc.showOpenDialog(BooksView.getInstance()) == JFileChooser.APPROVE_OPTION)
				{
					localImageTextField.setText(fc.getSelectedFile().getAbsolutePath());
				}
			}
		});

		basicPanel.setLayout(new ColumnLayout(0, 0));
		basicPanel.add("rx", priceCheckURLLabel);
		basicPanel.add("hwx", priceURLComboBox);
		basicPanel.add("rx", isbnCheckLabel);
		basicPanel.add("hwx", isbnURLComboBox);
		basicPanel.add("rx", isbnAccessKeyLabel);
		basicPanel.add("hwx", isbnAccessKeyTextField);
		basicPanel.add("rx", internetImageLabel);
		basicPanel.add("hwx", internetImageTextField);
		basicPanel.add("rx", localImageLabel);
		basicPanel.add("hx", localImageTextField);
		basicPanel.add("wx", pathSelectButton);

		advancedPanel.setBorder(BorderFactory.createTitledBorder("Advanced"));
		advancedPanel.setLayout(new ColumnLayout(0, 2));
		advancedPanel.add("hwx", toolTipCheckBox);
		advancedPanel.add("hwx", publisherInfoCheckBox);
		advancedPanel.add("hwx", closeTabCheckBox);
		advancedPanel.add("hwx", autoIncrementCheckBox);
		advancedPanel.add("hwx", autoIncNumberLabel);
		advancedPanel.add("hwx", autoIncNumberTextField);

		paymentMethodsPanel.setBorder(BorderFactory.createTitledBorder("Payment Methods"));
		paymentMethodsScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		Constants.setTabs(paymentMethodsTextArea);
		paymentMethodsScrollPanel.setViewportView(paymentMethodsTextArea);

		paymentMethodsPanel.setLayout(new ColumnLayout(0, 0));
		paymentMethodsPanel.add("hvwx", paymentMethodsScrollPanel);

		JPanel bottomPanel = new JPanel(new ColumnLayout(0, 0));
		bottomPanel.add("xhv", paymentMethodsPanel);
		bottomPanel.add("xhvw", advancedPanel);

		miscPane.setLayout(new ColumnLayout(0, 0));
		miscPane.add("xhvw", basicPanel);
		miscPane.add("xhvw", bottomPanel);
		settingsTabbedPane.addTab("Misc", miscPane);

		setLayout(new ColumnLayout(1, 1));
		add("4x", saveButton);
		add("", cancelButton);
		add("", importButton);
		add("w", exportButton);
		add("hvwx", settingsTabbedPane);
	}
}
