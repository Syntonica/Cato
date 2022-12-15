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

/**
* © 2014 KJ Donaldson
*
* ColumnLayout.java
* GroupLayout has issues: not very human-readable, likes to squirm when PrefSizes are recalculated
* This custom layout manager, suggested by another similar custom layout manager, uses the idea of
* tab stops to layout components.
*
* List of constraints:
* w - Add a return (wrap)
* x - Add a tab stop
* h - Extend component horizontally
* v - Extend component vertically
* l - Align component to the left (default)
* c - Align component centered
* z - Align component vertically centered (default)
* r - Align component to the right
* t - Align component to the top
* b - Align component to the bottom
*
* # - Number of components in this tab on this row
* p - Keep component proportional to its preferred size
* k - put in any child panel's add to force recalc
*
* No x's, only # with a w to end wiil flow PrefSizes across row
* Every row must end in a wrap
*
* There are a couple of nicitie still missing in regards to recording sizing
*/

package cato;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.ArrayList;

public class ColumnLayout implements LayoutManager
{
	private static final String WRAP = "w";
	private static final String COLUMN = "x";
	private static final String HFILL = "h";
	private static final String VFILL = "v";

	private static final String RIGHT = "r";
	private static final String CENTER = "c";
	private static final String TOP = "t";
	private static final String BOTTOM = "b";

	private static final String PROPORTIONAL = "p";
	private static final String RECALC = "k";

	private ArrayList<ComponentEntry> components = new ArrayList<ComponentEntry>();
	private int hInset, vInset;
	private Dimension lastTarget = new Dimension(0, 0);

	public ColumnLayout()
	{
		this(0, 0);
	}

	public ColumnLayout(int h, int v)
	{
		hInset = h;
		vInset = v;
	}

	public void addLayoutComponent(String name, Component comp)
	{
		ComponentEntry ce = new ComponentEntry();
		ce.comp = comp;
		ce.constraint = name;
		if (name.length() > 0)
		{
			char c = name.charAt(0);
			if (Character.isDigit(c))
			{
				ce.numCompsInTab = Integer.parseInt(name.replaceAll("[^0-9]", ""));
			}
		}
		components.add(ce);
	}

	public void removeLayoutComponent(Component comp)
	{
		for (int i = 0; i < components.size(); i++)
		{
			if (components.get(i).comp == comp) components.remove(i);
		}
	}

	public Dimension minimumLayoutSize(Container target)
	{
		synchronized (target.getTreeLock())
		{
			int maxX = 0;
			int maxY = 0;
			int currentX = hInset;
			int currentY = vInset;
			Insets in = target.getInsets();
			for (int i = 0; i < components.size(); i++)
			{
				Dimension pref = components.get(i).comp.getMinimumSize();
				// calculate the needed space
				currentX += pref.width + hInset * 2;
				// more than one component in tab
				if (components.get(i).numCompsInTab > 1)
				{
					for (int j = 1; j < components.get(i).numCompsInTab; j++)
					{
						i++;
						pref = components.get(i).comp.getPreferredSize();
						currentX += pref.width + hInset * 2;
					}
				}
				maxX = Math.max(maxX, currentX);
				currentY = Math.max(currentY, pref.height);
				if (components.get(i).constraint.contains(WRAP))
				{
					currentX = hInset;
					maxY += currentY + vInset * 2;
				}
			}
			return new Dimension(maxX + hInset + in.left + in.right, maxY + vInset + in.top + in.bottom);
		}
	}

	public Dimension preferredLayoutSize(Container target)
	{
		synchronized (target.getTreeLock())
		{
			Insets in = target.getInsets();
			int maxX = 0;
			int maxY = 0;
			int currentX = hInset;
			int currentY = vInset;
			for (int i = 0; i < components.size(); i++)
			{
				Dimension pref = components.get(i).comp.getPreferredSize();
				// calculate the needed space
				currentX += pref.width + hInset * 2;
				// more than one component in tab
				if (components.get(i).numCompsInTab > 1)
				{
					for (int j = 1; j < components.get(i).numCompsInTab; j++)
					{
						i++;
						pref = components.get(i).comp.getPreferredSize();
						currentX += pref.width + hInset * 2;
					}
				}
				maxX = Math.max(maxX, currentX);
				currentY = Math.max(currentY, pref.height);
				if (components.get(i).constraint.contains(WRAP))
				{
					currentX = hInset;
					maxY += currentY + vInset * 2;
				}
			}
			return new Dimension(maxX + in.left + in.right, maxY + in.top + in.bottom);
		}
	}

	public void layoutContainer(Container target)
	{
		// only allow targets that have resized to relayout
		if (lastTarget.equals(target.getSize()))
		{
			return;
		} ;
		lastTarget = target.getSize();
		synchronized (target.getTreeLock())
		{
			Insets in = target.getInsets();
			int targetX = target.getWidth();
			int targetY = target.getHeight();
			int numCols = 1;
			int numRows = 0;
			int nc = 0;

			// squirrel away these values
			for (int i = 0; i < components.size(); i++)
			{
				if (components.get(i).constraint.contains(RECALC)) layoutContainer((Container) components.get(i).comp);
				Dimension pref = components.get(i).comp.getPreferredSize();
				components.get(i).bigBounds = new Dimension(pref.width + hInset * 2, pref.height + vInset * 2);
				components.get(i).littleBounds = new Rectangle(0, 0, pref.width, pref.height);
				if (components.get(i).constraint.contains(COLUMN)) nc++;
				if (components.get(i).constraint.contains(WRAP))
				{
					numRows++;
					if (nc > numCols) numCols = nc;
					nc = 0;
				}
			}

			int[] xRuler = new int[numCols];
			int[] yRuler = new int[numRows];
			boolean[] xPand = new boolean[numCols];
			boolean[] yPand = new boolean[numRows];
			for (int i = 0; i < numCols; i++)
			{
				xRuler[i] = 0;
				xPand[i] = false;
			}
			for (int i = 0; i < numRows; i++)
			{
				yRuler[i] = 0;
				yPand[i] = false;
			}

			int currentCol = 0;
			int currentRow = 0;
			int currentY = 0;
			for (int i = 0; i < components.size(); i++)
			{
				currentY = Math.max(currentY, components.get(i).bigBounds.height);
				if (components.get(i).constraint.contains(HFILL)) xPand[currentCol] = true;
				if (components.get(i).constraint.contains(VFILL)) yPand[currentRow] = true;
				if (components.get(i).constraint.contains(COLUMN))
				{
					components.get(i).tab = currentCol;
					int tempX = components.get(i).bigBounds.width;
					if (components.get(i).numCompsInTab > 1)
					{
						for (int j = 1; j < components.get(i).numCompsInTab; j++)
						{
							i++;
							tempX += components.get(i).bigBounds.width;
							components.get(i).tab = currentCol;
						}
					}
					xRuler[currentCol] = Math.max(xRuler[currentCol], tempX);
					currentCol++;
				}
				if (components.get(i).constraint.contains(WRAP))
				{
					currentCol = 0;
					yRuler[currentRow] = currentY;
					currentRow++;
					currentY = 0;
				}
			}
			// Calc actualX and actualY
			int actualX = 0;
			for (int i = 0; i < xRuler.length; i++)
				actualX += xRuler[i];
			int actualY = 0;
			for (int i = 0; i < yRuler.length; i++)
				actualY += yRuler[i];

			// turn the w/h values into absolute x/y values
			// get the trues in each ruler and share
			int xBumpCount = 0;
			int yBumpCount = 0;
			for (boolean hasIt : xPand)
				if (hasIt) xBumpCount++;
			for (boolean hasIt : yPand)
				if (hasIt) yBumpCount++;
			// get the bump sizes && add the bumps...
			if (xBumpCount > 0)
			{
				int xBumpSize = (targetX - actualX - in.right - in.left) / xBumpCount;
				for (int i = 0; i < xRuler.length; i++)
					if (xPand[i]) xRuler[i] += xBumpSize;
			}
			if (yBumpCount > 0)
			{
				int yBumpSize = (targetY - actualY - in.top - in.bottom) / yBumpCount;
				for (int i = 0; i < yRuler.length; i++)
					if (yPand[i]) yRuler[i] += yBumpSize;
			}
			// set BigBounds for components to fit into and then adjust sizes if needed
			currentRow = 0;
			for (int i = 0; i < components.size();)
			{
				// do the xes...
				int fullX = xRuler[components.get(i).tab];
				int numHfills = 0;
				for (int j = 0; j < components.get(i).numCompsInTab; j++)
				{
					if (components.get(i + j).constraint.contains(HFILL)) numHfills++;
					fullX -= (int) components.get(i + j).bigBounds.width;
				}
				int bump = (numHfills == 0) ? fullX : fullX / numHfills;
				for (int j = 0; j < components.get(i).numCompsInTab; j++)
				{
					int w = (int) components.get(i + j).bigBounds.width;
					if (components.get(i + j).constraint.contains(HFILL) || components.get(i).numCompsInTab == 1)
						w += bump;

					components.get(i + j).bigBounds.width = w;
					components.get(i + j).bigBounds.height = yRuler[currentRow];

					if (components.get(i + j).constraint.contains(WRAP)) currentRow++;
				}
				i = i + components.get(i).numCompsInTab;
			}
			// each component now has a range for the container
			// and a size to fit inside the range
			int currentX = in.left;
			currentY = in.top;
			for (int i = 0; i < components.size(); i++)
			{
				if (components.get(i).constraint.contains(HFILL))
					components.get(i).littleBounds.width = components.get(i).bigBounds.width - hInset * 2;
				if (components.get(i).constraint.contains(VFILL))
					components.get(i).littleBounds.height = components.get(i).bigBounds.height - vInset * 2;

				if (components.get(i).constraint.contains(PROPORTIONAL))
				{
					Dimension d = components.get(i).comp.getPreferredSize();
					double xScale = components.get(i).bigBounds.width / d.getWidth();
					double yScale = components.get(i).bigBounds.height / d.getHeight();
					double scale = Math.min(xScale, yScale);
					components.get(i).littleBounds.width = (int) (d.getWidth() * scale) - hInset * 2;
					components.get(i).littleBounds.height = (int) (d.getHeight() * scale) - vInset * 2;
				}
				// set the default placements - X
				if (components.get(i).constraint.contains(CENTER))
				{
					components.get(i).littleBounds.x = currentX + (components.get(i).bigBounds.width / 2)
							- (components.get(i).littleBounds.width / 2);
				}
				else if (components.get(i).constraint.contains(RIGHT))
				{
					components.get(i).littleBounds.x = currentX + components.get(i).bigBounds.width
							- components.get(i).littleBounds.width - hInset;
				}
				else
				{
					components.get(i).littleBounds.x = currentX + hInset;
				}
				// placements Y
				if (components.get(i).constraint.contains(TOP))
				{
					components.get(i).littleBounds.y = currentY + vInset;
				}
				else if (components.get(i).constraint.contains(BOTTOM))
				{
					components.get(i).littleBounds.y = currentY + components.get(i).bigBounds.height
							- components.get(i).littleBounds.height - vInset;
				}
				else
				{
					components.get(i).littleBounds.y = currentY + components.get(i).bigBounds.height / 2
							- components.get(i).littleBounds.height / 2;
				}
				// set component
				components.get(i).comp.setBounds(components.get(i).littleBounds);
				// update curX with the saved value
				currentX += components.get(i).bigBounds.width;
				if (components.get(i).constraint.contains(WRAP))
				{
					currentX = in.left;
					currentY += components.get(i).bigBounds.height;
				}
			}
		}
	}
}

final class ComponentEntry
{
	Component comp;
	// bigBounds = bounding box that takes up actual real estate
	Dimension bigBounds = new Dimension(0, 0);
	// littleBounds = bounding box that defines component size
	Rectangle littleBounds = new Rectangle(0, 0, 0, 0);
	String constraint = "";
	int tab = 0;
	int numCompsInTab = 1;
}