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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class BookListView extends GenericListView
{
	DefaultTableModel tm;
	private static BookListView instance = null;

	public static BookListView getInstance()
	{
		if (instance == null)
		{
			instance = new BookListView();
		}
		return instance;
	}

	JEditorPane previewTextArea = new JEditorPane();

	public BookListView()
	{
		super("BKS", "Book", Constants.names, BookEditView.getInstance(), 8);
		initComponents();
		Constants.windowNames.put(this, "Books");
		Constants.windowIcons.put(this, "book.png");
		tm = (DefaultTableModel) listTable.getModel();
	}

	private Action toolAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			selectTool();
		}
	};

	private JButton toolButton = new GenericButton("tool.png", "Select a Tool", toolAction);
	private String totalCost;
	private String totalList1;
	private String totalList2;
	private String totalList3;
	private String totalList4;
	private String totalList5;
	private String totalQuantity;
	private String asp;

	private ArrayList<Integer> numerics = new ArrayList<Integer>();

	public void populateTable()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				String[] fieldContent = new String[8];
				boolean[] numeric = new boolean[8];
				int[] compareField = new int[8];
				int oldRow = listTable.getSelectedRow();
				tm.setRowCount(0);
				for (int i = 0; i < 8; i++)
				{
					fieldContent[i] = anyTextField[i].getText().toLowerCase();
					compareField[i] = Arrays.asList(Constants.names).indexOf(anyComboBox[i].getSelectedItem());
					numeric[i] = numerics.contains(compareField[i]);
				}

				String[] row;
				for (int i = 0; i < DBFunctions.getInstance().size(); i++)
				{
					if (DBFunctions.getInstance().get(i, 0).equals("BKS"))
					{
						if (!applyFilter(fieldContent[0], DBFunctions.getInstance().get(i, compareField[0]).toLowerCase(),
								numeric[0]))
							continue;
						if (!applyFilter(fieldContent[1], DBFunctions.getInstance().get(i, compareField[1]).toLowerCase(),
								numeric[1]))
							continue;
						if (!applyFilter(fieldContent[2], DBFunctions.getInstance().get(i, compareField[2]).toLowerCase(),
								numeric[2]))
							continue;
						if (!applyFilter(fieldContent[3], DBFunctions.getInstance().get(i, compareField[3]).toLowerCase(),
								numeric[3]))
							continue;
						if (!applyFilter(fieldContent[4], DBFunctions.getInstance().get(i, compareField[4]).toLowerCase(),
								numeric[4]))
							continue;
						if (!applyFilter(fieldContent[5], DBFunctions.getInstance().get(i, compareField[5]).toLowerCase(),
								numeric[5]))
							continue;
						if (!applyFilter(fieldContent[6], DBFunctions.getInstance().get(i, compareField[6]).toLowerCase(),
								numeric[6]))
							continue;
						if (!applyFilter(fieldContent[7], DBFunctions.getInstance().get(i, compareField[7]).toLowerCase(),
								numeric[7]))
							continue;
						// must clone because we use inexact copies in our table
						row = DBFunctions.getInstance().getRecord(i).clone();
						// change the tag to the record number
						row[0] = Integer.toString(i);
						tm.addRow(row);
					}
				}

				totalQuantity = sum(Constants.BOOK_QUANTITY);
				totalCost = sum(Constants.BOOK_COST);
				totalList1 = sum(Constants.BOOK_LIST1);
				totalList2 = sum(Constants.BOOK_LIST2);
				totalList3 = sum(Constants.BOOK_LIST3);
				totalList4 = sum(Constants.BOOK_LIST4);
				totalList5 = sum(Constants.BOOK_LIST5);
				asp = "0.00";

				if (Double.parseDouble(totalQuantity) > 0.0)
				{
					asp = Constants.twoPlaces
							.format(Double.parseDouble(totalList1) / Double.parseDouble(totalQuantity));
				}
				else if (listTable.getRowCount() > 0)
				{
					asp = Constants.twoPlaces.format(Double.parseDouble(totalList1) / listTable.getRowCount());
				}

				if (listTable.getRowCount() > 0)
				{
					oldRow = Constants.clamp(oldRow, 0, listTable.getRowCount() - 1);
					listTable.setRowSelectionInterval(oldRow, oldRow);
				}
			}
		});

	}

	public void addRecord()
	{
		if (BooksView.getInstance().tabbedPane.isTabOpen(BookEditView.getInstance()))
		{
			if (!BookEditView.getInstance().saveEntry())
			{
				return;
			}
		}
		else
		{
			BooksView.getInstance().tabbedPane.openTab(BookEditView.getInstance());
		}
		BookEditView.getInstance().setupEntry(-1);
	}

	public void editRecord()
	{
		int index = listTable.getSelectedRow();
		if (BooksView.getInstance().tabbedPane.isTabOpen(BookEditView.getInstance()))
		{
			if (!BookEditView.getInstance().saveEntry())
			{
				return;
			}
		}
		else
		{
			BooksView.getInstance().tabbedPane.openTab(BookEditView.getInstance());
		}
		BookEditView.getInstance().bookIndex = listTable.getSelectedRow();
		BookEditView.getInstance().bookCount = listTable.getRowCount();
		BookEditView.getInstance().setupEntry(Integer.parseInt((String) listTable.getValueAt(index, 0)));
	}

	public void deleteRecord()
	{
		int index = listTable.getSelectedRow();
		if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null,
				"Delete Book ID " + listTable.getValueAt(index, Constants.BOOK_ID) + "?", "Deleting",
				JOptionPane.OK_CANCEL_OPTION))
		{
			DBFunctions.getInstance().remove(Integer.parseInt((String) listTable.getValueAt(index, 0)));
			populateTable();
		}
	}

	public void setValueLabel()
	{
		int index = listTable.getSelectedRow();
		previewTextArea.setText(previewString(index));
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				previewTextArea.setCaretPosition(0);
			}
		});
		String val = "[ Entries: " + Integer.toString(listTable.getSelectedRow() + 1) + " of "
				+ Integer.toString(listTable.getRowCount()) + " ]" + "[ Qty: " + (int) Double.parseDouble(totalQuantity)
				+ " ]" + "[ Cost: " + totalCost + " ]" + "[ " + SettingsView.getInstance().get("list1") + ": " + totalList1
				+ " ]";
		if (!totalList2.equals("0.00"))
		{
			val += "[ " + SettingsView.getInstance().get("list2") + ": " + totalList2 + " ]";
		}
		if (!totalList3.equals("0.00"))
		{
			val += "[ " + SettingsView.getInstance().get("list3") + ": " + totalList3 + " ]";
		}
		if (!totalList4.equals("0.00"))
		{
			val += "[ " + SettingsView.getInstance().get("list4") + ": " + totalList4 + " ]";
		}
		if (!totalList5.equals("0.00"))
		{
			val += "[ " + SettingsView.getInstance().get("list5") + ": " + totalList5 + " ]";
		}
		val += "[ ASP: " + asp + " ]";
		valueLabel.setText(val);
	}

	private String previewString(int idx)
	{
		if (idx == -1)
		{
			return "";
		}
		return ("[Book ID:&nbsp;<b>" + listTable.getValueAt(idx, Constants.BOOK_ID) + "</b>] [Author:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_AUTHOR) + "</b>] [Title:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_TITLE) + "</b>] [Illustrator:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_ILLUSTRATOR) + "</b>] [Publisher:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_PUBLISHER) + "</b>] [Place:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_PLACE) + "</b>] [Date:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_DATE) + "</b>] [ISBN:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_ISBN) + "</b>] [Edition/Printing:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_EDITION) + "/"
				+ listTable.getValueAt(idx, Constants.BOOK_PRINTING) + "</b>] [Grading:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_CONDITION) + "/"
				+ listTable.getValueAt(idx, Constants.BOOK_DJ_CONDITION) + "</b>] [Binding:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_BINDING_TYPE) + "/"
				+ listTable.getValueAt(idx, Constants.BOOK_BINDING_STYLE) + "</b>] [Pages:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_PAGES) + "</b>] [Language:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_LANGUAGE) + "</b>] [Signed:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_SIGNED) + "</b>] [Size:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_SIZE) + "</b>] [BookType:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_TYPE) + "</b>] [Weight:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_WEIGHT) + "</b>] [Height:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_HEIGHT) + "</b>] [Width:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_WIDTH) + "</b>] [Keywords:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_KEYWORDS) + "</b>] [Catalog1:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_CATALOG1) + "</b>] [Catalog2:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_CATALOG2) + "</b>] [Catalog3:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_CATALOG3) + "</b>] [Cost:&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_COST) + "</b>] [" + SettingsView.getInstance().get("list1")
				+ ":&nbsp;<b>" + listTable.getValueAt(idx, Constants.BOOK_LIST1) + "</b>] ["
				+ SettingsView.getInstance().get("list2") + ":&nbsp;<b>" + listTable.getValueAt(idx, Constants.BOOK_LIST2)
				+ "</b>] [" + SettingsView.getInstance().get("list3") + ":&nbsp;<b>"
				+ listTable.getValueAt(idx, Constants.BOOK_LIST3) + "</b>] [" + SettingsView.getInstance().get("list4")
				+ ":&nbsp;<b>" + listTable.getValueAt(idx, Constants.BOOK_LIST4) + "</b>] ["
				+ SettingsView.getInstance().get("list5") + ":&nbsp;<b>" + listTable.getValueAt(idx, Constants.BOOK_LIST5)
				+ "</b>] [Quantity:&nbsp;<b>" + listTable.getValueAt(idx, Constants.BOOK_QUANTITY)
				+ "</b>] [Status:&nbsp;<b>" + listTable.getValueAt(idx, Constants.BOOK_STATUS)
				+ "</b>] [Location:&nbsp;<b>" + listTable.getValueAt(idx, Constants.BOOK_LOCATION) + "</b>]<br>"
				+ listTable.getValueAt(idx, Constants.BOOK_DESCRIPTION) + "<br>"
				+ listTable.getValueAt(idx, Constants.BOOK_PRIVATE));
	}

	public void updateCombos()
	{
		Constants.setUserFields();
		listTable.getColumnModel().getColumn(Constants.BOOK_USER1)
				.setHeaderValue(Constants.names[Constants.BOOK_USER1]);
		listTable.getColumnModel().getColumn(Constants.BOOK_USER2)
				.setHeaderValue(Constants.names[Constants.BOOK_USER2]);
		listTable.getColumnModel().getColumn(Constants.BOOK_LIST1)
				.setHeaderValue(Constants.names[Constants.BOOK_LIST1]);
		listTable.getColumnModel().getColumn(Constants.BOOK_LIST2)
				.setHeaderValue(Constants.names[Constants.BOOK_LIST2]);
		listTable.getColumnModel().getColumn(Constants.BOOK_LIST3)
				.setHeaderValue(Constants.names[Constants.BOOK_LIST3]);
		listTable.getColumnModel().getColumn(Constants.BOOK_LIST4)
				.setHeaderValue(Constants.names[Constants.BOOK_LIST4]);
		listTable.getColumnModel().getColumn(Constants.BOOK_LIST5)
				.setHeaderValue(Constants.names[Constants.BOOK_LIST5]);
		super.updateCombos();
	}

	private void initComponents()
	{
		numerics.add(0);
		numerics.add(Constants.BOOK_LIST1);
		numerics.add(Constants.BOOK_LIST2);
		numerics.add(Constants.BOOK_LIST3);
		numerics.add(Constants.BOOK_LIST4);
		numerics.add(Constants.BOOK_LIST5);
		numerics.add(Constants.BOOK_COST);
		numerics.add(Constants.BOOK_QUANTITY);

		((DefaultRowSorter) listTable.getRowSorter()).setComparator(Constants.BOOK_LIST1, Comparators.NUMBER_ORDER);
		((DefaultRowSorter) listTable.getRowSorter()).setComparator(Constants.BOOK_LIST2, Comparators.NUMBER_ORDER);
		((DefaultRowSorter) listTable.getRowSorter()).setComparator(Constants.BOOK_LIST3, Comparators.NUMBER_ORDER);
		((DefaultRowSorter) listTable.getRowSorter()).setComparator(Constants.BOOK_LIST4, Comparators.NUMBER_ORDER);
		((DefaultRowSorter) listTable.getRowSorter()).setComparator(Constants.BOOK_LIST5, Comparators.NUMBER_ORDER);
		((DefaultRowSorter) listTable.getRowSorter()).setComparator(Constants.BOOK_COST, Comparators.NUMBER_ORDER);
		((DefaultRowSorter) listTable.getRowSorter()).setComparator(Constants.BOOK_QUANTITY, Comparators.NUMBER_ORDER);
		((DefaultRowSorter) listTable.getRowSorter()).setComparator(Constants.BOOK_NUMBER, Comparators.NUMBER_ORDER);
		((DefaultRowSorter) listTable.getRowSorter()).setComparator(0, Comparators.NUMBER_ORDER);

		listTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				setValueLabel();
			}
		});

		JScrollPane previewScrollPane = new JScrollPane(previewTextArea);
		previewTextArea.setBackground(Cato.whiteAndBlack);
		previewTextArea.setContentType("text/html");
		previewTextArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		previewTextArea.setFont(Cato.previewFont);
		previewTextArea.setEditable(false);
		previewTextArea.setFocusable(false);

		valueLabel.setFont(Cato.catoFont);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScrollPane, previewScrollPane);
		splitPane.setResizeWeight(0.7);

		setLayout(new ColumnLayout(1, 1));
		add("6x", clearButton);
		add("", addButton);
		add("", editButton);
		add("", deleteButton);
		add("", toolButton);
		add("w", printButton);
		add("wxh", searchPanel);
		add("whvx", splitPane);
		add("kwcx", valueLabel);
	}

	private void selectTool()
	{
		String[] theTools = {
		"Play Macro",
		"Export Books to File",
		"Import Books from File",
		"Create a Sale for Selected Book",
		"Create an Invoice from Selected Book",
		"Clear Include in Update Checkmarks",
		"Change Field Values",
		"Reprice Books",
		"Populate AMZ Field",
		"Delete Books"};

		ScrollablePopupMenu popup = new ScrollablePopupMenu();
		popup.setMaximumVisibleRows(11);
		for (int i = 0; i < theTools.length; i++)
		{
			JMenuItem mi = new JMenuItem(theTools[i]);
			mi.setBackground(Cato.whiteAndBlack);
			mi.setActionCommand(Integer.toString(i));
			mi.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					runTool(ae.getActionCommand());
				}
			});
			popup.add(mi);
			if (i == 8) popup.addSeparator();
		}

		int x = toolButton.getX();
		int y = toolButton.getY();
		y = y + toolButton.getHeight();
		// use clearButton as hack due to layout
		popup.show(clearButton, x, y);
	}

	private void runTool(String tool)
	{
		switch (Integer.parseInt(tool))
		{
			case 0:
				playMacro();
				break;
			case 1:
				exportBooks();
				break;
			case 2:
				importBooks();
				break;
			case 3:
				sellBook();
				break;
			case 4:
				sellInv();
				break;
			case 5:
				clearUpdateChecks();
				break;
			case 6:
				changeFieldValues();
				break;
			case 7:
				changePrices();
				break;
			case 8:
				populateAmazon();
				break;
			case 9:
				deleteBooks();
				break;
		}
	}

	private void playMacro()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new ColumnLayout(1, 1));

		ArrayList<String> macroNames = DBFunctions.getInstance().generateList("MAC", Constants.MACRO_NAME);
		Collections.sort(macroNames, String.CASE_INSENSITIVE_ORDER);
		JComboBox macroComboBox = new JComboBox(macroNames.toArray());

		JCheckBox includeCheckBox = new JCheckBox("Include in Update?");
		panel.add("xhw", macroComboBox);
		panel.add("xhw", includeCheckBox);
		int ret = JOptionPane.showConfirmDialog(null, panel, "Plaqying a Macro", JOptionPane.OK_CANCEL_OPTION);
		if (ret == JOptionPane.OK_OPTION)
		{
			playMacro2((String)macroComboBox.getSelectedItem(), includeCheckBox.isSelected());
		}
	}

	private int getFieldIndex(String name)
	{
		ArrayList<String> a = new ArrayList<String>(Arrays.asList(Constants.names));
		return a.indexOf(name);
	}

	public void changeFieldValues()
	{
		// all or selected, field, new value
		JPanel panel = new JPanel();
		panel.setLayout(new ColumnLayout(1, 1));

		ArrayList<String> a = new ArrayList<String>(Arrays.asList(Constants.names));
		a.remove("Key");
		a.remove("Has Image?");
		Collections.sort(a);
		a.add(0, "(Same as Target");
		JComboBox sourceFieldComboBox = new JComboBox(a.toArray());
		sourceFieldComboBox.setPrototypeDisplayValue("XXXXXXXXXXXXXX");
		a.remove(0);
		JComboBox targetFieldComboBox = new JComboBox(a.toArray());
		targetFieldComboBox.setPrototypeDisplayValue("XXXXXXXXXXXXXX");

		JTextField sourceTextField = new JTextField("*");
		JComboBox targetComboBox = new JComboBox();
		targetComboBox.addItem("Replace");
		targetComboBox.addItem("Insert at Beginning");
		targetComboBox.addItem("Insert at End");
		JCheckBox regexCheckBox = new JCheckBox("Regex?");
		JCheckBox moveCheckBox = new JCheckBox("Move?");
		JCheckBox spaceCheckBox = new JCheckBox("Insert Space?");
		JCheckBox includeCheckBox = new JCheckBox("Include in Update?");
		JLabel fromLabel = new JLabel("From:");
		JLabel toLabel = new JLabel("To:");

		panel.add("hx", fromLabel);
		panel.add("whx", toLabel);
		panel.add("hx", sourceFieldComboBox);
		panel.add("whx", targetFieldComboBox);
		panel.add("hx", sourceTextField);
		panel.add("whx", targetComboBox);
		panel.add("hx", moveCheckBox);
		panel.add("whx", regexCheckBox);
		panel.add("hx", spaceCheckBox);
		panel.add("whx", includeCheckBox);

		int ret = JOptionPane.showConfirmDialog(null, panel, "Changing a Field", JOptionPane.OK_CANCEL_OPTION);
		if (ret == JOptionPane.OK_OPTION)
		{
			final WaitBox frame = new WaitBox();
			class ChangeFields extends SwingWorker
			{
				public Object doInBackground()
				{
					int fromField = getFieldIndex((String) sourceFieldComboBox.getSelectedItem());
					int toField = getFieldIndex((String) targetFieldComboBox.getSelectedItem());
					if (fromField == -1)
					{
						fromField = toField;
					}
					String spacer = "";
					if (spaceCheckBox.isSelected())
					{
						spacer = " ";
					}
					DBFunctions.getInstance().backup();
					boolean include = includeCheckBox.isSelected();
					String fromValue = ""; // contents of source field
					String toValue = ""; // contents of target field
					String oldText = ""; // contents of source textfield

					for (int i = 0; i < tm.getRowCount(); i++)
					{
						// field = source field name
						// field2 = target field name
						int record = Integer.parseInt((String) tm.getValueAt(i, 0)); // get record number to pull
						int placement = targetComboBox.getSelectedIndex();

						fromValue = (String) tm.getValueAt(i, fromField);
						toValue = (String) tm.getValueAt(i, toField);
						oldText = sourceTextField.getText();
						if (oldText.equals("*"))
						{
							oldText = fromValue;
						}
						else if (regexCheckBox.isSelected())
						{
							// only use the first capture
							oldText = fromValue.replaceAll(oldText, "$1");
						}
						if (placement == 0) // replace
						{
							toValue = oldText;
						}
						else if (placement == 1) // beginning
						{
							if (toValue.equals(""))
							{
								toValue = oldText;
							}
							else
							{
								toValue = oldText + spacer + toValue;
							}
						}
						else if (placement == 2) // end
						{
							if (toValue.equals(""))
							{
								toValue = oldText;
							}
							else
							{
								toValue = toValue + spacer + oldText;
							}
						}

						if (moveCheckBox.isSelected()) // delete oldValue
						{
							DBFunctions.getInstance().put(record, fromField, "");
						}
						DBFunctions.getInstance().put(record, toField, toValue);
						DBFunctions.getInstance().put(record, Constants.BOOK_CHANGED_DATE, Constants.getTimestamp());
						if (include)
							DBFunctions.getInstance().put(record, Constants.BOOK_INCLUDE_IN_UPDATE, "true");
					}
					populateTable();
					return true;
				}

				public void done()
				{
					frame.dispose();
				}

			};
			(new ChangeFields()).execute();
		}
	}

	private void deleteBooks()
	{
		int ret = JOptionPane.showConfirmDialog(null,
			"This will delete all listings currently visible.\nDo you wish to proceed?",
			"Deleting", JOptionPane.YES_NO_OPTION);
		if (ret == JOptionPane.NO_OPTION)
		{
			return;
		}
		int count = tm.getRowCount();
		final WaitBox frame = new WaitBox();
		class DeleteBooks extends SwingWorker
		{
			public Object doInBackground()
			{
				DBFunctions.getInstance().backup();
				for (int i = 0; i < count; i++)
				{
					int record = Integer.parseInt((String) tm.getValueAt(i, 0));
					DBFunctions.getInstance().remove(record);
				}
				populateTable();
				return true;
			}

			public void done()
			{
				frame.dispose();
			}

		};
		(new DeleteBooks()).execute();
		JOptionPane.showMessageDialog(null, count + " listings deleted.");
	}

	private void clearUpdateChecks()
	{
		final WaitBox frame = new WaitBox();
		class ClearChecks extends SwingWorker
		{
			public Object doInBackground()
			{
				DBFunctions.getInstance().backup();
				for (int i = 0; i < tm.getRowCount(); i++)
				{
					int record = Integer.parseInt((String) tm.getValueAt(i, 0));
					DBFunctions.getInstance().put(record, Constants.BOOK_INCLUDE_IN_UPDATE, "false");
				}
				populateTable();
				return true;
			}

			public void done()
			{
				frame.dispose();
			}

		};
		(new ClearChecks()).execute();
	}

	private void sellInv()
	{
		int index = listTable.getSelectedRow();
		if (index != -1)
		{
			// open new invoice
			if (!BooksView.getInstance().tabbedPane.isTabOpen(InvoiceEditView.getInstance()))
			{
				InvoiceListView.getInstance().addRecord();
			}

			// add book to invoice
			InvoiceEditView.getInstance().addBookButton.doClick();

			// populate sale info
			SaleEditView.getInstance().bookIDTextField.setText((String) listTable.getValueAt(index, Constants.BOOK_ID));
			SaleEditView.getInstance().updateBookInfo();
		}
	}

	private void sellBook()
	{
		int index = listTable.getSelectedRow();
		if (index != -1)
		{
			if (BooksView.getInstance().tabbedPane.isTabOpen(SaleEditView.getInstance()))
			{
				if (!SaleEditView.getInstance().saveEntry())
				{
					return;
				}
			}
			BooksView.getInstance().tabbedPane.openTab(SaleEditView.getInstance());
			SaleEditView.getInstance().caller = "books";
			SaleEditView.getInstance().setupEntry(-1);
			SaleEditView.getInstance().bookIDTextField.setText((String) listTable.getValueAt(index, Constants.BOOK_ID));
			SaleEditView.getInstance().updateBookInfo();
		}
	}

	private JComboBox makeListComboBox()
	{
		JComboBox listComboBox = new JComboBox();
		listComboBox.addItem(Constants.names[Constants.BOOK_LIST1]);
		listComboBox.addItem(Constants.names[Constants.BOOK_LIST2]);
		listComboBox.addItem(Constants.names[Constants.BOOK_LIST3]);
		listComboBox.addItem(Constants.names[Constants.BOOK_LIST4]);
		listComboBox.addItem(Constants.names[Constants.BOOK_LIST5]);
		return listComboBox;
	}

	public void importBooks()
	{
		// get file to import
		JFileChooser fc = new JFileChooser(Constants.HOME_DIR);
		Constants.setFileChooserFont(fc.getComponents());
		fc.setDialogTitle("Import Books");
		int reply = fc.showOpenDialog(null);
		if (reply == JFileChooser.APPROVE_OPTION)
		{
			// merge or replace?
			File fn = fc.getSelectedFile();
			JPanel panel = new JPanel(new ColumnLayout(2, 2));
			JComboBox mergeComboBox = new JComboBox();
			mergeComboBox.addItem("Replace");
			mergeComboBox.addItem("Merge");
			JComboBox formatComboBox = new JComboBox();
			formatComboBox.removeAllItems();
			File dir = new File(Constants.TEMPLATES_DIR + Constants.ps);
			String[] children = dir.list();
			Arrays.sort(children);
			if (children != null)
			{
				for (String f : children)
				{
					if (f.endsWith(".format"))
					{
						formatComboBox.addItem(f);
					}
				}
			}
			JLabel mergeLabel = new JLabel("Selection:");
			mergeLabel.setPreferredSize(new Dimension(70, 25));
			JLabel formatLabel = new JLabel("Format:");
			JLabel dummy = new JLabel();
			JCheckBox includeCheckBox = new JCheckBox("Include in Update?");

			panel.add("xh", formatLabel);
			panel.add("xhw", formatComboBox);
			panel.add("xh", mergeLabel);
			panel.add("xhw", mergeComboBox);
			panel.add("hx", dummy);
			panel.add("hxw", includeCheckBox);

			int ret = JOptionPane.showConfirmDialog(null, panel, "Importing", JOptionPane.OK_CANCEL_OPTION);
			if (ret == JOptionPane.OK_OPTION)
			{
				final WaitBox frame = new WaitBox();
				class ImportBooks extends SwingWorker
				{
					public Object doInBackground()
					{
						DBFunctions.getInstance().backup();
						boolean merge = false;
						if (mergeComboBox.getSelectedIndex() == 1)
						{
							merge = true;
						}
						BooksImportExport bie = new BooksImportExport();
						bie.setInclude(includeCheckBox.isSelected());
						bie.setFileName(fn);
						bie.setMerging(merge);
						bie.setFormat((String) formatComboBox.getSelectedItem());
						int booksImported = bie.importBooks();
						populateTable();
						return booksImported;
					}

					public void done()
					{
						frame.dispose();
						try
						{
							int result = (int) get();
							if (result > 0)
							{
								JOptionPane.showMessageDialog(null, result + " records processed.");
							}
							else if (result == 0)
							{
								JOptionPane.showMessageDialog(null, "No records were processed.");
							}
							else if (result == -1)
							{
								JOptionPane.showMessageDialog(null, "Template error: No date format specified.");
							}
							else if (result == -2)
							{
								JOptionPane.showMessageDialog(null, "Template error: delimiter.");
							}
							else if (result == -3)
							{
								JOptionPane.showMessageDialog(null, "Template error: CR/LF missing.");
							}
							else
							{
								Constants.writeBlog("Book file '" + fn + "' imported.");
							}
						}
						catch (Exception ex)
						{
							Constants.writeBlog("BooksListView > importBooks > " + ex);
						}
					}
				};
				(new ImportBooks()).execute();
			}
			populateTable();
		}
	}

	public void exportBooks()
	{
		JPanel panel = new JPanel(new ColumnLayout(2, 2));
		JComboBox formatComboBox = new JComboBox();
		formatComboBox.removeAllItems();
		File dir = new File(Constants.TEMPLATES_DIR);
		String[] children = dir.list();
		Arrays.sort(children);
		if (children != null)
		{
			for (String fn : children)
			{
				if (fn.endsWith(".format"))
				{
					formatComboBox.addItem(fn);
				}
			}
		}
		JComboBox listComboBox = makeListComboBox();

		JLabel formatLabel = new JLabel("Format:");
		JLabel listLabel = new JLabel("List:");

		panel.add("xh", formatLabel);
		panel.add("xhw", formatComboBox);
		panel.add("xh", listLabel);
		panel.add("xhw", listComboBox);
		int ret = JOptionPane.showConfirmDialog(null, panel, "Exporting", JOptionPane.OK_CANCEL_OPTION);
		if (!(ret == JOptionPane.OK_OPTION))
		{
			return;
		}
		String list = (String) listComboBox.getSelectedItem();

		BooksImportExport bie = new BooksImportExport();

		String format = (String) formatComboBox.getSelectedItem();
		ArrayList bookList = new ArrayList();

		for (int i = 0; i < tm.getRowCount(); i++)
		{
			int index = listTable.convertRowIndexToModel(i);
			bookList.add((String) tm.getValueAt(index, Constants.BOOK_ID));
		}
		String[] pieces = format.split("\\.", -1);
		String dot3 = pieces[pieces.length - 2];
		String exportFile = "";

		if (format.startsWith("Cato"))
		{
			exportFile = "CTO_";
		}
		else if (format.startsWith("Homebase 2"))
		{
			exportFile = "HB2_" + list;
		}
		else if (format.startsWith("Homebase 3"))
		{
			exportFile = "HB3_" + list;
		}
		else if (format.startsWith("Amazon"))
		{
			exportFile = "AMZ_" + list;
		}
		else if (format.startsWith("UIEE"))
		{
			exportFile = "UIE_" + list;
		}
		else if (format.startsWith("Catalog"))
		{
			exportFile = "CLG_" + list;
		}
		else
		{
			exportFile = "USR_" + list;
		}

		String fname = exportFile + "_" + Constants.getFileDateTime() + "." + dot3;
		fname = fname.replaceAll("[- :]", "");
		String f = Constants.EXPORTS_DIR + Constants.ps + fname;

		final WaitBox frame = new WaitBox();
		class ExportBooks extends SwingWorker
		{
			public Object doInBackground()
			{
				bie.setFileName(new File(f));
				bie.setBookList(bookList);
				bie.setFormat(format);
				bie.setList(list);
				return bie.exportBooks();
			}

			public void done()
			{
				frame.dispose();
				try
				{
					JOptionPane.showMessageDialog(null, get() + " records processed.");
				}
				catch (Exception ex)
				{
					Constants.writeBlog("BooksImportExport > exportBooks > " + ex);
				}
				populateTable();
			}

		};
		(new ExportBooks()).execute();
	}

	public void changePrices()
	{
		// all or selected, flat or %, value
		JPanel panel = new JPanel();
		panel.setLayout(new ColumnLayout(1, 1));
		JComboBox typeComboBox = new JComboBox();
		typeComboBox.addItem("Flat");
		typeComboBox.addItem("Percentage");
		typeComboBox.addItem("Round");
		typeComboBox.addItem("New");
		final JTextField amountTextField = new JTextField();
		JCheckBox includeCheckBox = new JCheckBox("Include in Update?");
		amountTextField.addKeyListener(new KeyAdapter()
		{
			public void keyTyped(KeyEvent evt)
			{
				char c = evt.getKeyChar();
				if (((c < '0') || (c > '9')) && (c != '.') && (c != 'U') && (c != 'D') && (c != 'u') && (c != 'd')
						&& (c != '-'))
				{
					evt.consume();
				}
				if ((amountTextField.getText().length() > 0) && (c == '-'))
				{
					evt.consume();
				}
				if (amountTextField.getText().length() > 15)
				{
					evt.consume();
					amountTextField.setText(amountTextField.getText().substring(0, 16));
				}
			}
		});
		JComboBox listComboBox = makeListComboBox();

		JLabel typeLabel = new JLabel("Change Type:");
		JLabel amountLabel = new JLabel("Amount:");
		JLabel listLabel = new JLabel("Price:");
		panel.add("xhw", listLabel);
		panel.add("xhw", listComboBox);
		panel.add("xhw", typeLabel);
		panel.add("xhw", typeComboBox);
		panel.add("xhw", amountLabel);
		panel.add("xhw", amountTextField);
		panel.add("xhw", includeCheckBox);
		int ret = JOptionPane.showConfirmDialog(null, panel, "Repricing", JOptionPane.OK_CANCEL_OPTION);
		if (ret == JOptionPane.OK_OPTION)
		{
			String list = (String) listComboBox.getSelectedItem();
			String choice = (String) typeComboBox.getSelectedItem();
			int field = getFieldIndex(list);
			boolean include = includeCheckBox.isSelected();
			if (choice.equals("Flat"))
			{
				flatChoice(field, amountTextField.getText(), include);
			}
			else if (choice.equals("Percentage"))
			{
				percentageChoice(field, amountTextField.getText(), include);
			}
			else if (choice.equals("Round"))
			{
				roundChoice(field, amountTextField.getText(), include);
			}
			else if (choice.equals("New"))
			{
				newChoice(field, amountTextField.getText(), include);
			}
		}
	}

	private void roundChoice(int field, String mask, boolean include)
	{
		if ((mask.length() < 4) || (mask.indexOf(".") != mask.length() - 3))
		{
			JOptionPane.showMessageDialog(null,
					"Rounding mask is not of a proper format: at least 3 digits and a decimal point.");
			return;
		}
		final WaitBox frame = new WaitBox();
		class Round extends SwingWorker
		{
			public Object doInBackground()
			{
				DBFunctions.getInstance().backup();
				char[] m = (mask.toLowerCase()).toCharArray();
				char[] v;
				for (int k = 0; k < tm.getRowCount(); k++)
				{
					int record = Integer.parseInt((String) tm.getValueAt(k, 0));
					String oldAmount = (String) listTable.getValueAt(k, field); // list price
					// apply mask: 0=stet 1=lower to zero 5=5/4 round 9=raise to 10 U=up to nearest
					// 5/0, D=down to nearest 5/0
					// Double mult = .01;
					v = ("0" + oldAmount).toCharArray();
					for (int i = 0; i < v.length; i++)
					{
						// index backwards
						int mi = m.length - i - 1;
						int vi = v.length - i - 1;
						if (v[vi] > '9')
						{
							v[vi] = '0';
							v[vi - 1]++;
						}
						else if (v[vi] == '/') // we inc'd a decimal point
						{
							v[vi] = '.';
							v[vi - 1]++;
						}
						if (mi > -1 && vi > -1)
						{
							switch (m[mi])
							{
								case '.':
								case '0':
									break;
								case '1':
									v[vi] = 0;
									break;
								case '5':
									if (v[vi] >= '5')
									{
										v[vi - 1]++;
									}
									v[vi] = '0';
									break;
								case '9':
									if (v[vi] != '0')
									{
										v[vi] = '0';
										v[vi - 1]++;
									}
									break;
								case 'u':
									if ((v[vi] == '0') || v[vi] == '5')
									{
										// do nothing
									}
									else if (v[vi] < '5')
									{
										v[vi] = '5';
									}
									else
									{
										v[vi] = '0';
										v[vi - 1]++;
									}
									break;
								case 'd':
									if (v[vi] < '5')
									{
										v[vi] = '0';
									}
									else
									{
										v[vi] = '5';
									}
									break;
							}
						}
					}
					String vr = new String(v);
					if (vr.charAt(0) == '0')
					{
						vr = vr.substring(1);
					}
					DBFunctions.getInstance().put(record, field, vr);
					DBFunctions.getInstance().put(record, Constants.BOOK_CHANGED_DATE, Constants.getTimestamp());
					if (include)
						DBFunctions.getInstance().put(record, Constants.BOOK_INCLUDE_IN_UPDATE, "true");
				}
				return true;
			}

			public void done()
			{
				frame.dispose();
				populateTable();
			}

		};
		(new Round()).execute();
	}

	private void percentageChoice(int field, String amountText, boolean include)
	{
		final WaitBox frame = new WaitBox();
		class Percentage extends SwingWorker
		{
			public Object doInBackground()
			{
				DBFunctions.getInstance().backup();
				double amount = Double.parseDouble(amountText);
				for (int i = 0; i < tm.getRowCount(); i++)
				{
					int record = Integer.parseInt((String) tm.getValueAt(i, 0));
					double oldAmount = Double.parseDouble((String) listTable.getValueAt(i, field));
					oldAmount = (oldAmount + (amount * oldAmount / 100));
					oldAmount = Math.floor(oldAmount * 100 + .5) / 100;
					DBFunctions.getInstance().put(record, field, Constants.twoPlaces.format(oldAmount));
					DBFunctions.getInstance().put(record, Constants.BOOK_CHANGED_DATE, Constants.getTimestamp());
					if (include)
						DBFunctions.getInstance().put(record, Constants.BOOK_INCLUDE_IN_UPDATE, "true");
				}
				return true;
			}

			public void done()
			{
				frame.dispose();
				populateTable();
			}

		};
		(new Percentage()).execute();
	}

	private void newChoice(int field, String amountText, boolean include)
	{
		final WaitBox frame = new WaitBox();
		class NewChoice extends SwingWorker
		{
			public Object doInBackground()
			{
				DBFunctions.getInstance().backup();
				for (int i = 0; i < tm.getRowCount(); i++)
				{
					int record = Integer.parseInt((String) tm.getValueAt(i, 0));
					DBFunctions.getInstance().put(record, field, amountText);
					DBFunctions.getInstance().put(record, Constants.BOOK_CHANGED_DATE, Constants.getTimestamp());
					if (include)
						DBFunctions.getInstance().put(record, Constants.BOOK_INCLUDE_IN_UPDATE, "true");
				}
				return true;
			}

			public void done()
			{
				frame.dispose();
				populateTable();
			}

		};
		(new NewChoice()).execute();
	}

	private void flatChoice(int field, String amountText, boolean include)
	{
		final WaitBox frame = new WaitBox();
		class Flat extends SwingWorker
		{
			public Object doInBackground()
			{
				DBFunctions.getInstance().backup();
				double amount = Double.parseDouble(amountText);
				for (int i = 0; i < tm.getRowCount(); i++)
				{
					int record = Integer.parseInt((String) tm.getValueAt(i, 0));
					double oldAmount = Double.parseDouble((String) listTable.getValueAt(i, field));
					oldAmount += amount;
					DBFunctions.getInstance().put(record, field, Constants.twoPlaces.format(oldAmount));
					DBFunctions.getInstance().put(record, Constants.BOOK_CHANGED_DATE, Constants.getTimestamp());
					if (include)
						DBFunctions.getInstance().put(record, Constants.BOOK_INCLUDE_IN_UPDATE, "true");
				}
				return true;
			}

			public void done()
			{
				frame.dispose();
				populateTable();
			}
		};
		(new Flat()).execute();
	}

	private void playMacro2(String selection, boolean include)
	{
		final WaitBox frame = new WaitBox();
		class Flat extends SwingWorker
		{
			public Object doInBackground()
			{
				DBFunctions.getInstance().backup();
				//"key", "addeddate", "changeddate", "validateddate", "recordno"
				ArrayList<String> map = new ArrayList<String>(Arrays.asList(Constants.shortNames));
				int currentComponent = -1;
				int currentCaret = 0;
				String pasteBoard = "";
				int idx = DBFunctions.getInstance().selectOne("MAC", Constants.MACRO_NAME, selection);
				String macro = DBFunctions.getInstance().get(idx, Constants.MACRO_TEXT);
				ArrayList<String> tokens = BooksImportExport.parseTokens(macro);

				for (int index = 0; index < tm.getRowCount(); index++)
				{
					int record = Integer.parseInt((String) tm.getValueAt(index, 0));
					for (String token : tokens)
					{
						if (!token.startsWith("[["))
						{
							if (currentComponent > 0)
							{
								StringBuilder text = new StringBuilder(DBFunctions.getInstance().get(record, currentComponent));
								text.insert(currentCaret, token);
								DBFunctions.getInstance().put(record, currentComponent, text.toString());
								currentCaret = currentCaret + token.length();
							}
						}
						else
						{
							token = token.substring(2, token.length() - 2);
							if (token.equals("clear"))
							{
								DBFunctions.getInstance().put(record, currentComponent, "");
								currentCaret = 0;
							}
							else if (token.equals("eof"))
							{
								currentCaret = DBFunctions.getInstance().get(record, currentComponent).length();
							}
							else if (token.equals("space?"))
							{
								if (currentCaret > 0)
								{
									StringBuilder text = new StringBuilder(DBFunctions.getInstance().get(record, currentComponent));
									text.append(" ");
									DBFunctions.getInstance().put(record, currentComponent, text.toString());
									currentCaret = currentCaret + 1;
								}
							}
							else if (token.startsWith("caret"))
							{
								int len = DBFunctions.getInstance().get(record, currentComponent).length();
								String[] q = token.split("-", 2);
								currentCaret = Math.min(Integer.parseInt(q[1]), len);
							}
							else if (token.startsWith("seekstart"))
							{
								String[] q = token.split("-", 2);
								String source = DBFunctions.getInstance().get(record, currentComponent);
								currentCaret = source.indexOf(q[1]);
								if (currentCaret < 0)
								{
									currentCaret = 0;
								}
							}
							else if (token.startsWith("seekend"))
							{
								String[] q = token.split("-", 2);
								String source = DBFunctions.getInstance().get(record, currentComponent);
								currentCaret = source.indexOf(q[1]);
								currentCaret = (currentCaret < 0) ? q[1].length() : currentCaret + q[1].length();
							}
							else if (token.equals("bs"))
							{
								if (currentCaret > 0)
								{
									StringBuilder text = new StringBuilder(DBFunctions.getInstance().get(record, currentComponent));
									text.deleteCharAt(currentCaret - 1);
									DBFunctions.getInstance().put(record, currentComponent, text.toString());
									currentCaret--;
								}
							}
							else if (token.equals("cut"))
							{
								pasteBoard = DBFunctions.getInstance().get(record, currentComponent);
								DBFunctions.getInstance().put(record, currentComponent, "");
							}
							else if (token.equals("copy"))
							{
								pasteBoard = DBFunctions.getInstance().get(record, currentComponent);
							}
							else if (token.equals("paste"))
							{
								DBFunctions.getInstance().put(record, currentComponent, pasteBoard);
							}
							else if (token.equals("true"))
							{
								DBFunctions.getInstance().put(record, currentComponent, "true");
							}
							else if (token.equals("false"))
							{
								DBFunctions.getInstance().put(record, currentComponent, "false");
							}
							else if (token.startsWith("field"))
							{
								String[] field = (token.split("-", 2));
								currentComponent = map.indexOf(field[1]);
								currentCaret = 0;
							}
						}
					}
					if (include)
					{
						DBFunctions.getInstance().put(record, Constants.BOOK_INCLUDE_IN_UPDATE, "true");
					}
				}
				return true;
			}

			public void done()
			{
				frame.dispose();
				populateTable();
			}
		};
		(new Flat()).execute();
	}

	private void populateAmazon()
	{
		int user;
		if (SettingsView.getInstance().get("user1").contains("AMZ"))
		{
			user = Constants.BOOK_USER1;
		}
		else if (SettingsView.getInstance().get("user2").contains("AMZ"))
		{
			user = Constants.BOOK_USER2;
		}
		else
		{
			JOptionPane.showMessageDialog(null, "No AMZ field found.");
			return;
		}

		JPanel panel = new JPanel(new ColumnLayout(1, 1));
		JLabel label = new JLabel("Selected Expedited Shipping choices:");
		JCheckBox nextCheckBox = new JCheckBox("Next Day");
		JCheckBox secondCheckBox = new JCheckBox("Two Day");
		JCheckBox domesticCheckBox = new JCheckBox("Domestic Expedited");
		JCheckBox intlExpCheckBox = new JCheckBox("Internat'l Expedited");
		JLabel seperator = new JLabel("--------------------");
		JCheckBox intlCheckBox = new JCheckBox("Standard International shipping??");
		JCheckBox includeCheckBox = new JCheckBox("Include in Update?");
		panel.add("whx", label);
		panel.add("whx", nextCheckBox);
		panel.add("whx", secondCheckBox);
		panel.add("whx", domesticCheckBox);
		panel.add("whx", intlExpCheckBox);
		panel.add("whx", seperator);
		panel.add("whx", intlCheckBox);
		panel.add("whx", includeCheckBox);
		int ret = JOptionPane.showConfirmDialog(null, panel, "Populating Amazon",
			JOptionPane.OK_CANCEL_OPTION);
		if (ret == JOptionPane.OK_OPTION)
		{
			final WaitBox frame = new WaitBox();
			class PopulateAmazon extends SwingWorker
			{
				public Object doInBackground()
				{
					DBFunctions.getInstance().backup();
					DBFunctions.getInstance().backup();
					String condition = ""; // field 0
					String djCondition = ""; // field 1
					String productID = ""; // field 2
					String intlShip = "n"; // field 3
					String expShip = "";  //field 4
					StringBuilder amzString = new StringBuilder();
					if (intlCheckBox.isSelected())
					{
						intlShip = "y";
					}
					if (nextCheckBox.isSelected())
					{
						expShip = "next";
					}
					if (secondCheckBox.isSelected())
					{
						if (!expShip.equals("")) expShip += ", ";
						expShip += "second";
					}
					if (domesticCheckBox.isSelected())
					{
						if (!expShip.equals("")) expShip += ", ";
						expShip += "domestic";
					}
					if (intlExpCheckBox.isSelected())
					{
						if (!expShip.equals("")) expShip += ", ";
						expShip += "international";
					}
					if (expShip.equals("")) expShip = "*";
					for (int i = 0; i < tm.getRowCount(); i++)
					{
						// Condition
						int index = Integer.parseInt((String)listTable.getValueAt(i, 0));
						String isbn = DBFunctions.getInstance().get(index, Constants.BOOK_ISBN);
						String cond = DBFunctions.getInstance().get(index, Constants.BOOK_CONDITION).toLowerCase();
						String djCond = DBFunctions.getInstance().get(index, Constants.BOOK_DJ_CONDITION).toLowerCase();
						String hors = DBFunctions.getInstance().get(index, Constants.BOOK_BINDING_TYPE).toLowerCase();
						String hasDJ = DBFunctions.getInstance().get(index, Constants.BOOK_HAS_DJ);
						String first = DBFunctions.getInstance().get(index, Constants.BOOK_FIRST_EDITION);
						String signed = DBFunctions.getInstance().get(index, Constants.BOOK_SIGNED);
						condition = "4";
						if (cond.startsWith("fine") || cond.startsWith("near fine")) condition = "1";
						else if (cond.startsWith("very g")) condition = "2";
						else if (cond.startsWith("good")) condition = "3";
						if ((hors.contains("hard")) && hasDJ.equals("false") && (Integer.parseInt(condition) < 3)) condition = "3";
						if (first.equals("true") || !signed.equals(""))
						{
							condition = Integer.toString(Integer.parseInt(condition) + 4 );
						}
						if (cond.startsWith("new")) condition = "11";

						// DJ Condition
						djCondition = "*";
						if (djCond.startsWith("fine") || djCond.startsWith("near fine")) djCondition = "1";
						else if (djCond.startsWith("very g")) djCondition = "2";
						else if (djCond.startsWith("good")) djCondition = "3";
						else if (djCond.startsWith("fair") || djCond.startsWith("poor")) djCondition = "4";
						else if (djCond.contains("no") || djCond.contains("miss")) djCondition = "0";

						// ProductID
						productID = "*";
						if (isbn.length() == 10) productID = isbn;
						amzString.setLength(0);
						amzString.append(condition);
						amzString.append("/");
						amzString.append(djCondition);
						amzString.append("/");
						amzString.append(productID);
						amzString.append("/");
						amzString.append(intlShip);
						amzString.append("/");
						amzString.append(expShip);

						DBFunctions.getInstance().put(index, user, amzString.toString());
					}
					populateTable();
					return true;
				}

				public void done()
				{
					frame.dispose();
				}

			};
			(new PopulateAmazon()).execute();
		}
		populateTable();
	}
}
