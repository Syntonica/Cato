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

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Scanner;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.JTextComponent;

public class HelpView extends JPanel
{
	private static HelpView instance = null;

	public static HelpView getInstance()
	{
		if (instance == null)
		{
			instance = new HelpView();
		}
		return instance;
	}

	public HelpView()
	{
		Constants.windowNames.put(this, "Help");
		Constants.windowIcons.put(this, "help.png");
		JEditorPane booksHelpTextArea = new JEditorPane();
		booksHelpTextArea.setEditable(false);
		booksHelpTextArea.setContentType("text/html");
		String html = "";
		Scanner scan;
		// URL imgsrc;
		try
		{
			scan = new Scanner(getClass().getResourceAsStream("/resources/help.html"), "UTF-8");
			scan.useDelimiter("\\A");
			html = scan.next();
		}
		catch (Exception ex)
		{
			Constants.writeBlog("HelpView > " + ex);
		} ;

		html = html.replaceFirst("FFGG", Cato.helpForeColor);
		html = html.replaceFirst("BBGG", Cato.helpBackColor);
		booksHelpTextArea.setText(html);
		booksHelpTextArea.addHyperlinkListener(new HyperlinkListener()
		{
			public void hyperlinkUpdate(HyperlinkEvent e)
			{
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				{
					try
					{
						int frag = Integer.parseInt(e.getDescription());
						String theText = booksHelpTextArea.getDocument().getText(0,
								booksHelpTextArea.getDocument().getLength());
						int loc = 0;
						for (int i = 1; i <= frag; i++)
						{
							loc = theText.indexOf('§', loc) + 1;
						}
						centerLineInScrollPane(booksHelpTextArea, loc);
					}
					catch (Exception ex)
					{
						Constants.writeBlog("HelpView > booksHelpTextArea > " + ex);
					}
				}
			}
		});

		JScrollPane booksHelpScrollPane = new JScrollPane(booksHelpTextArea);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				booksHelpScrollPane.getVerticalScrollBar().setValue(0);
			}
		});
		booksHelpTextArea.setEditable(false);
		setLayout(new ColumnLayout(1, 1));
		add("hvwx", booksHelpScrollPane);
	}

	private static void centerLineInScrollPane(JTextComponent component, int loc)
	{
		Container container = SwingUtilities.getAncestorOfClass(JViewport.class, component);
		if (container == null)
		{
			return;
		}
		try
		{
			Rectangle r = component.modelToView(loc);
			JViewport viewport = (JViewport) container;
			int extentHeight = viewport.getExtentSize().height;
			int viewHeight = viewport.getViewSize().height;

			int y = Math.max(0, r.y - 50);
			y = Math.min(y, viewHeight - extentHeight);

			viewport.setViewPosition(new Point(0, y));
		}
		catch (Exception ex)
		{
			Constants.writeBlog("HelpView > centerLineInScrollPane > " + ex);
		}
	}
}
