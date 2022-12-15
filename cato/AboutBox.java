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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AboutBox extends JPanel
{
	private JLabel imageLabel = new JLabel();

	private JLabel appTitleLabel = new JLabel("<html><b>Cato the Younger:</b></html>");
	private JLabel versionLabel = new JLabel("<html><b>Product Version:</b></html>");
	private JLabel vendorLabel = new JLabel("<html><b>Vendor:</b></html>");
	private JLabel javaVersionLabel = new JLabel("<html><b>Java Version:</b></html>");
	private JLabel osVersionLabel = new JLabel("<html><b>OS Version:</b></html>");

	private JLabel appDescLabel = new JLabel(
			"A slim bookseller's tool. Includes ftp4j by Sauron Software licenced under LGPL.");
	private JLabel appVersionLabel = new JLabel(Constants.version);
	private JLabel appVendorLabel = new JLabel("Bridgetown Books");
	private JLabel appJavaVersionLabel = new JLabel(System.getProperty("java.version"));
	private JLabel appOSVersionLabel = new JLabel(
			System.getProperty("os.name") + " " + System.getProperty("os.version"));
	private static AboutBox instance = null;

	public static AboutBox getInstance()
	{
		if (instance == null)
		{
			instance = new AboutBox();
		}
		return instance;
	}

	public AboutBox()
	{
		initComponents();
		Constants.windowNames.put(this, "About");
		Constants.windowIcons.put(this, "about.png");
	}

	private void initComponents()
	{
		imageLabel.setIcon(new ImageIcon(getClass().getClassLoader().getResource("resources/about.png")));

		appTitleLabel.setFont(Cato.catoFont);
		versionLabel.setFont(Cato.catoFont);
		appVersionLabel.setFont(Cato.catoFont);
		vendorLabel.setFont(Cato.catoFont);
		appVendorLabel.setFont(Cato.catoFont);
		appDescLabel.setFont(Cato.catoFont);
		imageLabel.setFont(Cato.catoFont);
		osVersionLabel.setFont(Cato.catoFont);
		appOSVersionLabel.setFont(Cato.catoFont);
		javaVersionLabel.setFont(Cato.catoFont);
		appJavaVersionLabel.setFont(Cato.catoFont);

		setLayout(new ColumnLayout(10, 5));
		add("w", imageLabel);
		add("x", appTitleLabel);
		add("wx", appDescLabel);
		add("x", versionLabel);
		add("wx", appVersionLabel);
		add("x", vendorLabel);
		add("wx", appVendorLabel);
		add("x", osVersionLabel);
		add("wx", appOSVersionLabel);
		add("x", javaVersionLabel);
		add("wx", appJavaVersionLabel);
	}
}
