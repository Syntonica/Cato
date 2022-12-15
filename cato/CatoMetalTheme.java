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

import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalTheme;

public class CatoMetalTheme extends MetalTheme
{
	ColorUIResource acceleratorForeground;
	ColorUIResource acceleratorSelectedForeground;
	ColorUIResource black;
	ColorUIResource control;
	ColorUIResource controlDarkShadow;
	ColorUIResource controlDisabled;
	ColorUIResource controlHighlight;
	ColorUIResource controlInfo;
	ColorUIResource controlShadow;
	ColorUIResource controlTextColor;
	ColorUIResource desktopColor;
	ColorUIResource focusColor;
	ColorUIResource highlightedTextColor;
	ColorUIResource inactiveControlTextColor;
	ColorUIResource inactiveSystemTextColor;
	ColorUIResource menuBackground;
	ColorUIResource menuDisabledForeground;
	ColorUIResource menuForeground;
	ColorUIResource menuSelectedBackground;
	ColorUIResource menuSelectedForeground;
	ColorUIResource primary1;
	ColorUIResource primary2;
	ColorUIResource primary3;
	ColorUIResource primaryControl;
	ColorUIResource primaryControlDarkShadow;
	ColorUIResource primaryControlHighlight;
	ColorUIResource primaryControlInfo;
	ColorUIResource primaryControlShadow;
	ColorUIResource secondary1;
	ColorUIResource secondary2;
	ColorUIResource secondary3;
	ColorUIResource separatorBackground;
	ColorUIResource separatorForeground;
	ColorUIResource systemTextColor;
	ColorUIResource textHighlightColor;
	ColorUIResource userTextColor;
	ColorUIResource white;
	ColorUIResource windowBackground;
	ColorUIResource windowTitleBackground;
	ColorUIResource windowTitleForeground;
	ColorUIResource windowTitleInactiveBackground;
	ColorUIResource windowTitleInactiveForeground;

	public CatoMetalTheme()
	{
		setDark();
	}

	public void setDark()
	{
		menuBackground = new ColorUIResource(new Color(25, 25, 25));
		windowTitleInactiveBackground = new ColorUIResource(new Color(25, 25, 25));
		separatorBackground = new ColorUIResource(new Color(45, 45, 45));
		// text fields, areas
		windowBackground = new ColorUIResource(new Color(45, 45, 45));
		windowTitleBackground = new ColorUIResource(new Color(45, 45, 45));
		menuSelectedBackground = new ColorUIResource(new Color(65, 65, 65));

		separatorForeground = new ColorUIResource(new Color(115, 115, 115));
		menuDisabledForeground = new ColorUIResource(new Color(115, 115, 115));
		windowTitleInactiveForeground = new ColorUIResource(new Color(115, 115, 115));
		menuForeground = new ColorUIResource(new Color(225, 225, 225));
		menuSelectedForeground = new ColorUIResource(new Color(255, 255, 255));
		acceleratorForeground = new ColorUIResource(new Color(225, 225, 225));
		windowTitleForeground = new ColorUIResource(new Color(225, 225, 225));
		acceleratorSelectedForeground = new ColorUIResource(new Color(255, 255, 255));

		// borders on everything, bezels and the arrows on spinners... why???
		controlDarkShadow = new ColorUIResource(new Color(35, 35, 35));
		// selected buttons, borders on: disabled components, scroll bar track
		// uneditable text fields, menu bar base, inactive tabs
		controlShadow = new ColorUIResource(new Color(35, 35, 35));
		// Menu borders, some icon accents, scroll bar handle borders and dotting
		primaryControlDarkShadow = new ColorUIResource(new Color(35, 35, 35));
		// Scroll bar handles
		primaryControlShadow = new ColorUIResource(new Color(35, 35, 35));

		// disabled button text
		inactiveControlTextColor = new ColorUIResource(new Color(115, 115, 115));
		// nothing... label text?
		inactiveSystemTextColor = new ColorUIResource(new Color(115, 115, 115));
		// Label Text
		systemTextColor = new ColorUIResource(new Color(225, 225, 225));
		// Selected text background
		textHighlightColor = new ColorUIResource(new Color(25, 25, 25));
		// text fields, areas
		userTextColor = new ColorUIResource(new Color(225, 225, 225));
		// text on buttons, tabs, combos
		controlTextColor = new ColorUIResource(new Color(225, 225, 225));
		// user selected text
		highlightedTextColor = new ColorUIResource(new Color(255, 225, 225));

		// panels, active tabs, buttons, popup menus
		control = new ColorUIResource(new Color(65, 65, 65));
		// ??
		controlDisabled = new ColorUIResource(new Color(0, 255, 0));
		// borders on scrollpanes, active buttons, textfields, combos
		controlHighlight = new ColorUIResource(new Color(35, 35, 35));
		// arrows on combos and scroll panes (nbut not spinners)
		controlInfo = new ColorUIResource(new Color(225, 225, 225));
		// system icons on FileChooser
		primaryControl = new ColorUIResource(new Color(45, 45, 45));
		// edges on menu dropdowns, sys icons, butnot combo dropdowns...
		primaryControlHighlight = new ColorUIResource(new Color(25, 25, 25));
		// toolTip text, sys icons
		primaryControlInfo = new ColorUIResource(new Color(225, 225, 225));

		// unknown
		desktopColor = new ColorUIResource(new Color(65, 65, 65));
		// focus ring  Apparently, editable text fields don't get a focus ring,
		// but focusable components like JComboBoxes do! Why?!?  This appears
		// sto be a Metal thing as Ocean functions similarly.  Maybe Dark
		// Nimbus someday...
		focusColor = new ColorUIResource(Color.cyan);//new Color(0, 118, 155));

		// all unused
		primary1 = new ColorUIResource(new Color(255, 65, 65));
		primary2 = new ColorUIResource(new Color(255, 65, 65));
		primary3 = new ColorUIResource(new Color(255, 65, 65));
		secondary1 = new ColorUIResource(new Color(255, 65, 65));
		secondary2 = new ColorUIResource(new Color(255, 65, 65));
		secondary3 = new ColorUIResource(new Color(255, 65, 65));

		black = new ColorUIResource(new Color(0, 0, 0));
		white = new ColorUIResource(new Color(255, 255, 255));
	}

	public void addCustomEntriesToTable(UIDefaults table)
	{

	}

	public ColorUIResource getAcceleratorForeground()
	{
		return acceleratorForeground;
	}

	public ColorUIResource getAcceleratorSelectedForeground()
	{
		return acceleratorSelectedForeground;
	}

	public ColorUIResource getBlack()
	{
		return black;
	}

	public ColorUIResource getControl()
	{
		return control;
	}

	public ColorUIResource getControlDarkShadow()
	{
		return controlDarkShadow;
	}

	public ColorUIResource getControlDisabled()
	{
		return controlDisabled;
	}

	public ColorUIResource getControlHighlight()
	{
		return controlHighlight;
	}

	public ColorUIResource getControlInfo()
	{
		return controlInfo;
	}

	public ColorUIResource getControlShadow()
	{
		return controlShadow;
	}

	public ColorUIResource getControlTextColor()
	{
		return controlTextColor;
	}

	public FontUIResource getControlTextFont()
	{
		return new FontUIResource("Dialog", Font.PLAIN, 12);
	}

	public ColorUIResource getDesktopColor()
	{
		return desktopColor;
	}

	public ColorUIResource getFocusColor()
	{
		return focusColor;
	}

	public ColorUIResource getHighlightedTextColor()
	{
		return highlightedTextColor;
	}

	public ColorUIResource getInactiveControlTextColor()
	{
		return inactiveControlTextColor;
	}

	public ColorUIResource getInactiveSystemTextColor()
	{
		return inactiveSystemTextColor;
	}

	public ColorUIResource getMenuBackground()
	{
		return menuBackground;
	}

	public ColorUIResource getMenuDisabledForeground()
	{
		return menuDisabledForeground;
	}

	public ColorUIResource getMenuForeground()
	{
		return menuForeground;
	}

	public ColorUIResource getMenuSelectedBackground()
	{
		return menuSelectedBackground;
	}

	public ColorUIResource getMenuSelectedForeground()
	{
		return menuSelectedForeground;
	}

	public FontUIResource getMenuTextFont()
	{
		return new FontUIResource("Dialog", Font.PLAIN, 12);
	}

	public String getName()
	{
		return "AuthorMetalTheme";
	}

	public ColorUIResource getPrimary1()
	{
		return primary1;
	}

	public ColorUIResource getPrimary2()
	{
		return primary2;
	}

	public ColorUIResource getPrimary3()
	{
		return primary3;
	}

	public ColorUIResource getPrimaryControl()
	{
		return primaryControl;
	}

	public ColorUIResource getPrimaryControlDarkShadow()
	{
		return primaryControlDarkShadow;
	}

	public ColorUIResource getPrimaryControlHighlight()
	{
		return primaryControlHighlight;
	}

	public ColorUIResource getPrimaryControlInfo()
	{
		return primaryControlInfo;
	}

	public ColorUIResource getPrimaryControlShadow()
	{
		return primaryControlShadow;
	}

	public ColorUIResource getSecondary1()
	{
		return secondary1;
	}

	public ColorUIResource getSecondary2()
	{
		return secondary2;
	}

	public ColorUIResource getSecondary3()
	{
		return secondary3;
	}

	public ColorUIResource getSeparatorBackground()
	{
		return separatorBackground;
	}

	public ColorUIResource getSeparatorForeground()
	{
		return separatorForeground;
	}

	public FontUIResource getSubTextFont()
	{
		return new FontUIResource("Dialog", Font.PLAIN, 10);
	}

	public ColorUIResource getSystemTextColor()
	{
		return systemTextColor;
	}

	public FontUIResource getSystemTextFont()
	{
		return new FontUIResource("Dialog", Font.PLAIN, 12);
	}

	public ColorUIResource getTextHighlightColor()
	{
		return textHighlightColor;
	}

	public ColorUIResource getUserTextColor()
	{
		return userTextColor;
	}

	public FontUIResource getUserTextFont()
	{
		return new FontUIResource("Dialog", Font.PLAIN, 12);
	}

	public ColorUIResource getWhite()
	{
		return white;
	}

	public ColorUIResource getWindowBackground()
	{
		return windowBackground;
	}

	public ColorUIResource getWindowTitleBackground()
	{
		return windowTitleBackground;
	}

	public FontUIResource getWindowTitleFont()
	{
		return new FontUIResource("Dialog", Font.PLAIN, 12);

	}

	public ColorUIResource getWindowTitleForeground()
	{
		return windowTitleForeground;
	}

	public ColorUIResource getWindowTitleInactiveBackground()
	{
		return windowTitleInactiveBackground;
	}

	public ColorUIResource getWindowTitleInactiveForeground()
	{
		return windowTitleInactiveForeground;
	}
}
