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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

public class BooksImportExport
{

	private boolean parsingHeader = false;
	private boolean parsingRecord = false;
	private boolean parsingFooter = false;
	private boolean keyValuePairs = false;
	private boolean isQuoted = false;
	private boolean negativePrice = false;
	private boolean addsOnly = false;
	private boolean deletesOnly = false;
	private boolean importing = false;
	private boolean exporting = false;
	private boolean merging = false;
	private boolean wantsExporting = false;

	private boolean include = false;

	private String quoteEscapeChar = "";
	private String delimiterChar = "";
	private StringBuilder wholeOut;
	private String format;
	private String list;
	private String encoding;

	private File fn;

	private int numberOfAdds = 0;
	private int numberOfDeletes = 0;

	private String externalDateFormat = null;

	private ArrayList<String> bookList;
	private ArrayList<String> headerTokens = new ArrayList<String>();
	private ArrayList<String> recordTokens = new ArrayList<String>();
	private ArrayList<String> footerTokens = new ArrayList<String>();

	private Map<String, String> m = new HashMap<String, String>(150);
	public Map<String, String> amazonValues = new HashMap<String, String>(300);
	String user;

	private String rnString = "\r\n";

	public BooksImportExport()
	{
		if (SettingsView.getInstance().get("user1").equals("AMZ"))
		{
			user = "user1";
		}
		else if (SettingsView.getInstance().get("user2").equals("AMZ"))
		{
			user = "user2";
		}
		readAmazonValues();
	}

	public void setInclude(boolean i)
	{
		include = i;
	}

	public void setBookList(ArrayList l)
	{
		bookList = l;
	}

	public void setFormat(String f)
	{
		format = f;
	}

	public void setFileName(File f)
	{
		fn = f;
	}

	public void setList(String v)
	{
		list = v;
	}

	public void setMerging(boolean b)
	{
		merging = b;
	}

	public int importBooks()
	{
		exporting = false;
		importing = true;

		encoding = "UTF-8";
		setList("All");

		if (!merging)
		{
			DBFunctions.getInstance().clear("BKS");
		}

		// open and scan template for import, setting flags and variables
		parseTemplate();
		if (externalDateFormat == null)
		{ // error out
			return -1;
		}
		Constants.writeBlog("Import: Template parsed.");
		if (keyValuePairs) // tokenize and find in template
		{
			HashMap<String, String> tokenMap = new HashMap<String, String>();
			createEmptyTokenMap();
			for (int i = 0; i < recordTokens.size() - 3; i += 4)
			{ // move tokens into hash map pairs
				String key = recordTokens.get(i);
				String d = recordTokens.get(i + 1);
				String value = recordTokens.get(i + 2);
				if (!d.equals("[[d]]"))
				{ // error out
					return -2;
				}
				tokenMap.put(key, value);
				if (!(recordTokens.get(i + 3)).equals("[[crlf]]"))
				{ // error out
					return -3;
				}
			}
			try
			{
				// start scanning lines
				FileInputStream fis = new FileInputStream(fn);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis, encoding));
				String str = "";
				String[] pieces = { "", "" };
				while ((str = in.readLine()) != null)
				{
					if (str.equals("") || str.equals("BOOE|")) // save book and clear map, Abe hack for EOR
					{
						numberOfAdds++;
						if (!format.startsWith("Cato")) // not a Cato.kto import, update dates
						{
							if (m.get("addeddate").equals(""))
							{
								m.put("addeddate", Constants.getTimestamp());
							}
							else
							{
								m.put("changeddate", Constants.getTimestamp());
							}
							m.put("validateddate", "");
						}
						mapToDatabase();
						createEmptyTokenMap();
					}
					if (delimiterChar.equals("|"))
					{
						pieces = str.split("\\|", 2);
					}
					else
					{
						pieces = str.split(delimiterChar, 2);
					}
					// key = pieces[0] value = pieces[1]
					String s = "";
					s = tokenMap.get(pieces[0]);
					if (s != null)
					{
						saveValueToMap(s, pieces[1]);
					}
				}
				in.close();
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(null, "Something went wrong.\nPlease verify and try again.");
				Constants.writeBlog("BooksImportExport > importBooks1 > " + ex);
			}
		}
		else // else match field for field
		{
			while (recordTokens.remove("[[d]]")) {} // strip out delimiters
			try
			{
				FileInputStream fis = new FileInputStream(fn);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis, encoding));
				String str = "";
				if (headerTokens.size() > 0)
				{
					str = in.readLine(); // throw away header
				}
				while ((str = in.readLine()) != null)
				{
					String[] pieces;
					createEmptyTokenMap();
					if (delimiterChar.equals("|"))
					{
						pieces = str.split("\\|", 2);
					}
					else
					{
						pieces = str.split(delimiterChar);
					}
					for (int i = 0; i < pieces.length; i++)
					{
						if (isQuoted)
						{
							pieces[i] = pieces[i].substring(1, pieces[i].length() - 1); // strip quotes
						}
						saveValueToMap(recordTokens.get(i), pieces[i]);
					}
					if (m.get("addeddate").equals(""))
					{
						saveValueToMap("[[addeddate]]", Constants.getTimestamp());
					}
					else
					{
						saveValueToMap("[[changeddate]]", Constants.getTimestamp());
					}
					mapToDatabase();
					numberOfAdds++;
				}
				in.close();
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(null, "Something went wrong.\nPlease verify and try again.");
				Constants.writeBlog("BooksImportExport > importBooks2 > " + ex);
			}
		}
		Constants.writeBlog("Books file'" + fn + "' imported." + numberOfAdds + " records processed.");
		// get largest book_record_number value and reset seq + 1
		// SettingsView.getInstance().setProperty("BOOKS",Integer.toString(DBFunctions.getInstance().maxVal(DBFunctions.getInstance().BOOKSARRAY,
		// DBFunctions.getInstance().BOOK_RECORD_NUMBER) + 1));
		return numberOfAdds;
	}

	public int exportBooks()
	{
		exporting = true;
		importing = false;
		encoding = "UTF-8";
		quoteEscapeChar = "";
		delimiterChar = "";

		// open and scan template, setting flags and variable
		parseTemplate();
		Constants.writeBlog("Export: Template parsed.");

		// write header
		Constants.writeBlog("Export: " + fn);

		try
		{
			FileOutputStream fos = new FileOutputStream(fn);
			BufferedWriter of = new BufferedWriter(new OutputStreamWriter(fos, encoding));
			StringBuilder out = new StringBuilder();
			if (headerTokens.size() > 0)
			{
				for (String t : headerTokens)
				{
					String ct;
					if (t.startsWith("["))
					{
						ct = convertTokenToValue(t);
					}
					else
					{
						ct = t;
					}
					// detab and de-return field, replace with spaces
					if ((!ct.equals(rnString)) && (!ct.equals("\t")))
					{
						ct = ct.replaceAll("[\r\n\t]", "  ");
					}
					out.append(ct);
				}
				// write out to file
				of.write(out.toString());
				out.setLength(0);
			}
			Constants.writeBlog("Export: Header Exported.");
			numberOfAdds = 0;
			numberOfDeletes = 0;
			int recordCount = 0;
			// write books
			for (String bookID : bookList)
			{
				// get record for bookID
				recordCount++;
				if (wantsExporting)
				{
					wantsToMap(bookID);
				}
				else
				{
					databaseToMap(bookID);
				}

				negativePrice = false;
				wholeOut = new StringBuilder("");
				//
				String previousToken = "";
				for (String t : recordTokens)
				{
					String ct;
					if (t.startsWith("[["))
					{
						ct = convertTokenToValue(t);
					}
					else
					{
						ct = t;
					}
					// detab and de-return field, replace with spaces
					if ((!ct.equals(rnString)) && (!ct.equals("\t")))
					{
						ct = ct.replaceAll("[\r\n\t]", "  ");
					}
					// escape any quotes in quote-surrounded fields
					if ((isQuoted) && (!ct.equals("\"")))
					{
						ct = ct.replaceAll("\"", quoteEscapeChar);
					}
					out.append(ct);
					if (ct.equals(rnString))
					{
						if ((keyValuePairs) && (!previousToken.equals("[[null]]")))
						{
							// if there's no value, then drop the line
							String[] pieces = out.toString().split("\\|", 2);
							if (pieces[1].equals(rnString)) out.setLength(0);
						}
						if (out.toString().startsWith("KE|")) // test for keywords
						{
							String tkn = out.substring(0, 3);
							String[] pieces = out.substring(3, out.length() - 3).split(" ");
							out.setLength(0);
							for (String p : pieces)
							{
								out.append(tkn);
								out.append(p);
								out.append(rnString);
							}
						}
						else if ((out.toString().startsWith("NT|")) && (out.length() > 70)) // test for desc
						{
							String tkn = out.substring(0, 3);
							String text = out.substring(3, out.length() - 3);
							out.setLength(0);
							while (text.length() > 70)
							{
								int len = 70;
								while (!(text.charAt(len) == ' '))
								{
									len--;
									if (len < 1)
									{
										len = 70;
										text = text.substring(0, 69) + " " + text.substring(70, text.length());
									}
								}
								out.append(tkn);
								out.append(text.substring(0, len));
								out.append(rnString);
								text = text.substring(len, text.length());
							}
							if (!text.equals(""))
							{
								out.append(tkn);
								out.append(text);
								out.append(rnString);
							}
						}
						wholeOut.append(out);
						out.setLength(0);
					}
					// must do this at the last minute
					previousToken = t;
				}
				if (!wantsExporting)
				{
					if (((m.get("quantity").equals("0")) || (m.get("status").equals("Sold"))) && (addsOnly))
					{
						wholeOut.setLength(0);
					}
					if ((!m.get("quantity").equals("0")) && (!m.get("status").equals("Sold")) && (deletesOnly))
					{
						wholeOut.setLength(0);
					}
				}
				if (!negativePrice)
				{
					of.write(wholeOut.toString());
				}
			}

			Constants.writeBlog("Export: Listings Exported.");
			// write footer
			if (footerTokens.size() > 0)
			{
				out.setLength(0);
				for (String t : footerTokens)
				{
					String ct;
					if (t.startsWith("["))
					{
						ct = convertTokenToValue(t);
					}
					else
					{
						ct = t;
					}
					// detab and de-return field, replace with spaces
					if ((!ct.equals(rnString)) && (!ct.equals("\t")))
					{
						ct = ct.replaceAll("[\r\n\t]", "  ");
					}
					// escape any quotes in quote-surrounded fields
					if ((isQuoted) && (!ct.equals("\"")))
					{
						ct = ct.replaceAll("\"", quoteEscapeChar);
					}
					out.append(ct);
				}
				of.write(out.toString());
			}
			// write out to file
			of.close();
			Constants.writeBlog("Export: Footer Exported.");
			Constants.writeBlog("Books file export Complete.");
			return recordCount;
		}
		catch (Exception ex)
		{
			Constants.writeBlog("BooksImportExport > exportBooks > " + ex);

		}
		return 0;
	}

	private void parseTemplate()
	{
		try
		{
			File fi = new File(Constants.TEMPLATES_DIR + Constants.ps + format);
			FileInputStream fis = new FileInputStream(fi);
			BufferedReader in = new BufferedReader(new InputStreamReader(fis)); // removed , "UTF-8"
			String str;
			StringBuilder tokenString = new StringBuilder("");
			while ((str = in.readLine()) != null)
			{
				if (str.startsWith("#"))
				{
					// just a comment, nothing to add
				}
				else if (str.startsWith("$"))
				{
					if (importing)
					{
						tokenString.append(str.substring(1, str.length()));
					}
				}
				else if (str.startsWith("@"))
				{
					if (exporting)
					{
						tokenString.append(str.substring(1, str.length()));
					}
				}
				else
				{
					tokenString.append(str);
				}
			}
			in.close();

			headerTokens.clear();
			recordTokens.clear();
			footerTokens.clear();

			ArrayList<String> tokens = parseTokens(tokenString.toString());
			for (String token : tokens)
			{
				if (token.startsWith("[[doublequotes"))
				{
					String s = token.substring(token.length() - 3, token.length() - 2);
					if (s.equals("Q"))
					{ //Quote ""
						quoteEscapeChar = "\"\"";
					}
					else if (s.equals("B"))
					{ //Backslash \"
						quoteEscapeChar = "\\\"";
					}
				}
				else if (token.startsWith("[[delimiter"))
				{
					String d = token.substring(token.length() - 3, token.length() - 2);
					if (d.equals("P"))
					{
						delimiterChar = "|";
					}
					else if (d.equals("C"))
					{
						delimiterChar = ",";
					}
					else if (d.equals("T"))
					{
						delimiterChar = "\t";
					}
				}
				else if (token.startsWith("[[encoding"))
				{
					encoding = token.substring(11, token.length() - 2);
				}
				else if (token.startsWith("[[dateformat"))
				{
					externalDateFormat = token.substring(13, token.length() - 2);
				}
				else if (token.equals("[[headerstart]]"))
				{
					parsingHeader = true;
				}
				else if (token.equals("[[headerend]]"))
				{
					parsingHeader = false;
				}
				else if (token.equals("[[recordstart]]"))
				{
					parsingRecord = true;
				}
				else if (token.equals("[[recordend]]"))
				{
					parsingRecord = false;
				}
				else if (token.equals("[[footerstart]]"))
				{
					parsingFooter = true;
				}
				else if (token.equals("[[footerend]]"))
				{
					parsingFooter = false;
				}
				else if (token.equals("[[keyValuePairs]]"))
				{
					keyValuePairs = true;
				}
				else if (token.equals("[[isquoted]]"))
				{
					isQuoted = true;
				}
				else if (token.equals("[[addsonly]]"))
				{
					addsOnly = true;
				}
				else if (token.equals("[[deletesonly]]"))
				{
					deletesOnly = true;
				}
				else if (token.startsWith("[[exportname"))
				{
					String a = getLengthLimit(token);
					String newFileName = a + fn.getName();
					String newPath = fn.getParent();
					fn = new File(newPath + Constants.ps + newFileName);
				}
				else if (parsingHeader)
				{
					headerTokens.add(token);
				}
				else if (parsingRecord)
				{
					recordTokens.add(token);
				}
				else if (parsingFooter)
				{
					footerTokens.add(token);
				}
			}
		}
		catch (Exception ex)
		{
			Constants.writeBlog("BooksImportExport > parseTemplate > " + ex);
		}
	}

	public static ArrayList parseTokens(String tokenString)
	{
		ArrayList<String> tokens = new ArrayList<String>();
		String ts = tokenString + "•";
		StringBuilder newToken = new StringBuilder("");

		int i = 0;
		while (i < ts.length() - 1)
		{
			if (ts.substring(i, i + 2).equals("[["))
			{
				while ((i < ts.length() - 1) && !ts.substring(i, i + 2).equals("]]"))
				{
					newToken.append(ts.charAt(i));
					i++;
				}
				newToken.append("]]");
				i = i + 2;
			}
			else
			{
				while ((i < ts.length() - 1) && !ts.substring(i, i + 2).equals("[["))
				{
					newToken.append(ts.charAt(i));
					i++;
				}
			}
			tokens.add(newToken.toString());
			newToken.setLength(0);
		}
		return tokens;
	}


	private void createEmptyTokenMap()
	{
		m.put("addeddate", "");
		m.put("author", "");
		m.put("binding", "");
		m.put("bindingstyle", "");
		m.put("bookcondition", "");
		m.put("bookid", "");
		m.put("booktype", "");
		m.put("catalog1", "");
		m.put("catalog2", "");
		m.put("catalog3", "");
		m.put("changeddate", "");
		m.put("description", "");
		m.put("cost", "");
		m.put("date", "");
		m.put("djcondition", "");
		m.put("edition", "");
		m.put("firstedition", "false");
		m.put("hasdj", "false");
		m.put("unused", "false");
		m.put("height", "");
		m.put("illustrator", "");
		if (include)
		{
			m.put("includeinupdate", "true");
		}
		else
		{
			m.put("includeinupdate", "false");
		}
		m.put("isbn", "");
		m.put("keywords", "");
		m.put("language", "");
		m.put("list1", "");
		m.put("list2", "");
		m.put("list3", "");
		m.put("list4", "");
		m.put("list5", "");
		m.put("location", "");
		m.put("pages", "");
		m.put("place", "");
		m.put("printing", "");
		m.put("privateString", "");
		m.put("publisher", "");
		m.put("quantity", "1");
		m.put("recordno", "");
		m.put("signed", "");
		m.put("size", "");
		m.put("status", "For Sale");
		m.put("title", "");
		m.put("user1", "");
		m.put("user2", "");
		m.put("validateddate", "");
		m.put("weight", "");
		m.put("width", "");
	}

	private void databaseToMap(String bookID)
	{
		int i = DBFunctions.getInstance().selectOne("BKS", Constants.BOOK_ID, bookID);
		String[] book = DBFunctions.getInstance().getRecord(i);
		m.put("addeddate", book[Constants.BOOK_ADDED_DATE]);
		m.put("author", book[Constants.BOOK_AUTHOR]);
		m.put("bindingstyle", book[Constants.BOOK_BINDING_STYLE]);
		m.put("binding", book[Constants.BOOK_BINDING_TYPE]);
		m.put("catalog1", book[Constants.BOOK_CATALOG1]);
		m.put("catalog2", book[Constants.BOOK_CATALOG2]);
		m.put("catalog3", book[Constants.BOOK_CATALOG3]);
		m.put("changeddate", book[Constants.BOOK_CHANGED_DATE]);
		m.put("description", book[Constants.BOOK_DESCRIPTION]);
		m.put("bookcondition", book[Constants.BOOK_CONDITION]);
		m.put("cost", book[Constants.BOOK_COST]);
		m.put("date", book[Constants.BOOK_DATE]);
		m.put("djcondition", book[Constants.BOOK_DJ_CONDITION]);
		m.put("edition", book[Constants.BOOK_EDITION]);
		m.put("firstedition", book[Constants.BOOK_FIRST_EDITION]);
		m.put("hasdj", book[Constants.BOOK_HAS_DJ]);
		m.put("unused", book[Constants.BOOK_UNUSED]);
		m.put("height", book[Constants.BOOK_HEIGHT]);
		m.put("bookid", book[Constants.BOOK_ID]);
		m.put("illustrator", book[Constants.BOOK_ILLUSTRATOR]);
		m.put("includeinupdate", book[Constants.BOOK_INCLUDE_IN_UPDATE]);
		m.put("isbn", book[Constants.BOOK_ISBN]);
		m.put("keywords", book[Constants.BOOK_KEYWORDS]);
		m.put("language", book[Constants.BOOK_LANGUAGE]);
		m.put("list1", book[Constants.BOOK_LIST1]);
		m.put("list2", book[Constants.BOOK_LIST2]);
		m.put("list3", book[Constants.BOOK_LIST3]);
		m.put("list4", book[Constants.BOOK_LIST4]);
		m.put("list5", book[Constants.BOOK_LIST5]);
		m.put("location", book[Constants.BOOK_LOCATION]);
		m.put("pages", book[Constants.BOOK_PAGES]);
		m.put("place", book[Constants.BOOK_PLACE]);
		m.put("printing", book[Constants.BOOK_PRINTING]);
		m.put("privateString", book[Constants.BOOK_PRIVATE]);
		m.put("publisher", book[Constants.BOOK_PUBLISHER]);
		m.put("quantity", book[Constants.BOOK_QUANTITY]);
		m.put("recordno", book[Constants.BOOK_NUMBER]);
		m.put("signed", book[Constants.BOOK_SIGNED]);
		m.put("size", book[Constants.BOOK_SIZE]);
		m.put("status", book[Constants.BOOK_STATUS]);
		m.put("title", book[Constants.BOOK_TITLE]);
		m.put("booktype", book[Constants.BOOK_TYPE]);
		m.put("user1", book[Constants.BOOK_USER1]);
		m.put("user2", book[Constants.BOOK_USER2]);
		m.put("validateddate", book[Constants.BOOK_VALIDATED_DATE]);
		m.put("weight", book[Constants.BOOK_WEIGHT]);
		m.put("width", book[Constants.BOOK_WIDTH]);
	}

	private void truncate(String field, int length)
	{
		if (m.get(field).length() > length)
		{
			m.put(field, m.get(field).substring(0, length));
			Constants.writeBlog(m.get(field) + ": addeddate truncated.");
		}
	}

	private void mapToDatabase()
	{ // m.get
		if ((m.get("recordno").equals("")) || (merging))
		{
			m.put("recordno", DBFunctions.getInstance().getNextID("BKS", Constants.BOOK_NUMBER));
		}
		if (merging)
		{
			int i = DBFunctions.getInstance().selectOne("BKS", Constants.BOOK_ID, m.get("bookid"));
			if (i >= 0)
			{
				Constants.writeBlog(m.get("bookid") + ": Not unique in merge.  Appended -2.");
				m.put("bookid", m.get("bookid") + "-2");
			}
		}
		truncate("addeddate", 32);
		truncate("author", 256);
		truncate("bindingstyle", 256);
		truncate("binding", 256);
		truncate("catalog1", 256);
		truncate("catalog2", 256);
		truncate("catalog3", 256);
		truncate("description", 16384);
		truncate("bookcondition", 256);
		truncate("cost", 16);
		truncate("date", 256);
		truncate("djcondition", 256);
		truncate("edition", 256);
		truncate("height", 256);
		truncate("bookid", 256);
		truncate("illustrator", 256);
		truncate("isbn", 256);
		truncate("keywords", 256);
		truncate("language", 256);
		truncate("list1", 16);
		truncate("list2", 16);
		truncate("list3", 16);
		truncate("list4", 16);
		truncate("list5", 16);
		truncate("location", 256);
		truncate("pages", 256);
		truncate("place", 256);
		truncate("printing", 256);
		truncate("privateString", 4096);
		truncate("publisher", 256);
		truncate("quantity", 16);
		truncate("signed", 256);
		truncate("size", 256);
		truncate("status", 16);
		truncate("title", 256);
		truncate("booktype", 256);
		truncate("user1", 256);
		truncate("user2", 256);
		truncate("validateddate", 32);
		truncate("weight", 256);
		truncate("width", 256);

		String[] bk = { "BKS", m.get("bookid"), m.get("isbn"), m.get("author"), m.get("title"), m.get("illustrator"),
				m.get("publisher"), m.get("place"), m.get("date"), m.get("edition"), m.get("printing"), m.get("pages"),
				m.get("language"), m.get("keywords"), m.get("bookcondition"), m.get("firstedition"), m.get("binding"),
				m.get("djcondition"), m.get("bindingstyle"), m.get("hasdj"), m.get("booktype"), m.get("signed"),
				m.get("location"), m.get("includeinupdate"), m.get("user1"), m.get("user2"), m.get("cost"),
				m.get("list1"), m.get("list2"), m.get("list3"), m.get("list4"), m.get("list5"), m.get("size"),
				m.get("weight"), m.get("height"), m.get("width"), m.get("quantity"), m.get("unused"), m.get("status"),
				m.get("description"), m.get("privateString"), m.get("catalog1"), m.get("catalog2"), m.get("catalog3"),
				m.get("addeddate"), m.get("changeddate"), m.get("validateddate"), m.get("recordno") };
		DBFunctions.getInstance().updateNoLog(-1, bk);
	}

	private String convertTokenToValue(String t) // export values
	{
		String retVal = "";
		String lx = getLengthLimit(t);
		if (t.startsWith("[[abeadds"))
		{
			retVal = Integer.toString(numberOfAdds);
		}
		else if (t.startsWith("[[abebinding"))
		{
			if (m.get("binding").toLowerCase().startsWith("hard"))
			{
				retVal = "H";
			}
			else if (m.get("binding").toLowerCase().startsWith("soft"))
			{
				retVal = "S";
			}
		}
		else if (t.startsWith("[[abecount"))
		{
			retVal = Integer.toString(numberOfAdds + numberOfDeletes);
		}
		else if (t.startsWith("[[abedeletes"))
		{
			retVal = Integer.toString(numberOfDeletes);
		}
		else if (t.startsWith("[[abefirst"))
		{
			if (m.get("firstedition").toLowerCase().equals("true"))
			{
				retVal = "F";
			}
		}
		else if (t.startsWith("[[abehasdj"))
		{
			if (m.get("hasdj").toLowerCase().equals("true"))
			{
				retVal = "J";
			}
		}
		else if (t.startsWith("[[abeissigned"))
		{
			if (!m.get("signed").equals("") && !m.get("signed").toLowerCase().equals("false"))
			{
				retVal = "I";
			}
		}
		else if (t.startsWith("[[abetransaction"))
		{
			if (wantsExporting)
			{
				if (m.get("status").equals("Found"))
				{
					retVal = "D";
					numberOfDeletes++;
				}
				else
				{
					retVal = "A";
					numberOfAdds++;
				}
			}
			else
			{
				if ((m.get("quantity").equals("0")) || (m.get("status").equals("Sold")))
				{
					retVal = "D";
					numberOfDeletes++;
				}
				else
				{
					retVal = "A";
					numberOfAdds++;
				}
			}
		}
		else if (t.startsWith("[[abeuser"))
		{
			retVal = "1";
		}
		else if (t.startsWith("[[addeddate"))
		{
			retVal = exportDateFormat(m.get("addeddate"));
		}

		else if (t.startsWith("[[amz"))
		{
			retVal = parseAmazonValue(t);
		}


		else if (t.startsWith("[[author"))
		{
			retVal = m.get("author");
		}
		else if (t.startsWith("[[bindingstyle"))
		{
			retVal = m.get("bindingstyle");
		}
		else if (t.startsWith("[[binding"))
		{
			retVal = m.get("binding");
		}
		else if (t.startsWith("[[bookcondition"))
		{
			retVal = m.get("bookcondition");
		}
		else if (t.startsWith("[[bookid"))
		{
			retVal = m.get("bookid");
		}
		else if (t.startsWith("[[booktype"))
		{
			retVal = m.get("booktype");
		}
		else if (t.startsWith("[[catalog1"))
		{
			retVal = m.get("catalog1");
		}
		else if (t.startsWith("[[catalog2"))
		{
			retVal = m.get("catalog2");
		}
		else if (t.startsWith("[[catalog3"))
		{
			retVal = m.get("catalog3");
		}
		// this token is currently unused, a better way may be needed
		else if (t.startsWith("[[catalogs"))
		{
			retVal = m.get("catalog1").replace(",", " ");
			if (!m.get("catalog2").equals(""))
			{
				retVal += ", " + m.get("catalog2").replace(",", " ");
			}
			if (!m.get("catalog3").equals(""))
			{
				retVal += ", " + m.get("catalog3").replace(",", " ");
			}
		}
		else if (t.startsWith("[[changeddate"))
		{
			retVal = exportDateFormat(m.get("changeddate"));
			if (retVal.equals(""))
			{
				retVal = exportDateFormat(m.get("addeddate"));
			}
		}
		else if (t.startsWith("[[cost"))
		{
			retVal = m.get("cost");
		}
		else if (t.startsWith("[[crlf"))
		{
			retVal = rnString;
		}
		else if (t.equals("[[d]]"))
		{
			retVal = delimiterChar;
		}
		// [[dateymd]] //pub date in yyyy-mm-dd format
		else if (t.startsWith("[[dateymd"))
		{
			retVal = m.get("date");
			if (retVal.length() > 4)
			{
				// dummy non-4-digit dates for now
				retVal = "1111"; // (retVal.replaceAll("[\\D]", "")).substring(0,4);
			}
		}
		else if (t.startsWith("[[date"))
		{
			retVal = m.get("date");
		}
		else if (t.startsWith("[[description"))
		{
			retVal = m.get("description").trim();
		}
		else if (t.startsWith("[[djcondition"))
		{
			retVal = m.get("djcondition");
		}
		else if (t.startsWith("[[edition"))
		{
			retVal = m.get("edition");
		}
		else if (t.startsWith("[[filename"))
		{
			retVal = fn.toString();
		}
		else if (t.startsWith("[[firstedition"))
		{
			retVal = m.get("firstedition");
		}
		else if (t.startsWith("[[hardorsoft"))
		{
			if (m.get("binding").toLowerCase().startsWith("hard"))
			{
				retVal = "Hardcover";
			}
			else if (m.get("binding").toLowerCase().startsWith("soft"))
			{
				retVal = "Paperback";
			}
		}
		else if (t.startsWith("[[hasdj"))
		{
			retVal = m.get("hasdj");
		}
		else if (t.startsWith("[[unused"))
		{
			retVal = m.get("unused");
		}
		else if (t.startsWith("[[height"))
		{
			retVal = m.get("height");
		}
		else if (t.startsWith("[[illustrator"))
		{
			retVal = m.get("illustrator");
		}
		else if (t.startsWith("[[includeinupdate"))
		{
			retVal = m.get("includeinupdate");
		}
		else if (t.startsWith("[[isbn"))
		{
			retVal = m.get("isbn");
		}
		else if (t.startsWith("[[keywords"))
		{
			retVal = m.get("keywords");
		}
		else if (t.startsWith("[[language"))
		{
			retVal = m.get("language");
		}
		else if (t.startsWith("[[listingtype"))
		{
			retVal = ""; // should not be currently used
		}
		else if (t.startsWith("[[list1"))
		{
			retVal = m.get("list1");
			if (retVal.startsWith("-"))
			{
				negativePrice = true;
			}
		}
		else if (t.startsWith("[[list2"))
		{
			retVal = m.get("list2");
			if (retVal.startsWith("-"))
			{
				negativePrice = true;
			}
		}
		else if (t.startsWith("[[list3"))
		{
			retVal = m.get("list3");
			if (retVal.startsWith("-"))
			{
				negativePrice = true;
			}
		}
		else if (t.startsWith("[[list4"))
		{
			retVal = m.get("list4");
			if (retVal.startsWith("-"))
			{
				negativePrice = true;
			}
		}
		else if (t.startsWith("[[list5"))
		{
			retVal = m.get("list5");
			if (retVal.startsWith("-"))
			{
				negativePrice = true;
			}
		}
		else if (t.startsWith("[[list"))
		{
			if (list.equals(SettingsView.getInstance().get("list1")))
			{
				retVal = m.get("list1");
			}
			else if (list.equals(SettingsView.getInstance().get("list2")))
			{
				retVal = m.get("list2");
			}
			else if (list.equals(SettingsView.getInstance().get("list3")))
			{
				retVal = m.get("list3");
			}
			else if (list.equals(SettingsView.getInstance().get("list4")))
			{
				retVal = m.get("list4");
			}
			else if (list.equals(SettingsView.getInstance().get("list5")))
			{
				retVal = m.get("list5");
			}
			if (retVal.startsWith("-"))
			{
				negativePrice = true;
			}
		}
		else if (t.startsWith("[[location"))
		{
			retVal = m.get("location");
		}
		else if (t.startsWith("[[null"))
		{
			retVal = "";
		}
		else if (t.startsWith("[[pages"))
		{
			retVal = m.get("pages");
		}
		else if (t.startsWith("[[imageurl"))
		{
			retVal = SettingsView.getInstance().get("internetImage") + m.get("bookid") + ".jpg";
		}
		else if (t.startsWith("[[place"))
		{
			retVal = m.get("place");
		}
		else if (t.startsWith("[[printing"))
		{
			retVal = m.get("printing");
		}
		else if (t.startsWith("[[private"))
		{
			retVal = m.get("privateString");
		}
		else if (t.startsWith("[[publisher"))
		{
			retVal = m.get("publisher");
		}
		else if (t.startsWith("[[quantity"))
		{
			retVal = m.get("quantity");
		}
		else if (t.startsWith("[[recordno"))
		{
			retVal = m.get("recordno");
		}
		else if (t.startsWith("[[signed"))
		{
			retVal = m.get("signed");
		}
		else if (t.startsWith("[[sizeunits"))
		{
			if (lx.equals("65536"))
			{
				lx = "IN";
			}
			retVal = lx;
		}
		else if (t.startsWith("[[size"))
		{
			retVal = m.get("size");
		}
		else if (t.startsWith("[[status"))
		{
			retVal = m.get("status");
		}
		else if (t.startsWith("[[systemdate"))
		{
			DateFormat dateFormat = new SimpleDateFormat(externalDateFormat);
			retVal = dateFormat.format(new Date());
		}
		else if (t.startsWith("[[systemtime"))
		{
			DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
			retVal = dateFormat.format(new Date());
		}
		else if (t.startsWith("[[title"))
		{
			retVal = m.get("title");
		}
		else if (t.startsWith("[[uieesalesorwants"))
		{
			retVal = "S";
		}
		else if (t.startsWith("[[uieestatus"))
		{
			retVal = "1";
			if (m.get("quantity").equals("0"))
			{
				retVal = "2";
			}
			if (m.get("status").equals("On Hold"))
			{
				retVal = "7";
			}
		}
		else if (t.startsWith("[[user1"))
		{
			retVal = m.get("user1");
		}
		else if (t.startsWith("[[user2"))
		{
			retVal = m.get("user2");
		}
		else if (t.startsWith("[[validateddate"))
		{
			retVal = exportDateFormat(m.get("validateddate"));
		}
		else if (t.startsWith("[[wantsclient"))
		{
			retVal = m.get("wantsclient");
		}
		else if (t.startsWith("[[wantspricemin"))
		{
			retVal = m.get("pricemin");
		}
		else if (t.startsWith("[[wantspricemax"))
		{
			retVal = m.get("pricemax");
		}
		else if (t.startsWith("[[wantsstatus"))
		{
			retVal = m.get("status");
		}
		else if (t.startsWith("[[weight"))
		{
			retVal = m.get("weight");
		}
		else if (t.startsWith("[[weightunits"))
		{
			if (lx.equals("65536"))
			{
				lx = "LB";
			}
			retVal = lx;
		}
		else if (t.startsWith("[[width"))
		{
			retVal = m.get("width");
		}

		try // make sure l is a number
		{
			int len = Integer.parseInt(lx);
			if (len == 0)
			{
				if (retVal.length() > 0)
				{
					retVal = " " + retVal + ".";
				}
			}
			else if (retVal.length() > len)
			{
				retVal = retVal.substring(0, len);
				Constants.writeBlog(fn.toString() + ": Token: " + t + " clipped to: " + retVal);
			}
		}
		catch (Exception ex)
		{
			// do nothing, String l was NAN
		}

		return retVal;
	}

	private void saveValueToMap(String token, String value) // import values
	{
		if (token.startsWith("[[abebinding"))
		{
			if (value.equals("H"))
			{
				m.put("binding", "Hardcover");
			}
			else if (value.equals("S"))
			{
				m.put("binding", "Softcover");
			}
		}
		else if (token.startsWith("[[abefirst"))
		{
			if (value.equals("F"))
			{
				m.put("firstedition", "true");
			}
		}
		else if (token.startsWith("[[abehasdj"))
		{
			if (value.equals("J"))
			{
				m.put("hasdj", "true");
			}
		}
		else if (token.startsWith("[[abeissigned"))
		{
			// null
		}
		else if (token.startsWith("[[abetransaction"))
		{
			if (value.equals("A"))
			{
				m.put("quantity", "1");
				m.put("status", "For Sale");
			}
			else if (value.equals("D"))
			{
				m.put("quantity", "0");
				m.put("status", "Sold");
			}
		}
		else if (token.startsWith("[[abeuser"))
		{
			// ignore m.put("addeduser", SQL.SettingsView.getVendorName());
		}
		else if (token.startsWith("[[addeddate"))
		{
			m.put("addeddate", importDateFormat(value));
		}
		else if (token.startsWith("[[amz"))
		{
			convertAmazonValue(token, value);
		}
		else if (token.startsWith("[[author"))
		{
			m.put("author", value);
		}
		else if (token.startsWith("[[bindingstyle"))
		{
			m.put("bindingstyle", value);
		}
		else if (token.startsWith("[[binding"))
		{
			m.put("binding", value);
		}
		else if (token.startsWith("[[bookcondition"))
		{
			m.put("bookcondition", value);
		}
		else if (token.startsWith("[[bookid"))
		{
			value = value.replaceAll("\\'", "");
			m.put("bookid", value);
		}
		else if (token.startsWith("[[booktype"))
		{
			m.put("booktype", value);
		}
		// this token is currently unused, a better way may be needed
		else if (token.startsWith("[[catalogs"))
		{
			String[] values = value.split(",", 3);
			if (values[0] != null)
			{
				m.put("catalog1", values[0]);
			}
			if (values[1] != null)
			{
				m.put("catalog2", values[1]);
			}
			if (values[2] != null)
			{
				m.put("catalog3", values[2]);
			}
		}
		else if (token.startsWith("[[catalog"))
		{
			if (m.get("catalog1").equals(""))
			{
				m.put("catalog1", value);
			}
			else if (m.get("catalog2").equals(""))
			{
				m.put("catalog2", value);
			}
			else if (m.get("catalog3").equals(""))
			{
				m.put("catalog3", value);
			}
		}
		else if (token.startsWith("[[changeddate"))
		{
			m.put("changeddate", importDateFormat(value));
		}
		else if (token.startsWith("[[cost"))
		{
			m.put("cost", Constants.twoPlaces.format(Double.parseDouble(value)));
		}
		// [[dateymd]] //pub date in yyyy-mm-dd format
		else if (token.startsWith("[[dateymd"))
		{
			m.put("date", value);
		}
		else if (token.startsWith("[[date"))
		{
			m.put("date", value);
		}
		else if (token.startsWith("[[description"))
		{
			String k = m.get("description");
			k += value + " ";
			m.put("description", k);
		}
		else if (token.startsWith("[[djcondition"))
		{
			m.put("djcondition", value);
		}
		else if (token.startsWith("[[edition"))
		{
			m.put("edition", value);
		}
		else if (token.startsWith("[[firstedition"))
		{
			m.put("firstedition", value);
		}
		else if (token.startsWith("[[hardorsoft"))
		{
			m.put("binding", value);
		}
		else if (token.startsWith("[[hasdj"))
		{
			m.put("hasdj", value);
		}
		else if (token.startsWith("[[unused"))
		{
			m.put("unused", value);
		}
		else if (token.startsWith("[[height"))
		{
			m.put("height", value);
		}
		else if (token.startsWith("[[illustrator"))
		{
			m.put("illustrator", value);
		}
		else if (token.startsWith("[[includeinupdate"))
		{
			m.put("includeinupdate", value);
		}
		else if (token.startsWith("[[isbn"))
		{
			m.put("isbn", value);
		}
		else if (token.startsWith("[[keywords"))
		{
			m.put("keywords", value);
		}
		else if (token.startsWith("[[keyword"))
		{
			String k = m.get("keywords");
			k += value + " ";
			m.put("keywords", k);
		}
		else if (token.startsWith("[[language"))
		{
			m.put("language", value);
		}
		else if (token.startsWith("[[list1"))
		{
			m.put("list1", Constants.twoPlaces.format(Double.parseDouble(value)));
		}
		else if (token.startsWith("[[list2"))
		{
			m.put("list2", Constants.twoPlaces.format(Double.parseDouble(value)));
		}
		else if (token.startsWith("[[list3"))
		{
			m.put("list3", Constants.twoPlaces.format(Double.parseDouble(value)));
		}
		else if (token.startsWith("[[list4"))
		{
			m.put("list4", Constants.twoPlaces.format(Double.parseDouble(value)));
		}
		else if (token.startsWith("[[list5"))
		{
			m.put("list5", Constants.twoPlaces.format(Double.parseDouble(value)));
		}
		else if (token.startsWith("[[listingtype"))
		{
			// currently null, maybe add as a user field option
		}
		else if (token.startsWith("[[list"))
		{
			if (list.equals(SettingsView.getInstance().get("list2")))
			{
				m.put("list2", Constants.twoPlaces.format(Double.parseDouble(value)));
			}
			else if (list.equals(SettingsView.getInstance().get("list3")))
			{
				m.put("list3", Constants.twoPlaces.format(Double.parseDouble(value)));
			}
			else if (list.equals(SettingsView.getInstance().get("list4")))
			{
				m.put("list4", Constants.twoPlaces.format(Double.parseDouble(value)));
			}
			else if (list.equals(SettingsView.getInstance().get("list5")))
			{
				m.put("list5", Constants.twoPlaces.format(Double.parseDouble(value)));
			}
			else
			{
				m.put("list1", Constants.twoPlaces.format(Double.parseDouble(value)));
			}
		}
		else if (token.startsWith("[[location"))
		{
			m.put("location", value);
		}
		else if (token.startsWith("[[pages"))
		{
			m.put("pages", value);
		}
		else if (token.startsWith("[[imageurl"))
		{
			// ignore for now
		}
		else if (token.startsWith("[[place"))
		{
			m.put("place", value);
		}
		else if (token.startsWith("[[printing"))
		{
			m.put("printing", value);
		}
		else if (token.startsWith("[[private"))
		{
			m.put("privateString", value);
		}
		else if (token.startsWith("[[publisher"))
		{
			m.put("publisher", value);
		}
		else if (token.startsWith("[[quantity"))
		{
			m.put("quantity", value);
		}
		else if (token.startsWith("[[recordno"))
		{
			m.put("recordno", value);
		}
		else if (token.startsWith("[[signed"))
		{
			m.put("signed", value);
		}
		else if (token.startsWith("[[sizeunits"))
		{
			// null for now
		}
		else if (token.startsWith("[[size"))
		{
			m.put("size", value);
		}
		else if (token.startsWith("[[status"))
		{
			m.put("status", value);
		}
		else if (token.startsWith("[[title"))
		{
			m.put("title", value);
		}
		else if (token.startsWith("[[uieesalesorwants"))
		{
			// null for now
		}
		else if (token.startsWith("[[uieestatus"))
		{
			if (value.equals("1"))
			{
				m.put("status", "For Sale");
			}
			else if (value.equals("D"))
			{
				m.put("status", "Sold");
				m.put("quantity", "0");
			}
		}
		else if (token.startsWith("[[user1"))
		{
			m.put("user1", value);
		}
		else if (token.startsWith("[[user2"))
		{
			m.put("user2", value);
		}
		else if (token.startsWith("[[validateddate"))
		{
			m.put("validateddate", importDateFormat(value));
		}
		else if (token.startsWith("[[weightunits"))
		{
			// null for now
		}
		else if (token.startsWith("[[weight"))
		{
			m.put("weight", value);
		}
		else if (token.startsWith("[[width"))
		{
			m.put("width", value);
		}
	}

	/**
	 * ********************************************************** Date fiddling
	 ***********************************************************
	 */
	private String importDateFormat(String d)
	{
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(externalDateFormat);
			Date date = dateFormat.parse(d);
			return Long.toString(date.getTime() / 1000);
		}
		catch (Exception ex)
		{
			return d;
		}
	}

	private String exportDateFormat(String d)
	{
		try
		{
			DateFormat dateFormat = new SimpleDateFormat(externalDateFormat);
			return dateFormat.format(Long.parseLong(d) * 1000);
		}
		catch (Exception ex)
		{
			return "";
		}
	}

	private String getLengthLimit(String s)
	{
		String retS = "65536";
		String[] pieces = s.split("-", 2);
		if (pieces.length > 1)
		{
			retS = pieces[1].substring(0, pieces[1].length() - 2);
		}
		return retS;
	}

	/**
	 * ********************************************************* Wants Exporting Be
	 * here! ********************************************************
	 */
	public void exportWants()
	{
		wantsExporting = true;
		try
		{
			exportBooks();
		}
		catch (Exception ex)
		{
			Constants.writeBlog("BooksImportExport > ExportWants > " + ex);
		}
		wantsExporting = false;
	}

	public void wantsToMap(String ii)
	{
		int i = Integer.parseInt(ii);
		m.clear();
		String clientName = DBFunctions.getInstance().get(i, Constants.WANT_CLIENT);
		m.put("author", DBFunctions.getInstance().get(i, Constants.WANT_AUTHOR));
		m.put("title", DBFunctions.getInstance().get(i, Constants.WANT_TITLE));
		m.put("publisher", DBFunctions.getInstance().get(i, Constants.WANT_PUBLISHER));
		m.put("keywords", DBFunctions.getInstance().get(i, Constants.WANT_KEYWORDS));
		m.put("binding", DBFunctions.getInstance().get(i, Constants.WANT_BINDING));
		m.put("description", DBFunctions.getInstance().get(i, Constants.WANT_DESCRIPTION));
		m.put("pricemin", DBFunctions.getInstance().get(i, Constants.WANT_PRICE_MIN));
		m.put("pricemax", DBFunctions.getInstance().get(i, Constants.WANT_PRICE_MAX));
		m.put("firstedition", DBFunctions.getInstance().get(i, Constants.WANT_FIRST_EDITION));
		m.put("hasdj", DBFunctions.getInstance().get(i, Constants.WANT_DUST_JACKET));
		m.put("signed", DBFunctions.getInstance().get(i, Constants.WANT_SIGNED));
		m.put("status", DBFunctions.getInstance().get(i, Constants.WANT_STATUS));
		m.put("addeddate", DBFunctions.getInstance().get(i, Constants.WANT_ADDED_DATE));
		m.put("changeddate", DBFunctions.getInstance().get(i, Constants.WANT_CHANGED_DATE));
		m.put("wantsclient", clientName);
	}

	/**
	 * ********************* Amazon routines
	 ***********************************************************
	 */
	public String getAmazonValue(int i)
	{
		String amVals;
		amVals = m.get(user);
		if (amVals.length() < 9)
		{
			amVals = "*/*/*/*/*";
		}
		String[] pieces = amVals.split("/", 5);
		return pieces[i];
	}

	public boolean putAmazonValue(int i, String value)
	{
		String amVals;
		amVals = m.get(user);
		if (amVals.length() < 9)
		{
			amVals = "*/*/*/*/*";
		}
		String[] pieces = amVals.split("/", 5);
		pieces[i] = value;
		amVals = pieces[0] + "/" + pieces[1] + "/" + pieces[2] + "/" + pieces[3] + "/" + pieces[4];
		m.put(user, amVals);
		return true;
	}

	private String parseAmazonValue(String t)
	{
		String retVal = "";
		// [[amzexpship]]  expedited shipping
		// Comma separated values chosen from: next, second, domestic, international
		if (t.startsWith("[[amzexpship"))
		{
			String value = getAmazonValue(4);
			if (!value.equals("*"))
			{
				retVal = value;
			}
		}
		// comes before amzproductid
		else if (t.startsWith("[[amzproductidtype"))
		{
			String pt = getAmazonValue(2);
			if (Character.isDigit(pt.charAt(0)))
			{
				retVal = "2";
			}
			else
			{
				retVal = "1";
			}
		}
		// [[amzproductid]]
		//  1 = ASIN (Start with B) 2 = ISBN      3 = UPC  4 = EAN
		else if (t.startsWith("[[amzproductid"))
		{
			String value = getAmazonValue(2);
			if (!value.equals("*"))
			{
				retVal = value;
			}
		}
		// [[amzintlship]] // will ship international?
		// y or n
		else if (t.startsWith("[[amzintlship"))
		{
			String value = getAmazonValue(3);
			if (!value.equals("*"))
			{
				retVal = value;
			}
		}
		// [[amzdjcond]] // 0-4
		else if (t.startsWith("[[amzdjcond"))
		{
			String value = getAmazonValue(1);
			if (!value.equals("*"))
			{
				retVal = value;
			}
		}
		else if (t.startsWith("[[amzcond"))
		{
			String value = getAmazonValue(0);
			if (!value.equals("*"))
			{
				retVal = value;
			}
		}
		// [[amzsigned]] // signed by ?
		// author  illustrator   editor  photographer
		else if (t.startsWith("[[amzsigned"))
		{
			String s = m.get("signed");
			if (s.toLowerCase().contains("author"))
			{
				retVal = "author";
			}
			else if (s.toLowerCase().contains("illustrator"))
			{
				retVal = "illustrator";
			}
			else if (s.toLowerCase().contains("photographer"))
			{
				retVal = "photographer";
			}
			else if (s.toLowerCase().contains("editor"))
			{
				retVal = "editor";
			}

		}
		else if (t.startsWith("[[amzbinding"))
		{
			String b = m.get("bindingstyle");
			String hors = m.get("binding");
			retVal = amazonValues.get(b);
			if (retVal == null)
			{
				retVal = amazonValues.get(hors);
			}
			if  (retVal == null)
			{
				retVal = "";
			}
		}
		else if (t.startsWith("[[amzcategory"))
		{
			String b = m.get("catalog1");
			retVal = amazonValues.get(b);
			if (retVal == null) retVal = "";
		}
		return retVal;
	}

	private void convertAmazonValue(String token, String value)
	{
		if (token.startsWith("[[amzcond"))
		{
			if (value.equals("11"))
			{
				m.put("bookcondition", "New");
			}
			else
			{
				int n = Integer.parseInt(value);
				if (n > 4) n = n-4;
				String s = amazonValues.get(Integer.toString(n));
				s = s.substring(5);
				if (s.equals("Acceptable")) s = "Fair";
				m.put("bookcondition", s);
			}
		}
		else if (token.startsWith("[[amzproductid"))
		{
			putAmazonValue(2, value);
		}
		else if (token.startsWith("[[amzexpship"))
		{
			putAmazonValue(4, value);
		}
		else if (token.startsWith("[[amzintlship"))
		{
			putAmazonValue(3, value);
		}
		else if (token.startsWith("[[amzdjcond"))
		{
			if (value.equals("0"))
			{
				m.put("djcondition", "No Dust Jacket");
			}
			else
			{
				int n = Integer.parseInt(value);
				String s = amazonValues.get(Integer.toString(n));
				s = s.substring(5);
				if (s.equals("Acceptable")) s = "Fair";
				m.put("djcondition", s);
			}
		}
		else if (token.startsWith("[[amzsigned"))
		{

		}
		else if (token.startsWith("[[amzproductidtype"))
		{
			// do nothing with this
		}
		else if (token.startsWith("[[amzbinding"))
		{
			String s = amazonValues.get(value);
			if (s.toLowerCase().contains("leather")) s = "Leather";
			m.put("binding", s);
		}
		else if (token.startsWith("[[amzcategory"))
		{
			String s = amazonValues.get(value);
			m.put("catalog", s);
		}

	}

	private void readAmazonValues()
	{
		try
		{
			File fi = new File(Constants.TEMPLATES_DIR + Constants.ps + "AmazonValues.txt");
			FileInputStream fis = new FileInputStream(fi);
			BufferedReader in = new BufferedReader(new InputStreamReader(fis));
			String str;
			while ((str = in.readLine()) != null)
			{
				if (!str.startsWith("#"))
				{
					String[] pieces = str.split("\\|", 2);
					amazonValues.put(pieces[0], pieces[1]);
				}
			}
			in.close();
			fis.close();
		}
		catch (Exception ex)
		{

		}
	}
}
