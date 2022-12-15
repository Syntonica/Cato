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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class DataValidationEditView extends GenericEditView
{
	private DataValidationEntry dataValidationEntry;
	private JComboBox fieldComboBox = new JComboBox();
	private JLabel correctLabel = new JLabel("Correct:", SwingConstants.RIGHT);
	private JLabel fieldLabel = new JLabel("Field:", SwingConstants.RIGHT);
	private JLabel incorrectLabel = new JLabel("Incorrect:", SwingConstants.RIGHT);
	private JTextField correctTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField incorrectTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private GenericEditButtonPanel buttonPanel = new GenericEditButtonPanel(this);

	private static DataValidationEditView instance = null;

	public static DataValidationEditView getInstance()
	{
		if (instance == null)
		{
			instance = new DataValidationEditView();
		}
		return instance;
	}

	public DataValidationEditView()
	{
		Constants.windowNames.put(this, "Data Val Edit");
		Constants.windowIcons.put(this, "val.png");
		initComponents();
	}

	public void setupEntry(int id)
	{
		if (id == -1)
		{
			dataValidationEntry = new DataValidationEntry();
		}
		else
		{
			dataValidationEntry = new DataValidationEntry(id);
		}
		incorrectTextField.setText(dataValidationEntry.incorrect);
		correctTextField.setText(dataValidationEntry.correct);
		fieldComboBox.setSelectedItem(dataValidationEntry.field);
	}

	public boolean saveEntry()
	{
		dataValidationEntry.incorrect = incorrectTextField.getText();
		dataValidationEntry.correct = correctTextField.getText();
		dataValidationEntry.field = (String) fieldComboBox.getSelectedItem();
		dataValidationEntry.saveDataValidationEntry();
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
						incorrectTextField.requestFocusInWindow();
					}
				});
			}
		});

		fieldComboBox.addItem("Author");
		fieldComboBox.addItem("Binding Type");
		fieldComboBox.addItem("Binding Style");
		fieldComboBox.addItem("Book Condition");
		fieldComboBox.addItem("Book Type");
		fieldComboBox.addItem("Catalog");
		fieldComboBox.addItem("DJ Condition");
		fieldComboBox.addItem("Edition");
		fieldComboBox.addItem("Illustrator");
		fieldComboBox.addItem("Keywords");
		fieldComboBox.addItem("Language");
		fieldComboBox.addItem("Location");
		fieldComboBox.addItem("Place");
		fieldComboBox.addItem("Printing");
		fieldComboBox.addItem("Publisher");
		fieldComboBox.addItem("Signed");
		fieldComboBox.addItem("Size");
		fieldComboBox.addItem("Title");

		setLayout(new ColumnLayout(1, 1));
		add("wx", buttonPanel);
		add("rx", incorrectLabel);
		add("hwx", incorrectTextField);
		add("rx", correctLabel);
		add("hwx", correctTextField);
		add("rx", fieldLabel);
		add("hwx", fieldComboBox);
	}
}
