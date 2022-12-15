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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import javax.swing.JTable;

public class PrintThis implements Printable
{
	Component component;

	public PrintThis(Component c)
	{
		this(c, PageFormat.LANDSCAPE);
	}

	public PrintThis(Component c, int orientation)
	{
		this.component = c;
		PrinterJob pj = PrinterJob.getPrinterJob();
		PageFormat pf = pj.defaultPage();
		pf.setOrientation(orientation);
		if (c instanceof JTable)
		{
			pj.setPrintable(((JTable) c).getPrintable(JTable.PrintMode.FIT_WIDTH, null, null), pf);
		}
		else
		{
			pj.setPrintable(this, pf);
		}
		if (pj.printDialog())
		{
			try
			{
				Constants.printing = true;
				pj.print();
			}
			catch (Exception ex)
			{
				Constants.writeBlog("PrintThis > " + ex);
			}
			finally
			{
				Constants.printing = false;
			}
		}
	}

	public int print(Graphics g, PageFormat pf, int pageIndex)
	{
		Dimension d = component.getSize();
		double scale = 1.0;
		if (d.getWidth() > pf.getImageableWidth())
		{
			scale = pf.getImageableWidth() / d.getWidth();
		}
		if (pageIndex > (int) (d.getHeight() * scale / pf.getImageableHeight()))
		{
			return Printable.NO_SUCH_PAGE;
		}
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		g2d.translate(0f, -pageIndex * pf.getImageableHeight());
		g2d.scale(scale, scale);
		component.print(g2d);
		return Printable.PAGE_EXISTS;
	}
}
