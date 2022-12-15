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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class PublisherEditView extends GenericEditView
{
	PublisherEntry publisherEntry;
	private JLabel endLabel = new JLabel("End Date:", SwingConstants.RIGHT);
	private JLabel firstEditionLabel = new JLabel("First Edition:", SwingConstants.RIGHT);
	private JLabel placeLabel = new JLabel("Place:", SwingConstants.RIGHT);
	private JLabel publisherLabel = new JLabel("Publisher:", SwingConstants.RIGHT);
	private JLabel startLabel = new JLabel("Start Date:", SwingConstants.RIGHT);
	private JLabel notesLabel = new JLabel("Notes:", SwingConstants.RIGHT);
	private JTextArea notesTextArea = new GenericTextArea(true);
	private JTextField endDateTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField firstEditionTextField = new GenericTextField(Constants.FIELD_ALL, true);
	public JTextField placeTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JTextField startDateTextField = new GenericTextField(Constants.FIELD_ALL, true);
	public JTextField publisherTextField = new GenericTextField(Constants.FIELD_ALL, true);
	private JScrollPane notesScrollPane = new JScrollPane(notesTextArea);
	private GenericEditButtonPanel buttonPanel = new GenericEditButtonPanel(this);

	private static PublisherEditView instance = null;

	public static PublisherEditView getInstance()
	{
		if (instance == null)
		{
			instance = new PublisherEditView();
		}
		return instance;
	}

	public PublisherEditView()
	{
		initComponents();
		Constants.windowNames.put(this, "Publisher Edit");
		Constants.windowIcons.put(this, "publisher.png");
	}

	public void setupEntry(int id)
	{
		// if id <> -1 retrieve data and enter into fields
		if (id == -1)
		{
			publisherEntry = new PublisherEntry();
		}
		else
		{
			publisherEntry = new PublisherEntry(id);
		}
		publisherTextField.setText(publisherEntry.publisher);
		placeTextField.setText(publisherEntry.place);
		startDateTextField.setText(publisherEntry.start);
		endDateTextField.setText(publisherEntry.end);
		firstEditionTextField.setText(publisherEntry.firstEdition);
		notesTextArea.setText(publisherEntry.notes);
		notesTextArea.setCaretPosition(0);
	}

	public boolean saveEntry()
	{
		publisherEntry.publisher = publisherTextField.getText();
		publisherEntry.place = placeTextField.getText();
		publisherEntry.start = startDateTextField.getText();
		publisherEntry.end = endDateTextField.getText();
		publisherEntry.firstEdition = firstEditionTextField.getText();
		publisherEntry.notes = notesTextArea.getText();
		if (publisherTextField.getText().trim().equals(""))
		{
			JOptionPane.showMessageDialog(BooksView.getInstance(), "Publisher name must not be blank.");
			return false;
		}
		publisherEntry.savePublisherEntry();
		BookEditView.getInstance().updatePublishers();
		return true;
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
						publisherTextField.requestFocusInWindow();
					}
				});
			}
		});

		Constants.setTabs(notesTextArea);
		notesTextArea.setFont(Cato.catoFont);

		setLayout(new ColumnLayout(1, 1));
		add("w", buttonPanel);
		add("rx", publisherLabel);
		add("hwx", publisherTextField);
		add("rx", placeLabel);
		add("hwx", placeTextField);
		add("rx", startLabel);
		add("hwx", startDateTextField);
		add("rx", endLabel);
		add("hwx", endDateTextField);
		add("rx", firstEditionLabel);
		add("hwx", firstEditionTextField);
		add("rtx", notesLabel);
		add("hvwx", notesScrollPane);
	}
}
