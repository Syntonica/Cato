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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ListEditView extends GenericListView
{
	Action addAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			suggestion = "";
			addRecord();
		}
	};

	Action printAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			printListEditView();
		}
	};

	Action saveAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			exitListEditView();
		}
	};

	Action exportAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			exportListEditView();
		}
	};

	Action importAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			importListEditView();
		}
	};

	JButton saveButton = new GenericButton("exit.png", "Close.", saveAction);
	JButton addButton = new GenericButton("add.png", "Add a new row.", addAction);
	JButton deleteButton = new GenericButton("delete.png", "Delete a row.", deleteAction);
	JButton printButton = new GenericButton("print.png", "Print the displayed list.", printAction);
	JButton editButton = new GenericButton("edit.png", "Edit selected row.", editAction);
	JButton exportButton = new GenericButton("up.png", "Export this list to a file.", exportAction);
	JButton importButton = new GenericButton("down.png", "Import this list from a file.", importAction);

	public static String[] listNames = { "Tag", "List" };
	public String tag;
	public String name;
	public String suggestion = "";
	public GenericComboBox cb;

	private static ListEditView instance = null;

	public static ListEditView getInstance()
	{
		if (instance == null)
		{
			instance = new ListEditView();
		}
		return instance;
	}

	public ListEditView()
	{
		super("LST", "List", listNames, null, 0);
		initComponents();
		Constants.windowNames.put(this, "List Edit");
		Constants.windowIcons.put(this, "edit.png");
	}

	public void showListEditWindow(GenericComboBox cb, String name, String tag)
	{
		this.tag = tag;
		this.name = name;
		this.cb = cb;
		if (cb != null) suggestion = cb.getText();
		populateTable();
		listTable.getColumnModel().getColumn(0).setMinWidth(1);
		listTable.getColumnModel().getColumn(0).setPreferredWidth(1);
		listTable.getColumnModel().getColumn(0).setMaxWidth(1);
		listTable.getColumnModel().getColumn(1).setHeaderValue(name);
		BooksView.getInstance().tabbedPane.openTab(this);
		if (cb != null && !suggestion.equals("") && DBFunctions.getInstance().selectOne(tag, Constants.LIST_VALUE, suggestion) < 0)
		{
			addRecord();
		}
		else
		{
			suggestion = "";
		}
	}

	public void importListEditView()
	{
		DBFunctions.getInstance().importList(name, tag, Constants.MERGEABLE);
		populateTable();
	}

	public void exportListEditView()
	{
		DBFunctions.getInstance().exportList(name, tag);
	}

	public void exitListEditView()
	{
		if (tag.equals("DCT"))
		{
			SpellCheck.getInstance().loadDictionary(BooksView.getInstance().langButtonGroup.getSelection().getActionCommand());
		}
		BooksView.getInstance().tabbedPane.closeTab(this);
	}

	public void upCombos()
	{
		if (cb != null)
		{
			if (tag.equals("CAT"))
			{
				BookEditView.getInstance().catalog1ComboBox.setModel(DBFunctions.getInstance().generateList(tag, Constants.LIST_VALUE));
				BookEditView.getInstance().catalog2ComboBox.setModel(DBFunctions.getInstance().generateList(tag, Constants.LIST_VALUE));
				BookEditView.getInstance().catalog3ComboBox.setModel(DBFunctions.getInstance().generateList(tag, Constants.LIST_VALUE));
			}
			else
			{
				cb.setModel(DBFunctions.getInstance().generateList(tag, Constants.LIST_VALUE));
			}
		}
	}

	public void populateTable()
	{
		((DefaultTableModel) listTable.getModel()).setRowCount(0);
		for (int i = 0; i < DBFunctions.getInstance().size(); i++)
		{
			if (DBFunctions.getInstance().get(i, Constants.RECORD_TAG).equals(tag))
			{
				((DefaultTableModel) listTable.getModel())
						.addRow(new String[] { Integer.toString(i), DBFunctions.getInstance().get(i, Constants.LIST_VALUE) });
			}
		}
		upCombos();
	}

	public void addRecord()
	{
		String edited = JOptionPane.showInputDialog(BooksView.getInstance(), "Add an entry:", suggestion);
		if ((edited != null) && (!edited.equals("")) && (DBFunctions.getInstance().selectOne(tag, Constants.LIST_VALUE, edited) < 0))
		{
			DBFunctions.getInstance().update(-1, new String[] { tag, edited });
			populateTable();
		}
	}

	public void editRecord()
	{
		int index = listTable.getSelectedRow();
		if (index != -1)
		{
			String entry = (String) listTable.getValueAt(index, 1);
			String edited = JOptionPane.showInputDialog(BooksView.getInstance(), "Edit this entry:", entry);
			if ((edited != null) && (!edited.equals("")))
			{
				if (DBFunctions.getInstance().selectOne(tag, Constants.LIST_VALUE, edited) != -1) return;
				DBFunctions.getInstance().update(Integer.parseInt((String) listTable.getValueAt(index, 0)),
						new String[] { tag, edited });
				populateTable();
			}
		}
	}

	public void deleteRecord()
	{
		int index = listTable.getSelectedRow();
		if (index != -1)
		{
			String val = (String) listTable.getValueAt(index, 1);
			if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null, "Delete " + val + "?", "Deleting",
					JOptionPane.OK_CANCEL_OPTION))
			{
				DBFunctions.getInstance().remove(Integer.parseInt((String) listTable.getValueAt(index, 0)));
				populateTable();
			}
		}
	}

	public void printListEditView()
	{
		new PrintThis(listTable);
	}

	private void initComponents()
	{
		setLayout(new ColumnLayout(1, 1));
		add("7x", saveButton);
		add("", addButton);
		add("", editButton);
		add("", deleteButton);
		add("", printButton);
		add("", importButton);
		add("w", exportButton);
		add("hvwx", listScrollPane);
	}
}
