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
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PrintTable
{
	public static void printTable(JTable table)
	{
		JScrollPane printScrollPane = new JScrollPane();
		final JPanel printPanel = new JPanel();
		final JFrame frame = new JFrame();
		final JTable printTable = new JTable();
		printScrollPane.setViewportView(printTable);

		JButton cancelPrintButton = new GenericButton("cancel.png", "Cancel this print.", null);
		Action cancelButtonAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent ae1) // printList
			{
				frame.dispose();
			}
		};
		cancelPrintButton.addActionListener(cancelButtonAction);

		JButton printPrintButton = new GenericButton("print.png", "Print this window.", null);
		Action printButtonAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent ae2) // printList
			{
				new PrintThis(printTable);
				frame.dispose();
			}
		};
		printPrintButton.addActionListener(printButtonAction);

		JPanel buttonPanel = new JPanel(new ColumnLayout(0, 0));
		buttonPanel.add("x", cancelPrintButton);
		buttonPanel.add("w", printPrintButton);

		int count = table.getColumnCount();
		int height = table.getRowCount();

		String[] columnHeaders = new String[count];
		JCheckBox[] cb = new JCheckBox[count];
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(350, (count /2 ) * 16));
		panel.setLayout(new ColumnLayout(0, 0));
		// build check box window from table headers
		for (int i = 1; i < count; i++)
		{
			columnHeaders[i] = table.getColumnName(i);
			cb[i] = new JCheckBox(table.getColumnName(i));
			if (((i - 1) % 3 < 2) && (i != count - 1))
			{
				panel.add("hx", cb[i]);
			}
			else
			{
				panel.add("hxw", cb[i]);
			}
		}
		printPanel.setLayout(new ColumnLayout(0, 0));
		printPanel.add("hvwx", printScrollPane);
		printPanel.add("rwx", buttonPanel);

		if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(BooksView.getInstance(), panel, "Printing",
				JOptionPane.OK_CANCEL_OPTION))
		{
			// build an array to print from above choices
			ArrayList<Integer> selectedColumns = new ArrayList<Integer>();
			ArrayList<String> selectedColumnNames = new ArrayList<String>();
			for (int i = 1; i < count; i++)
			{
				if (cb[i].isSelected())
				{
					selectedColumns.add(i);
					selectedColumnNames.add(columnHeaders[i]);
				}
			}
			DefaultTableModel tm = new DefaultTableModel(height, selectedColumns.size());
			tm.setColumnIdentifiers(selectedColumnNames.toArray());
			printTable.setModel(tm);
			// columnHeaders.get(selectedColumns.get(i)) for column names
			for (int j = 0; j < height; j++)
			{// table.getValueAt(j,selectedColumns.get(i)) for cell
				for (int i = 0; i < selectedColumns.size(); i++)
				{
					printTable.setValueAt(table.getValueAt(j, selectedColumns.get(i)), j, i);
				}
			}
			frame.add(printPanel);
			frame.setSize(new Dimension(800, 600));
			frame.setVisible(true);
		}
	}
}
