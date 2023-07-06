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

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.text.JTextComponent;

// All entries are static, keeps a treasure trove of needed info about Cato
// as well as some useful methods tthat have no home elsewhere.

public class Constants
{
	public static final Set<? extends AWTKeyStroke> tab = KeyboardFocusManager.getCurrentKeyboardFocusManager()
			.getDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
	public static final Set<? extends AWTKeyStroke> shifttab = KeyboardFocusManager.getCurrentKeyboardFocusManager()
			.getDefaultFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);

	public static final void setTabs(Component c)
	{
		c.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, tab);
		c.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, shifttab);
	}

	public static final String ps = System.getProperty("file.separator");
	public static final String HOME_DIR = System.getProperty("user.home") + ps + "Cato";
	public static final String SYSTEM_DIR = HOME_DIR + ps + "system";
	public static final String EXPORTS_DIR = HOME_DIR + ps + "exports";
	public static final String IMAGES_DIR = HOME_DIR + ps + "images";
	public static final String TEMPLATES_DIR = HOME_DIR + ps + "templates";
	public static final String crlf = System.getProperty("line.separator");
	public static final String rn = "\r\n";
	public static final DecimalFormat twoPlaces = new DecimalFormat("#########0.00");
	public static final int modifierKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	public static final int altKey = Event.ALT_MASK;

	public static final String version = "4.0";
	public static final int textAreaSize = 65536;
	public static final int textFieldSize = 256;

	public static final boolean MERGEABLE  = true;
	public static final boolean NO_MERGEABLE = false;

	public static final int FIELD_ALL = 0;
	public static final int FIELD_NUMBER = 1;
	public static final int FIELD_CURRENCY = 2;
	public static final int FIELD_ISBN = 3;
	public static final int FIELD_FILENAME = 4;

	public static final int RECORD_TAG = 0;
	public static final int LIST_VALUE = 1;

	public static final int AUTHOR_NAME = 1;
	public static final int AUTHOR_REAL_NAME = 2;
	public static final int AUTHOR_BIOGRAPHY = 3;

	public static final int BOOK_ID = 1;
	public static final int BOOK_ISBN = 2;
	public static final int BOOK_AUTHOR = 3;
	public static final int BOOK_TITLE = 4;
	public static final int BOOK_ILLUSTRATOR = 5;
	public static final int BOOK_PUBLISHER = 6;
	public static final int BOOK_PLACE = 7;
	public static final int BOOK_DATE = 8;
	public static final int BOOK_EDITION = 9;
	public static final int BOOK_PRINTING = 10;
	public static final int BOOK_PAGES = 11;
	public static final int BOOK_LANGUAGE = 12;
	public static final int BOOK_KEYWORDS = 13;
	public static final int BOOK_CONDITION = 14;
	public static final int BOOK_FIRST_EDITION = 15;
	public static final int BOOK_BINDING_TYPE = 16;
	public static final int BOOK_DJ_CONDITION = 17;
	public static final int BOOK_BINDING_STYLE = 18;
	public static final int BOOK_HAS_DJ = 19;
	public static final int BOOK_TYPE = 20;
	public static final int BOOK_SIGNED = 21;
	public static final int BOOK_LOCATION = 22;
	public static final int BOOK_INCLUDE_IN_UPDATE = 23;
	public static final int BOOK_USER1 = 24;
	public static final int BOOK_USER2 = 25;
	public static final int BOOK_COST = 26;
	public static final int BOOK_LIST1 = 27;
	public static final int BOOK_LIST2 = 28;
	public static final int BOOK_LIST3 = 29;
	public static final int BOOK_LIST4 = 30;
	public static final int BOOK_LIST5 = 31;
	public static final int BOOK_SIZE = 32;
	public static final int BOOK_WEIGHT = 33;
	public static final int BOOK_HEIGHT = 34;
	public static final int BOOK_WIDTH = 35;
	public static final int BOOK_QUANTITY = 36;
	public static final int BOOK_UNUSED = 37;
	public static final int BOOK_STATUS = 38;
	public static final int BOOK_DESCRIPTION = 39;
	public static final int BOOK_PRIVATE = 40;
	public static final int BOOK_CATALOG1 = 41;
	public static final int BOOK_CATALOG2 = 42;
	public static final int BOOK_CATALOG3 = 43;
	public static final int BOOK_ADDED_DATE = 44;
	public static final int BOOK_CHANGED_DATE = 45;
	public static final int BOOK_VALIDATED_DATE = 46;
	public static final int BOOK_NUMBER = 47;

	public static final int CLIENT_NAME = 1;
	public static final int CLIENT_ADDRESS1 = 2;
	public static final int CLIENT_ADDRESS2 = 3;
	public static final int CLIENT_ADDRESS3 = 4;
	public static final int CLIENT_CITY = 5;
	public static final int CLIENT_POSTAL_CODE = 6;
	public static final int CLIENT_PHONE1 = 7;
	public static final int CLIENT_PHONE2 = 8;
	public static final int CLIENT_FAX = 9;
	public static final int CLIENT_EMAIL = 10;
	public static final int CLIENT_CONTACT_NAME = 11;
	public static final int CLIENT_STATE = 12;
	public static final int CLIENT_COUNTRY = 13;
	public static final int CLIENT_COMMENTS = 14;

	public static final int DATA_VALIDATION_INCORRECT = 1;
	public static final int DATA_VALIDATION_CORRECT = 2;
	public static final int DATA_VALIDATION_FIELD = 3;

	public static final int FTP_SETTINGS_NAME = 1;
	public static final int FTP_SETTINGS_HOSTNAME = 2;
	public static final int FTP_SETTINGS_USERNAME = 3;
	public static final int FTP_SETTINGS_PASSWORD = 4;
	public static final int FTP_SETTINGS_LIST = 5;
	public static final int FTP_SETTINGS_FORMAT = 6;

	public static final int INVOICE_NUMBER = 1;
	public static final int INVOICE_DATE = 2;
	public static final int INVOICE_CLIENT_NAME = 3;
	public static final int INVOICE_USER = 4;
	public static final int INVOICE_COMMENTS = 5;
	public static final int INVOICE_SHIP_TO = 6;
	public static final int INVOICE_PAYMENT_METHOD = 7;
	public static final int INVOICE_TAXES1 = 8;
	public static final int INVOICE_TAXES2 = 9;
	public static final int INVOICE_TAXES3 = 10;
	public static final int INVOICE_DISCOUNT = 11;
	public static final int INVOICE_SHIPPING = 12;
	public static final int INVOICE_FINAL_TOTAL = 13;

	public static final int MACRO_NAME = 1;
	public static final int MACRO_TEXT = 2;

	public static final int PUBLISHER_PUBLISHER = 1;
	public static final int PUBLISHER_PLACE = 2;
	public static final int PUBLISHER_START_DATE = 3;
	public static final int PUBLISHER_END_DATE = 4;
	public static final int PUBLISHER_FIRST_EDITION = 5;
	public static final int PUBLISHER_NOTES = 6;

	public static final int SALE_NUMBER = 1;
	public static final int SALE_SALESPERSON = 2;
	public static final int SALE_DATE = 3;
	public static final int SALE_BOOK_ID = 4;
	public static final int SALE_AUTHOR = 5;
	public static final int SALE_TITLE = 6;
	public static final int SALE_QUANTITY = 7;
	public static final int SALE_INVOICE_NUMBER = 8;
	public static final int SALE_REGULAR_PRICE = 9;
	public static final int SALE_LIST = 10;
	public static final int SALE_SALE_AMOUNT = 11;
	public static final int SALE_SHIPPING_CREDIT = 12;
	public static final int SALE_COMMISSION = 13;
	public static final int SALE_SECOND_COMMISSION = 14;
	public static final int SALE_FEE = 15;
	public static final int SALE_SHIPPING_COST = 16;
	public static final int SALE_TAXES1 = 17;
	public static final int SALE_TAXES2 = 18;
	public static final int SALE_TAXES3 = 19;
	public static final int SALE_COST = 20;

	public static final int SETTING_KEY = 1;
	public static final int SETTING_VALUE = 2;

	public static final int WANT_CLIENT = 1;
	public static final int WANT_AUTHOR = 2;
	public static final int WANT_TITLE = 3;
	public static final int WANT_PUBLISHER = 4;
	public static final int WANT_KEYWORDS = 5;
	public static final int WANT_BINDING = 6;
	public static final int WANT_DESCRIPTION = 7;
	public static final int WANT_PRICE_MIN = 8;
	public static final int WANT_PRICE_MAX = 9;
	public static final int WANT_FIRST_EDITION = 10;
	public static final int WANT_DUST_JACKET = 11;
	public static final int WANT_SIGNED = 12;
	public static final int WANT_STATUS = 13;
	public static final int WANT_ADDED_DATE = 14;
	public static final int WANT_CHANGED_DATE = 15;
	public static final int WANT_INCLUDE_IN_UPDATE = 16;

	public static final int NOTEPAD_NAME = 1;
	public static final int NOTEPAD_TEXT = 2;


	// Some not-so-final stuff that needs a central location
	// needed by GenericComboBox special draw routine
	public static boolean printing = false;

	public static String[] names = { "Key", "Book ID", "ISBN", "Author", "Title",
			"Illustrator", "Publisher", "Publisher Place", "Published Date",
			"Edition", "Printing", "Pages", "Language", "Keywords",
			"Book Condition", "First Edition?", "Binding Type", "DJ Condition",
			"Binding Style", "Has DJ?", "Book Type", "Signed", "Location",
			"Update?", "User 1", "User 2", "Cost", "List 1", "List 2", "List 3",
			"List 4", "List 5", "Size", "Weight", "Height", "Width", "Quantity",
			"Unused", "Status", "Description", "Private", "Catalog 1",
			"Catalog 2", "Catalog 3", "Added Date", "Changed Date",
			"Validated Date", "Record #" };

	public static String[] shortNames = { "key", "bookid", "isbn", "author",
			"title", "illustrator", "publisher", "place", "date", "edition",
			"printing", "pages", "language", "keywords", "bookcondition",
			"firstedition", "bindingtype", "djcondition", "bindingstyle",
			"hasdj", "booktype", "signed", "location", "update", "user1",
			"user2", "cost", "list1", "list2", "list3", "list4", "list5", "size",
			"weight", "height", "width", "quantity", "unused", "status",
			"description", "private", "catalog1", "catalog2", "catalog3",
			"addeddate", "changeddate", "validateddate", "recordno" };

	public static HashMap<Component, String>windowNames = new HashMap<Component, String>();
	public static HashMap<Component, String>windowIcons = new HashMap<Component, String>();

	Constants()
	{
		createFileSystem();
		BooksView.getInstance().tabbedPane.openTab(WelcomePanel.getInstance());
	}

	public void createFileSystem()
	{
		// Check file system and recreate as needed
		makeDirectory(HOME_DIR);
		makeDirectory(SYSTEM_DIR);
		makeDirectory(EXPORTS_DIR);
		makeDirectory(IMAGES_DIR);
		{
			saveOutImage("cato.png");
			saveOutImage("NoImage.png");
		}
		makeDirectory(TEMPLATES_DIR);
		{
			saveOutTemplate("Abe Wants.txt.format");
			saveOutTemplate("Amazon.tsv.format");
			saveOutTemplate("AmazonValues.txt");
			saveOutTemplate("Catalog.html.format");
			saveOutTemplate("Cato.kto.format");
			saveOutTemplate("Homebase 2.3.txt.format");
			saveOutTemplate("Homebase 3.txt.format");
			saveOutTemplate("UIEE.txt.format");
			saveOutTemplate("Invoice.html");
		}
	}

	private void makeDirectory(String folder)
	{
		File dir = new File(folder);
		if (dir.mkdir())
		{
			writeBlog(folder + ps + " created.");
		}
	}

	private void saveOutImage(String name)
	{
		File file = new File(Constants.IMAGES_DIR + Constants.ps + name);
		if (!file.exists())
		{
			try
			{
				BufferedImage img = ImageIO.read(getClass().getClassLoader().getResource("resources/" + name));
				ImageIO.write(img, "png", file);
			}
			catch (IOException ex)
			{
				Constants.writeBlog("Constants > saveOutImage > " + ex);
			}
		}
	}

	private void saveOutTemplate(String name)
	{
		File file = new File(Constants.TEMPLATES_DIR + Constants.ps + name);
		if (!file.exists())
		{
			String result = "";
			Scanner scan;
			try
			{
				scan = new Scanner(getClass().getResourceAsStream("/resources/" + name), "UTF-8");
				scan.useDelimiter("\\A");
				result = scan.next();
				FileWriter fw = new FileWriter(new File(TEMPLATES_DIR + ps + name), true);
				fw.write(result);
				fw.close();
				writeBlog(name + "  created.");
			}
			catch (Exception ex)
			{
				Constants.writeBlog("Constants > saveOutTemplate > " + ex);
			}
		}
	}

	public static void setUserFields()
	{
		names[BOOK_LIST1] = SettingsView.getInstance().get("list1");
		names[BOOK_LIST2] = SettingsView.getInstance().get("list2");
		names[BOOK_LIST3] = SettingsView.getInstance().get("list3");
		names[BOOK_LIST4] = SettingsView.getInstance().get("list4");
		names[BOOK_LIST5] = SettingsView.getInstance().get("list5");
		names[BOOK_USER1] = SettingsView.getInstance().get("user1");
		names[BOOK_USER2] = SettingsView.getInstance().get("user2");
	}

	public static boolean parseDollars(JTextComponent c)
	{
		String text = c.getText();
		if (text.length() > 0)
		{
			try
			{
				c.setText(twoPlaces.format(Double.parseDouble(text)));
				return true;
			}
			catch (Exception ex)
			{
				c.setForeground(Color.red);
				return false;
			}
		}
		return true;
	}

	public static double scrapeDollars(JTextComponent c)
	{
		String text = c.getText();
		double amount = 0.0;
		try
		{
			amount = Double.parseDouble(text);
		}
		catch (Exception ex)
		{}
		return amount;
	}

	public static int clamp(int val, int min, int max)
	{
		if (val > max)
		{
			return max;
		}
		if (val < min)
		{
			return min;
		}
		return val;
	}

	public static String getTimestamp()
	{
		return Long.toString(System.currentTimeMillis() / 1000);
	}

	public static String getFileDateTime()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return dateFormat.format(new Date());
	}

	public static String getPrettyDateTime()
	{
		DateFormat dateFormat = new SimpleDateFormat(SettingsView.getInstance().get("dateFormat") + " " + "hh:mm:ss");
		return dateFormat.format(new Date());
	}

	// some of these may be blank
	public static String getPrettyDate(String d)
	{
		DateFormat dateFormat = new SimpleDateFormat(SettingsView.getInstance().get("dateFormat"));
		return dateFormat.format(Long.parseLong(d) * 1000);
	}

	public static void setFileChooserFont(Component[] comp)
	{
		for (int x = 0; x < comp.length; x++)
		{
			if (comp[x] instanceof Container)
			{
				setFileChooserFont(((Container) comp[x]).getComponents());
			}
			comp[x].setFont(Cato.catoFont);
		}
	}

	public static void writeBlog(String message)
	{
		String dataBase = "System";
		message = message.replaceAll("[\t\f\n\r]", " • ");
		dataBase = SettingsView.getInstance().get("dbName");
		if (dataBase.equals(""))
		{
			dataBase = "System";
		}
		File of = new File(SYSTEM_DIR + ps + dataBase + ".log");
		if (!of.exists())
		{
			message = "New log created." + crlf + getFileDateTime() + ": " + message;
		}
		else if (of.length() > 5242880L)
		{
			File of2 = new File(SYSTEM_DIR + ps + dataBase + ".log");
			File fn2 = new File(SYSTEM_DIR + ps + dataBase + "_" + getFileDateTime() + ".log");
			of2.renameTo(fn2);
			message = "New log created." + crlf + getFileDateTime() + ": " + message;
		}
		try
		{
			FileWriter fw = new FileWriter(of, true);
			fw.write(getFileDateTime() + ": " + message + crlf);
			fw.close();
		}
		catch (Exception ex)
		{
			System.out.println("WriteBlog > " + ex);
		}
	}
}
