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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;

public class SpellCheck
{

	HashMap<String, ArrayList<String>> dict;
	HashSet<String> hashes = new HashSet<String>();
	HashMap<String, String> phonemes = new HashMap<String, String>(150);
	ArrayList<String> suggestions = new ArrayList<String>();
	String DELIMITERS = "0123456789-_ ,.:;?!()*&^%$#@~`{}[]/<>+=\\\n\t\"";
	static HighlightPainter hp = new ColorUnderlinePainter(Color.red);
	int start;
	int end;
	boolean capital = false;
	String currentLang;

	String englishPhonemes = "a A aa A ae A ai A au A aw A ay A b P bb P c K cc K ce SA ch C ck K ci SA cy SA d T dd T dg C e A ea A eau A ee A ei A eu A ew A ey A f F ff F g K ge CA gg K gh H gi CA gn N gy CA h H hh H i A ii A ie A io A iu A j C jj C k K kk K kn N l L ll L m M mm M mn N n N ng N nk N nn N o A oa A oe A oi A oo A ou A ow A oy A p P ph F pn N pp P ps S pt T q K qq K qu K r R rh R rr R s S sc SK sce SA sch SK sci SA sh C ss S ssi CA t T tch C th D tt T u A ue A ui A uu A uy A v F vv F w W wh H wr R ww W x KS xx KS y A z S zz S";
	String germanPhonemes = "ah A ä A äh A äu A ch H chs KS dt T eh A ieh A ih A ö A oh A öh A qu KF sch C ß S th T ü A uh A üh A";
	String spanishPhonemes = "á A ch C é A í A ll Y ñ NY ó A qu K ú A ü A";
	String frenchPhonemes = "à A â A aî A aï A aie A aou A ç S ch C cs A é A è A ê A ée A eî A eû A ge C gn NY gu K ll Y nd N nt N ô A œu A oï A oie A oue A qu K th T";
	String italianPhonemes = "á A à A che KA chi KA é A è A ge CA gh K gi C gn NY í A ì A ò A ó A sci CA sce SA sch SK ú A ù A";
	String dutchPhonemes = "aai A auw A ch H é A eeuw A ieuw A ij A oei A ooi A ouw A sch SH th T uw A";
	String polishPhonemes = "ą A ć C ch H cz C dz C dź C dż C ę A ł W ń N ó A rz C ś C sz C ź C ż C";

	private static SpellCheck instance = null;

	public static SpellCheck getInstance()
	{
		if (instance == null)
		{
			instance = new SpellCheck();
		}
		return instance;
	}

	void loadDictionary(String lang)
	{
		currentLang = lang;
		phonemes.clear();
		addPhonemes(englishPhonemes);
		if (currentLang.equals("de"))
		{
			addPhonemes(germanPhonemes);
		}
		else if (currentLang.equals("es"))
		{
			addPhonemes(spanishPhonemes);
		}
		else if (currentLang.equals("fr"))
		{
			addPhonemes(frenchPhonemes);
		}
		else if (currentLang.equals("it"))
		{
			addPhonemes(italianPhonemes);
		}
		else if (currentLang.equals("nl"))
		{
			addPhonemes(dutchPhonemes);
		}
		else if (currentLang.equals("pl"))
		{
			addPhonemes(polishPhonemes);
			phonemes.remove("ck");
		}

		// The English dictionary generates ~90k hash buckets, 120k to start off
		dict = new HashMap<String, ArrayList<String>>(120000);
		try
		{
			String file = "/resources/dict_" + currentLang + ".txt";
			InputStream is = getClass().getResourceAsStream(file);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String w = "";
			while ((w = br.readLine()) != null)
			{
				addWord(w);
			}
			br.close();
		}
		catch (Exception ex)
		{
			Constants.writeBlog("SpellCheck > loadDict > " + ex);
		}
		ArrayList<Integer> userWords = DBFunctions.getInstance().selectAll("DCT", 1, "");
		for (int index : userWords)
		{
			addWord(DBFunctions.getInstance().get(index, Constants.LIST_VALUE));
		}
	}

	String hashWord(String word)
	{
		String hash = "";
		String word2 = word.toLowerCase();
		while (word2.length() > 0)
		{
			if (word2.length() > 2)
			{
				String ph = word2.substring(0, 3);
				String s = phonemes.get(ph);
				if (s != null)
				{
					hash += s;
					word2 = word2.substring(3);
					continue;
				}
			}
			if (word2.length() > 1)
			{
				String ph = word2.substring(0, 2);
				String s = phonemes.get(ph);
				if (s != null)
				{
					hash += s;
					word2 = word2.substring(2);
					continue;
				}
			}
			String ph = word2.substring(0, 1);
			String s = phonemes.get(ph);
			// this will drop apostrophes
			if (s != null)
			{
				hash += s;
			}
			word2 = word2.substring(1);
		}
		return hash;
	}

	void addSuggestedHash(String hash)
	{
		if (dict.containsKey(hash) && !hashes.contains(hash))
		{
			hashes.add(hash);
		}
	}

	void orderSuggestions(String word)
	{
		ArrayList<String> al = new ArrayList<String>();
		for (String h : hashes)
		{
			for (String entry : dict.get(h))
			{
				int d = distance(entry.toLowerCase(), word.toLowerCase());
				d = Constants.clamp(d, 0, 9);
				al.add(0, d + entry);
				if (capital)
				{
					al.add(d + toCapitalCase(entry));
				}
			}
		}

		// doSplits
		for (int i = 1; i < word.length() - 2; i++)
		{
			String w1 = word.substring(0, i);
			String w2 = word.substring(i, word.length());
			boolean w1l = isWord(w1);
			boolean w2l = isWord(w2);
			if (!w1l)
			{
				w1l = isWord(toCapitalCase(w1));
				if (w1l)
				{
					w1 = toCapitalCase(w1);
				}
			}
			if (!w2l)
			{
				w2l = isWord(toCapitalCase(w2));
				if (w2l)
				{
					w2 = toCapitalCase(w2);
				}
			}
			if (w1l && w2l)
			{
				// it's a split! it has a L-dist of 1
				al.add("1" + w1 + " " + w2);
			}
		}

		Collections.sort(al, String.CASE_INSENSITIVE_ORDER);
		suggestions.clear();

		for (int i = 0; i < 10; i++)
		{
			for (String entry : al)
			{
				if ((entry.charAt(0) - '0') == i)
				{
					entry = entry.substring(1);
					if (!suggestions.contains(entry))
					{
						suggestions.add(entry);
						if (capital && !isCapitalCase(entry))
						{
							suggestions.add(toCapitalCase(entry));
						}
					}
				}
			}
			if (suggestions.size() > 10)
			{
				return;
			}
		}
	}

	void generateSuggestions(String word)
	{
		capital = isCapitalCase(word);
		hashes.clear();
		String h = hashWord(word);
		StringBuilder hash = new StringBuilder(h);
		for (char sub : "ACDFHKLMNPRSTWY".toCharArray())
		{
			for (int i = 0; i < hash.length(); i++)
			{
				// insert a phoneme
				StringBuilder hash2 = new StringBuilder(h);
				hash2.insert(i, sub);
				addSuggestedHash(hash2.toString());
				// swap
				if (i < hash.length() - 1)
				{
					hash2 = new StringBuilder(h);
					char c1 = hash2.charAt(i);
					char c2 = hash2.charAt(i + 1);
					hash2.setCharAt(i, c2);
					hash2.setCharAt(i + 1, c1);
					addSuggestedHash(hash2.toString());
				}
				// replace a phoneme (will catch original)
				hash2 = new StringBuilder(h);
				hash2.setCharAt(i, sub);
				addSuggestedHash(hash2.toString());
				// delete a phoneme
				hash2 = new StringBuilder(h);
				hash2.deleteCharAt(i);
				addSuggestedHash(hash2.toString());
			}
			StringBuilder hash2 = new StringBuilder(h);
			hash2.append(sub);
			addSuggestedHash(hash2.toString());
		}
		orderSuggestions(word);
	}

	void addWord(String word)
	{
		String hash = hashWord(word);
		if (dict.containsKey(hash))
		{ // hash exists
			if (!dict.get(hash).contains(word))
			{
				dict.get(hash).add(word);
			}
		}
		else
		{ // hash no exist
			ArrayList a = new ArrayList();
			a.add(word);
			dict.put(hash, a);
		}
	}

	boolean isWord(String word)
	{
		boolean isaWord = isWord2(word);
		if (word.contains("'") && !isaWord)
		{
			String[] pieces = word.split("'");
			for (String word2 : pieces)
			{
				if (word2.length() > 1)
				{
					isaWord = isWord2(word2);
				}
			}
		}
		return isaWord;
	}

	boolean isWord2(String word)
	{
		String hash = hashWord(word);
		if (dict.get(hash) != null)
		{
			return (dict.get(hash).contains(word) || dict.get(hash).contains(toUncapitalCase(word)));
		}
		return false;
	}

	void addPhonemes(String p)
	{
		String[] phon = p.split(" ");
		for (int i = 0; i < phon.length; i = i + 2)
		{
			phonemes.put(phon[i], phon[i + 1]);
		}
	}

	void makeSuggestions(MouseEvent evt)
	{
		final JTextComponent c = (JTextComponent) evt.getComponent();
		c.requestFocusInWindow();
		Point p = evt.getPoint();
		int position = c.viewToModel(p);
		String word = getWordAt(c.getText(), position);
		if (word.length() < 2)
		{
			return;
		}

		suggestions.clear();
		generateSuggestions(word);

		ScrollablePopupMenu popup = new ScrollablePopupMenu();
		for (String s : suggestions)
		{
			JMenuItem mi = new JMenuItem(s);
			mi.setBackground(Cato.whiteAndBlack);
			mi.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					String repString = ae.getActionCommand();
					String s = c.getText();
					s = s.substring(0, start) + repString + s.substring(end);
					c.setText(s);
					c.setCaretPosition(start + repString.length());
					runSpellCheck(c);
				}
			});
			popup.add(mi);
		}
		popup.addSeparator();
		JMenuItem mi = new JMenuItem("Add \"" + word + "\" (" + suggestions.size() + ")");
		mi.setBackground(Cato.whiteAndBlack);
		mi.addActionListener(new ActionListener()
		{ // add to dictionary
			public void actionPerformed(ActionEvent ae)
			{// add word to user dict
				try
				{
					addWord(word);
					String[] newWord = { "DCT", word };
					DBFunctions.getInstance().updateNoLog(-1, newWord);
					runSpellCheck(c);
				}
				catch (Exception ioe)
				{}
			}
		});
		popup.add(mi);
		popup.show(c, p.x - 75, p.y + Cato.textSize);
	}

	void runSpellCheck(final JTextComponent c)
	{
		if (!BooksView.getInstance().spellCheckMenuItem.isSelected()) return;
		if (c.isEnabled() && (c.getText().length() > 0))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					checkWords(c);
				}
			});
		}
	}

	void checkWords(JTextComponent c)
	{
		String text = c.getText() + " ";
		Highlighter h = c.getHighlighter();
		h.removeAllHighlights();
		String wordToCheck = "";
		for (int i = 0; i < text.length(); i++)
		{
			if (DELIMITERS.contains(String.valueOf(text.charAt(i))))
			{ // end of word
				int j = i;
				if (wordToCheck.endsWith("'s"))
				{
					wordToCheck = wordToCheck.substring(0, wordToCheck.length() - 2);
					j -= 2;
				}
				while (wordToCheck.endsWith("'"))
				{
					wordToCheck = wordToCheck.substring(0, wordToCheck.length() - 1);
					j -= 1;
				}
				while (wordToCheck.startsWith("'"))
				{
					wordToCheck = wordToCheck.substring(1);
				}
				if (wordToCheck.length() > 1)
				{
					if (!isWord(wordToCheck))
					{
						try
						{
							h.addHighlight(j - wordToCheck.length(), j, hp);
						}
						catch (Exception ex)
						{
							Constants.writeBlog("SpellCheck > checkWords > " + ex);
						}
					}
				}
				wordToCheck = "";
			}
			else
			{
				wordToCheck += text.charAt(i);
			}
		}
	}

	String getWordAt(String text, int position)
	{
		start = scanToEnd(text, position, -1);
		end = scanToEnd(text, position, 1);
		end++;
		if (start > end)
		{
			return "";
		}
		String w = text.substring(start, end);
		while (w.startsWith("'"))
		{
			w = w.substring(1);
			start++;
		}
		while (w.endsWith("'"))
		{
			w = w.substring(0, w.length() - 1);
			end--;
		}
		return w;
	}

	int scanToEnd(String text, int position, int direction)
	{
		position = Constants.clamp(position, 0, text.length() - 1);
		while (!DELIMITERS.contains(String.valueOf(text.charAt(position))))
		{
			position += direction;
			if (position < 0 || position > text.length() - 1)
			{
				position = Constants.clamp(position, 0, text.length() - 1);
				return position;
			}
		}
		return position - direction;
	}

	boolean isCapitalCase(String s)
	{
		return Character.isUpperCase(s.charAt(0));
	}

	String toCapitalCase(String s)
	{
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	String toUncapitalCase(String s)
	{
		return Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}

	int distance(CharSequence source, CharSequence target)
	{
		int sourceLength = source.length();
		int targetLength = target.length();
		if (sourceLength == 0)
		{
			return targetLength;
		}
		if (targetLength == 0)
		{
			return sourceLength;
		}
		int[][] dist = new int[sourceLength + 1][targetLength + 1];
		for (int i = 0; i < sourceLength + 1; i++)
		{
			dist[i][0] = i;
		}
		for (int j = 0; j < targetLength + 1; j++)
		{
			dist[0][j] = j;
		}
		for (int i = 1; i < sourceLength + 1; i++)
		{
			for (int j = 1; j < targetLength + 1; j++)
			{
				int cost = source.charAt(i - 1) == target.charAt(j - 1) ? 0 : 1;
				dist[i][j] = Math.min(Math.min(dist[i - 1][j] + 1, dist[i][j - 1] + 1), dist[i - 1][j - 1] + cost);
				if (i > 1 && j > 1 && source.charAt(i - 1) == target.charAt(j - 2)
						&& source.charAt(i - 2) == target.charAt(j - 1))
				{
					dist[i][j] = Math.min(dist[i][j], dist[i - 2][j - 2] + cost);
				}
			}
		}
		return dist[sourceLength][targetLength];
	}
}
