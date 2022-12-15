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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class WantEditView extends GenericEditView
{
	private JCheckBox dustJacketCheckBox = new JCheckBox("Dust Jacket?");
	private JCheckBox firstEditionCheckBox = new JCheckBox("First Edition?");
	private JCheckBox signedCheckBox = new JCheckBox("Signed by Author?");
	private JCheckBox updateCheckBox = new JCheckBox("Include in Update?");
	private GenericComboBox authorComboBox = new GenericComboBox();
	private JComboBox bindingComboBox = new JComboBox();
	public  GenericComboBox clientComboBox = new GenericComboBox();
	private GenericComboBox publisherComboBox = new GenericComboBox();
	private JComboBox statusComboBox = new JComboBox();
	private JLabel addedDateLabel = new JLabel("addedDateLabel", SwingConstants.CENTER);
	private JLabel addedLabel = new JLabel("Added:", SwingConstants.CENTER);
	private JLabel authorLabel = new JLabel("Author:", SwingConstants.RIGHT);
	private JLabel bindingLabel = new JLabel("Binding:", SwingConstants.RIGHT);
	private JLabel changedDateLabel = new JLabel("changedDateLabel", SwingConstants.CENTER);
	private JLabel clientLabel = new JLabel("• Client:", SwingConstants.RIGHT);
	private JLabel commentsLabel = new JLabel("Comments:", SwingConstants.RIGHT);
	private JLabel djLabel = new JLabel("", SwingConstants.RIGHT);
	private JLabel firstEditionLabel = new JLabel("", SwingConstants.RIGHT);
	private JLabel includeLabel = new JLabel("", SwingConstants.RIGHT);
	private JLabel keywordsLabel = new JLabel("Keywords:", SwingConstants.RIGHT);
	private JLabel priceLabel = new JLabel("Price Max:", SwingConstants.RIGHT);
	private JLabel priceMinLabel = new JLabel("Price Min:", SwingConstants.RIGHT);
	private JLabel publisherLabel = new JLabel("Publisher:", SwingConstants.RIGHT);
	// private JLabel recordNoLabel = new JLabel("Record #:", SwingConstants.RIGHT);
	private JLabel signedLabel = new JLabel("", SwingConstants.RIGHT);
	private JLabel statusLabel = new JLabel("Status:", SwingConstants.RIGHT);
	private JLabel titleLabel = new JLabel("Title:", SwingConstants.RIGHT);
	private JScrollPane wantsEditScrollPane = new JScrollPane();
	private JTextArea commentsTextArea = new GenericTextArea(true);
	private JTextField keywordsTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField priceMaxTextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField priceMinTextField = new GenericTextField(Constants.FIELD_ALL, false);
	private JTextField recordNoTextField = new GenericTextField(Constants.FIELD_NUMBER, false);
	private JTextField titleTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private GenericEditButtonPanel buttonPanel = new GenericEditButtonPanel(this);

	private WantEntry wantsEntry;

	private static WantEditView instance = null;

	public static WantEditView getInstance()
	{
		if (instance == null)
		{
			instance = new WantEditView();
		}
		return instance;
	}

	public WantEditView()
	{
		initComponents();
		Constants.windowNames.put(this, "Want Edit");
		Constants.windowIcons.put(this, "want.png");
	}

	public void setupEntry(int id)
	{
		clientListBuild();
		authorComboBox.setModel(DBFunctions.getInstance().generateList("AUT", Constants.AUTHOR_NAME));
		publisherComboBox.setModel(DBFunctions.getInstance().generateList("PUB", Constants.PUBLISHER_PUBLISHER));
		// if id <> -1 retrieve data and enter into fields
		if (id == -1)
		{
			wantsEntry = new WantEntry();
			wantsEntry.addedDate = Constants.getPrettyDateTime();
		}
		else
		{
			wantsEntry = new WantEntry(id);
			wantsEntry.changedDate = Constants.getPrettyDateTime();
		}
		recordNoTextField.setText(wantsEntry.id);
		clientComboBox.setText(wantsEntry.client);
		authorComboBox.setText(wantsEntry.author);
		titleTextField.setText(wantsEntry.title);
		publisherComboBox.setText(wantsEntry.publisher);
		keywordsTextField.setText(wantsEntry.keywords);
		statusComboBox.setSelectedItem(wantsEntry.status);
		updateCheckBox.setSelected(Boolean.parseBoolean(wantsEntry.includeInUpdate));
		firstEditionCheckBox.setSelected(Boolean.parseBoolean(wantsEntry.firstEdition));
		dustJacketCheckBox.setSelected(Boolean.parseBoolean(wantsEntry.dustJacket));
		signedCheckBox.setSelected(Boolean.parseBoolean(wantsEntry.signed));
		bindingComboBox.setSelectedItem(wantsEntry.binding);
		priceMinTextField.setText(wantsEntry.priceMin);
		priceMaxTextField.setText(wantsEntry.priceMax);
		commentsTextArea.setText(wantsEntry.comments);
		addedDateLabel.setText(wantsEntry.addedDate);
		changedDateLabel.setText(wantsEntry.changedDate);
		addedLabel.setText("Added: " + addedDateLabel.getText() + " || " + "Changed: " + changedDateLabel.getText());
	}

	public void clientListBuild()
	{
		clientComboBox.setModel(DBFunctions.getInstance().generateList("CLI", Constants.CLIENT_NAME));
	}

	public boolean saveEntry()
	{
		if (priceMinTextField.getText().equals(""))
		{
			priceMinTextField.setText("0.00");
		}
		else
		{
			priceMinTextField.setText(Constants.twoPlaces.format(Double.parseDouble(priceMinTextField.getText())));
		}
		if (priceMaxTextField.getText().equals(""))
		{
			priceMaxTextField.setText("0.00");
		}
		else
		{
			priceMaxTextField.setText(Constants.twoPlaces.format(Double.parseDouble(priceMaxTextField.getText())));
		}
		wantsEntry.id = recordNoTextField.getText();
		wantsEntry.client = clientComboBox.getText();
		wantsEntry.author = authorComboBox.getText();
		wantsEntry.title = titleTextField.getText();
		wantsEntry.publisher = publisherComboBox.getText();
		wantsEntry.keywords = keywordsTextField.getText();
		wantsEntry.status = (String) statusComboBox.getSelectedItem();
		wantsEntry.includeInUpdate = Boolean.toString(updateCheckBox.isSelected());
		wantsEntry.firstEdition = Boolean.toString(firstEditionCheckBox.isSelected());
		wantsEntry.dustJacket = Boolean.toString(dustJacketCheckBox.isSelected());
		wantsEntry.signed = Boolean.toString(signedCheckBox.isSelected());
		wantsEntry.binding = (String) bindingComboBox.getSelectedItem();
		wantsEntry.priceMin = priceMinTextField.getText();
		wantsEntry.priceMax = priceMaxTextField.getText();
		wantsEntry.comments = commentsTextArea.getText();
		wantsEntry.addedDate = addedDateLabel.getText();
		wantsEntry.changedDate = changedDateLabel.getText();
		wantsEntry.saveWantsEntry();
		return true;
	}

	public void listBuild()
	{}

	public void viewClient()
	{
		int clientID = -1;
		String client = clientComboBox.getText();
		if (!client.equals(""))
		{
			clientID = DBFunctions.getInstance().selectOne("CLI", Constants.CLIENT_NAME, client);
		}
		ClientEditView.getInstance().setupEntry(clientID);
		ClientEditView.getInstance().caller = "wants";
		BooksView.getInstance().tabbedPane.openTab(ClientEditView.getInstance());
	}

	protected void initComponents()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent e)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						clientComboBox.requestFocusInWindow();
					}
				});
			}
		});

		ActionListener al = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{}
		};

		recordNoTextField.setEditable(false);

		clientComboBox.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() > 1) viewClient();
			}
		});
		authorComboBox.setPreferredSize(new Dimension(120, Cato.compHeight));

		statusComboBox.setModel(new DefaultComboBoxModel(new String[] { "Pending", "Found" }));
		bindingComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Hardcover", "Softcover" }));

		Constants.setTabs(commentsTextArea);
		wantsEditScrollPane.setViewportView(commentsTextArea);

		JPanel leftPane = new JPanel(new ColumnLayout(0, 0));
		leftPane.add("rx", clientLabel);
		leftPane.add("hwx", clientComboBox);
		leftPane.add("rx", authorLabel);
		leftPane.add("hwx", authorComboBox);
		leftPane.add("rx", titleLabel);
		leftPane.add("hwx", titleTextField);
		leftPane.add("rx", publisherLabel);
		leftPane.add("hwx", publisherComboBox);
		leftPane.add("rx", keywordsLabel);
		leftPane.add("hwx", keywordsTextField);
		leftPane.add("rx", statusLabel);
		leftPane.add("hwx", statusComboBox);

		// make them stand tall
		updateCheckBox.setPreferredSize(new Dimension(75, Cato.compHeight));
		updateCheckBox.addActionListener(al);
		firstEditionCheckBox.setPreferredSize(new Dimension(75, Cato.compHeight));
		firstEditionCheckBox.addActionListener(al);
		dustJacketCheckBox.setPreferredSize(new Dimension(75, Cato.compHeight));
		dustJacketCheckBox.addActionListener(al);
		signedCheckBox.setPreferredSize(new Dimension(75, Cato.compHeight));
		signedCheckBox.addActionListener(al);

		JPanel rightPane = new JPanel(new ColumnLayout(0, 0));
		rightPane.add("rx", includeLabel);
		rightPane.add("hwx", updateCheckBox);
		rightPane.add("rx", firstEditionLabel);
		rightPane.add("hwx", firstEditionCheckBox);
		rightPane.add("rx", djLabel);
		rightPane.add("hwx", dustJacketCheckBox);
		rightPane.add("rx", signedLabel);
		rightPane.add("hwx", signedCheckBox);
		rightPane.add("rx", bindingLabel);
		rightPane.add("hwx", bindingComboBox);
		rightPane.add("rx", priceMinLabel);
		rightPane.add("hwx", priceMinTextField);
		rightPane.add("rx", priceLabel);
		rightPane.add("hwx", priceMaxTextField);

		JPanel bottomPanel = new JPanel(new ColumnLayout(0, 0));
		bottomPanel.add("thx", leftPane);
		bottomPanel.add("hwx", rightPane);

		setLayout(new ColumnLayout(1, 1));
		add("w", buttonPanel);
		add("hxw", bottomPanel);
		add("wx", commentsLabel);
		add("wxhv", wantsEditScrollPane);
		add("cwx", addedLabel);
	}
}
