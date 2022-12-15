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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;


public class QuickBookLookup extends JDialog
{
	private Object[] columnNames = { "Book ID", "Author", "Title" };
	private DefaultTableModel tm = new DefaultTableModel(columnNames, 0);

	private JTextField bookIDTextField = new JTextField();
	private JTextField authorTextField = new JTextField();
	private JTextField titleTextField = new JTextField();
	private JTable bookListTable = new JTable(tm)
	{
		public boolean isCellEditable(int row, int column)
		{
			return (false);
		}
	};
	private JScrollPane bookListScrollPane = new JScrollPane(bookListTable);
	SaleEditView sev;

	public QuickBookLookup(SaleEditView sev)
	{
		this.sev = sev;
		initComponents();
	}

	public void bookLookup()
	{
		this.setVisible(true);
	}

	private void populateTable()
	{
		tm.setRowCount(0);
		for (int i = 0; i < DBFunctions.getInstance().size(); i++)
		{
			if (DBFunctions.getInstance().get(i, Constants.RECORD_TAG).equals("BKS"))
			{
				String[] r = {
						DBFunctions.getInstance().get(i, Constants.BOOK_ID),
						DBFunctions.getInstance().get(i, Constants.BOOK_AUTHOR),
						DBFunctions.getInstance().get(i, Constants.BOOK_TITLE)
						};
				if ((r[0].toLowerCase().startsWith(bookIDTextField.getText().toLowerCase()))
					&& (r[1].toLowerCase().startsWith(authorTextField.getText().toLowerCase()))
					&& (r[2].toLowerCase().startsWith(titleTextField.getText().toLowerCase())))
				{
					tm.addRow(r);
				}
			}
		}
		bookListTable.setModel(tm);
	}

	private void initComponents()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent e)
			{
				populateTable();
			}
		});

		bookListTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() > 1)
				{
					int index = bookListTable.getSelectedRow();
					sev.lookupBookReentry((String) bookListTable.getValueAt(index, 0));
					dispose();
				}
			}
		});

		bookIDTextField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent evt)
			{
				populateTable();
			}
		});
		authorTextField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent evt)
			{
				populateTable();
			}
		});
		titleTextField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent evt)
			{
				populateTable();
			}
		});

		JPanel searchPanel = new JPanel(new ColumnLayout());
		searchPanel.add("hx", bookIDTextField);
		searchPanel.add("hx", authorTextField);
		searchPanel.add("hxw", titleTextField);
		JPanel layout = new JPanel(new ColumnLayout());
		layout.add("xhw", searchPanel);
		layout.add("xvhw", bookListScrollPane);
		add(layout);

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent ev)
			{
				dispose();
			}
		});

		bookListTable.getColumnModel().getColumn(0).setMinWidth(234);
		bookListTable.getColumnModel().getColumn(0).setPreferredWidth(234);
		bookListTable.getColumnModel().getColumn(0).setMaxWidth(234);
		bookListTable.getColumnModel().getColumn(0).setResizable(false);
		bookListTable.getColumnModel().getColumn(1).setMinWidth(234);
		bookListTable.getColumnModel().getColumn(1).setPreferredWidth(234);
		bookListTable.getColumnModel().getColumn(1).setMaxWidth(234);
		bookListTable.getColumnModel().getColumn(1).setResizable(false);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("Quick Book Lookup");
		setMinimumSize(new Dimension(702, 360));
		setPreferredSize(new Dimension(702, 360));
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}
}