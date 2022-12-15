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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

public class ClosableTabbedPane extends JTabbedPane
{
	ArrayList<JPanel> tabStack = new ArrayList<JPanel>();

	public void openTab(final JPanel window)
	{
		int index = indexOfComponent(window);
		if  (index < 0)
		{
			int insertPoint = getTabCount();
			add(window, insertPoint);
			String tabName = " " + Constants.windowNames.get(window);
			String icon = Constants.windowIcons.get(window);
			JPanel panel = new JPanel(new ColumnLayout(2, 2));
			panel.setOpaque(false);
			JLabel label = new JLabel(tabName);
			label.setFont(Cato.catoFont);
			label.setFocusable(false);
			JButton check = new TabButton(window);
			panel.add("xb", label);
			panel.add("xbw", check);
			setTabComponentAt(insertPoint, panel);
			index = insertPoint;
		}
		setSelectedIndex(index);
		if (tabStack.indexOf(window) < 0)
		{
			tabStack.add(0, window);
		}
		else
		{
			moveTabToTopOfStack();
		}
	}

	public void closeAllTabsWithSave()
	{
		while (tabStack.size() > 1)
		{
			JPanel jp = tabStack.get(0);
			if (jp != WelcomePanel.getInstance())
			{
				closeTabWithSave(jp);
			}
			else
			{
				closeTabWithSave(tabStack.get(1));
			}
		}
	}

	public void closeTabWithSave(JPanel window)
	{
		if (window instanceof GenericEditView)
		{
			if (isTabOpen(window))
			{
				openTab(window);
				if (!((GenericEditView) window).saveEntry()) return;
			}
		}
		else if (window == SettingsView.getInstance())
		{
			SettingsView.getInstance().saveEntry();
		}
		closeTab(window);
	}

	public void closeTab(JPanel window)
	{
		int tabNumber = indexOfComponent(window);
		if (tabNumber > -1)
		{
			removeTabAt(tabNumber);
			tabStack.remove(tabStack.indexOf(window));
			if (tabStack.size() < 1)
			{
				openTab(WelcomePanel.getInstance());
			}
			else
			{
				setSelectedIndex(indexOfComponent(tabStack.get(0)));
			}
		}
	}

	public boolean isTabOpen(JPanel window)
	{
		int tabNumber = indexOfComponent(window);
		if (tabNumber < 0) return false;
		setSelectedIndex(tabNumber);
		return true;
	}

	public void moveTabToTopOfStack()
	{
		JPanel jp = (JPanel) getSelectedComponent();
		int idx = tabStack.indexOf(jp);
		if (idx > 0)
		{
			tabStack.remove(jp);
			tabStack.add(0, jp);
		}
	}

	private class TabButton extends JButton
	{
		Color naturalColor;

		public TabButton(JPanel window)
		{
			naturalColor = getForeground();
			int size = 17;
			setPreferredSize(new Dimension(size, size));
			setToolTipText("close this tab");
			setUI(new BasicButtonUI());
			setContentAreaFilled(false);
			setFocusable(false);
			setBorderPainted(false);
			setRolloverEnabled(true);
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					if (SettingsView.getInstance().get("closeTab").equalsIgnoreCase("true"))
					{
						closeTabWithSave(window);
					}
					else
					{
						closeTab(window);
					}
				}
			});
		}

		//we don't want to update UI for this button
		public void updateUI() {}

		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setStroke(new BasicStroke(1));
			if (getModel().isRollover())
			{
				g2.setColor(Color.red);
			}
			else
			{
				g2.setColor(naturalColor);
			}
			g2.drawLine(3, 3, getWidth()-3, 3);
			g2.drawLine(getWidth()-3, 3, getWidth()-3, getHeight()-3);
			g2.drawLine(3, getHeight()-3, getWidth()-3, getHeight()-3);
			g2.drawLine(3, 3, 3, getHeight()-3);

			g2.setStroke(new BasicStroke(2));
			int centerX = getWidth()/2 +1;
			int centerY = getHeight()/2 +1;
			g2.drawLine(centerX-2, centerY-2, centerX+2, centerY+2);
			g2.drawLine(centerX-2, centerY+2, centerX+2, centerY-2);
			g2.dispose();
		}
	}
}
