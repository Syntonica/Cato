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

/*
The database uses a data soup approach where everything is in the same array.
Despite this, it makes for very fast response from the filters on the data
tables.  SQL DBs may have caught up by now, but this method has no dependencies
or install hassles.

Database entry tags:
"AUT" = Authors
"BCN" = Book Condition
"BKS" = Books
"BKT" = Book Type
"BND" = Binding Type
"BST" = Binding Style
"CAT" = Category
"CLI" = Clients
"DCT" = User Dictionary Entry
"DJC" = DJ Condition
"DVA" = Data Validations
"EDT" = Edition
"FTP" = FTP
"INV" = Invoices
"ISN" = ISBN Lookup URL
"LAN" = Language
"LST" = Dummy tag for List Views
"MAC" = Macro
"NPD" = Notepad
"PLU" = Price Lookup URL
"PRT" = Printing
"PUB" = Publishers
"SAL" = Sales Entry
"SET" = Settings
"SGN" = Signed
"SIZ" = Book size
"US1" = User1 Entries
"US2" = User2 Entries
"WNT" = Want
"XXX" = Deleted Record
*/

package cato;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class DBFunctions
{
	private static DBFunctions instance = null;
	private ArrayList<String[]> database = new ArrayList<String[]>();
	private HashMap<String, Integer>recordSizes = new HashMap<String, Integer>();
	public static DBFunctions getInstance()
	{
		if (instance == null)
		{
			instance = new DBFunctions();
		}
		return instance;
	}

	private DBFunctions()
	{
		String sizes = "AUT 4 BCN 2 BKS 48 BKT 2 BND 2 BST 2 CAT 2 CLI 15 " +
					"DCT 2 DJC 2 DVA 4 EDT 2 FTP 7 INV 14 ISN 2 LAN 2 " +
					"LST 2 MAC 3 NPD 3 PLU 2 PRT 2 PUB 7 SAL 21 SET 3 " +
					"SGN 2 SIZ 2 US1 2 US2 2 WNT 17";
		String[] pieces = sizes.split(" ", 58);
		for (int i = 0; i <57; i += 2)
		{
		recordSizes.put(pieces[i], Integer.parseInt(pieces[i+1]));
		}
	}
	// **********************************************************
	// SQL Type functions
	// ***********************************************************

	public int size()
	{
		return database.size();
	}

	public String[] getRecord(int index)
	{
		return database.get(index);
	}

	public String get(int index, int field)
	{
		return database.get(index)[field];
	}

	public void put(int index, int field, String value)
	{
		database.get(index)[field] = value;
	}

	public void update(int index, String[] record)
	{
		// this allows duplicates to be added, publishers and authors of the
		// same name.  Compacting the db will remove any exact duplicates
		if (index == -1)
		{
			database.add(record);
			Constants.writeBlog("Added: " + processLineOut(record));
		}
		else
		{
			Constants.writeBlog("From: " + processLineOut(database.get(index)));
			database.set(index, record);
			Constants.writeBlog("  To: " + processLineOut(database.get(index)));
		}
	}

	public void updateNoLog(int index, String[] record)
	{
		if (index == -1)
		{
			database.add(record);
		}
		else
		{
			database.set(index, record);
		}
	}

	public void remove(int index)
	{
		Constants.writeBlog("Removed: " + processLineOut(database.get(index)));
		database.get(index)[0] = "XXX"; // XXX out the record, preserve order!
	}

	public void clear(String tag)
	{
		for (int i = 0; i < database.size(); i++)
		{
			if (database.get(i)[0].equals(tag))
			{
				database.get(i)[0] = "XXX";
			}
		}
	}

	// return first index
	public int selectOne(String tag, int field, String matchValue)
	{
		for (int i = 0; i < database.size(); i++)
		{
			if ((database.get(i)[0].equals(tag)) && (database.get(i)[field].equals(matchValue)))
			{
				return i;
			}
		}
		return -1;
	}

	// return a set of indices
	public ArrayList<Integer> selectAll(String tag, int field, String matchValue)
	{
		ArrayList<Integer> j = new ArrayList<Integer>();
		for (int i = 0; i < database.size(); i++)
		{
			if (database.get(i)[Constants.RECORD_TAG].equals(tag))
			{
				if (matchValue.equals("") || (database.get(i)[field].equals(matchValue)))
				{
					j.add(i);
				}
			}
		}
		return j;
	}

	public int checkFor(String tag, int field1, String value1, int field2, String value2)
	{ // get index of record where field1 = value1 and field2 = value2
		for (int i = 0; i < database.size(); i++)
		{
			if (database.get(i)[Constants.RECORD_TAG].equals(tag))
			{
				if ((database.get(i)[field1].equals(value1)) && ((database.get(i)[field2].equals(value2))))
				{
					return i;
				}
			}
		}
		return -1;
	}

	public ArrayList<String> generateList(String tag, int field)
	{
		ArrayList hs = new ArrayList();
		for (int i = 0; i < size(); i++)
		{
			if (get(i, Constants.RECORD_TAG).equals(tag))
			{
				String s = get(i, field);
				if (!hs.contains(s))
				{
					hs.add(s);
				}
			}
		}
		Collections.sort(hs, String.CASE_INSENSITIVE_ORDER);
		return hs;
	}

	public String getNextID(String tag, int field)
	{
		int max = 0;
		for (int i = 0; i < database.size(); i++)
		{
			if (database.get(i)[Constants.RECORD_TAG].equals(tag))
			{
				int m = Integer.parseInt(database.get(i)[field]);
				if (m > max)
				{
					max = m;
				}
			}
		}
		max++;
		return Integer.toString(max);
	}

	/**
	 * **********************************************************
	 *
	 * **********************************************************
	 */
	public void makeNewDB(String databaseName)
	{
		try
		{
			InputStream is = getClass().getResourceAsStream("/resources/NewDB.txt");
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			loadDatabase(br);
			SettingsView.getInstance().set("dbName", databaseName);
			saveDatabase(databaseName + ".database");
			Constants.writeBlog("MakeNewDB > Success!");
		}
		catch (Exception ex)
		{
			Constants.writeBlog(" > makeNewDB > " + ex);
		}
	}

	public void backup()
	{
		String dbn = SettingsView.getInstance().get("dbName") + "_" + Constants.getFileDateTime() + ".backup";
		saveDatabase(dbn);
	}

	public void unloadDatabase()
	{
		String dbase = SettingsView.getInstance().get("dbName");
		Long max = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		database.clear();
		Constants.writeBlog(dbase + " unloaded.");
		Constants.writeBlog("Memory Max: " + max);
		Constants.writeBlog(
				"Memory Min: " + Long.toString(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
	}

	public void loadDatabase(String fileName)
	{
		File fi = new File(Constants.SYSTEM_DIR + Constants.ps + fileName);
		loadDatabase(fi);
	}

	public void loadDatabase(File fi)
	{
		try
		{
			FileInputStream fis = new FileInputStream(fi);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			loadDatabase(br);
		}
		catch (Exception ex)
		{
			Constants.writeBlog(" > loadDatabase > " + ex);
		}
	}

	public void loadDatabase(BufferedReader in) // ignore all space String.split("\\|", -1)
	{
		try
		{
			String str = "";
			int count = 0;
			while ((str = in.readLine()) != null)
			{
				if (!str.trim().equals("") && !str.startsWith("#")) // throw out blanks and comments
				{
					processLineIn(str);
					count++;
				}
			}
			in.close();
			Constants.writeBlog("Load: " + count + " records processed.");
		}
		catch (Exception ex)
		{
			Constants.writeBlog(" > loadDatabase > " + ex);
		}
	}

	public void compactDatabase(File fileName)
	{
		ArrayList<String> al = new ArrayList<String>();
		String name = fileName.getName().substring(0, fileName.getName().length() - 9);
		File fnold = new File(
				Constants.SYSTEM_DIR + Constants.ps + name + "_" + Constants.getFileDateTime() + ".backup");
		fileName.renameTo(fnold);
		String s = "";
		try
		{
			FileInputStream fis = new FileInputStream(fnold);
			BufferedReader in = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			while ((s = in.readLine()) != null)
			{
				// remove deleted records and exact duplicates
				if (!s.startsWith("XXX") && !al.contains(s))
				{
					s = verifyRecord(s);
					al.add(s);
				}
			}
			in.close();
			Collections.sort(al);
			FileOutputStream fos = new FileOutputStream(fileName);
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			for (String str : al)
			{
				out.write(str + Constants.rn);
			}
			out.close();
		}
		catch (Exception ex)
		{
			Constants.writeBlog("DBFunctions > compactDatabase > " + ex);
		}
	}

	private String verifyRecord(String s)
	{
		String[] record = s.split("\\|", 48);
		// verify record size
		if (record.length != recordSizes.get(record[0]))
		{
			Constants.writeBlog("Bad record size. Should be: " + recordSizes.get(record[0]) +
			". Got: " + record.length + ". Record: " + record);
		}
		// just check book lisings for now
		if (record[0].equals("BKS"))
		{

		}
		return s;
	}

	public void saveDatabase(final String fileName)
	{
		if (fileName.equals(""))
		{
			return;
		}
		File fn = new File(Constants.SYSTEM_DIR + Constants.ps + fileName);
		File fnold = new File(Constants.SYSTEM_DIR + Constants.ps + fileName + ".old");
		// rename old file
		if (fn.exists()) // rename original database file so we can make a new one
		{
			fn.renameTo(fnold);
		}
		// output new file
		try
		{
			FileOutputStream fos = new FileOutputStream(fn);
			BufferedWriter of = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			int count = 0;
			for (int i = 0; i < database.size(); i++)
			{
				of.write(processLineOut(database.get(i)) + Constants.rn);
				count++;
			}
			if (fnold.exists())
			{
				fnold.delete();
			}
			Constants.writeBlog("Save: " + count + " records processed.");
			of.close();
			fos.close();
		}
		catch (Exception ex)
		{
			if (fnold.exists())
			{
				fnold.renameTo(fn);
			}
			Constants.writeBlog(" > saveDatabase > " + ex);
			JOptionPane.showMessageDialog(BooksView.getInstance(), ex);
		}
		if (fn.length() == 0)
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(), "Saving file failed, zero bytes written");
		}
	}

	public void processLineIn(String str)
	{
		str = unstrip(str);
		String[] pieces = str.split("\\|", 48);
		database.add(pieces);
	}

	public String processLineOut(String[] s)
	{
		StringBuilder record = new StringBuilder();
		for (String val : s)
		{
			record.append(strip(val));
			record.append("|");
		}
		return record.substring(0, record.length() - 1);
	}

	public void exportList(String name, String tag)
	{
		ArrayList<String> al = new ArrayList<String>();
		for (int i = 0; i < size(); i++)
		{
			if (get(i, 0).equals(tag))
			{
				al.add(processLineOut(getRecord(i)));
			}
		}
		Collections.sort(al, String.CASE_INSENSITIVE_ORDER);
		File outputFile = new File(Constants.EXPORTS_DIR + Constants.ps +
				name + "_" + Constants.getFileDateTime() + ".txt");
		try
		{
			FileOutputStream ofs = new FileOutputStream(outputFile);
			BufferedWriter ofw = new BufferedWriter(new OutputStreamWriter(ofs));
			for (int i = 0; i < al.size(); i++)
			{
				ofw.write(al.get(i) + Constants.rn);
			}
			ofw.close();
			ofs.close();
			JOptionPane.showMessageDialog(BooksView.getInstance(),
					al.size() + " items exported.", "Export", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception ex)
		{
			Constants.writeBlog("DBFunctions > exportList > " + ex);
		}
	}

	public void importList(String name, String tag, boolean mergeable)
	{
		ArrayList<String> al = new ArrayList<String>();
		JFileChooser fc = new JFileChooser(Constants.HOME_DIR);
		Constants.setFileChooserFont(fc.getComponents());
		fc.setDialogTitle("Import " + name);
		int reply = fc.showOpenDialog(null);
		if (reply == JFileChooser.APPROVE_OPTION)
		{
			if (mergeable)
			{
				Object[] options = { "Merge", "Replace" };
				int reply2 = JOptionPane.showOptionDialog(null, "Merge or Replace?", "Importing File",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (reply2 == 0) // merge
				{
					for (int i = 0; i < size(); i++)
					{
						if (get(i, 0).equals(tag))
						{
							al.add(processLineOut(getRecord(i)));
						}
					}
				}
			}
			clear(tag);
			try
			{
				FileInputStream fis = new FileInputStream(fc.getSelectedFile());
				BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
				String w = "";
				while ((w = br.readLine()) != null)
				{
					if (w.startsWith(tag))
					{
						if (!al.contains(w)) al.add(w);
					}
				}
				br.close();
				fis.close();
			}
			catch (Exception ex)
			{
				Constants.writeBlog("DBFunctions > importListE > " + ex);
			}
			for (String w : al)
			{
				String[] record = unstrip(w).split("\\|", 48);
				database.add(record);
			}
			JOptionPane.showMessageDialog(BooksView.getInstance(),
					al.size() + " items after import", "Import", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public String strip(String s)
	{
		s = s.replace("|", "/");
		s = s.replace(Constants.crlf, "∞");
		return s;
	}

	public String unstrip(String s)
	{
		s = s.replace("∞", Constants.crlf);
		return s;
	}

}
