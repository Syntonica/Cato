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

import java.awt.event.*;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class SaleEditView extends GenericEditView
{
	private JLabel authorLabel = new JLabel("Author:", SwingConstants.RIGHT);
	private JLabel bookIDLabel = new JLabel("• Book ID #:", SwingConstants.RIGHT);
	private JLabel commissionLabel = new JLabel("Commission:", SwingConstants.RIGHT);
	private JLabel costLabel = new JLabel("Cost:", SwingConstants.RIGHT);
	private JLabel dateLabel = new JLabel("Date:", SwingConstants.RIGHT);
	private JLabel feeLabel = new JLabel("Fee:", SwingConstants.RIGHT);
	private JLabel invoiceNoLabel = new JLabel("• Invoice #:", SwingConstants.RIGHT);
	private JLabel netLabel = new JLabel("Net:", SwingConstants.RIGHT);
	private JLabel quantityLabel = new JLabel("Quantity:", SwingConstants.RIGHT);
	private JLabel regularPriceLabel = new JLabel("Regular Price:", SwingConstants.RIGHT);
	private JLabel saleAmountLabel = new JLabel("Sale Amount:", SwingConstants.RIGHT);
	private JLabel salesIDLabel = new JLabel("Sales ID #:", SwingConstants.RIGHT);
	private JLabel salespersonLabel = new JLabel("Salesperson:", SwingConstants.RIGHT);
	private JLabel secondCommissionLabel = new JLabel("2nd Comm:", SwingConstants.RIGHT);
	private JLabel shippingChargeLabel = new JLabel("Shipping Charge:", SwingConstants.RIGHT);
	private JLabel shippingCreditLabel = new JLabel("Shipping Credit:", SwingConstants.RIGHT);
	private JCheckBox tax1Label = new JCheckBox("Tax 1:");
	private JCheckBox tax2Label = new JCheckBox("Tax 2:");
	private JCheckBox tax3Label = new JCheckBox("Tax 3:");
	private JLabel titleLabel = new JLabel("Title:", SwingConstants.RIGHT);
	private JLabel listLabel = new JLabel("List:", SwingConstants.RIGHT);
	public JComboBox listComboBox = new JComboBox();
	public JTextField authorTextField = new GenericTextField(Constants.FIELD_ALL, false);
	public JTextField bookIDTextField = new GenericTextField(Constants.FIELD_ALL, false);
	public JTextField commissionTextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField dateTextField = new GenericTextField(Constants.FIELD_ALL, false);
	public JTextField feeTextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField invoiceNumberTextField = new GenericTextField(Constants.FIELD_NUMBER, false);
	public JTextField netTextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField quantityTextField = new GenericTextField(Constants.FIELD_NUMBER, false);
	public JTextField regularPriceTextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField saleAmountTextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField salesIDTextField = new GenericTextField(Constants.FIELD_NUMBER, false);
	public JTextField salespersonTextField = new GenericTextField(Constants.FIELD_ALL, false);
	public JTextField secondCommissionTextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField shippingChargeTextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField shippingCreditTextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField tax1TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField tax2TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField tax3TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public JTextField titleTextField = new GenericTextField(Constants.FIELD_ALL, false);
	public JTextField costTextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	private GenericEditButtonPanel buttonPanel = new GenericEditButtonPanel(this);

	private int originalQuantity;
	private int originalID = -1;
	public String caller = "";

	private String[] listPrice = new String[6];

	public SaleEntry saleEntry;

	private static SaleEditView instance = null;

	public static SaleEditView getInstance()
	{
		if (instance == null)
		{
			instance = new SaleEditView();
		}
		return instance;
	}

	public SaleEditView()
	{
		initComponents();
		Constants.windowNames.put(this, "Sale Edit");
		Constants.windowIcons.put(this, "sell.png");
	}

	public void setupEntry(int id)
	{
		originalID = id;
		tax1Label.setText(SettingsView.getInstance().get("tax1") + ":");
		tax2Label.setText(SettingsView.getInstance().get("tax2") + ":");
		tax3Label.setText(SettingsView.getInstance().get("tax3") + ":");

		listComboBox.removeAllItems();
		listComboBox.addItem(SettingsView.getInstance().get("list1"));
		listComboBox.addItem(SettingsView.getInstance().get("list2"));
		listComboBox.addItem(SettingsView.getInstance().get("list3"));
		listComboBox.addItem(SettingsView.getInstance().get("list4"));
		listComboBox.addItem(SettingsView.getInstance().get("list5"));

		// if id <> -1 retrieve data and enter into fields
		if (id != -1)
		{
			saleEntry = new SaleEntry(id);
			originalQuantity = Integer.parseInt(saleEntry.quantity);
		}
		else
		{
			saleEntry = new SaleEntry();
			originalQuantity = 0;
			saleEntry.list = (String) listComboBox.getItemAt(0);
			saleEntry.date = Constants.getPrettyDateTime();
			if (caller.equals("invoice"))

			{
				saleEntry.invoiceNumber = InvoiceEditView.getInstance().invoiceEntry.invoiceNo;
				saleEntry.salesperson = InvoiceEditView.getInstance().invoiceEntry.user;
			}
		}

		salesIDTextField.setText(saleEntry.saleNumber);
		salespersonTextField.setText(saleEntry.salesperson);
		dateTextField.setText(saleEntry.date);
		quantityTextField.setText(saleEntry.quantity);
		invoiceNumberTextField.setText(saleEntry.invoiceNumber);
		listComboBox.setSelectedItem(saleEntry.list);
		saleAmountTextField.setText(saleEntry.saleAmount);
		shippingCreditTextField.setText(saleEntry.shippingCredit);
		commissionTextField.setText(saleEntry.commission);
		secondCommissionTextField.setText(saleEntry.secondCommission);
		feeTextField.setText(saleEntry.fee);
		shippingChargeTextField.setText(saleEntry.shippingCost);
		tax1TextField.setText(saleEntry.taxes1);
		tax2TextField.setText(saleEntry.taxes2);
		tax3TextField.setText(saleEntry.taxes3);
		costTextField.setText(saleEntry.cost);

		if (Double.parseDouble(SettingsView.getInstance().get("percentage1")) > 0.0)
		{
			tax1Label.setSelected(true);
		}
		if (Double.parseDouble(SettingsView.getInstance().get("percentage2")) > 0.0)
		{
			tax2Label.setSelected(true);
		}
		if (Double.parseDouble(SettingsView.getInstance().get("percentage3")) > 0.0)
		{
			tax3Label.setSelected(true);
		}
		bookIDTextField.setText(saleEntry.bookID);
		updateBookInfo();
	}

	public void updateBookInfo()
	{
		if (bookIDTextField.getText().equals(""))
		{
			authorTextField.setText("");
			titleTextField.setText("");
			regularPriceTextField.setText("0.00");
		}
		else
		{
			int idx = DBFunctions.getInstance().selectOne("BKS", Constants.BOOK_ID, bookIDTextField.getText());
			authorTextField.setText(DBFunctions.getInstance().get(idx, Constants.BOOK_AUTHOR));
			titleTextField.setText(DBFunctions.getInstance().get(idx, Constants.BOOK_TITLE));

			// get list pricing
			listPrice[0] = DBFunctions.getInstance().get(idx, Constants.BOOK_LIST1);
			listPrice[1] = DBFunctions.getInstance().get(idx, Constants.BOOK_LIST2);
			listPrice[2] = DBFunctions.getInstance().get(idx, Constants.BOOK_LIST3);
			listPrice[3] = DBFunctions.getInstance().get(idx, Constants.BOOK_LIST4);
			listPrice[4] = DBFunctions.getInstance().get(idx, Constants.BOOK_LIST5);
			for (int j = 0; j < 5; j++)
			{
				if (listPrice[j].equals("")) listPrice[j] = "0.00";
			}
			regularPriceTextField.setText(listPrice[listComboBox.getSelectedIndex()]);
			saleAmountTextField.setText(listPrice[listComboBox.getSelectedIndex()]);

			updateNet();
		}
	}

	public void lookupBook()
	{
		if (!caller.equals("books") && (originalID < 0)) new QuickBookLookup(this);
	}

	public void lookupBookReentry(String id)
	{
		bookIDTextField.setText(id);
		updateBookInfo();
	}

	private void updateNet()
	{
		// Update taxes
		double charge = Constants.scrapeDollars(saleAmountTextField);
		if (tax1Label.isSelected())
		{
			double taxRate = Double.parseDouble(SettingsView.getInstance().get("percentage1"));
			double taxAmount = charge * taxRate / 100;
			tax1TextField.setText(Constants.twoPlaces.format(taxAmount));
		}
		else
		{
			tax1TextField.setText("0.00");
		}
		if (tax2Label.isSelected())
		{
			double taxRate = Double.parseDouble(SettingsView.getInstance().get("percentage2"));
			double taxAmount = charge * taxRate / 100;
			tax2TextField.setText(Constants.twoPlaces.format(taxAmount));
		}
		else
		{
			tax2TextField.setText("0.00");
		}
		if (tax3Label.isSelected())
		{
			double taxRate = Double.parseDouble(SettingsView.getInstance().get("percentage3"));
			double taxAmount = charge * taxRate / 100;
			tax3TextField.setText(Constants.twoPlaces.format(taxAmount));
		}
		else
		{
			tax3TextField.setText("0.00");
		}

		// Update net
		double net = Constants.scrapeDollars(saleAmountTextField) + Constants.scrapeDollars(shippingCreditTextField)
				- Constants.scrapeDollars(commissionTextField) - Constants.scrapeDollars(secondCommissionTextField)
				- Constants.scrapeDollars(feeTextField) - Constants.scrapeDollars(shippingChargeTextField)
				- Constants.scrapeDollars(costTextField);
		netTextField.setText(Constants.twoPlaces.format(net));
	}

	public boolean saveEntry()
	{
		if (bookIDTextField.getText().trim().equals(""))
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(), "Book ID cannot be blank.");
			return false;
		}
		else if ((saleAmountTextField.getText().equals("")) || (quantityTextField.getText().equals("")))
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(), "Quantity must be 1 or greater.");
			return false;
		}
		else if (Integer.parseInt(quantityTextField.getText()) < 1)
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(), "Quantity must be 1 or greater.");
			return false;
		}
		else if (Double.parseDouble(saleAmountTextField.getText()) <= 0)
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(), "Sale Amount must be entered.");
			return false;
		}

		if (shippingChargeTextField.getText().equals("")) shippingChargeTextField.setText("0.00");
		if (shippingCreditTextField.getText().equals("")) shippingCreditTextField.setText("0.00");
		if (commissionTextField.getText().equals("")) commissionTextField.setText("0.00");
		if (secondCommissionTextField.getText().equals("")) secondCommissionTextField.setText("0.00");
		if (feeTextField.getText().equals("")) feeTextField.setText("0.00");
		if (tax1TextField.getText().equals("")) tax1TextField.setText("0.00");
		if (tax2TextField.getText().equals("")) tax2TextField.setText("0.00");
		if (tax3TextField.getText().equals("")) tax3TextField.setText("0.00");
		if (costTextField.getText().equals("")) costTextField.setText("0.00");
		if (authorTextField.getText().trim().equals("")) authorTextField.setText("Unknown Author");
		if (titleTextField.getText().trim().equals("")) titleTextField.setText("Unknown Title");

		saleEntry.salesperson = salespersonTextField.getText();
		saleEntry.date = dateTextField.getText();
		saleEntry.bookID = bookIDTextField.getText();
		saleEntry.quantity = quantityTextField.getText();
		saleEntry.invoiceNumber = invoiceNumberTextField.getText();
		saleEntry.regularPrice = regularPriceTextField.getText();
		saleEntry.list = (String) listComboBox.getSelectedItem();
		saleEntry.saleAmount = saleAmountTextField.getText();
		saleEntry.shippingCredit = shippingCreditTextField.getText();
		saleEntry.commission = commissionTextField.getText();
		saleEntry.secondCommission = secondCommissionTextField.getText();
		saleEntry.fee = feeTextField.getText();
		saleEntry.shippingCost = shippingChargeTextField.getText();
		saleEntry.taxes1 = tax1TextField.getText();
		saleEntry.taxes2 = tax2TextField.getText();
		saleEntry.taxes3 = tax3TextField.getText();
		saleEntry.author = authorTextField.getText();
		saleEntry.title = titleTextField.getText();
		saleEntry.cost = costTextField.getText();

		saleEntry.saveSalesEntry();

		// remove the sold book from stock
		int saleQuantity = Integer.parseInt(quantityTextField.getText());
		if (originalQuantity != saleQuantity)
		{
			int idx = DBFunctions.getInstance().selectOne("BKS", Constants.BOOK_ID, bookIDTextField.getText().trim());
			int qty = Integer.parseInt(DBFunctions.getInstance().get(idx, Constants.BOOK_QUANTITY));
			int newQty = qty - saleQuantity + originalQuantity;

			JPanel panel = new JPanel();
			panel.setLayout(new ColumnLayout(0, 0));
			JLabel label = new JLabel();
			if (originalQuantity < saleQuantity)
			{
				label.setText("Remove this Sale from stock?");
			}
			else
			{
				label.setText("Return these to stock?");
			}
			label.setFont(Cato.catoFont);
			JCheckBox restore = new JCheckBox();
			restore.setSelected(false);
			if (newQty < qty)
			{
				restore.setText("Remove copy from inventory?");
			}
			else
			{
				restore.setText("Restore copy to inventory?");
			}
			restore.setHorizontalAlignment(SwingConstants.LEFT);
			restore.setFont(Cato.catoFont);
			JCheckBox include = new JCheckBox("Include in Update?", false);
			include.setHorizontalAlignment(SwingConstants.LEFT);
			include.setFont(Cato.catoFont);
			panel.add("xw", label);
			panel.add("xw", restore);
			panel.add("xw", include);
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(BooksView.getInstance(), panel, "Selling a Book",
					JOptionPane.YES_NO_OPTION))
			{
				if (restore.isSelected())
				{
					String status = "For Sale";
					if (newQty < 0)
					{
						JOptionPane.showMessageDialog(BooksView.getInstance(), "Quantity in now negative, setting to '0'.");
						newQty = 0;
					}
					if (newQty == 0)
					{
						status = "Sold";
					}
					DBFunctions.getInstance().put(idx, Constants.BOOK_QUANTITY, Integer.toString(newQty));
					DBFunctions.getInstance().put(idx, Constants.BOOK_STATUS, status);
					DBFunctions.getInstance().put(idx, Constants.BOOK_CHANGED_DATE, Constants.getTimestamp());
				}
				if (include.isSelected())
				{
					DBFunctions.getInstance().put(idx, Constants.BOOK_INCLUDE_IN_UPDATE, "true");
				}

			}
			if (caller.equals("invoice"))
			{
				InvoiceEditView.getInstance().saleList.add(Double.parseDouble(saleEntry.saleNumber));
				InvoiceEditView.getInstance().refreshTable();
				InvoiceEditView.getInstance().updateTotals();
			}
		}
		caller = "";
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
						salespersonTextField.requestFocusInWindow();
					}
				});
			}
		});

		saleAmountTextField.addKeyListener(new CustomKeyListener());
		shippingCreditTextField.addKeyListener(new CustomKeyListener());
		shippingChargeTextField.addKeyListener(new CustomKeyListener());
		commissionTextField.addKeyListener(new CustomKeyListener());
		secondCommissionTextField.addKeyListener(new CustomKeyListener());
		feeTextField.addKeyListener(new CustomKeyListener());
		costTextField.addKeyListener(new CustomKeyListener());

		salesIDTextField.setEnabled(false);

		bookIDTextField.setEditable(false);
		bookIDTextField.setBackground(Cato.whiteAndBlack);
		bookIDTextField.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if ((evt.getClickCount() > 1) && (!caller.equals("invoice")))
				{
					lookupBook();
				}
			}
		});

		quantityTextField.addKeyListener(new KeyAdapter()
		{
			public void keyTyped(KeyEvent evt)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						if (quantityTextField.getText().length() > 0)
						{
							saleAmountTextField
									.setText(Constants.twoPlaces.format(Double.parseDouble(quantityTextField.getText())
											* Double.parseDouble(regularPriceTextField.getText())));
							updateNet();
						}
					}
				});
			}
		});

		invoiceNumberTextField.setEnabled(false);
		invoiceNumberTextField.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() > 1)
				{
					if (!invoiceNumberTextField.getText().equals(""))
					{
						int e = DBFunctions.getInstance().selectOne("INV", Constants.INVOICE_NUMBER,
								invoiceNumberTextField.getText());
						InvoiceEditView.getInstance().setupEntry(e);
						BooksView.getInstance().tabbedPane.openTab(InvoiceEditView.getInstance());
					}
				}
			}
		});

		listComboBox.addPopupMenuListener(new PopupMenuListener()
		{
			public void popupMenuCanceled(PopupMenuEvent evt)
			{}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0)
			{
				int sel = listComboBox.getSelectedIndex();
				regularPriceTextField.setText(listPrice[sel]);
				saleAmountTextField.setText(listPrice[sel]);
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0)
			{}
		});

		tax1Label.setHorizontalTextPosition(SwingConstants.LEFT);
		tax1Label.setHorizontalAlignment(SwingConstants.RIGHT);
		tax1Label.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				updateNet();
			}
		});
		tax2Label.setHorizontalTextPosition(SwingConstants.LEFT);
		tax2Label.setHorizontalAlignment(SwingConstants.RIGHT);
		tax2Label.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				updateNet();
			}
		});
		tax3Label.setHorizontalTextPosition(SwingConstants.LEFT);
		tax3Label.setHorizontalAlignment(SwingConstants.RIGHT);
		tax3Label.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				updateNet();
			}
		});
		tax1TextField.setEditable(false);
		tax2TextField.setEditable(false);
		tax3TextField.setEditable(false);

		JPanel leftPane = new JPanel(new ColumnLayout(2, 0));
		JPanel rightPane = new JPanel(new ColumnLayout(2, 0));
		leftPane.add("rx", salesIDLabel);
		leftPane.add("hxw", salesIDTextField);
		rightPane.add("rx", saleAmountLabel);
		rightPane.add("hwx", saleAmountTextField);
		leftPane.add("rx", salespersonLabel);
		leftPane.add("hxw", salespersonTextField);
		rightPane.add("rx", shippingCreditLabel);
		rightPane.add("hwx", shippingCreditTextField);
		leftPane.add("rx", dateLabel);
		leftPane.add("hxw", dateTextField);
		rightPane.add("rx", shippingChargeLabel);
		rightPane.add("hwx", shippingChargeTextField);
		rightPane.add("rx", commissionLabel);
		rightPane.add("hwx", commissionTextField);
		leftPane.add("rx", bookIDLabel);
		leftPane.add("hxw", bookIDTextField);
		rightPane.add("rx", secondCommissionLabel);
		rightPane.add("hwx", secondCommissionTextField);
		leftPane.add("rx", authorLabel);
		leftPane.add("hxw", authorTextField);
		rightPane.add("rx", feeLabel);
		rightPane.add("hwx", feeTextField);
		leftPane.add("rx", titleLabel);
		leftPane.add("hxw", titleTextField);
		leftPane.add("rx", quantityLabel);
		leftPane.add("hxw", quantityTextField);
		rightPane.add("hrx", tax1Label);
		rightPane.add("hwx", tax1TextField);
		leftPane.add("rx", invoiceNoLabel);
		leftPane.add("hxw", invoiceNumberTextField);
		rightPane.add("hrx", tax2Label);
		rightPane.add("hwx", tax2TextField);
		leftPane.add("rx", regularPriceLabel);
		leftPane.add("hxw", regularPriceTextField);
		rightPane.add("hrx", tax3Label);
		rightPane.add("hwx", tax3TextField);
		leftPane.add("rx", listLabel);
		leftPane.add("hxw", listComboBox);
		rightPane.add("rx", costLabel);
		rightPane.add("hwx", costTextField);
		rightPane.add("rx", netLabel);
		rightPane.add("hwx", netTextField);

		setLayout(new ColumnLayout(1, 1));
		add("w", buttonPanel);
		add("hvx", leftPane);
		add("vxw", rightPane);
	}

	class CustomKeyListener implements KeyListener
	{
		public void keyTyped(KeyEvent e)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					updateNet();
				}
			});
		}

		public void keyPressed(KeyEvent e)
		{}

		public void keyReleased(KeyEvent e)
		{}
	}
}
