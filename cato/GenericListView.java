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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

public class GenericListView extends JPanel
{
	public JTable listTable;
	private String[] choices;
	private String tag;
	private String name;
	public JScrollPane listScrollPane;
	private GenericEditView editView;
	public JPanel searchPanel = new JPanel();
	private int anyCount;
	public JComboBox[] anyComboBox;
	public JTextField[] anyTextField;

	public JLabel valueLabel = new JLabel();

	Action addAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			addRecord();
		}
	};

	Action editAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			editRecord();
		}
	};

	Action deleteAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			deleteRecord();
		}
	};

	Action printAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e) // printList
		{
			PrintTable.printTable(listTable);
		}
	};

	Action clearAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent evt) // printList
		{
			for (int i = 0; i < anyCount; i++)
			{
				anyTextField[i].setText("");
			}
			populateTable();
		}
	};

	Action exportAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			exportList();
		}
	};

	Action importAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			importList();
		}
	};

	JButton clearButton = new GenericButton("clear.png", "Clear the filters.", clearAction);
	JButton addButton = new GenericButton("add.png", "Add a new entry.", addAction);
	JButton deleteButton = new GenericButton("delete.png", "Delete selected entry.", deleteAction);
	JButton editButton = new GenericButton("edit.png", "Edit selected entry.", editAction);
	JButton printButton = new GenericButton("print.png", "Print the displayed list.", printAction);
	JButton exportButton = new GenericButton("up.png", "Export this list to a file.", exportAction);
	JButton importButton = new GenericButton("down.png", "Import this list from a file.", importAction);

	private Timer timer = new Timer(333, new ActionListener() // 333ms
	{
		public void actionPerformed(ActionEvent evt)
		{
			populateTable();
			timer.stop();
		}
	});

	public GenericListView(String tag, String name, String[] choices, GenericEditView editView, int anyCount)
	{
		this.tag = tag;
		this.name = name;
		this.choices = choices;
		this.editView = editView;
		this.anyCount = anyCount;
		initComponents();
	}

	public void populateTable()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				int oldRow = listTable.getSelectedRow();
				runFilter();
				setValueLabel();
				if (listTable.getRowCount() > 0)
				{
					oldRow = Constants.clamp(oldRow, 0, listTable.getRowCount() - 1);
					listTable.setRowSelectionInterval(oldRow, oldRow);
				}
			}
		});
	}

	public void loadColumns()
	{
		listTable.getColumnModel().getColumn(0).setMinWidth(1);
		listTable.getColumnModel().getColumn(0).setPreferredWidth(1);
		listTable.getColumnModel().getColumn(0).setMaxWidth(1);
		for (int i = 1; i < listTable.getColumnCount(); i++)
		{
			String width = SettingsView.getInstance().get(tag + ".col." + i);
			int w = 75;
			if (!width.equals("")) w = Integer.parseInt(width);
			listTable.getColumnModel().getColumn(i).setPreferredWidth(w);
		}
		listTable.doLayout();
	}

	public void setValueLabel()
	{
		valueLabel.setText("[ " + listTable.getRowCount() + " " + name + " Entries ]");
	}

	public void addRecord()
	{
		if (BooksView.getInstance().tabbedPane.isTabOpen(editView))
		{
			if (!editView.saveEntry()) return;
		}
		else
		{
			BooksView.getInstance().tabbedPane.openTab(editView);
		}
		editView.setupEntry(-1);
	}

	public void editRecord()
	{
		if (BooksView.getInstance().tabbedPane.isTabOpen(editView))
		{
			if (!editView.saveEntry()) return;
		}
		else
		{
			BooksView.getInstance().tabbedPane.openTab(editView);
		}
		final int index = listTable.getSelectedRow();
		editView.setupEntry(Integer.parseInt((String) listTable.getValueAt(index, 0)));
	}

	public void deleteRecord()
	{
		int index = listTable.getSelectedRow();
		if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(BooksView.getInstance(),
				"Delete " + name + " " + listTable.getValueAt(index, Constants.LIST_VALUE) + "?", "Deleting",
				JOptionPane.OK_CANCEL_OPTION))
		{
			DBFunctions.getInstance().remove(Integer.parseInt((String) listTable.getValueAt(index, 0)));
			populateTable();
		}
	}

	public String sum(int column)
	{
		double sum = 0.00;
		for (int i = 0; i < listTable.getRowCount(); i++)
		{
			String temp = (String) listTable.getValueAt(i, column);
			if (!temp.equals("")) sum += Double.parseDouble(temp);
		}
		return Constants.twoPlaces.format(sum);
	}

	public void runFilter()
	{
		((DefaultTableModel) listTable.getModel()).setRowCount(0);
		// now run the filter
		String[] row;
		for (int i = 0; i < DBFunctions.getInstance().size(); i++)
		{
			if (DBFunctions.getInstance().get(i, Constants.RECORD_TAG).equals(tag))
			{
				boolean addRow = true;
				for (int j = 0; j < anyCount; j++)
				{
					addRow = addRow && applyFilter(anyTextField[j].getText().toLowerCase(),
							DBFunctions.getInstance().get(i, anyComboBox[j].getSelectedIndex() + 1).toLowerCase(), false);
				}
				if (addRow)
				{ // change the tag to the record number
					row = DBFunctions.getInstance().getRecord(i).clone();
					row[0] = Integer.toString(i);
					((DefaultTableModel) listTable.getModel()).addRow(row);
				}
			}
		}
	}

	public static boolean applyFilter(String filter, String field, boolean numeric)
	{
		if (filter.length() < 1) return true;
		char c = filter.charAt(0);
		String filter2 = filter.substring(1, filter.length());
		boolean fl = (filter2.length() > 0);
		switch (c)
		{
			case '#':
				return field.length() <= 0;
			case '*':
				return ((fl || field.length() > 0) && (field.indexOf(filter2) >= 0));
			case '~':
				return (!fl || (field.indexOf(filter2) < 0));
			case '>':
				if (fl && numeric)
				{
					return (Comparators.NUMBER_ORDER.compare(field, filter2) > 0);
				}
				else
					return (!fl || (field.compareTo(filter2) >= 0));
			case '<':
				if (fl && numeric)
				{
					return (Comparators.NUMBER_ORDER.compare(field, filter2) < 0);
				}
				else
					return (!fl || (field.compareTo(filter2) <= 0));
			case '@':
				String[] filters = filter2.split("@");
				if (filters.length > 1)
				{
					if (numeric)
					{
						return ((Comparators.NUMBER_ORDER.compare(field, filters[0]) >= 0)
								&& (Comparators.NUMBER_ORDER.compare(field, filters[1]) < 0));
					}
					else
					{
						return ((field.compareTo(filters[0]) > 0) && (field.compareTo(filters[1]) < 0));
					}
				}
				return true;
			default:
				char[] f1 = filter.toCharArray();
				char[] f2 = field.toCharArray();
				for (int i = 0; i < Math.min(f1.length, f2.length); i++)
				{
					if (f1[i] == '?') f2[i] = '?';
				}
				field = new String(f2);
				// end
				return (field.startsWith(filter));
		}
	}

	public void updateCombos()
	{
		ArrayList list = new ArrayList(Arrays.asList(choices));
		list.remove(0);
		if (this == BookListView.getInstance()) Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < anyCount; i++)
		{
			anyComboBox[i].setModel(new DefaultComboBoxModel(list.toArray()));
		}

		for (int i = 0; i < anyCount; i++)
		{
			if (SettingsView.getInstance().get(tag + ".anyField" + i).equals(""))
			{
				anyComboBox[i].setSelectedIndex(i);
			}
			else
			{
				anyComboBox[i].setSelectedItem(SettingsView.getInstance().get(tag + ".anyField" + i));
			}
		}
	}

	private void importList()
	{
		DBFunctions.getInstance().importList(name, tag, Constants.MERGEABLE);
	}

	private void exportList()
	{
		DBFunctions.getInstance().exportList(name, tag);
	}

	private void initComponents()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent e)
			{
				updateCombos();
				populateTable();
				loadColumns();
				if ((GenericListView.this != ListEditView.getInstance()) && (GenericListView.this != FTPListView.getInstance()))
					anyTextField[0].requestFocusInWindow();
			}
		});

		listTable = new JTable(new DefaultTableModel(choices, 0))
		{
			public boolean isCellEditable(int row, int column)
			{
				return (false);
			}
		};

		listTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() > 1) editRecord();
			}
		});

		listTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listTable.getTableHeader().setReorderingAllowed(false);
		listTable.setShowGrid(true);
		listTable.setFont(Cato.catoFont);
		listTable.getTableHeader().setFont(Cato.catoFont);
		listTable.setRowHeight(Cato.textSize + 4);
		listTable.setAutoCreateRowSorter(true);

		listTable.getTableHeader().addMouseListener(new MouseAdapter()
		{ // allow Unsorted as an option
			private SortOrder currentOrder = SortOrder.UNSORTED;
			private int lastCol = -1;

			public void mouseClicked(MouseEvent e)
			{
				int column = listTable.getTableHeader().columnAtPoint(e.getPoint());
				column = listTable.convertColumnIndexToModel(column);
				if (column != lastCol)
				{
					currentOrder = SortOrder.UNSORTED;
					lastCol = column;
				}
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					RowSorter sorter = listTable.getRowSorter();
					ArrayList<SortKey> sortKeys = new ArrayList<SortKey>();
					switch (currentOrder)
					{
						case UNSORTED:
							sortKeys.add(new RowSorter.SortKey(column, currentOrder = SortOrder.ASCENDING));
							break;
						case ASCENDING:
							sortKeys.add(new RowSorter.SortKey(column, currentOrder = SortOrder.DESCENDING));
							break;
						case DESCENDING:
							sortKeys.add(new RowSorter.SortKey(column, currentOrder = SortOrder.UNSORTED));
							break;
					}
					sorter.setSortKeys(sortKeys);
				}
			}

			public void mouseReleased(MouseEvent arg0)
			{
				for (int i = 1; i < listTable.getColumnCount(); i++)
				{
					SettingsView.getInstance().setNoLog(tag + ".col." + i,
							Integer.toString(listTable.getColumnModel().getColumn(i).getPreferredWidth()));
				}
			}
		});

		if (choices.length > 7) listTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		listScrollPane = new JScrollPane(listTable);

		anyTextField = new GenericTextField[anyCount];
		anyComboBox = new JComboBox[anyCount];
		for (int i = 0; i < anyCount; i++)
		{
			final int j = i;
			anyTextField[i] = new GenericTextField(Constants.FIELD_ALL, false);
			anyTextField[i].setPreferredSize(new Dimension(100, Cato.compHeight));
			anyTextField[i].addKeyListener(new KeyAdapter()
			{
				public void keyTyped(KeyEvent evt)
				{
					timer.restart();
				}
			});
			List<Object> list = new ArrayList<Object>(Arrays.asList(choices));
			list.remove(0);
			anyComboBox[i] = new JComboBox();
			anyComboBox[i].setModel(new DefaultComboBoxModel(list.toArray()));
			anyComboBox[i].setFont(Cato.catoFont);
			anyComboBox[i].setFocusable(false);
			anyComboBox[i].addPopupMenuListener(new PopupMenuListener()
			{
				public void popupMenuCanceled(PopupMenuEvent evt)
				{}

				public void popupMenuWillBecomeInvisible(PopupMenuEvent evt)
				{
					populateTable();
					SettingsView.getInstance().setNoLog(tag + ".anyField" + j, (String) anyComboBox[j].getSelectedItem());
				}

				public void popupMenuWillBecomeVisible(PopupMenuEvent evt)
				{}
			});
		}

		searchPanel.setLayout(new ColumnLayout(0, 0));
		for (int i = 0; i < anyCount / 2; i++)
		{
			searchPanel.add("x", anyComboBox[i]);
			searchPanel.add("hx", anyTextField[i]);
			searchPanel.add("x", anyComboBox[i + anyCount / 2]);
			searchPanel.add("hwx", anyTextField[i + anyCount / 2]);
		}

		if ((!tag.equals("BKS")) && (!tag.equals("FTP")) && (!tag.equals("LST")))
		{
			setLayout(new ColumnLayout(1, 1));
			add("7x", clearButton);
			add("", addButton);
			add("", editButton);
			add("", deleteButton);
			add("", printButton);
			add("", importButton);
			add("w", exportButton);
			add("wxh", searchPanel);
			add("whvx", listScrollPane);
			add("wcx", valueLabel);
		}
	}
}