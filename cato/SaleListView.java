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

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.table.TableRowSorter;

public class SaleListView extends GenericListView
{
	public static final String[] saleNames = { "SAL", "Sales Number", "Sales Person", "Date", "Book ID", "Author",
			"Title", "Qty", "Inv. #", "Reg. Price", "List", "Sales Price", "Ship Cred.", "Comm.", "2nd Comm.", "Fee",
			"Shipping", "Tax 1", "Tax 2", "Tax 3", "Cost" };

	private static SaleListView instance = null;

	public static SaleListView getInstance()
	{
		if (instance == null)
		{
			instance = new SaleListView();
		}
		return instance;
	}

	public SaleListView()
	{
		super("SAL", "Sale", saleNames, SaleEditView.getInstance(), 4);
		Constants.windowNames.put(this, "Sales");
		Constants.windowIcons.put(this, "sell.png");
		// valueLabel.setPreferredSize(new Dimension(600, 40));
		((TableRowSorter) listTable.getRowSorter()).setComparator(Constants.SALE_NUMBER, Comparators.NUMBER_ORDER);
	}

	public void updateCombos()
	{
		saleNames[Constants.SALE_TAXES1] = SettingsView.getInstance().get("tax1");
		listTable.getColumnModel().getColumn(Constants.SALE_TAXES1).setHeaderValue(saleNames[Constants.SALE_TAXES1]);
		saleNames[Constants.SALE_TAXES2] = SettingsView.getInstance().get("tax2");
		listTable.getColumnModel().getColumn(Constants.SALE_TAXES2).setHeaderValue(saleNames[Constants.SALE_TAXES2]);
		saleNames[Constants.SALE_TAXES3] = SettingsView.getInstance().get("tax3");
		listTable.getColumnModel().getColumn(Constants.SALE_TAXES3).setHeaderValue(saleNames[Constants.SALE_TAXES3]);
		super.updateCombos();
	}

	public void deleteRecord()
	{
		int index = listTable.getSelectedRow();
		verboseDeleteSale(Integer.parseInt((String) listTable.getValueAt(index, 0)), true);
	}

	public void setValueLabel()
	{
		String asp = "0.00";
		String totalQuantity = sum(Constants.SALE_QUANTITY);
		String totalSaleAmount = sum(Constants.SALE_SALE_AMOUNT);
		String totalShippingCredit = sum(Constants.SALE_SHIPPING_CREDIT);
		String totalSaleShippingCost = sum(Constants.SALE_SHIPPING_COST);
		String totalSalesCommission = sum(Constants.SALE_COMMISSION);
		String total2ndSalesCommission = sum(Constants.SALE_SECOND_COMMISSION);
		String totalSalesFee = sum(Constants.SALE_FEE);
		String totalCost = sum(Constants.SALE_COST);

		double tsa = Double.parseDouble(totalSaleAmount);
		double tq = Double.parseDouble(totalQuantity);
		if (tq > 0) asp = Constants.twoPlaces.format(tsa / tq);

		double net = tsa + Double.parseDouble(totalShippingCredit) - Double.parseDouble(totalSaleShippingCost)
				- Double.parseDouble(totalSalesCommission) - Double.parseDouble(total2ndSalesCommission)
				- Double.parseDouble(totalSalesFee) - Double.parseDouble(totalCost);

		valueLabel.setText("<html><center>[ Total Sales: " + Integer.toString(listTable.getRowCount()) + " ]"
				+ "[ Quantity: " + totalQuantity + " ]" + "[ Sale Amount: " + totalSaleAmount + " ]" + "[ ASP: " + asp
				+ " ]" + "[ Net: " + Constants.twoPlaces.format(net) + " ]" + "[ Ship Credit: " + totalShippingCredit
				+ " ]" + "[ Ship Cost: " + totalSaleShippingCost + " ]</center>" +

				"<center>[ Commission: " + totalSalesCommission + " ]" + "[ 2nd Comm.: " + total2ndSalesCommission
				+ " ]" + "[ Fee: " + totalSalesFee + " ]" + "[ " + SettingsView.getInstance().get("tax1") + ": "
				+ sum(Constants.SALE_TAXES1) + " ]" + "[ " + SettingsView.getInstance().get("tax2") + ": "
				+ sum(Constants.SALE_TAXES2) + " ]" + "[ " + SettingsView.getInstance().get("tax3") + ": "
				+ sum(Constants.SALE_TAXES3) + " ]" + "[ Cost: " + totalCost + " ]</center></html>");
	}

	public void verboseDeleteSale(int index, boolean fromSales)
	{
		String invoiceNumber = DBFunctions.getInstance().get(index, Constants.SALE_INVOICE_NUMBER);
		if (!invoiceNumber.equals("") && fromSales)
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(), "This sale belongs Invoice #" + invoiceNumber + ".");
		}
		else
		{
			JPanel panel = new JPanel();
			panel.setLayout(new ColumnLayout(0, 0));
			JLabel label = new JLabel("Delete Sale " + DBFunctions.getInstance().get(index, Constants.SALE_BOOK_ID) + ": "
					+ DBFunctions.getInstance().get(index, Constants.SALE_AUTHOR) + ", "
					+ DBFunctions.getInstance().get(index, Constants.SALE_TITLE) + "?");
			label.setFont(Cato.catoFont);
			JCheckBox restore = new JCheckBox("Restore copy to inventory?", false);
			restore.setHorizontalAlignment(SwingConstants.LEFT);
			restore.setFont(Cato.catoFont);
			JCheckBox include = new JCheckBox("Include in Update?", false);
			include.setHorizontalAlignment(SwingConstants.LEFT);
			include.setFont(Cato.catoFont);
			panel.add("xw", label);
			panel.add("xw", restore);
			panel.add("xw", include);
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(BooksView.getInstance(), panel, "Delete Sale",
					JOptionPane.YES_NO_OPTION))
			{
				if (restoreSale(index, include.isSelected()))
				{
					DBFunctions.getInstance().remove(index);
				}
			}
			else
			{ // remove sale from invoice
				DBFunctions.getInstance().put(index, Constants.SALE_INVOICE_NUMBER, "");
			}
			populateTable();
		}
	}

	public boolean restoreSale(int index, boolean include)
	{
		int qty = Integer.parseInt(DBFunctions.getInstance().get(index, Constants.SALE_QUANTITY));
		int i = DBFunctions.getInstance().selectOne("BKS", Constants.BOOK_ID, DBFunctions.getInstance().get(index, Constants.SALE_BOOK_ID));
		if (i != -1)
		{
			int qty2 = qty + Integer.parseInt(DBFunctions.getInstance().get(i, Constants.BOOK_QUANTITY));
			DBFunctions.getInstance().put(i, Constants.BOOK_QUANTITY, Integer.toString(qty2));
			DBFunctions.getInstance().put(i, Constants.BOOK_STATUS, "For Sale");
			DBFunctions.getInstance().put(i, Constants.BOOK_CHANGED_DATE, Constants.getTimestamp());
			if (include)
			{
				DBFunctions.getInstance().put(i, Constants.BOOK_INCLUDE_IN_UPDATE, Boolean.toString(include));
			}
		}
		else
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(), "Book ID: "
					+ DBFunctions.getInstance().get(index, Constants.SALE_BOOK_ID) + " no longer exists in the database.");
		}
		return true;
	}
}
