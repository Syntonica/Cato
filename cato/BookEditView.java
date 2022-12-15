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
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;

public class BookEditView extends GenericEditView implements ClipboardOwner
{
	private Action priceCheckAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			openPrice();
		}
	};

	private Action printAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			new PrintThis(BookEditView.this);
		}
	};

	private Action cancelAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			cancelEntry();
		}
	};

	private Action saveAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			if (saveEntry())
			{
				BooksView.getInstance().tabbedPane.closeTab(BookEditView.this);
			}
		}
	};

	private Action saveAddAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			saveBookEntryAndAddAnother();
		}
	};

	private Action validateAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			validateRecord();
		}
	};

	private Action cloneAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object[] options = { "Cancel", "Clear All", "Select All", "Clone" };
			int selection = JOptionPane.showOptionDialog(cloneButton, "Select a Sticky Label action:",
					"Select a Sticky Label action", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					options, options[0]);
			if (selection == 3)
			{
				if (saveEntry())
				{
					validateButton.setText("New Listing");
					enableNavButtons(false);
					bookEntry.key = "-1";
					recordNoTextField.setText("-1");
					bookEntry.addedDate = Constants.getTimestamp();
					bookEntry.changedDate = "";
					bookEntry.validatedDate = "";
					if (SettingsView.getInstance().get("autoInc").equals("true"))
					{
						bookIDTextField.setText(SettingsView.getInstance().get("autoIncNumber"));
					}
					else
					{
						bookIDTextField.setText("");
					}
					setUserDateLabel();
					bookIDTextField.requestFocusInWindow();
				}
			}
			else if (selection == 2) // select all
			{
				for (JLabel l : stickyLabelMap.keySet())
				{
					l.setForeground(Cato.hiliteColor);
				}
			}
			else if (selection == 1) // clear all
			{
				for (JLabel l : stickyLabelMap.keySet())
				{
					l.setForeground(Cato.naturalColor);
				}
			}
		}
	};

	private Action pubInfoAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent evt)
		{
			doPlaceTextFieldClick();
		}
	};

	private Action isbnFillAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent evt)
		{
			openISBN();
		}
	};

	private Action minusAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent evt)
		{
			updateQuantity(-1);
		}
	};

	private Action plusAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent evt)
		{
			updateQuantity(1);
		}
	};

	private Action dictAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent evt)
		{
			ListEditView.getInstance().showListEditWindow(null, "Edit User Dictionary", "DCT");
		}
	};

	private Action backAllAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent evt)
		{
			traverse(0);
		}
	};

	private Action backOneAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent evt)
		{
			traverse(bookIndex - 1);
		}
	};

	private Action forwardAllAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent evt)
		{
			traverse(bookCount - 1);
		}
	};

	private Action forwardOneAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent evt)
		{
			traverse(bookIndex + 1);
		}
	};

	private Action toolAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			selectTool();
		}
	};

	private GenericButton toolButton = new GenericButton("tool.png", "Select a Tool", toolAction);

	private GenericButton priceCheckButton = new GenericButton("price.png", "Open browser window to search for prices.",
			priceCheckAction);
	private GenericButton printButton = new GenericButton("print.png", "Print this page.", printAction);
	private GenericButton cancelButton = new GenericButton("cancel.png", "Discard all changes to this listing.",
			cancelAction);
	private GenericButton saveButton = new GenericButton("save.png", "Save this listing.", saveAction);
	private GenericButton saveButton1 = new GenericButton("save1.png", "Save this listing and add another.",
			saveAddAction);
	private JButton validateButton = new JButton(validateAction);
	private GenericButton backAllButton = new GenericButton("2leftarrow.png", null, backAllAction);
	private GenericButton backOneButton = new GenericButton("1leftarrow.png", null, backOneAction);
	private GenericButton forwardAllButton = new GenericButton("2rightarrow.png", null, forwardAllAction);
	private GenericButton forwardOneButton = new GenericButton("1rightarrow.png", null, forwardOneAction);
	public int bookCount;
	public int bookIndex;

	public GenericCheckBox firstEditionCheckBox = new GenericCheckBox();
	public GenericCheckBox hasDJCheckBox = new GenericCheckBox();
	public GenericCheckBox includeInUpdateCheckBox = new GenericCheckBox();

	public GenericComboBox authorComboBox = new GenericComboBox("Author");
	public GenericComboBox bindingComboBox = new GenericComboBox("Binding");
	public GenericComboBox bindingStyleComboBox = new GenericComboBox("Binding Style");
	public GenericComboBox bookConditionComboBox = new GenericComboBox("Book Condition");
	public GenericComboBox bookTypeComboBox = new GenericComboBox("Book Type");
	public GenericComboBox catalog1ComboBox = new GenericComboBox("Catalog");
	public GenericComboBox catalog2ComboBox = new GenericComboBox("Catalog");
	public GenericComboBox catalog3ComboBox = new GenericComboBox("Catalog");
	public GenericComboBox djConditionComboBox = new GenericComboBox("DJ Condition");
	public GenericComboBox editionComboBox = new GenericComboBox("Edition");
	public GenericComboBox illustratorComboBox = new GenericComboBox("Illustrator");
	public GenericComboBox languageComboBox = new GenericComboBox("Language");
	public GenericComboBox printingComboBox = new GenericComboBox("Printing");
	public GenericComboBox publisherComboBox = new GenericComboBox("Publisher");
	public GenericComboBox signedComboBox = new GenericComboBox("Signed");
	public GenericComboBox sizeComboBox = new GenericComboBox("Size");
	public GenericComboBox user1ComboBox = new GenericComboBox("User 1");
	public GenericComboBox user2ComboBox = new GenericComboBox("User 2");

	public JComboBox statusComboBox = new JComboBox();

	private GenericUpdateButton updateAuthorButton = new GenericUpdateButton(authorComboBox, "Author", "AUT");
	private GenericUpdateButton updateBindingStyleButton = new GenericUpdateButton(bindingStyleComboBox, "Binding Style", "BST");
	private GenericUpdateButton updateBindingTypeButton = new GenericUpdateButton(bindingComboBox, "Binding Type", "BND");
	private GenericUpdateButton updateBookConditionButton = new GenericUpdateButton(bookConditionComboBox, "Book Condition", "BCN");
	private GenericUpdateButton updateBookTypeButton = new GenericUpdateButton(bookTypeComboBox, "Book Type", "BKT");
	private GenericUpdateButton updateCatalogButton = new GenericUpdateButton(catalog1ComboBox, "Catalog", "CAT");
	private GenericUpdateButton updateDJConditionButton = new GenericUpdateButton(djConditionComboBox, "DJ Condition", "DJC");
	private GenericUpdateButton updateEditionButton = new GenericUpdateButton(editionComboBox, "Edition", "EDT");
	private GenericUpdateButton updateIllustratorButton = new GenericUpdateButton(illustratorComboBox, "Illustrator", "AUT");
	private GenericUpdateButton updateLanguageButton = new GenericUpdateButton(languageComboBox, "Language", "LAN");
	private GenericUpdateButton updatePrintingButton = new GenericUpdateButton(printingComboBox, "Printing", "PRT");
	private GenericUpdateButton updatePublisherButton = new GenericUpdateButton(publisherComboBox, "Publisher", "PUB");
	private GenericUpdateButton updateSignedButton = new GenericUpdateButton(signedComboBox, "Signed", "SGN");
	private GenericUpdateButton updateSizeButton = new GenericUpdateButton(sizeComboBox, "Book Size", "SIZ");
	private GenericUpdateButton updateUser1Button = new GenericUpdateButton(user1ComboBox, "User1 Field", "US1");
	private GenericUpdateButton updateUser2Button = new GenericUpdateButton(user2ComboBox, "User2 Field", "US2");

	private GenericButton cloneButton = new GenericButton("valsave.png", "Clone listing or change stickies.",
			cloneAction);
	private GenericButton pubInfoButton = new GenericButton("valsave.png", "Collate Publisher info, insert Place.",
			pubInfoAction);
	private GenericButton isbnFillButton = new GenericButton("about.png", "Verify ISBN and pull data from Internet.",
			isbnFillAction);
	private GenericButton minusButton = new GenericButton("1leftarrow.png", "Decrease quantity by 1.", minusAction);
	private GenericButton plusButton = new GenericButton("1rightarrow.png", "Increase quantity by 1.", plusAction);
	private GenericButton dictButton = new GenericButton("valone.png", "Edit User dictionary.", dictAction);

	public  JLabel bookIDLabel = new JLabel("Book ID#:");
	private JLabel includeInUpdateLabel = new JLabel("Update?:");
	private JLabel recordNoLabel = new JLabel("Record #:");
	private JLabel userDateLabel = new JLabel("");
	private JLabel firstWarningLabel = new JLabel("<- Verify!");
	private JLabel djWarningLabel = new JLabel("<- Verify!");

	private GenericLabel authorLabel = new GenericLabel("Author:");
	private GenericLabel bindingLabel = new GenericLabel("Binding Type:");
	private GenericLabel bindingStyleLabel = new GenericLabel("Binding Style:");
	private GenericLabel bookConditionLabel = new GenericLabel("Book Cond:");
	private GenericLabel bookTypeLabel = new GenericLabel("Book Type:");
	private GenericLabel catalog1Label = new GenericLabel("Catalog:");
	public  GenericLabel catalog2Label = new GenericLabel("Catalog2:");
	public  GenericLabel catalog3Label = new GenericLabel("Catalog3:");
	private GenericLabel costLabel = new GenericLabel("Cost:");
	private GenericLabel dateLabel = new GenericLabel("Date:");
	private GenericLabel descriptionLabel = new GenericLabel("Description:");
	private GenericLabel djConditionLabel = new GenericLabel("DJ Cond:");
	private GenericLabel editionLabel = new GenericLabel("Edition:");
	private GenericLabel firstEditionLabel = new GenericLabel("First Edition:");
	private GenericLabel hasDJLabel = new GenericLabel("Has DJ:");
	private GenericLabel heightLabel = new GenericLabel("Height:");
	private GenericLabel illustratorLabel = new GenericLabel("Illustrator:");
	private GenericLabel isbnLabel = new GenericLabel("ISBN:");
	private GenericLabel keywordsLabel = new GenericLabel("Keywords:");
	private GenericLabel languageLabel = new GenericLabel("Language:");
	private GenericLabel list1Label = new GenericLabel("List 1:");
	private GenericLabel list2Label = new GenericLabel("List 2:");
	private GenericLabel list3Label = new GenericLabel("List 3:");
	private GenericLabel list4Label = new GenericLabel("List 4:");
	private GenericLabel list5Label = new GenericLabel("List 5:");
	private GenericLabel locationLabel = new GenericLabel("Location:");
	private GenericLabel pagesLabel = new GenericLabel("Pages:");
	private GenericLabel placeLabel = new GenericLabel("Place:");
	private GenericLabel printingLabel = new GenericLabel("Printing:");
	private GenericLabel privateLabel = new GenericLabel("Private:");
	private GenericLabel publisherLabel = new GenericLabel("Publisher:");
	private GenericLabel quantityLabel = new GenericLabel("Qty:");
	private GenericLabel signedLabel = new GenericLabel("Signed:");
	private GenericLabel sizeLabel = new GenericLabel("Size:");
	private GenericLabel statusLabel = new GenericLabel("Status:");
	private GenericLabel titleLabel = new GenericLabel("Title:");
	private GenericLabel user1Label = new GenericLabel("User1:");
	private GenericLabel user2Label = new GenericLabel("User2:");
	private GenericLabel weightLabel = new GenericLabel("Weight:");
	private GenericLabel widthLabel = new GenericLabel("Width:");

	private JScrollPane jScrollPane1 = new JScrollPane();
	private JScrollPane jScrollPane2 = new JScrollPane();
	public GenericTextArea descriptionArea = new GenericTextArea(true);
	public GenericTextArea privateTextArea = new GenericTextArea(true);

	public GenericTextField bookIDTextField = new GenericTextField(Constants.FIELD_ALL, false);
	public GenericTextField costTextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public GenericTextField dateTextField = new GenericTextField(Constants.FIELD_ALL, true);
	public GenericTextField heightTextField = new GenericTextField(Constants.FIELD_ALL, true);
	public GenericTextField isbnTextField = new GenericTextField(Constants.FIELD_ISBN, false);
	public GenericTextField keywordsTextField = new GenericTextField(Constants.FIELD_ALL, true);
	public GenericTextField list1TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public GenericTextField list2TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public GenericTextField list3TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public GenericTextField list4TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public GenericTextField list5TextField = new GenericTextField(Constants.FIELD_CURRENCY, false);
	public GenericTextField locationTextField = new GenericTextField(Constants.FIELD_ALL, true);
	public GenericTextField pagesTextField = new GenericTextField(Constants.FIELD_ALL, true);
	public GenericTextField placeTextField = new GenericTextField(Constants.FIELD_ALL, true);
	public GenericTextField quantitySpinner = new GenericTextField(Constants.FIELD_NUMBER, false);
	public GenericTextField recordNoTextField = new GenericTextField(Constants.FIELD_ALL, false);
	public GenericTextField titleTextField = new GenericTextField(Constants.FIELD_ALL, true);
	public GenericTextField weightTextField = new GenericTextField(Constants.FIELD_ALL, true);
	public GenericTextField widthTextField = new GenericTextField(Constants.FIELD_ALL, true);

	private JLabel imagePanel = new JLabel();
	public BookEntry bookEntry;

	private HashMap<JLabel, Component> stickyLabelMap = new HashMap<JLabel, Component>();
	private HashMap<String, Component> macroFieldMap = new HashMap<String, Component>(64);

	private static BookEditView instance = null;

	public static BookEditView getInstance()
	{
		if (instance == null)
		{
			instance = new BookEditView();
		}
		return instance;
	}

	/**
	 * Creates new form BookEditView
	 */
	public BookEditView()
	{
		Constants.windowNames.put(this, "Book Edit");
		Constants.windowIcons.put(this, "book.png");
		Cato.naturalColor = bookIDLabel.getForeground();
		initComponents();
		setUpCombos();

		// associate labels with their fields
		stickyLabelMap.put(authorLabel, authorComboBox);
		stickyLabelMap.put(bindingLabel, bindingComboBox);
		stickyLabelMap.put(bindingStyleLabel, bindingStyleComboBox);
		stickyLabelMap.put(bookConditionLabel, bookConditionComboBox);
		stickyLabelMap.put(bookTypeLabel, bookTypeComboBox);
		stickyLabelMap.put(catalog1Label, catalog1ComboBox);
		stickyLabelMap.put(catalog2Label, catalog2ComboBox);
		stickyLabelMap.put(catalog3Label, catalog3ComboBox);
		stickyLabelMap.put(descriptionLabel, descriptionArea);
		stickyLabelMap.put(costLabel, costTextField);
		stickyLabelMap.put(dateLabel, dateTextField);
		stickyLabelMap.put(djConditionLabel, djConditionComboBox);
		stickyLabelMap.put(editionLabel, editionComboBox);
		stickyLabelMap.put(firstEditionLabel, firstEditionCheckBox);
		stickyLabelMap.put(hasDJLabel, hasDJCheckBox);
		stickyLabelMap.put(heightLabel, heightTextField);
		stickyLabelMap.put(illustratorLabel, illustratorComboBox);
		stickyLabelMap.put(isbnLabel, isbnTextField);
		stickyLabelMap.put(keywordsLabel, keywordsTextField);
		stickyLabelMap.put(languageLabel, languageComboBox);
		stickyLabelMap.put(list1Label, list1TextField);
		stickyLabelMap.put(list2Label, list2TextField);
		stickyLabelMap.put(list3Label, list3TextField);
		stickyLabelMap.put(list4Label, list4TextField);
		stickyLabelMap.put(list5Label, list5TextField);
		stickyLabelMap.put(locationLabel, locationTextField);
		stickyLabelMap.put(pagesLabel, pagesTextField);
		stickyLabelMap.put(placeLabel, placeTextField);
		stickyLabelMap.put(printingLabel, printingComboBox);
		stickyLabelMap.put(privateLabel, privateTextArea);
		stickyLabelMap.put(publisherLabel, publisherComboBox);
		stickyLabelMap.put(quantityLabel, quantitySpinner);
		stickyLabelMap.put(signedLabel, signedComboBox);
		stickyLabelMap.put(sizeLabel, sizeComboBox);
		stickyLabelMap.put(statusLabel, statusComboBox);
		stickyLabelMap.put(titleLabel, titleTextField);
		stickyLabelMap.put(user1Label, user1ComboBox);
		stickyLabelMap.put(user2Label, user2ComboBox);
		stickyLabelMap.put(weightLabel, weightTextField);
		stickyLabelMap.put(widthLabel, widthTextField);

		// associate shortNames with their fields
		macroFieldMap.put("bookid", bookIDTextField);
		macroFieldMap.put("isbn", isbnTextField);
		macroFieldMap.put("author", authorComboBox);
		macroFieldMap.put("title", titleTextField);
		macroFieldMap.put("illustrator", illustratorComboBox);
		macroFieldMap.put("publisher", publisherComboBox);
		macroFieldMap.put("place", placeTextField);
		macroFieldMap.put("date", dateTextField);
		macroFieldMap.put("edition", editionComboBox);
		macroFieldMap.put("printing", printingComboBox);
		macroFieldMap.put("firstedition", firstEditionCheckBox);
		macroFieldMap.put("hasdj", hasDJCheckBox);
		macroFieldMap.put("bookcondition", bookConditionComboBox);
		macroFieldMap.put("djcondition", djConditionComboBox);
		macroFieldMap.put("bindingtype", bindingComboBox);
		macroFieldMap.put("bindingstyle", bindingStyleComboBox);
		macroFieldMap.put("pages", pagesTextField);
		macroFieldMap.put("language", languageComboBox);
		macroFieldMap.put("keywords", keywordsTextField);
		macroFieldMap.put("booktype", bookTypeComboBox);
		macroFieldMap.put("signed", signedComboBox);
		macroFieldMap.put("location", locationTextField);
		macroFieldMap.put("description", descriptionArea);
		macroFieldMap.put("private", privateTextArea);
		macroFieldMap.put("update", includeInUpdateCheckBox);
		macroFieldMap.put("cost", costTextField);
		macroFieldMap.put("list1", list1TextField);
		macroFieldMap.put("list2", list2TextField);
		macroFieldMap.put("list3", list3TextField);
		macroFieldMap.put("list4", list4TextField);
		macroFieldMap.put("list5", list5TextField);
		macroFieldMap.put("size", sizeComboBox);
		macroFieldMap.put("height", heightTextField);
		macroFieldMap.put("width", widthTextField);
		macroFieldMap.put("weight", weightTextField);
		macroFieldMap.put("quantity", quantitySpinner);
		macroFieldMap.put("status", statusComboBox);
		macroFieldMap.put("user1", user1ComboBox);
		macroFieldMap.put("user2", user2ComboBox);
		macroFieldMap.put("catalog1", catalog1ComboBox);
		macroFieldMap.put("catalog2", catalog1ComboBox);
		macroFieldMap.put("catalog3", catalog1ComboBox);

	}

	public void setupEntry(int key)
	{
		list1Label.setText(Constants.names[Constants.BOOK_LIST1] + ":");
		list2Label.setText(Constants.names[Constants.BOOK_LIST2] + ":");
		list3Label.setText(Constants.names[Constants.BOOK_LIST3] + ":");
		list4Label.setText(Constants.names[Constants.BOOK_LIST4] + ":");
		list5Label.setText(Constants.names[Constants.BOOK_LIST5] + ":");
		user1Label.setText(Constants.names[Constants.BOOK_USER1] + ":");
		user2Label.setText(Constants.names[Constants.BOOK_USER2] + ":");
		publisherComboBox.setToolTipText(null);
		resetFormColors();
		bookEntry = new BookEntry(key);
		populateFields();
		checkForImages();
		setUserDateLabel();
		doLoadStickies(key);
		if (key > -1)
		{
			validateButton.setText((bookIndex + 1) + " of " + bookCount);
			enableNavButtons(true);
		}
		else
		{
			validateButton.setText("New Listing");
			enableNavButtons(false);
		}
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				bookIDTextField.requestFocusInWindow();
			}
		});
	}

	private void doLoadStickies(int id)
	{
		for (JLabel l : stickyLabelMap.keySet())
		{
			String val = SettingsView.getInstance().get("sticky." + l.getName());
			if (val.equals(""))
			{
				l.setForeground(Cato.naturalColor);
			}
			else
			{
				l.setForeground(Cato.hiliteColor);
				if (!val.equals(" ") && id < 0)
				{
					Component c = stickyLabelMap.get(l);
					if (c instanceof JTextComponent)
					{
						((JTextComponent) c).setText(val);
					}
					else if (c instanceof JCheckBox)
					{
						((JCheckBox) c).setSelected(Boolean.parseBoolean(val));
					}
				}
			}
		}
	}

	private void doSaveStickies()
	{
		for (JLabel l : stickyLabelMap.keySet())
		{
			if (l.getForeground().equals(Cato.naturalColor))
			{
				SettingsView.getInstance().setNoLog("sticky." + l.getName(), "");
			}
			else
			{
				Component c = stickyLabelMap.get(l);
				String val = "";
				if (c instanceof JTextComponent)
				{
					val = ((JTextComponent) c).getText();
				}
				else if (c instanceof JCheckBox)
				{
					val = Boolean.toString(((JCheckBox) c).isSelected());
				}
				if (val.equals(""))
				{
					val = " ";
				}
				SettingsView.getInstance().setNoLog("sticky." + l.getName(), val);
			}
		}
	}

	public void populateFields()
	{
		authorComboBox.setText(bookEntry.author);
		bindingComboBox.setText(bookEntry.binding);
		bindingStyleComboBox.setText(bookEntry.bindingStyle);
		bookConditionComboBox.setText(bookEntry.bookCondition);
		bookIDTextField.setText(bookEntry.bookID);
		bookTypeComboBox.setText(bookEntry.bookType);
		catalog1ComboBox.setText(bookEntry.catalog1);
		catalog2ComboBox.setText(bookEntry.catalog2);
		catalog3ComboBox.setText(bookEntry.catalog3);
		descriptionArea.setText(bookEntry.description);
		costTextField.setText(bookEntry.cost);
		dateTextField.setText(bookEntry.date);
		djConditionComboBox.setText(bookEntry.djCondition);
		editionComboBox.setText(bookEntry.edition);
		firstEditionCheckBox.setSelected(Boolean.parseBoolean(bookEntry.firstEdition));
		hasDJCheckBox.setSelected(Boolean.parseBoolean(bookEntry.hasDJ));
		heightTextField.setText(bookEntry.height);
		illustratorComboBox.setText(bookEntry.illustrator);
		includeInUpdateCheckBox.setSelected(Boolean.parseBoolean(bookEntry.includeInUpdate));
		isbnTextField.setText(bookEntry.isbn);
		keywordsTextField.setText(bookEntry.keywords);
		languageComboBox.setText(bookEntry.language);
		list1TextField.setText(bookEntry.list1);
		list2TextField.setText(bookEntry.list2);
		list3TextField.setText(bookEntry.list3);
		list4TextField.setText(bookEntry.list4);
		list5TextField.setText(bookEntry.list5);
		locationTextField.setText(bookEntry.location);
		pagesTextField.setText(bookEntry.pages);
		placeTextField.setText(bookEntry.place);
		printingComboBox.setText(bookEntry.printing);
		privateTextArea.setText(bookEntry.privateField);
		publisherComboBox.setText(bookEntry.publisher);
		quantitySpinner.setText(bookEntry.quantity);
		recordNoTextField.setText(String.valueOf(bookEntry.bookNumber) + "/" + bookEntry.key);
		signedComboBox.setText(bookEntry.signed);
		sizeComboBox.setText(bookEntry.size);
		statusComboBox.setSelectedItem(bookEntry.status);
		titleTextField.setText(bookEntry.title);
		user1ComboBox.setText(bookEntry.user1);
		user2ComboBox.setText(bookEntry.user2);
		weightTextField.setText(bookEntry.weight);
		widthTextField.setText(bookEntry.width);

		for (JLabel l : stickyLabelMap.keySet())
		{
			Component c = stickyLabelMap.get(l);
			if (c instanceof JTextComponent)
			{
				((JTextComponent) c).setCaretPosition(0);
			}
		}
	}

	private void setUserDateLabel()
	{
		String aDate = (bookEntry.addedDate.equals("")) ? "" : Constants.getPrettyDate(bookEntry.addedDate);
		String cDate = (bookEntry.changedDate.equals("")) ? "" : Constants.getPrettyDate(bookEntry.changedDate);
		String vDate = (bookEntry.validatedDate.equals("")) ? "" : Constants.getPrettyDate(bookEntry.validatedDate);
		userDateLabel
				.setText("[ Added: " + aDate + " ][ " + "Changed: " + cDate + " ][ " + "Validated: " + vDate + " ]");
	}

	private void scrapeFields()
	{
		bookEntry.author = authorComboBox.getText().trim();
		bookEntry.binding = bindingComboBox.getText().trim();
		bookEntry.bindingStyle = bindingStyleComboBox.getText().trim();
		bookEntry.bookCondition = bookConditionComboBox.getText().trim();
		bookEntry.bookID = bookIDTextField.getText().trim();
		bookEntry.bookType = bookTypeComboBox.getText().trim();
		bookEntry.catalog1 = catalog1ComboBox.getText().trim();
		bookEntry.catalog2 = catalog2ComboBox.getText().trim();
		bookEntry.catalog3 = catalog3ComboBox.getText().trim();
		bookEntry.description = descriptionArea.getText().trim();
		bookEntry.cost = costTextField.getText().trim();
		bookEntry.date = dateTextField.getText().trim();
		bookEntry.djCondition = djConditionComboBox.getText().trim();
		bookEntry.edition = editionComboBox.getText().trim();
		bookEntry.firstEdition = Boolean.toString(firstEditionCheckBox.isSelected());
		bookEntry.hasDJ = Boolean.toString(hasDJCheckBox.isSelected());
		bookEntry.height = heightTextField.getText().trim();
		bookEntry.illustrator = illustratorComboBox.getText().trim();
		bookEntry.includeInUpdate = Boolean.toString(includeInUpdateCheckBox.isSelected());
		bookEntry.isbn = isbnTextField.getText().trim();
		bookEntry.keywords = keywordsTextField.getText().trim();
		bookEntry.language = languageComboBox.getText().trim();
		bookEntry.list1 = list1TextField.getText().trim();
		bookEntry.list2 = list2TextField.getText().trim();
		bookEntry.list3 = list3TextField.getText().trim();
		bookEntry.list4 = list4TextField.getText().trim();
		bookEntry.list5 = list5TextField.getText().trim();
		bookEntry.location = locationTextField.getText().trim();
		bookEntry.pages = pagesTextField.getText().trim();
		bookEntry.place = placeTextField.getText().trim();
		bookEntry.printing = printingComboBox.getText().trim();
		bookEntry.privateField = privateTextArea.getText().trim();
		bookEntry.publisher = publisherComboBox.getText().trim();
		bookEntry.quantity = quantitySpinner.getText().trim();
		bookEntry.signed = signedComboBox.getText().trim();
		bookEntry.size = sizeComboBox.getText().trim();
		bookEntry.status = (String) statusComboBox.getSelectedItem();
		bookEntry.title = titleTextField.getText().trim();
		bookEntry.user1 = user1ComboBox.getText().trim();
		bookEntry.user2 = user2ComboBox.getText().trim();
		bookEntry.weight = weightTextField.getText().trim();
		bookEntry.width = widthTextField.getText().trim();
	}

	private void resetFormColors()
	{
		for (JLabel l : stickyLabelMap.keySet())
		{
			Component c = stickyLabelMap.get(l);
			c.setForeground(Cato.naturalColor);
		}
		djWarningLabel.setVisible(false);
		firstWarningLabel.setVisible(false);
	}

	private void checkForImages()
	{
		String dir = Constants.IMAGES_DIR + Constants.ps;
		if (!SettingsView.getInstance().get("localImage").equals(""))
		{
			dir = SettingsView.getInstance().get("localImage") + Constants.ps;
		}
		String bid = bookIDTextField.getText().toLowerCase();
		if (bid.equals(""))
		{
			bid = "noimage";
		}
		try
		{
			String[] children = new File(dir).list();
			if (children != null)
			{
				Arrays.sort(children);
				for (String s : children)
				{
					if (s.toLowerCase().startsWith(bid))
					{
						ImageIcon ii = new ImageIcon(dir + Constants.ps + s);
						imagePanel.setIcon(ii);
						return;
					}
				}
				ImageIcon ii = new ImageIcon(dir + Constants.ps + "NoImage.png");
				imagePanel.setIcon(ii);
			}
		}
		catch (Exception ex)
		{
			imagePanel.setIcon(null);
			Constants.writeBlog("BookEditView > checkForImages > " + ex);
		}
	}

	public void saveBookEntryAndAddAnother()
	{
		if (saveEntry())
		{
			setupEntry(-1);
		}
	}

	private void traverse(int next)
	{
		if (!saveEntry())
		{
			return;
		}
		if (bookCount != BookListView.getInstance().listTable.getRowCount())
		{
			bookIndex = 0;
			bookCount = BookListView.getInstance().listTable.getRowCount();
			if (bookCount == 0)
			{
				cancelEntry();
				return;
			}
		}
		else
		{
			bookIndex = Constants.clamp(next, 0, bookCount - 1);
		}
		setupEntry(Integer.parseInt((String) BookListView.getInstance().listTable.getValueAt(bookIndex, 0)));
	}

	public void cancelEntry()
	{
		doSaveStickies();
		BooksView.getInstance().tabbedPane.closeTab(this);
	}

	public boolean saveEntry()
	{
		doSaveStickies();
		String message = "";
		String nextID = DBFunctions.getInstance().getNextID("BKS", Constants.BOOK_NUMBER);
		int i = DBFunctions.getInstance().selectOne("BKS", Constants.BOOK_ID, bookIDTextField.getText());
		if ((i > -1) && (bookEntry.key.equals("-1")))
		{
			bookIDTextField.setText(bookIDTextField.getText() + "-2");
			message += "ID not unique.\n";
		}
		if (bookIDTextField.getText().trim().equals(""))
		{
			bookIDTextField.setText("DUMMY ID " + nextID);
			message += "ID cannot be blank.\n";
		}
		if (titleTextField.getText().trim().equals(""))
		{
			titleTextField.setText("DUMMY TITLE " + nextID);
			message += "Title canot be blank.\n";
		}
		if (list1TextField.getText().equals(""))
		{
			list1TextField.setText("-1.00");
			message += "First list price cannot be blank.\n";
		}
		if (!Constants.parseDollars(list1TextField))
		{
			message += SettingsView.getInstance().get("list1") + " invalid value was: " + list1TextField.getText() + ".\n";
			list1TextField.setText("-1.00");
		}
		if (!Constants.parseDollars(list2TextField))
		{
			message += SettingsView.getInstance().get("list2") + " invalid value was: " + list2TextField.getText() + ".\n";
			list2TextField.setText("-1.00");
		}
		if (!Constants.parseDollars(list3TextField))
		{
			message += SettingsView.getInstance().get("list3") + " invalid value was: " + list3TextField.getText() + ".\n";
			list3TextField.setText("-1.00");
		}
		if (!Constants.parseDollars(list4TextField))
		{
			message += SettingsView.getInstance().get("list4") + " invalid value was: " + list4TextField.getText() + ".\n";
			list4TextField.setText("-1.00");
		}
		if (!Constants.parseDollars(list5TextField))
		{
			message += SettingsView.getInstance().get("list5") + " invalid value was: " + list5TextField.getText() + ".\n";
			list5TextField.setText("-1.00");
		}
		if (message.length() > 0)
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(), "Invalid data found:\n" + message);
			return false;
		}
		bookEntry.changedDate = Constants.getTimestamp();
		scrapeFields();
		if (bookEntry.key.equals("-1"))
		{
			bookEntry.bookNumber = nextID;
			if (SettingsView.getInstance().get("autoInc").equalsIgnoreCase("true"))
			{
				String idx = SettingsView.getInstance().get("autoIncNumber");
				SettingsView.getInstance().set("autoIncNumber", Integer.toString((Integer.parseInt(idx) + 1)));
			}
		}
		bookEntry.saveBookEntry();
		return true;
	}

	private void openISBN()
	{
		if (SettingsView.getInstance().get("isbnUrl").equals(""))
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(),
					"No ISBN-lookup URL has been selected.  Please check the SettingsView.");
		}
		else
		{
			if (!validateISBN(isbnTextField.getText().trim()))
			{
				JOptionPane.showMessageDialog(BooksView.getInstance(), "ISBN checksum failed. Please verify ISBN.");
			}
			else
			{
				try
				{
					int idx = DBFunctions.getInstance().selectOne("ISN", 1, SettingsView.getInstance().get("isbnUrl"));
					URL url = URI.create(urlExpand(DBFunctions.getInstance().get(idx, 2))).toURL();
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					String str;
					String info = "";
					while ((str = in.readLine()) != null)
					{
						info += str;
					}
					in.close();
					String totalItems = info.replaceFirst("^.*?\"totalItems\".*?([0-9]+).*$", "$1");
					if (!totalItems.equals("0"))
					{
						String author = info.replaceFirst("^.*?\"authors\".*?\"(.*?)\".*$", "$1");
						if (!author.startsWith("{"))
						{
							authorComboBox.setText(author);
							authorComboBox.setCaretPosition(0);
						}
						String title = info.replaceFirst("^.*?\"title\".*?\"(.*?)\".*$", "$1");
						String subtitle = info.replaceFirst("^.*?\"subtitle\".*?\"(.*?)\".*$", "$1");
						if (!subtitle.equals("") && !subtitle.startsWith("{"))
						{
							title += ": " + subtitle;
						}
						if (!title.startsWith("{"))
						{
							titleTextField.setText(title);
							titleTextField.setCaretPosition(0);
						}
						String publisher = info.replaceFirst("^.*?\"publisher\".*?\"(.*?)\".*$", "$1");
						if (!publisher.startsWith("{"))
						{
							publisherComboBox.setText(publisher);
							publisherComboBox.setCaretPosition(0);
						}
						String date = info.replaceFirst("^.*?\"publishedDate\".*?\"(.*?)\".*$", "$1");
						if (!date.startsWith("{"))
						{
							dateTextField.setText(date);
							dateTextField.setCaretPosition(0);
						}
						String language = info.replaceFirst("^.*?\"language\".*?\"(.*?)\".*$", "$1");
						if (!language.startsWith("{"))
						{
							languageComboBox.setText(language);
							languageComboBox.setCaretPosition(0);
						}
						String comments = info.replaceFirst("^.*?\"description\".*?\"(.*?)\".*$", "$1");
						if (!comments.startsWith("{"))
						{
							privateTextArea.setText(comments);
							privateTextArea.setCaretPosition(0);
						}
						String pages = info.replaceFirst("^.*?\"pageCount\".*?([0-9]+).*$", "$1");
						if (!pages.startsWith("{"))
						{
							pagesTextField.setText(pages);
							pagesTextField.setCaretPosition(0);
						}
						String catalog = info.replaceFirst("^.*?\"categories\".*?\"(.*?)\".*$", "$1");
						if (!catalog.startsWith("{"))
						{
							catalog1ComboBox.setText(catalog);
							catalog1ComboBox.setCaretPosition(0);
						}
					}
					else
					{
						JOptionPane.showMessageDialog(BooksView.getInstance(), "No Data Found.");
					}
					authorComboBox.requestFocusInWindow();
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					StringSelection stringSelection = new StringSelection(isbnTextField.getText());
					clipboard.setContents(stringSelection, this);
				}
				catch (Exception ex)
				{
					Constants.writeBlog("BookEditView > openISBN > " + ex);
				}
			}
		}
	}

	private void openPrice()
	{
		if (SettingsView.getInstance().get("priceUrl").equals(""))
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(),
					"No Price-lookup URL has been selected.  Please choose one in the SettingsView.");
		}
		else
		{
			try
			{
				String url = SettingsView.getInstance().get("priceUrl");
				url = DBFunctions.getInstance().get(DBFunctions.getInstance().selectOne("PLU", 1, url), 2);
				url = urlExpand(url);
				Desktop.getDesktop().browse(new URI(url));
			}
			catch (Exception ex)
			{
				Constants.writeBlog("BookEditView > openPrice > " + ex);
			}
		}
	}

	private String urlExpand(String url)
	{
		try
		{
			url = url.replaceAll("\\[\\[isbn\\]\\]", URLEncoder.encode(isbnTextField.getText(), "UTF8"));
			url = url.replaceAll("\\[\\[author\\]\\]", URLEncoder.encode(authorComboBox.getText(), "UTF8"));
			url = url.replaceAll("\\[\\[title\\]\\]", URLEncoder.encode(titleTextField.getText(), "UTF8"));
			url = url.replaceAll("\\[\\[publisher\\]\\]", URLEncoder.encode(publisherComboBox.getText(), "UTF8"));
			url = url.replaceAll("\\[\\[place\\]\\]", URLEncoder.encode(placeTextField.getText(), "UTF8"));
			url = url.replaceAll("\\[\\[date\\]\\]", URLEncoder.encode(dateTextField.getText(), "UTF8"));
		}
		catch (UnsupportedEncodingException ex)
		{
			Constants.writeBlog("BookEditView > urlExpand > " + ex);
		}
		return (url);
	}

	private void selectTool()
	{
		ArrayList<String> macroNames = DBFunctions.getInstance().generateList("MAC", Constants.MACRO_NAME);
		Collections.sort(macroNames, String.CASE_INSENSITIVE_ORDER);

		ScrollablePopupMenu popup = new ScrollablePopupMenu();
		popup.setMaximumVisibleRows(20);
		popup.setFocusable(false);
		for (String s : macroNames)
		{
			JMenuItem mi = new JMenuItem(s);
			mi.setBackground(Cato.whiteAndBlack);
			mi.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					playMacro(ae.getActionCommand());
				}
			});
			popup.add(mi);
		}
		int x = toolButton.getX();
		int y = toolButton.getY();
		y = y + toolButton.getHeight();
		// use saveButton as hack due to layout
		popup.show(saveButton, x, y);
	}

	// **************************************************************************
	// Snip Snip Macros
	// **************************************************************************
	public void playMacro(String selection)
	{
		int index = DBFunctions.getInstance().selectOne("MAC", Constants.MACRO_NAME, selection);
		String macro = DBFunctions.getInstance().get(index, Constants.MACRO_TEXT);
		String pasteBoard = "";
		Component currentComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		boolean isText = (currentComponent instanceof JTextComponent);
		int currentCaret = 0;
		if (isText) currentCaret = ((JTextComponent) currentComponent).getCaretPosition();


		ArrayList<String> tokens = BooksImportExport.parseTokens(macro);
		for (String token : tokens)
		{
			if (!token.startsWith("[["))
			{
				if (isText)
				{
					((JTextComponent) currentComponent).setCaretPosition(currentCaret);
					((JTextComponent) currentComponent).replaceSelection(token);
					currentCaret = currentCaret + token.length();
				}
			}
			else
			{
				token = token.substring(2, token.length() - 2);
				if (token.equals("clear"))
				{
					if (isText)
					{
						((JTextComponent) currentComponent).setText("");
						currentCaret = 0;
					}
				}
				else if (token.equals("eof"))
				{
					if (isText)
					{
						currentCaret = ((JTextComponent) currentComponent).getText().length();
					}
				}
				else if (token.equals("space?"))
				{
					if (isText)
					{
						if (currentCaret > 0)
						{
							((JTextComponent) currentComponent).setCaretPosition(currentCaret);
							((JTextComponent) currentComponent).replaceSelection(" ");
							currentCaret = currentCaret + 1;
						}
					}
				}
				else if (token.startsWith("caret"))
				{
					String[] q = token.split("-", 2);
					currentCaret = Integer.parseInt(q[1]);
					if (currentCaret > ((JTextComponent) currentComponent).getText().length())
					{
						currentCaret = ((JTextComponent) currentComponent).getText().length();
					}
				}
				else if (token.startsWith("seekstart"))
				{
					if (isText)
					{
						String[] q = token.split("-", 2);
						String source = ((JTextComponent) currentComponent).getText();
						currentCaret = source.indexOf(q[1]);
						if (currentCaret < 0)
						{
							currentCaret = 0;
						}
					}
				}
				else if (token.startsWith("seekend"))
				{
					if (isText)
					{
						String[] q = token.split("-", 2);
						String source = ((JTextComponent) currentComponent).getText();
						currentCaret = source.indexOf(q[1]);
						currentCaret = (currentCaret < 0) ? q[1].length() : currentCaret + q[1].length();
					}
				}
				else if (token.startsWith("cut"))
				{
					if (isText)
					{
						pasteBoard = ((JTextComponent) currentComponent).getText();
						((JTextComponent) currentComponent).setText("");
						currentCaret = 0;
					}
					else if (currentComponent instanceof JCheckBox)
					{
						pasteBoard = Boolean.toString(((JCheckBox) currentComponent).isSelected());
					}
				}
				else if (token.startsWith("copy"))
				{
					if (isText)
					{
						pasteBoard = ((JTextComponent) currentComponent).getText();
					}
					else if (currentComponent instanceof JCheckBox)
					{
						pasteBoard = Boolean.toString(((JCheckBox) currentComponent).isSelected());
					}
				}
				else if (token.startsWith("paste"))
				{
					if (isText)
					{
						String[] q = token.split("-", 2);
						String source = ((JTextComponent) currentComponent).getText();
						currentCaret = source.indexOf(q[1]);
						currentCaret = (currentCaret < 0) ? 0 : currentCaret + q[1].length();
					}
					else if (currentComponent instanceof JCheckBox)
					{
						((JCheckBox) currentComponent).setSelected(Boolean.parseBoolean(pasteBoard));
					}
				}
				else if (token.equals("bs"))
				{
					StringBuilder xx = new StringBuilder(((JTextComponent) currentComponent).getText());
					if (currentCaret > 0)
					{
						xx.deleteCharAt(currentCaret - 1);
						((JTextComponent) currentComponent).setText(xx.toString());
						currentCaret--;
					}
				}
				else if (token.startsWith("ask"))
				{
					String[] q = token.split("-", 2);
					String reply = JOptionPane.showInputDialog(null, q[1]);
					if (isText)
					{
						((JTextComponent) currentComponent).setCaretPosition(currentCaret);
						((JTextComponent) currentComponent).replaceSelection(reply);
						currentCaret = currentCaret + reply.length();
					}
				}
				else if (token.equals("pick"))
				{
					if (!(currentComponent instanceof GenericComboBox)) return;
					ArrayList<String> al = ((GenericComboBox) currentComponent).getModel();
					AutoComplete pick = new AutoComplete(al);
					JPanel panel = new JPanel();
					panel.add(pick);
					pick.setSelectedItem(((GenericComboBox) currentComponent).getText());
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							pick.getEditor().selectAll();
							pick.requestFocusInWindow();
						}
					});
					int ret = JOptionPane.showConfirmDialog(this, panel, "Selection", JOptionPane.OK_CANCEL_OPTION);
					if (ret == JOptionPane.OK_OPTION)
					{
						String value = (String) pick.getSelectedItem();
						((GenericComboBox) currentComponent).setText(value);
					}
					currentCaret = ((JTextComponent) currentComponent).getText().length();
				}
				else if (token.equals("true"))
				{
					if (currentComponent instanceof JCheckBox)
					{
						((JCheckBox) currentComponent).setSelected(true);
					}
				}
				else if (token.equals("false"))
				{
					if (currentComponent instanceof JCheckBox)
					{
						((JCheckBox) currentComponent).setSelected(false);
					}
				}
				else if (token.startsWith("field"))
				{
					// set up map of fields and field names
					String[] field = (token.split("-", 2));
					currentComponent = macroFieldMap.get(field[1].toLowerCase());
					if (currentComponent instanceof JTextComponent)
					{
						currentCaret = ((JTextComponent) currentComponent).getCaretPosition();
						isText = true;
					}
					else
					{
						isText = false;
					}
					currentComponent.requestFocusInWindow();
				}
			}
		}
		if (currentComponent instanceof JTextComponent)
		{
			((JTextComponent) currentComponent).setCaretPosition(currentCaret);
		}
	}

	// ******************************************************************************************
	// Here Be Validation
	// ******************************************************************************************
	public void validateRecord()
	{
		resetFormColors();
		// hilite fields not correct
		for (JLabel l : stickyLabelMap.keySet())
		{
			Component c = stickyLabelMap.get(l);
			if (c instanceof GenericComboBox)
			{
				validateCombo((GenericComboBox) c);
				SpellCheck.getInstance().runSpellCheck((JTextComponent) c);
			}
			else if (c instanceof JTextComponent)
			{
				SpellCheck.getInstance().runSpellCheck((JTextComponent) c);
			}
		}

		// ISBN
		if (!validateISBN(isbnTextField.getText()))
		{
			isbnTextField.setForeground(Color.red);
		}

		// Place
		String s = publisherComboBox.getText();
		int i = DBFunctions.getInstance().checkFor("PUB", Constants.PUBLISHER_PUBLISHER, s, Constants.PUBLISHER_PLACE,
				placeTextField.getText());
		if (i == -1)
		{
			int idx = DBFunctions.getInstance().checkFor("DVA", Constants.DATA_VALIDATION_FIELD, "Place",
					Constants.DATA_VALIDATION_INCORRECT, placeTextField.getText());
			if (idx != -1)
			{
				placeTextField.setText(DBFunctions.getInstance().get(idx, Constants.DATA_VALIDATION_CORRECT));
			}
			else // omg! invalid data!
			{
				placeTextField.setForeground(Color.RED);
			}
		}

		// check boxes: fe? has dj?
		String djc = (djConditionComboBox.getText()).trim().toLowerCase();
		boolean djNo = djc.startsWith("no") || djc.equals("");
		boolean djs = hasDJCheckBox.isSelected();
		if (djNo == djs)
		{
			djWarningLabel.setVisible(true);
		}

		String edc = (editionComboBox.getText()).toLowerCase();
		String prc = (printingComboBox.getText()).toLowerCase();
		boolean ed1st = ((edc.contains("first")) || (edc.contains("1st")));
		boolean pr1st = ((prc.contains("first")) || (prc.contains("1st")));
		boolean cbs = firstEditionCheckBox.isSelected();
		if ((ed1st && pr1st) == !cbs)
		{
			firstWarningLabel.setVisible(true);
		}

		bookEntry.validatedDate = Constants.getTimestamp();
		setUserDateLabel();
	}

	public Boolean validateISBN(String isbn)
	{
		String s = isbn.trim().toLowerCase();
		if (s.length() == 10)
		{
			int sum = 0;
			char[] charArray = s.toCharArray();
			for (int i = 0; i < 10; i++)
			{
				if (charArray[i] == 'x')
				{
					sum += 10;
				}
				else
				{
					sum += (10 - i) * (charArray[i] - '0');
				}
			}
			if (sum % 11 == 0)
			{
				return true;
			}
		}
		else if (s.length() == 13)
		{
			int sum = 0;
			char[] charArray = s.toCharArray();
			for (int i = 0; i < 13; i++)
			{
				if (i % 2 == 0)
				{
					sum += (charArray[i] - '0');
				}
				else
				{
					sum += (charArray[i] - '0') * 3;
				}
			}
			if (sum % 10 == 0)
			{
				return true;
			}
		}
		return false;
	}

	public void validateCombo(GenericComboBox comboBox)
	{
		if (comboBox.isValueInList()) return;
		String value = comboBox.getText();
		if (value.equals("")) return;
		String field = comboBox.getName();

		int idx = DBFunctions.getInstance().checkFor("DVA", Constants.DATA_VALIDATION_FIELD, field,
				Constants.DATA_VALIDATION_INCORRECT, value);
		if (idx != -1)
		{
			String rep = (String) DBFunctions.getInstance().get(idx, Constants.DATA_VALIDATION_CORRECT);
			if (rep.startsWith("[["))
			{
				String macro = rep.substring(2, rep.length() - 2);
				comboBox.requestFocusInWindow();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						playMacro(macro);
					}
				});
			}
			else
			{
				comboBox.setText(rep);
			}
		}
		else // omg! invalid data!
		{
			comboBox.setForeground(Color.RED);
		}
	}

	// *****************************************
	// List Builds
	// ******************************************
	public void setUpCombos()
	{
		updateAuthors();
		updatePublishers();
		bindingStyleComboBox.setModel(DBFunctions.getInstance().generateList("BST", Constants.LIST_VALUE));
		bindingComboBox.setModel(DBFunctions.getInstance().generateList("BND", Constants.LIST_VALUE));
		bookConditionComboBox.setModel(DBFunctions.getInstance().generateList("BCN", Constants.LIST_VALUE));
		bookTypeComboBox.setModel(DBFunctions.getInstance().generateList("BKT", Constants.LIST_VALUE));
		catalog1ComboBox.setModel(DBFunctions.getInstance().generateList("CAT", Constants.LIST_VALUE));
		catalog2ComboBox.setModel(DBFunctions.getInstance().generateList("CAT", Constants.LIST_VALUE));
		catalog3ComboBox.setModel(DBFunctions.getInstance().generateList("CAT", Constants.LIST_VALUE));
		djConditionComboBox.setModel(DBFunctions.getInstance().generateList("DJC", Constants.LIST_VALUE));
		editionComboBox.setModel(DBFunctions.getInstance().generateList("EDT", Constants.LIST_VALUE));
		languageComboBox.setModel(DBFunctions.getInstance().generateList("LAN", Constants.LIST_VALUE));
		printingComboBox.setModel(DBFunctions.getInstance().generateList("PRT", Constants.LIST_VALUE));
		signedComboBox.setModel(DBFunctions.getInstance().generateList("SGN", Constants.LIST_VALUE));
		sizeComboBox.setModel(DBFunctions.getInstance().generateList("SIZ", Constants.LIST_VALUE));
		user1ComboBox.setModel(DBFunctions.getInstance().generateList("US1", Constants.LIST_VALUE));
		user2ComboBox.setModel(DBFunctions.getInstance().generateList("US2", Constants.LIST_VALUE));
	}

	public void updateAuthors()
	{
		authorComboBox.setModel(DBFunctions.getInstance().generateList("AUT", Constants.AUTHOR_NAME));
		illustratorComboBox.setModel(DBFunctions.getInstance().generateList("AUT", Constants.AUTHOR_NAME));
	}

	public void updatePublishers()
	{
		publisherComboBox.setModel(DBFunctions.getInstance().generateList("PUB", Constants.PUBLISHER_PUBLISHER));
	}

	public void updateAuthor(GenericComboBox j)
	{
		String author = j.getText();
		int i = DBFunctions.getInstance().selectOne("AUT", Constants.AUTHOR_NAME, author);
		if (i < 0)
		{
			AuthorListView.getInstance().addRecord();
			AuthorEditView.getInstance().nameTextField.setText(author);
		}
		else
		{
			BooksView.getInstance().tabbedPane.openTab(AuthorListView.getInstance());
			AuthorListView.getInstance().anyComboBox[0].setSelectedItem("Author/Illustrator");
			AuthorListView.getInstance().anyTextField[0].setText(author);
			AuthorListView.getInstance().anyTextField[1].setText("");
		}
	}

	public void updatePublisher()
	{
		String publisher = publisherComboBox.getText();
		int i = DBFunctions.getInstance().selectOne("PUB", Constants.PUBLISHER_PUBLISHER, publisher);
		if (i < 0)
		{
			PublisherListView.getInstance().addRecord();
			PublisherEditView.getInstance().publisherTextField.setText(publisher);
			PublisherEditView.getInstance().placeTextField.setText(placeTextField.getText());
		}
		else
		{
			BooksView.getInstance().tabbedPane.openTab(PublisherListView.getInstance());
			PublisherListView.getInstance().anyComboBox[0].setSelectedItem("Publisher");
			PublisherListView.getInstance().anyTextField[0].setText(publisher);
			PublisherListView.getInstance().anyTextField[1].setText("");
		}
	}

	private void doPlaceTextFieldClick()
	{
		String publisher = publisherComboBox.getText();
		ArrayList<Integer> i = DBFunctions.getInstance().selectAll("PUB", Constants.PUBLISHER_PUBLISHER, publisher);
		if (i.size() > 0)
		{
			placeTextField.setText(DBFunctions.getInstance().get(i.get(0), Constants.PUBLISHER_PLACE));
			String tt = "<html>" + DBFunctions.getInstance().get(i.get(0), Constants.PUBLISHER_PLACE) + " • "
					+ DBFunctions.getInstance().get(i.get(0), Constants.PUBLISHER_START_DATE) + " • "
					+ DBFunctions.getInstance().get(i.get(0), Constants.PUBLISHER_END_DATE) + " • "
					+ DBFunctions.getInstance().get(i.get(0), Constants.PUBLISHER_FIRST_EDITION);
			if (i.size() > 1)
			{
				for (int j = 1; j < i.size(); j++)
				{
					tt += "<br>" + DBFunctions.getInstance().get(i.get(j), Constants.PUBLISHER_PLACE) + " • "
							+ DBFunctions.getInstance().get(i.get(j), Constants.PUBLISHER_START_DATE) + " • "
							+ DBFunctions.getInstance().get(i.get(j), Constants.PUBLISHER_END_DATE) + " • "
							+ DBFunctions.getInstance().get(i.get(j), Constants.PUBLISHER_FIRST_EDITION);
				}
			}
			tt += "</html>";
			if (Boolean.parseBoolean(SettingsView.getInstance().get("publisherInfo")))
			{
				publisherComboBox.setToolTipText(tt);
			}
			else
			{
				JOptionPane.showMessageDialog(bookIDTextField, tt);
			}
		}
	}

	private void enableNavButtons(Boolean on)
	{
		backAllButton.setEnabled(on);
		backOneButton.setEnabled(on);
		forwardOneButton.setEnabled(on);
		forwardAllButton.setEnabled(on);
	}

	private void updateQuantity(int i)
	{
		int q = Integer.parseInt(quantitySpinner.getText());
		q = Constants.clamp(q + i, 0, 999999);
		quantitySpinner.setText(Integer.toString(q));
		if (!statusComboBox.getSelectedItem().equals("On Hold"))
		{
			if (q < 1)
			{
				statusComboBox.setSelectedItem("Sold");
			}
			else
			{
				statusComboBox.setSelectedItem("For Sale");
			}
		}
	}

	protected void initComponents()
	{
		recordNoTextField.setFocusable(false);
		imagePanel.setFocusable(false);
		validateButton.setFocusable(false);

		recordNoTextField.setEnabled(false);
		recordNoLabel.setFont(Cato.catoFont);
		recordNoLabel.setMinimumSize(new Dimension(70, Cato.compHeight));
		includeInUpdateLabel.setFont(Cato.catoFont);
		includeInUpdateLabel.setMinimumSize(new Dimension(70, Cato.compHeight));
		bookIDLabel.setFont(Cato.catoFont);
		bookIDLabel.setMinimumSize(new Dimension(70, Cato.compHeight));
		quantityLabel.setFont(Cato.catoFont);
		quantityLabel.setMinimumSize(new Dimension(70, Cato.compHeight));

		firstWarningLabel.setFont(Cato.catoFont);
		firstWarningLabel.setPreferredSize(new Dimension(150, Cato.compHeight));
		firstWarningLabel.setForeground(Color.red);
		firstWarningLabel.setVisible(false);
		djWarningLabel.setFont(Cato.catoFont);
		djWarningLabel.setPreferredSize(new Dimension(150, Cato.compHeight));
		djWarningLabel.setForeground(Color.red);
		djWarningLabel.setVisible(false);

		userDateLabel.setForeground(Cato.hiliteColor);
		userDateLabel.setHorizontalAlignment(JLabel.CENTER);
		userDateLabel.setFont(Cato.catoFont);

		hasDJCheckBox.setForeground(Cato.hiliteColor);
		firstEditionCheckBox.setForeground(Cato.hiliteColor);

		descriptionArea.setFocusTraversalKeysEnabled(true);
		jScrollPane1.setViewportView(descriptionArea);
		Constants.setTabs(descriptionArea);

		privateTextArea.setFocusTraversalKeysEnabled(true);
		jScrollPane2.setViewportView(privateTextArea);
		Constants.setTabs(privateTextArea);

		quantitySpinner.setPreferredSize(new Dimension(146, Cato.compHeight));

		publisherComboBox.addMouseListener(new MouseAdapter()
		{
			public void mouseEntered(MouseEvent arg0)
			{
				ToolTipManager.sharedInstance().setEnabled(true);
				ToolTipManager.sharedInstance().setInitialDelay(0);
				ToolTipManager.sharedInstance().setDismissDelay(10000);
			}

			public void mouseExited(MouseEvent arg0)
			{
				if (!SettingsView.getInstance().get("showToolTips").equalsIgnoreCase("true"))
				{
					ToolTipManager.sharedInstance().setEnabled(false);
				}
				ToolTipManager.sharedInstance().setInitialDelay(750);
				ToolTipManager.sharedInstance().setDismissDelay(4000);
			}
		});

		statusComboBox.setPreferredSize(new Dimension(200, Cato.compHeight));
		statusComboBox.setFont(Cato.catoFont);
		statusComboBox.setEditable(false);
		statusComboBox.setModel(new DefaultComboBoxModel(new String[] { "For Sale", "On Hold", "Sold" }));
		statusComboBox.addPopupMenuListener(new PopupMenuListener()
		{
			public void popupMenuCanceled(PopupMenuEvent arg0)
			{}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0)
			{
				if (statusComboBox.getSelectedItem().equals("Sold"))
				{
					quantitySpinner.setText("0");
				}
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0)
			{}
		});

		validateButton.setToolTipText("Validate this entry against tables and run spellcheck.");
		validateButton.setFont(Cato.catoFont);
		validateButton.setPreferredSize(new Dimension(150, Cato.compHeight));

		imagePanel.setSize(new Dimension(500, 500));
		imagePanel.setHorizontalAlignment(JLabel.CENTER);
		imagePanel.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if ((evt.getClickCount() > 1))
				{
					ShowImageView.getInstance().showImages(bookIDTextField.getText());
				}
			}
		});

		JPanel leftPane = new JPanel(new ColumnLayout(1, 0));
		leftPane.add("xr", bookIDLabel);
		leftPane.add("hx", bookIDTextField);
		leftPane.add("wx", cloneButton);
		leftPane.add("xr", isbnLabel);
		leftPane.add("hx", isbnTextField);
		leftPane.add("wx", isbnFillButton);
		leftPane.add("xr", authorLabel);
		leftPane.add("hx", authorComboBox);
		leftPane.add("wx", updateAuthorButton);
		leftPane.add("xr", titleLabel);
		leftPane.add("hwx", titleTextField);
		leftPane.add("xr", illustratorLabel);
		leftPane.add("hx", illustratorComboBox);
		leftPane.add("wx", updateIllustratorButton);
		leftPane.add("xr", publisherLabel);
		leftPane.add("hx", publisherComboBox);
		leftPane.add("wx", updatePublisherButton);
		leftPane.add("xr", placeLabel);
		leftPane.add("hx", placeTextField);
		leftPane.add("wx", pubInfoButton);
		leftPane.add("xr", dateLabel);
		leftPane.add("hwx", dateTextField);
		leftPane.add("xr", editionLabel);
		leftPane.add("hx", editionComboBox);
		leftPane.add("wx", updateEditionButton);
		leftPane.add("xr", firstEditionLabel);
		leftPane.add("2x", firstEditionCheckBox); // no h for checkboxes!
		leftPane.add("w", firstWarningLabel);
		leftPane.add("xr", printingLabel);
		leftPane.add("hx", printingComboBox);
		leftPane.add("wx", updatePrintingButton);
		leftPane.add("xr", bindingLabel);
		leftPane.add("hx", bindingComboBox);
		leftPane.add("wx", updateBindingTypeButton);
		leftPane.add("xr", bindingStyleLabel);
		leftPane.add("hx", bindingStyleComboBox);
		leftPane.add("wx", updateBindingStyleButton);
		leftPane.add("xr", descriptionLabel);
		leftPane.add("hvwx", jScrollPane1);

		JPanel middlePane = new JPanel(new ColumnLayout(1, 0));
		middlePane.add("xr", recordNoLabel);
		middlePane.add("hx", recordNoTextField);
		middlePane.add("wx", dictButton);
		middlePane.add("xr", bookConditionLabel);
		middlePane.add("hx", bookConditionComboBox);
		middlePane.add("wx", updateBookConditionButton);
		middlePane.add("xr", djConditionLabel);
		middlePane.add("hx", djConditionComboBox);
		middlePane.add("wx", updateDJConditionButton);
		middlePane.add("xr", hasDJLabel);
		middlePane.add("2x", hasDJCheckBox); // no h for checkboxes
		middlePane.add("w", djWarningLabel);
		middlePane.add("xr", pagesLabel);
		middlePane.add("hwx", pagesTextField);
		middlePane.add("xr", languageLabel);
		middlePane.add("hx", languageComboBox);
		middlePane.add("wx", updateLanguageButton);
		middlePane.add("xr", bookTypeLabel);
		middlePane.add("hx", bookTypeComboBox);
		middlePane.add("wx", updateBookTypeButton);
		middlePane.add("xr", signedLabel);
		middlePane.add("hx", signedComboBox);
		middlePane.add("wx", updateSignedButton);
		middlePane.add("xr", sizeLabel);
		middlePane.add("hx", sizeComboBox);
		middlePane.add("wx", updateSizeButton);
		middlePane.add("xr", locationLabel);
		middlePane.add("hwx", locationTextField);
		middlePane.add("xr", keywordsLabel);
		middlePane.add("hwx", keywordsTextField);
		middlePane.add("xr", user1Label);
		middlePane.add("xh", user1ComboBox);
		middlePane.add("wx", updateUser1Button);
		middlePane.add("xr", user2Label);
		middlePane.add("xh", user2ComboBox);
		middlePane.add("wx", updateUser2Button);
		middlePane.add("xr", privateLabel);
		middlePane.add("hvwx", jScrollPane2);

		JPanel rightPane = new JPanel(new ColumnLayout(1, 0));
		rightPane.add("xr", includeInUpdateLabel);
		rightPane.add("wx", includeInUpdateCheckBox); // no h for checkboxes
		rightPane.add("xr", costLabel);
		rightPane.add("hwx", costTextField);
		rightPane.add("xr", list1Label);
		rightPane.add("hwx", list1TextField);
		rightPane.add("xr", list2Label);
		rightPane.add("hwx", list2TextField);
		rightPane.add("xr", list3Label);
		rightPane.add("hwx", list3TextField);
		rightPane.add("xr", list4Label);
		rightPane.add("hwx", list4TextField);
		rightPane.add("xr", list5Label);
		rightPane.add("hwx", list5TextField);
		rightPane.add("xr", heightLabel);
		rightPane.add("hwx", heightTextField);
		rightPane.add("xr", widthLabel);
		rightPane.add("hwx", widthTextField);
		rightPane.add("xr", weightLabel);
		rightPane.add("hwx", weightTextField);
		rightPane.add("xr", quantityLabel);
		rightPane.add("3x", minusButton);
		rightPane.add("h", quantitySpinner);
		rightPane.add("w", plusButton);
		rightPane.add("xr", statusLabel);
		rightPane.add("hwx", statusComboBox);

		JPanel bigRightPane = new JPanel(new ColumnLayout(1, 0));
		bigRightPane.add("hwx", rightPane);
		bigRightPane.add("vhwx", imagePanel);

		JPanel bigPanel = new JPanel(new ColumnLayout(1, 0));
		bigPanel.add("hvx", leftPane);
		bigPanel.add("hvxw", middlePane);

		JPanel catalogPanel = new JPanel(new ColumnLayout(1, 0));
		catalogPanel.add("x", catalog1Label);
		catalogPanel.add("hx", catalog1ComboBox);
		catalogPanel.add("hx", catalog2ComboBox);
		catalogPanel.add("hx", catalog3ComboBox);
		catalogPanel.add("wx", updateCatalogButton);

		JPanel navPanel = new JPanel(new ColumnLayout(1, 0));
		navPanel.add("x", backAllButton);
		navPanel.add("x", backOneButton);
		navPanel.add("x", validateButton);
		navPanel.add("x", forwardOneButton);
		navPanel.add("xw", forwardAllButton);

		JPanel bigLeftPanel = new JPanel(new ColumnLayout(1, 0));
		bigLeftPanel.add("hvxw", bigPanel);
		bigLeftPanel.add("hxw", catalogPanel);
		bigLeftPanel.add("kcxw", userDateLabel);
		bigLeftPanel.add("cxw", navPanel);

		JPanel completePanel = new JPanel(new ColumnLayout(1, 0));
		completePanel.add("hvx", bigLeftPanel);
		completePanel.add("hvxw", bigRightPane);

		setLayout(new ColumnLayout(1, 0));
		add("6x", saveButton);
		add("", saveButton1);
		add("", printButton);
		add("", priceCheckButton);
		add("", toolButton);
		add("w", cancelButton);
		add("hvwx", completePanel);
	}

	public void lostOwnership(Clipboard arg0, Transferable arg1)
	{}
}
