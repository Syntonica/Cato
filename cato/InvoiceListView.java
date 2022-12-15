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

import javax.swing.JOptionPane;

public class InvoiceListView extends GenericListView
{
	private static String[] invoiceNames = { "ID", "Invoice Number", "Date", "Client Name", "User", "Comments",
			"Ship To", "Payment Method", "Tax 1", "Tax 2", "Tax 3", "Discount", "Shipping", "Final Total" };

	private static InvoiceListView instance = null;

	public static InvoiceListView getInstance()
	{
		if (instance == null)
		{
			instance = new InvoiceListView();
		}
		return instance;
	}

	public InvoiceListView()
	{
		super("INV", "Invoice", invoiceNames, InvoiceEditView.getInstance(), 4);
		Constants.windowNames.put(this, "Invoices");
		Constants.windowIcons.put(this, "invoice.png");
	}

	public void updateCombos()
	{
		invoiceNames[Constants.INVOICE_TAXES1] = SettingsView.getInstance().get("tax1");
		listTable.getColumnModel().getColumn(Constants.INVOICE_TAXES1)
				.setHeaderValue(invoiceNames[Constants.INVOICE_TAXES1]);
		invoiceNames[Constants.INVOICE_TAXES2] = SettingsView.getInstance().get("tax2");
		listTable.getColumnModel().getColumn(Constants.INVOICE_TAXES2)
				.setHeaderValue(invoiceNames[Constants.INVOICE_TAXES2]);
		invoiceNames[Constants.INVOICE_TAXES3] = SettingsView.getInstance().get("tax3");
		listTable.getColumnModel().getColumn(Constants.INVOICE_TAXES3)
				.setHeaderValue(invoiceNames[Constants.INVOICE_TAXES3]);
		super.updateCombos();
	}

	public void setValueLabel()
	{
		valueLabel.setText("<html><center>[ " + listTable.getRowCount() + " Invoices ]" + "[ "
				+ SettingsView.getInstance().get("tax1") + ": " + sum(Constants.INVOICE_TAXES1) + " ]" + "[ "
				+ SettingsView.getInstance().get("tax2") + ": " + sum(Constants.INVOICE_TAXES2) + " ]" + "[ "
				+ SettingsView.getInstance().get("tax3") + ": " + sum(Constants.INVOICE_TAXES3) + " ]" + "[ Discount: "
				+ sum(Constants.INVOICE_DISCOUNT) + " ]" + "[ Shipping: " + sum(Constants.INVOICE_SHIPPING) + " ]"
				+ "[ Final Total: " + sum(Constants.INVOICE_FINAL_TOTAL) + " ]</center></html>");
	}

	public void deleteRecord()
	{
		// get row selected
		int index = listTable.getSelectedRow();
		if (index != -1)
		{
			String id = (String) listTable.getValueAt(index, 0);
			String invNo = (String) listTable.getValueAt(index, Constants.INVOICE_NUMBER);
			if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(BooksView.getInstance(),
					"Delete Invoice #" + listTable.getValueAt(index, Constants.INVOICE_NUMBER) + "?", "Deleting", JOptionPane.OK_CANCEL_OPTION))
			{
				while (true)
				{
					int remove = DBFunctions.getInstance().selectOne("SAL", Constants.SALE_INVOICE_NUMBER, invNo);
					if (remove != -1)
					{
						SaleListView.getInstance().verboseDeleteSale(remove, false);
					}
					else
					{
						break;
					}
				}
				DBFunctions.getInstance().remove(Integer.parseInt((String) listTable.getValueAt(index, 0)));
				populateTable();
			}
		}
	}
}
