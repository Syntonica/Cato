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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InvoiceEditView extends GenericEditView
{
	Action addBookAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e) // printPage
		{
			addSaleItem();
		}
	};

	Action deleteBookAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e) // printPage
		{
			deleteSaleItem();
		}
	};

	Action attachSaleAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e) // printPage
		{
			attachSale();
		}
	};

	AbstractAction cancelAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			cancelEntry();
		}
	};

	AbstractAction saveAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			if (saveEntry()) cancelEntry();
		}
	};

	AbstractAction printInvoiceAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			printInvoice();
		}
	};

	private JLabel tax1Label = new JLabel("", SwingConstants.RIGHT);
	private JLabel tax2Label = new JLabel("", SwingConstants.RIGHT);
	private JLabel tax3Label = new JLabel("", SwingConstants.RIGHT);
	public GenericComboBox clientComboBox = new GenericComboBox();
	public GenericComboBox paymentComboBox = new GenericComboBox();
	private JLabel clientLabel = new JLabel("• Client:", SwingConstants.RIGHT);
	private JLabel commentsLabel = new JLabel("Comments:", SwingConstants.RIGHT);
	private JLabel dateLabel = new JLabel("Date:", SwingConstants.RIGHT);
	private JLabel saleLabel = new JLabel("Sale Amt:", SwingConstants.RIGHT);
	private JLabel finalTotalLabel = new JLabel("Final Total:", SwingConstants.RIGHT);
	private JLabel invoiceNoLabel = new JLabel("Invoice #:", SwingConstants.RIGHT);
	private JLabel pymtMethodLabel = new JLabel("Pymt. Method:", SwingConstants.RIGHT);
	private JLabel shippingLabel = new JLabel("Shipping:", SwingConstants.RIGHT);
	private JLabel shipToLabel = new JLabel("Ship To:", SwingConstants.RIGHT);
	private JLabel totalTaxesLabel = new JLabel("Taxes:", SwingConstants.RIGHT);
	private JLabel userLabel = new JLabel("User:", SwingConstants.RIGHT);
	public JTextArea commentsTextArea = new GenericTextArea(true);
	public JTextArea shipToTextArea = new GenericTextArea(true);
	private JScrollPane commentsScrollPane = new JScrollPane(commentsTextArea);
	private JScrollPane shipToScrollPane = new JScrollPane(shipToTextArea);
	public JTextField dateTextField = new GenericTextField(Constants.FIELD_ALL, true);
	public JTextField discountTextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField invoiceNoTextField = new GenericTextField(Constants.FIELD_NUMBER, true);
	public JTextField shippingTextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField tax1TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField tax2TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField tax3TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField taxesTextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField totalTextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField userTextField = new GenericTextField(Constants.FIELD_ALL, true);

	public JButton saveButton = new GenericButton("save.png", "Save this entry.", saveAction);
	public JButton cancelButton = new GenericButton("cancel.png", "Cancel this entry.", cancelAction);
	public JButton addBookButton = new GenericButton("addbook.png", "Add a book to this Invoice.", addBookAction);
	public JButton deleteBookButton = new GenericButton("deletebook.png", "Delete selected book from Invoice.",
			deleteBookAction);
	public JButton attachSaleButton = new GenericButton("sell.png", "Attach a Sale to this Invoice.",
			attachSaleAction);
	public JButton printeButton = new GenericButton("print.png", "Pretty print this Invoice.",
			printInvoiceAction);

	public InvoiceEntry invoiceEntry = new InvoiceEntry();
	public ArrayList<Double> saleList = new ArrayList<Double>();
	// Sales IDs <0 = deleted, >0 = added .5=original

	private Object[] columnNames = { "Sales ID", "Book ID", "Author", "Title", "Qty", "Reg Price", "Discount",
			"Net Price" };
	public JTable invoiceTable = new JTable(new DefaultTableModel(columnNames, 0))
	{
		public boolean isCellEditable(int row, int column)
		{
			return (false);
		}
	};
	private JScrollPane salesScrollPane = new JScrollPane(invoiceTable);

	private static InvoiceEditView instance = null;

	public static InvoiceEditView getInstance()
	{
		if (instance == null)
		{
			instance = new InvoiceEditView();
		}
		return instance;
	}

	public InvoiceEditView()
	{
		initComponents();
		Constants.windowNames.put(this, "Invoice Edit");
		Constants.windowIcons.put(this, "invoice.png");
	}

	public void setupEntry(int id)
	{
		saleList.clear();
		((DefaultTableModel) invoiceTable.getModel()).setRowCount(0);

		tax1Label.setText(SettingsView.getInstance().get("tax1") + ":");
		tax2Label.setText(SettingsView.getInstance().get("tax2") + ":");
		tax3Label.setText(SettingsView.getInstance().get("tax3") + ":");

		paymentListBuild();
		clientListBuild();

		if (id != -1)
		{
			invoiceEntry = new InvoiceEntry(id);
		}
		else
		{
			invoiceEntry = new InvoiceEntry();
		}
		invoiceNoTextField.setText(invoiceEntry.invoiceNo);
		dateTextField.setText(invoiceEntry.date);
		clientComboBox.setText(invoiceEntry.client);
		userTextField.setText(invoiceEntry.user);
		commentsTextArea.setText(invoiceEntry.comments);
		shipToTextArea.setText(invoiceEntry.shipTo);
		paymentComboBox.setText(invoiceEntry.paymentMethod);
		tax1TextField.setText(invoiceEntry.taxes1);
		tax2TextField.setText(invoiceEntry.taxes2);
		tax3TextField.setText(invoiceEntry.taxes3);
		discountTextField.setText(invoiceEntry.discount);
		shippingTextField.setText(invoiceEntry.shipping);
		totalTextField.setText(invoiceEntry.total);

		invoiceTable.getColumnModel().getColumn(0).setMinWidth(48);
		invoiceTable.getColumnModel().getColumn(0).setPreferredWidth(48);
		invoiceTable.getColumnModel().getColumn(0).setMaxWidth(80);
		invoiceTable.getColumnModel().getColumn(4).setPreferredWidth(24);
		invoiceTable.getColumnModel().getColumn(5).setMinWidth(7);
		invoiceTable.getColumnModel().getColumn(5).setPreferredWidth(7);
		invoiceTable.getColumnModel().getColumn(6).setMinWidth(7);
		invoiceTable.getColumnModel().getColumn(6).setPreferredWidth(7);

		buildTable();
		updateTotals();
	}

	public void buildTable()
	{
		((DefaultTableModel) invoiceTable.getModel()).setRowCount(0);
		ArrayList<Integer> i = DBFunctions.getInstance().selectAll("SAL", Constants.SALE_INVOICE_NUMBER, invoiceEntry.invoiceNo);
		for (int j : i)
		{
			Object[] row = { DBFunctions.getInstance().get(j, Constants.SALE_NUMBER), DBFunctions.getInstance().get(j, Constants.SALE_BOOK_ID),
					DBFunctions.getInstance().get(j, Constants.SALE_AUTHOR), DBFunctions.getInstance().get(j, Constants.SALE_TITLE),
					DBFunctions.getInstance().get(j, Constants.SALE_QUANTITY), DBFunctions.getInstance().get(j, Constants.SALE_REGULAR_PRICE),
					Constants.twoPlaces.format(Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_REGULAR_PRICE))
							- Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_SALE_AMOUNT))),
					Constants.twoPlaces.format(Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_SALE_AMOUNT))
							* Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_QUANTITY))) };
			((DefaultTableModel) invoiceTable.getModel()).addRow(row);
			saleList.add(Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_NUMBER)) + .5);
		}
	}

	public void refreshTable()
	{
		((DefaultTableModel) invoiceTable.getModel()).setRowCount(0);
		for (double i : saleList)
		{
			if (i > 0)
			{
				int j = DBFunctions.getInstance().selectOne("SAL", Constants.SALE_NUMBER, Integer.toString((int) Math.floor(i)));
				Object[] row = { DBFunctions.getInstance().get(j, Constants.SALE_NUMBER),
						DBFunctions.getInstance().get(j, Constants.SALE_BOOK_ID), DBFunctions.getInstance().get(j, Constants.SALE_AUTHOR),
						DBFunctions.getInstance().get(j, Constants.SALE_TITLE), DBFunctions.getInstance().get(j, Constants.SALE_QUANTITY),
						DBFunctions.getInstance().get(j, Constants.SALE_REGULAR_PRICE),
						Constants.twoPlaces.format(Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_REGULAR_PRICE))
								- Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_SALE_AMOUNT))),
						Constants.twoPlaces.format(Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_SALE_AMOUNT))
								* Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_QUANTITY))) };
				((DefaultTableModel) invoiceTable.getModel()).addRow(row);
			}
		}
	}

	public void attachSale()
	{
		String saleNumber = JOptionPane.showInputDialog(BooksView.getInstance(), "Which Sale Number to attach?");
		int idx = DBFunctions.getInstance().selectOne("SAL", Constants.SALE_NUMBER, saleNumber);
		if (idx > 0)
		{
			String invNo = DBFunctions.getInstance().get(idx, Constants.SALE_INVOICE_NUMBER);
			if (invNo.equals(""))
			{
				DBFunctions.getInstance().put(idx, Constants.SALE_INVOICE_NUMBER, invoiceEntry.invoiceNo);
				saleList.add(Double.parseDouble(saleNumber));
				refreshTable();
			}
			else
			{
				JOptionPane.showMessageDialog(BooksView.getInstance(), "That sales belongs to Invoice #" + invNo);
			}
		}
	}

	public void addSaleItem()
	{
		if (BooksView.getInstance().tabbedPane.isTabOpen(SaleEditView.getInstance()))
		{
			if (!SaleEditView.getInstance().saveEntry()) return;
		}
		else
		{
			BooksView.getInstance().tabbedPane.openTab(SaleEditView.getInstance());
		}
		SaleEditView.getInstance().caller = "invoice";
		SaleEditView.getInstance().setupEntry(-1);
	}

	public void editSaleItem()
	{
		int index = invoiceTable.getSelectedRow();
		if (index != -1)
		{
			if (BooksView.getInstance().tabbedPane.isTabOpen(SaleEditView.getInstance()))
			{
				if (!SaleEditView.getInstance().saveEntry()) return;
			}
			else
			{
				BooksView.getInstance().tabbedPane.openTab(SaleEditView.getInstance());
			}
			SaleEditView.getInstance().caller = "invoice";
			int i = DBFunctions.getInstance().selectOne("SAL", Constants.SALE_NUMBER, (String) invoiceTable.getValueAt(index, 0));
			SaleEditView.getInstance().setupEntry(i);
			BooksView.getInstance().tabbedPane.openTab(SaleEditView.getInstance());
		}
	}

	public void deleteSaleItem()
	{
		int index = invoiceTable.getSelectedRow();
		if (index != -1)
		{
			BooksView.getInstance().tabbedPane.closeTabWithSave(SaleEditView.getInstance());
			Double i = Double.parseDouble((String) invoiceTable.getValueAt(index, 0));
			for (Double j : saleList)
			{
				if (j > 0 && i == Math.floor(j))
				{
					saleList.set(saleList.indexOf(j), -j);
				}
			}
			refreshTable();
			updateTotals();
		}
	}

	public void cancelEntry()
	{
		if (saleList.size() > 0)
		{ // delete .0 added sales
			for (Double i : saleList)
			{
				i = Math.abs(i);
				if (i == Math.floor(i)) deleteASale(Integer.toString((int) Math.floor(i)), false);
			}
		}
		saleList.clear();
		BooksView.getInstance().tabbedPane.closeTab(this);
	}

	private void deleteASale(String i, boolean verbose)
	{
		int j = DBFunctions.getInstance().selectOne("SAL", Constants.SALE_NUMBER, i);
		if (verbose)
		{
			SaleListView.getInstance().verboseDeleteSale(j, false);
		}
		else
		{
			SaleListView.getInstance().restoreSale(j, false);
		}
		Constants.writeBlog("Remove Sale from Invoice: " + Arrays.toString(DBFunctions.getInstance().getRecord(j)));
		DBFunctions.getInstance().remove(j);
	}

	public boolean saveEntry()
	{
		if (clientComboBox.getText().equals(""))
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(), "Client cannot be blank.");
			return false;
		}
		else if (invoiceTable.getRowCount() == 0)
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(), "No books have been added.");
			return false;
		}

		if (tax1TextField.getText().trim().equals("")) tax1TextField.setText("0.00");
		if (tax2TextField.getText().trim().equals("")) tax2TextField.setText("0.00");
		if (tax3TextField.getText().trim().equals("")) tax3TextField.setText("0.00");
		if (discountTextField.getText().trim().equals("")) discountTextField.setText("0.00");
		if (shippingTextField.getText().trim().equals("")) shippingTextField.setText("0.00");
		if (totalTextField.getText().trim().equals("")) totalTextField.setText("0.00");

		invoiceEntry.date = dateTextField.getText();
		invoiceEntry.client = clientComboBox.getText();
		invoiceEntry.user = userTextField.getText();
		invoiceEntry.comments = commentsTextArea.getText();
		invoiceEntry.shipTo = shipToTextArea.getText();
		invoiceEntry.paymentMethod = paymentComboBox.getText();
		invoiceEntry.taxes1 = tax1TextField.getText();
		invoiceEntry.taxes2 = tax2TextField.getText();
		invoiceEntry.taxes3 = tax3TextField.getText();
		invoiceEntry.discount = discountTextField.getText();
		invoiceEntry.shipping = shippingTextField.getText();
		invoiceEntry.total = totalTextField.getText();

		invoiceEntry.saveInvoiceEntry();

		for (Double i : saleList)
		{ // delete unwanted sales
			if (i < 0)
			{
				if (-i == Math.floor(-i))
				{
					deleteASale(Integer.toString((int) Math.floor(-i)), false);
				}
				else
				{ // deleting a .5 sale
					deleteASale(Integer.toString((int) Math.floor(-i)), true);
				}
			}
		}
		saleList.clear();
		return true;
	}

	public void listBuild()
	{}

	public void updateTotals()
	{
		double total = 0;
		double sale = 0;
		double shipping = 0;
		double taxAmount1 = 0;
		double taxAmount2 = 0;
		double taxAmount3 = 0;

		ArrayList<Integer> i = DBFunctions.getInstance().selectAll("SAL", Constants.SALE_INVOICE_NUMBER, invoiceEntry.invoiceNo);
		for (int j : i)
		{
			total += Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_SALE_AMOUNT));
			sale += Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_SALE_AMOUNT));
			shipping += Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_SHIPPING_CREDIT));
			taxAmount1 += Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_TAXES1));
			taxAmount2 += Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_TAXES2));
			taxAmount3 += Double.parseDouble(DBFunctions.getInstance().get(j, Constants.SALE_TAXES3));
		}
		tax1TextField.setText(Constants.twoPlaces.format(taxAmount1));
		tax2TextField.setText(Constants.twoPlaces.format(taxAmount2));
		tax3TextField.setText(Constants.twoPlaces.format(taxAmount3));
		shippingTextField.setText(Constants.twoPlaces.format(shipping));
		double totalTax = taxAmount1 + taxAmount2 + taxAmount3;
		taxesTextField.setText(Constants.twoPlaces.format(totalTax));
		totalTextField.setText(Constants.twoPlaces.format(total + shipping + totalTax));
		discountTextField.setText(Constants.twoPlaces.format(sale));
	}

	public void viewClient()
	{
		int clientID = -1;
		String client = clientComboBox.getText();
		if (!client.equals(""))
		{
			clientID = DBFunctions.getInstance().selectOne("CLI", Constants.CLIENT_NAME, client);
		}
		ClientEditView.getInstance().setupEntry(clientID);
		ClientEditView.getInstance().caller = "invoice";
		BooksView.getInstance().tabbedPane.openTab(ClientEditView.getInstance());
	}

	public void clientListBuild()
	{
		clientComboBox.setModel(DBFunctions.getInstance().generateList("CLI", Constants.CLIENT_NAME));
	}

	public void paymentListBuild()
	{
		String[] pmts = SettingsView.getInstance().get("paymentMethods").split("\\r?\\n|\\r");
		ArrayList al = new ArrayList(Arrays.asList(pmts));
		paymentComboBox.setModel(al);
	}

	public void clientAddressBuild()
	{
		if (!clientComboBox.getText().equals(""))
		{
			int i = DBFunctions.getInstance().selectOne("CLI", Constants.CLIENT_NAME, clientComboBox.getText());
			if (i != -1)
			{
				String address = DBFunctions.getInstance().get(i, Constants.CLIENT_NAME) + "\n";
				if (!DBFunctions.getInstance().get(i, Constants.CLIENT_ADDRESS1).trim().equals(""))
				{
					address += DBFunctions.getInstance().get(i, Constants.CLIENT_ADDRESS1) + "\n";
				}
				if (!DBFunctions.getInstance().get(i, Constants.CLIENT_ADDRESS2).trim().equals(""))
				{
					address += DBFunctions.getInstance().get(i, Constants.CLIENT_ADDRESS2) + "\n";
				}
				if (!DBFunctions.getInstance().get(i, Constants.CLIENT_ADDRESS3).trim().equals(""))
				{
					address += DBFunctions.getInstance().get(i, Constants.CLIENT_ADDRESS3) + "\n";
				}
				if (!DBFunctions.getInstance().get(i, Constants.CLIENT_CITY).trim().equals(""))
				{
					address += DBFunctions.getInstance().get(i, Constants.CLIENT_CITY);
				}
				if (!DBFunctions.getInstance().get(i, Constants.CLIENT_STATE).trim().equals(""))
				{
					address += ", " + DBFunctions.getInstance().get(i, Constants.CLIENT_STATE);
				}
				address += "\n";
				if (!DBFunctions.getInstance().get(i, Constants.CLIENT_COUNTRY).trim().equals(""))
				{
					address += DBFunctions.getInstance().get(i, Constants.CLIENT_COUNTRY) + "\n";
				}
				if (!DBFunctions.getInstance().get(i, Constants.CLIENT_POSTAL_CODE).trim().equals(""))
				{
					address += DBFunctions.getInstance().get(i, Constants.CLIENT_POSTAL_CODE) + "\n";
				}
				address = address.substring(0, address.length() - 1);
				shipToTextArea.setText(address);
			}
		}
	}

	public void printInvoice()
	{
		saveEntry();
		try
		{
			String fn = Constants.TEMPLATES_DIR + Constants.ps + "Invoice.html";
			Scanner scan = new Scanner(new File(fn));
			scan.useDelimiter("\\Z");
			String invoice = scan.next();

			invoice = invoice.replace("[[name]]", SettingsView.getInstance().get("vendorName"));
			invoice = invoice.replace("[[address1]]", SettingsView.getInstance().get("address1"));
			invoice = invoice.replace("[[address2]]", SettingsView.getInstance().get("address2"));
			invoice = invoice.replace("[[address3]]", SettingsView.getInstance().get("address3"));
			invoice = invoice.replace("[[city]]", SettingsView.getInstance().get("city"));
			invoice = invoice.replace("[[state]]", SettingsView.getInstance().get("state"));
			invoice = invoice.replace("[[country]]", SettingsView.getInstance().get("country"));
			invoice = invoice.replace("[[postalcode]]", SettingsView.getInstance().get("postalCode"));
			invoice = invoice.replace("[[phone]]", SettingsView.getInstance().get("phone"));
			invoice = invoice.replace("[[fax]]", SettingsView.getInstance().get("fax"));
			invoice = invoice.replace("[[email]]", SettingsView.getInstance().get("email"));
			invoice = invoice.replace("[[taxid]]", SettingsView.getInstance().get("taxID"));
			invoice = invoice.replace("[[invoicenumber]]", invoiceNoTextField.getText());
			invoice = invoice.replace("[[date]]", dateTextField.getText());
			invoice = invoice.replace("[[client]]", (String) clientComboBox.getText());
			invoice = invoice.replace("[[user]]", userTextField.getText());
			invoice = invoice.replace("[[comments]]", commentsTextArea.getText());
			String imgsrc = "file:" + Constants.IMAGES_DIR + Constants.ps;
			invoice = invoice.replace("[[imgsrc]]", imgsrc);
			String shipTo = shipToTextArea.getText();
			String[] pieces = shipTo.split("\r?\n|\r");
			shipTo = "";
			for (String piece : pieces)
			{
				shipTo += piece + "<br>";
			}
			invoice = invoice.replace("[[shipto]]", shipTo);
			invoice = invoice.replace("[[paymentmethod]]", (String) paymentComboBox.getText());
			invoice = invoice.replace("[[taxes1]]", tax1TextField.getText());
			invoice = invoice.replace("[[taxes2]]", tax2TextField.getText());
			invoice = invoice.replace("[[taxes3]]", tax3TextField.getText());
			invoice = invoice.replace("[[discount]]", discountTextField.getText());
			invoice = invoice.replace("[[shipping]]", shippingTextField.getText());
			invoice = invoice.replace("[[taxes]]", taxesTextField.getText());
			invoice = invoice.replace("[[finaltotal]]", totalTextField.getText());
			String saleTable = "<tr><th>Sales ID #</th><th>Book #</th><th>Author</th>"
					+ "<th>Title</th><th>Qty</th><th>Price</th><th>Net Price</th></tr>";
			for (int i = 0; i < invoiceTable.getRowCount(); i++)
			{
				saleTable += "<tr><td>" + invoiceTable.getValueAt(i, 0) + "</td><td>" + invoiceTable.getValueAt(i, 1)
						+ "</td><td>" + invoiceTable.getValueAt(i, 2) + "</td><td>" + invoiceTable.getValueAt(i, 3)
						+ "</td><td>" + invoiceTable.getValueAt(i, 4) + "</td><td>" + invoiceTable.getValueAt(i, 5)
						+ "</td><td>" + invoiceTable.getValueAt(i, 7) + "</td></tr>";
			}
			invoice = invoice.replace("[[salestable]]", saleTable);

			final File f = new File(
					Constants.EXPORTS_DIR + Constants.ps + "invoice_" + invoiceNoTextField.getText() + ".html");
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			out.write(invoice);
			out.close();

			final JEditorPane ep = new JEditorPane();
			ep.setBackground(Color.white);
			ep.setForeground(Color.black);
			URL fn2 = f.toURI().toURL();
			ep.setPage(fn2);

			JScrollPane printScrollPane = new JScrollPane();
			printScrollPane.setViewportView(ep);
			JDialog printPanel = new JDialog();
			printPanel.setTitle("Invoice");

			Action cancelButtonAction = new AbstractAction()
			{
				@Override
				public void actionPerformed(ActionEvent ae1) // printList
				{
					printPanel.dispose();
					f.delete();
				}
			};
			JButton cancelPrintButton = new GenericButton("cancel.png", "Cancel this print.", cancelButtonAction);

			Action printButtonAction = new AbstractAction()
			{
				@Override
				public void actionPerformed(ActionEvent ae2) // printList
				{
					PrintThis pt = new PrintThis(ep, PageFormat.PORTRAIT);
					printPanel.dispose();
					f.delete();
				}
			};
			JButton printPrintButton = new GenericButton("print.png", "Pretty print this Invoice.", printButtonAction);

			printPanel.setLayout(new ColumnLayout(1, 1));
			printPanel.add("2", cancelPrintButton);
			printPanel.add("w", printPrintButton);
			printPanel.add("xwvh", printScrollPane);
			printPanel.setPreferredSize(new Dimension(750, 500));
			printPanel.setResizable(true);
			printPanel.pack();
			printPanel.setLocationRelativeTo(null);

			printPanel.setVisible(true);
		}
		catch (Exception ex)
		{
			Constants.writeBlog("InvoiceEditView > printInvoice > " + ex);
		}
	}

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
						clientComboBox.requestFocusInWindow();
					}
				});
			}
		});

		invoiceNoTextField.setEditable(false);

		clientComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				clientAddressBuild();
			}
		});
		clientComboBox.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() > 1) viewClient();
			}
		});

		commentsTextArea.setRows(5);
		shipToTextArea.setRows(5);

		invoiceTable.setColumnSelectionAllowed(false);
		invoiceTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() > 1)
				{
					editSaleItem();
				}
			}
		});

		tax1TextField.setEditable(false);
		tax2TextField.setEditable(false);
		tax3TextField.setEditable(false);
		totalTextField.setEditable(false);
		taxesTextField.setEditable(false);

		shippingTextField.setEditable(false);

		discountTextField.setEditable(false);
		Constants.setTabs(commentsTextArea);
		Constants.setTabs(shipToTextArea);

		invoiceTable.setPreferredSize(new Dimension(600, 100));

		JPanel moneyPane = new JPanel(new ColumnLayout(0, 0));
		moneyPane.add("rx", pymtMethodLabel);
		moneyPane.add("hx", paymentComboBox);
		moneyPane.add("rx", saleLabel);
		moneyPane.add("hwx", discountTextField);
		moneyPane.add("rx", tax1Label);
		moneyPane.add("hx", tax1TextField);
		moneyPane.add("rx", shippingLabel);
		moneyPane.add("hwx", shippingTextField);
		moneyPane.add("rx", tax2Label);
		moneyPane.add("hx", tax2TextField);
		moneyPane.add("rx", totalTaxesLabel);
		moneyPane.add("hwx", taxesTextField);
		moneyPane.add("rx", tax3Label);
		moneyPane.add("hx", tax3TextField);
		moneyPane.add("rx", finalTotalLabel);
		moneyPane.add("hwx", totalTextField);

		JPanel topPane = new JPanel(new ColumnLayout(0, 0));
		topPane.add("rx", invoiceNoLabel);
		topPane.add("hx", invoiceNoTextField);
		topPane.add("rx", dateLabel);
		topPane.add("hwx", dateTextField);
		topPane.add("rx", clientLabel);
		topPane.add("hx", clientComboBox);
		topPane.add("rx", userLabel);
		topPane.add("hwx", userTextField);
		topPane.add("x", shipToLabel);
		topPane.add("hvx", shipToScrollPane);
		topPane.add("rx", commentsLabel);
		topPane.add("hwvx", commentsScrollPane);

		setLayout(new ColumnLayout(1, 1));
		add("6x", addBookButton);
		add("", deleteBookButton);
		add("", attachSaleButton);
		add("", cancelButton);
		add("", saveButton);
		add("w", printeButton);

		add("hwx", topPane);
		add("hvwx", salesScrollPane);
		add("hwx", moneyPane);
	}
}