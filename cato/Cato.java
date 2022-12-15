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
import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class Cato
{
	// Dark is a custom Metal theme, light is Nimbus.  It's not really
	// possible to switch themes in the program without some difficulty
	// due to the complexity of the GUI customizations
	static public String catosMood = "dark";
	//static public String catosMood = "light";

	// Changes the font size of pretty much all components
	// The font size can be set to 20 or more
	static public int textSize = 12;

	// Component height.  Make larger to accomodate larger font sizes
	// 29 is generous for font sizes of up to 20
	static public int compHeight = 25;

	// used for all components
	public static Font catoFont = new Font(Font.SANS_SERIF, Font.PLAIN, textSize);
	// used in all text areas
	public static Font userFont = new Font(Font.SANS_SERIF, Font.PLAIN, textSize + 1);
	// Preview pane in BookListView
	public static Font previewFont = new Font(Font.SERIF, Font.PLAIN, textSize + 1);
	// blue or cyan, used for sticky labels and WaitBox
	public static Color hiliteColor;
	// used for backgrounds in popup menus and some fields
	public static Color whiteAndBlack;
	// Hex strings to inject into the HelpView
	public static String helpForeColor;
	public static String helpBackColor;

	public static Color naturalColor;

	public static void main(String[] args)
	{
		// use Apple's mood setting
		// also can use -Dapple.awt.application.appearance=system
		// from startup.  
		System.setProperty("apple.awt.application.appearance", "system");

		setMood();
		new Constants();
	}

	public static void setMood()
	{
		if (catosMood.equals("dark"))
		{
			hiliteColor = Color.cyan;
			whiteAndBlack = Color.black;
			helpForeColor = "#E1E1E1";
			helpBackColor = "#1E1E1E";
			try
			{
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				CatoMetalTheme theme = new CatoMetalTheme();
				MetalLookAndFeel.setCurrentTheme(theme);
				UIManager.setLookAndFeel(new MetalLookAndFeel());
			}
			catch (Exception ex)
			{
				System.out.println(ex);
			}
		}
		else
		{
			hiliteColor = Color.blue;
			whiteAndBlack = Color.white;
			helpForeColor = "#1E1E1E";
			helpBackColor = "#E1E1E1";
			try
			{
				UIManager.setLookAndFeel(new NimbusLookAndFeel());
			}
			catch (Exception ex)
			{
				System.out.println(ex);
			}
		}
	}
}
