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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ShowImageView extends JPanel
	{
	private ArrayList<String> imageList = new ArrayList<String>();
	private int imageIndex;

	private JLabel imageLabel = new JLabel();
	private JButton backAllButton = new GenericButton("2leftarrow.png", "Back to first image.", null);
	private JButton backOneButton = new GenericButton("1leftarrow.png", "Back one image.", null);
	private JButton forwardAllButton = new GenericButton("2rightarrow.png", "Forward to last image.", null);
	private JButton forwardOneButton = new GenericButton("1rightarrow.png", "Forward one image.", null);
	private JLabel  navBarLabel = new JLabel();
	String localImage = "";

	private static ShowImageView instance = null;

	public static ShowImageView getInstance()
	{
		if (instance == null)
		{
			instance = new ShowImageView();
		}
		return instance;
	}

	public ShowImageView()
		{
		initComponents();
		Constants.windowNames.put(this, "Images");
		Constants.windowIcons.put(this, "image.png");
		localImage = SettingsView.getInstance().get("localImage");
		if (localImage.equals("")) localImage = Constants.IMAGES_DIR;
		localImage += Constants.ps;
		}

	public void showImages(String bookID)
		{
		File dir = new File(localImage);
		String[] children = dir.list();
		imageList.clear();
		for (String s : children)
			{
			if (s.startsWith(bookID.trim()))
				{
				imageList.add(s);
				}
			}
		if (imageList.size() > 0)
			{
			openImagePanel();
			}
		}

	private void openImagePanel()
	{
		Collections.sort(imageList);
		imageIndex = 0;
		showAnImage();
		BooksView.getInstance().tabbedPane.openTab(this);
	}

	public void showAllImages()
		{
		File dir = new File(localImage);
		String[] children = dir.list();
		imageList.clear();
		for (String s : children)
			{
			if ((s.endsWith(".jpeg")) || (s.endsWith(".gif")) || (s.endsWith(".jpg")) || (s.endsWith(".png")))
				{
				imageList.add(s);
				}
			}
		if (imageList.size() > 0)
			{
			openImagePanel();
			}
		}

	public void closeShowImageView()
		{
		BooksView.getInstance().tabbedPane.closeTab(this);
		}

	private void showAnImage()
		{
		navBarLabel.setText((imageIndex + 1) + " of " + imageList.size() + " :: " + imageList.get(imageIndex));
		ImageIcon ii = new ImageIcon(localImage + Constants.ps + imageList.get(imageIndex));
		imageLabel.setIcon(ii);
		}

	private void initComponents()
		{
		backAllButton.addActionListener(new ActionListener()
			{
			public void actionPerformed(ActionEvent evt)
				{
				imageIndex = 0;
				showAnImage();
				}
			});

		backOneButton.addActionListener(new ActionListener()
			{
			public void actionPerformed(ActionEvent evt)
				{
				imageIndex--;
				if (imageIndex < 0) imageIndex = 0;
				showAnImage();
				}
			});

		forwardAllButton.addActionListener(new ActionListener()
			{
			public void actionPerformed(ActionEvent evt)
				{
				imageIndex = imageList.size() - 1;
				showAnImage();
				}
			});

		forwardOneButton.addActionListener(new ActionListener()
			{
			public void actionPerformed(ActionEvent evt)
				{
				imageIndex++;
				if (imageIndex > imageList.size() - 1) imageIndex = imageList.size() - 1;
				showAnImage();
				}
			});
		navBarLabel.setFont(Cato.catoFont);
		navBarLabel.setForeground(Cato.hiliteColor);
		navBarLabel.setHorizontalAlignment(SwingConstants.CENTER);
		navBarLabel.setPreferredSize(new Dimension(250, 25));

		imageLabel.setHorizontalAlignment(JLabel.CENTER);

		JPanel buttonPanel = new JPanel(new ColumnLayout(0,0));
		buttonPanel.add("x", backAllButton);
		buttonPanel.add("x", backOneButton);
		buttonPanel.add("x", navBarLabel);
		buttonPanel.add("x", forwardOneButton);
		buttonPanel.add("xw", forwardAllButton);

		setLayout(new ColumnLayout(1,2));
		add("hvxw", imageLabel);
		add("cwx", buttonPanel);
		}
	}
