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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

public class ColorUnderlinePainter extends DefaultHighlighter.DefaultHighlightPainter
{

	public ColorUnderlinePainter(Color c)
	{
		super(c);
	}

	public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view)
	{
		Rectangle r = getDrawingArea(offs0, offs1, bounds, view);
		if (r == null) return null;

		Color color = getColor();
		g.setColor(color == null ? c.getSelectionColor() : color);

		int squiggle = 2;
		int twoSquiggles = squiggle * 2;
		int y = r.y + r.height - squiggle - 2; // -2 to keep from leaving crumbs

		for (int x = r.x; x <= r.x + r.width - twoSquiggles; x += twoSquiggles)
		{
			g.drawArc(x, y, squiggle, squiggle, 0, 180);
			g.drawArc(x + squiggle, y, squiggle, squiggle, 180, 181);
			g.drawArc(x, y + 1, squiggle, squiggle, 0, 180);
			g.drawArc(x + squiggle, y + 1, squiggle, squiggle, 180, 181);
		}
		return r;
	}

	private Rectangle getDrawingArea(int offs0, int offs1, Shape bounds, View view)
	{
		if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset())
		{
			return bounds.getBounds();
		}
		else
		{
			try
			{
				Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);
				return shape.getBounds();
			}
			catch (Exception ex)
			{
				Constants.writeBlog("ColorUnderlinePainter > getDrawingArea > " + ex);
			}
		}
		return null;
	}
}