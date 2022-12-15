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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

public class BooksView extends JFrame
{
	private static BooksView instance = null;
	public ClosableTabbedPane tabbedPane = new ClosableTabbedPane();

	private JMenuBar menuBar = new JMenuBar();

	private GenericMenu fileMenu = new GenericMenu(" File ");
	private GenericMenuItem newMenuItem = new GenericMenuItem("New...", KeyEvent.VK_N, false, "dbnew.png");
	private GenericMenuItem openMenuItem = new GenericMenuItem("Open...", KeyEvent.VK_O, false, "dbopen.png");
	private GenericMenuItem closeMenuItem = new GenericMenuItem("Close", KeyEvent.VK_E, false, "dbclose.png");
	private GenericMenuItem backupMenuItem = new GenericMenuItem("Backup", KeyEvent.VK_B, false, "backup.png");
	private GenericMenuItem restoreMenuItem = new GenericMenuItem("Restore...", KeyEvent.VK_R, false, "restore.png");
	private GenericMenuItem renameMenuItem = new GenericMenuItem("Rename", KeyEvent.VK_M, false, "rename.png");
	private GenericMenuItem compactMenuItem = new GenericMenuItem("Compact...", KeyEvent.VK_X, true, "zip.png");
	private GenericMenuItem exitMenuItem = new GenericMenuItem("Quit", KeyEvent.VK_Q, false, "exit.png");

	private GenericMenu windowsMenu = new GenericMenu(" Windows ");
	private GenericMenuItem booksMenuItem = new GenericMenuItem("Books", KeyEvent.VK_B, true, "book.png");
	private GenericMenuItem wantsMenuItem = new GenericMenuItem("Wants", KeyEvent.VK_W, true, "want.png");
	private GenericMenuItem clientsMenuItem = new GenericMenuItem("Clients", KeyEvent.VK_C, true, "client.png");
	private GenericMenuItem invoicesMenuItem = new GenericMenuItem("Invoices", KeyEvent.VK_I, true, "invoice.png");
	private GenericMenuItem salesMenuItem = new GenericMenuItem("Sales", KeyEvent.VK_S, true, "sell.png");
	private GenericMenuItem ftpMenuItem = new GenericMenuItem("FTP", KeyEvent.VK_F, true, "ftp.png");
	private GenericMenuItem notepadMenuItem = new GenericMenuItem("Notepad", KeyEvent.VK_N, true, "note.png");

	private GenericMenuItem authorMenuItem = new GenericMenuItem("Authors/Illustrators", KeyEvent.VK_A, true,
			"author.png");
	private GenericMenuItem dataValidationMenuItem = new GenericMenuItem("Data Validation", KeyEvent.VK_D, true,
			"val.png");
	private GenericMenuItem publisherMenuItem = new GenericMenuItem("Publishers", KeyEvent.VK_P, true, "publisher.png");
	private GenericMenuItem macrossMenuItem = new GenericMenuItem("Macros", KeyEvent.VK_T, true, "macro.png");

	private GenericMenu langMenu = new GenericMenu(" Language ");
	private JRadioButtonMenuItem englishMenuItem = new JRadioButtonMenuItem("English");
	private JRadioButtonMenuItem germanMenuItem = new JRadioButtonMenuItem("Deutsch");
	private JRadioButtonMenuItem frenchMenuItem = new JRadioButtonMenuItem("Français");
	private JRadioButtonMenuItem italianMenuItem = new JRadioButtonMenuItem("Italiano");
	private JRadioButtonMenuItem polishMenuItem = new JRadioButtonMenuItem("Polska");
	private JRadioButtonMenuItem dutchMenuItem = new JRadioButtonMenuItem("Nederlands");
	private JRadioButtonMenuItem spanishMenuItem = new JRadioButtonMenuItem("Español");
	public JCheckBoxMenuItem spellCheckMenuItem = new JCheckBoxMenuItem("SpellChecker");
	public ButtonGroup langButtonGroup = new ButtonGroup();

	private GenericMenu utilityMenu = new GenericMenu(" Utilities ");
	private GenericMenuItem saveMenuItem = new GenericMenuItem("Save", KeyEvent.VK_S, false, "save.png");
	private GenericMenuItem cancelMenuItem = new GenericMenuItem("Cancel", KeyEvent.VK_W, false, "cancel.png");
	private GenericMenuItem cutMenuItem = new GenericMenuItem("Cut", KeyEvent.VK_X, false, "cut.png");
	private GenericMenuItem copyMenuItem = new GenericMenuItem("Copy", KeyEvent.VK_C, false, "copy.png");
	private GenericMenuItem pasteMenuItem = new GenericMenuItem("Paste", KeyEvent.VK_V, false, "paste.png");
	private GenericMenuItem selectAllMenuItem = new GenericMenuItem("Select All", KeyEvent.VK_A, false,
			"selectall.png");
	private GenericMenuItem printMenuItem = new GenericMenuItem("Print", KeyEvent.VK_P, false, "print.png");

	private GenericMenu helpMenu = new GenericMenu(" Help ");
	private GenericMenuItem aboutMenuItem = new GenericMenuItem("About...", KeyEvent.VK_F1, false, "about.png");
	private GenericMenuItem manageFilesItem = new GenericMenuItem("Manager", KeyEvent.VK_M, false, "manager.png");
	private GenericMenuItem showImagesItem = new GenericMenuItem("Images", KeyEvent.VK_I, false, "image.png");
	private GenericMenuItem settingsMenuItem = new GenericMenuItem("Settings", KeyEvent.VK_COMMA, false,
			"settings.png");
	private GenericMenuItem helpMenuItem = new GenericMenuItem("Help", KeyEvent.VK_H, false, "help.png");

	Timer timer = new Timer(900000, new ActionListener() // 900000 = 15 min
	{
		public void actionPerformed(ActionEvent evt)
		{
			DBFunctions.getInstance().saveDatabase(SettingsView.getInstance().get("dbName") + ".database");
			timer.restart();
		}
	});

	public static BooksView getInstance()
	{
		if (instance == null)
		{
			instance = new BooksView();
		}
		return instance;
	}

	public BooksView()
	{
		// Another Apple bit to allow full screen to happen
		getRootPane().putClientProperty( "apple.awt.fullscreenable", true );
		initComponents();
		enableEnvironment(false);
		timer.start();
	}

	/**
	 * *******************************************************************************
	 */
	// Window Handlers
	/**
	 * *******************************************************************************
	 */
	public void showNewDBDialog(String toDo)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new ColumnLayout(5, 5));
		JLabel dbNameLabel = new JLabel("New Database Name:");
		dbNameLabel.setFont(Cato.catoFont);
		JTextField dbNameTextField = new GenericTextField(Constants.FIELD_FILENAME, false);
		dbNameTextField.setFont(Cato.catoFont);
		panel.add("wx", dbNameLabel);
		panel.add("wx", dbNameTextField);
		String[] options = { "OK", "Cancel" };
		int ret = JOptionPane.showOptionDialog(BooksView.getInstance(), panel, toDo, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, null);
		String newDbName = dbNameTextField.getText().trim();
		if ((ret == JOptionPane.OK_OPTION) && (newDbName.length() > 0))
		{
			if (new File(Constants.SYSTEM_DIR + Constants.ps + newDbName + ".database").exists())
			{
				JOptionPane.showMessageDialog(BooksView.getInstance(), "Database name already exists.");
				return;
			}
			else
			{
				if (toDo.startsWith("Create"))
				{
					DBFunctions.getInstance().makeNewDB(newDbName);
					openDB();
					tabbedPane.openTab(SettingsView.getInstance());
				}
				else
				{
					SettingsView.getInstance().set("dbName", newDbName);
					DBFunctions.getInstance().saveDatabase(newDbName + ".database");
				}
			}
			setTitle("Cato the Younger :: " + newDbName);
		}
	}

	public void openDB()
	{
		String checkLang = SettingsView.getInstance().get("checkLang");
		if (checkLang.equals(""))
		{
			checkLang = "en";
		}

		if (checkLang.equals("en"))
		{
			englishMenuItem.setSelected(true);
		}
		else if (checkLang.equals("de"))
		{
			germanMenuItem.setSelected(true);
		}
		else if (checkLang.equals("fr"))
		{
			frenchMenuItem.setSelected(true);
		}
		else if (checkLang.equals("it"))
		{
			italianMenuItem.setSelected(true);
		}
		else if (checkLang.equals("es"))
		{
			spanishMenuItem.setSelected(true);
		}
		else if (checkLang.equals("nl"))
		{
			dutchMenuItem.setSelected(true);
		}
		else if (checkLang.equals("pl"))
		{
			polishMenuItem.setSelected(true);
		}
		loadDictionary();
		tabbedPane.openTab(NotepadView.getInstance());
		NotepadView.getInstance().showReadMe("Read Me");
		tabbedPane.openTab(BookListView.getInstance());
		enableEnvironment(true);
		setTitle("Cato the Younger :: " + SettingsView.getInstance().get("dbName"));
		Constants.writeBlog(SettingsView.getInstance().get("dbName") + " opened.");
		ToolTipManager.sharedInstance().setEnabled(SettingsView.getInstance().get("showToolTips").equalsIgnoreCase("true"));
	}

	public void loadDictionary()
	{

		SpellCheck.getInstance().loadDictionary(langButtonGroup.getSelection().getActionCommand());
	}

	public void closeDatabase(boolean exit)
	{
		if (!SettingsView.getInstance().get("dbName").equals(""))
		{
			tabbedPane.closeAllTabsWithSave();
			if ((tabbedPane.getTabCount() == 1) && (tabbedPane.isTabOpen(WelcomePanel.getInstance())))
			{
				enableEnvironment(false);
				final WaitBox frame = new WaitBox();
				class CloseDb extends SwingWorker
				{
					public Object doInBackground()
					{
						DBFunctions.getInstance().saveDatabase(SettingsView.getInstance().get("dbName") + ".database");
						DBFunctions.getInstance().unloadDatabase();
						return true;
					}

					public void done()
					{
						frame.dispose();
						setTitle("Cato the Younger");
						if (exit)
						{
							Constants.writeBlog("Program shut down.");
							System.exit(0);
						}
					}
				};
				(new CloseDb()).execute();

			}
		}
		else if (exit)
		{
			Constants.writeBlog("Program shut down.");
			System.exit(0);
		}
	}

	public void enableEnvironment(boolean on)
	{
		closeMenuItem.setEnabled(on);
		backupMenuItem.setEnabled(on);
		restoreMenuItem.setEnabled(!on);
		compactMenuItem.setEnabled(!on);
		renameMenuItem.setEnabled(on);
		newMenuItem.setEnabled(!on);
		openMenuItem.setEnabled(!on);
		windowsMenu.setEnabled(on);
		langMenu.setEnabled(on);
		utilityMenu.setEnabled(on);
		showImagesItem.setEnabled(on);
		settingsMenuItem.setEnabled(on);
	}

	/***************************************************************************
	 * // Exit Here /
	 **************************************************************************/
	public void exitBooks()
	{
		closeDatabase(true);
	}

	private void initComponents()
	{
		tabbedPane.setFocusable(false);
		tabbedPane.setFont(Cato.catoFont);
		tabbedPane.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				tabbedPane.moveTabToTopOfStack();
			}
		});

		newMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				showNewDBDialog("Create New Database");
			}
		});
		fileMenu.add(newMenuItem);

		openMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				JFileChooser chooser = new JFileChooser(Constants.SYSTEM_DIR + Constants.ps);
				Constants.setFileChooserFont(chooser.getComponents());
				FileFilter filter = new FileNameExtensionFilter("Database", "database");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(BooksView.getInstance());
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					final WaitBox frame = new WaitBox();
					class LoadDb extends SwingWorker
					{
						public Object doInBackground()
						{
							DBFunctions.getInstance().loadDatabase(chooser.getSelectedFile());
							return true;
						}

						public void done()
						{
							openDB();
							frame.dispose();
						}
					};
					(new LoadDb()).execute();
				}
			}
		});
		fileMenu.add(openMenuItem);

		closeMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				closeDatabase(false);
			}
		});
		fileMenu.add(closeMenuItem);

		backupMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				final WaitBox frame = new WaitBox();
				class BackUp extends SwingWorker
				{
					public Object doInBackground()
					{
						DBFunctions.getInstance().backup();
						return true;
					}

					public void done()
					{
						frame.dispose();
					}
				};
				(new BackUp()).execute();
			}
		});
		fileMenu.add(backupMenuItem);

		restoreMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				JFileChooser chooser = new JFileChooser(Constants.SYSTEM_DIR + Constants.ps);
				Constants.setFileChooserFont(chooser.getComponents());
				FileFilter filter = new FileNameExtensionFilter("Backup", "backup");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(BooksView.getInstance());
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					final WaitBox frame = new WaitBox();
					class Restore extends SwingWorker
					{
						public Object doInBackground()
						{
							String temp = chooser.getSelectedFile().getName();
							String[] t = temp.split("_");
							SettingsView.getInstance().set("dbName", t[0]);
							DBFunctions.getInstance().loadDatabase(temp);
							DBFunctions.getInstance().saveDatabase(temp);
							BooksView.getInstance().openDB();
							return true;
						}

						public void done()
						{
							frame.dispose();
						}
					};
					(new Restore()).execute();
				}
			}
		});
		fileMenu.add(restoreMenuItem);

		compactMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				JFileChooser chooser = new JFileChooser(Constants.SYSTEM_DIR + Constants.ps);
				Constants.setFileChooserFont(chooser.getComponents());
				FileFilter filter = new FileNameExtensionFilter("Compact", "database");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(BooksView.getInstance());
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					final WaitBox frame = new WaitBox();
					class Compact extends SwingWorker
					{
						public Object doInBackground()
						{
							File temp = chooser.getSelectedFile();
							DBFunctions.getInstance().compactDatabase(temp);
							return true;
						}

						public void done()
						{
							frame.dispose();
						}
					};
					(new Compact()).execute();
				}
			}
		});
		fileMenu.add(compactMenuItem);

		renameMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				showNewDBDialog("Rename Database");
			}
		});
		fileMenu.add(renameMenuItem);

		exitMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				exitBooks();
			}
		});
		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);

		booksMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tabbedPane.openTab(BookListView.getInstance());
			}
		});
		windowsMenu.add(booksMenuItem);

		clientsMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tabbedPane.openTab(ClientListView.getInstance());
			}
		});
		windowsMenu.add(clientsMenuItem);

		ftpMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tabbedPane.openTab(FTPListView.getInstance());
			}
		});
		windowsMenu.add(ftpMenuItem);

		invoicesMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tabbedPane.openTab(InvoiceListView.getInstance());
			}
		});
		windowsMenu.add(invoicesMenuItem);

		salesMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tabbedPane.openTab(SaleListView.getInstance());
			}
		});
		windowsMenu.add(salesMenuItem);

		wantsMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tabbedPane.openTab(WantListView.getInstance());
			}
		});
		windowsMenu.add(wantsMenuItem);

		windowsMenu.addSeparator();

		authorMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tabbedPane.openTab(AuthorListView.getInstance());
			}
		});
		windowsMenu.add(authorMenuItem);

		dataValidationMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tabbedPane.openTab(DataValidationListView.getInstance());
			}
		});
		windowsMenu.add(dataValidationMenuItem);

		publisherMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tabbedPane.openTab(PublisherListView.getInstance());
			}
		});
		windowsMenu.add(publisherMenuItem);

		macrossMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tabbedPane.openTab(MacroListView.getInstance());
			}
		});
		windowsMenu.add(macrossMenuItem);
		windowsMenu.addSeparator();

		notepadMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tabbedPane.openTab(NotepadView.getInstance());
			}
		});
		windowsMenu.add(notepadMenuItem);

		menuBar.add(windowsMenu);

		spellCheckMenuItem.setSelected(true);
		spellCheckMenuItem.setFont(Cato.catoFont);
		spellCheckMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, Constants.modifierKey));

		ActionListener setLang = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				SettingsView.getInstance().set("checkLang", evt.getActionCommand());
				loadDictionary();
			}
		};
		englishMenuItem.setActionCommand("en");
		englishMenuItem.addActionListener(setLang);
		germanMenuItem.setActionCommand("de");
		germanMenuItem.addActionListener(setLang);
		frenchMenuItem.setActionCommand("fr");
		frenchMenuItem.addActionListener(setLang);
		italianMenuItem.setActionCommand("it");
		italianMenuItem.addActionListener(setLang);
		spanishMenuItem.setActionCommand("es");
		spanishMenuItem.addActionListener(setLang);
		dutchMenuItem.setActionCommand("nl");
		dutchMenuItem.addActionListener(setLang);
		polishMenuItem.setActionCommand("pl");
		polishMenuItem.addActionListener(setLang);

		langButtonGroup.add(englishMenuItem);
		langButtonGroup.add(germanMenuItem);
		langButtonGroup.add(frenchMenuItem);
		langButtonGroup.add(italianMenuItem);
		langButtonGroup.add(spanishMenuItem);
		langButtonGroup.add(dutchMenuItem);
		langButtonGroup.add(polishMenuItem);
		langButtonGroup.setSelected(englishMenuItem.getModel(), true);

		langMenu.add(englishMenuItem);
		langMenu.add(germanMenuItem);
		langMenu.add(spanishMenuItem);
		langMenu.add(frenchMenuItem);
		langMenu.add(italianMenuItem);
		langMenu.add(dutchMenuItem);
		langMenu.add(polishMenuItem);
		langMenu.addSeparator();
		langMenu.add(spellCheckMenuItem);
		menuBar.add(langMenu);

		saveMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				JPanel op = (JPanel) tabbedPane.getSelectedComponent();
				tabbedPane.closeTabWithSave(op);
			}
		});
		utilityMenu.add(saveMenuItem);

		cancelMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				JPanel op = (JPanel) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
				if (op instanceof GenericEditView)
				{
					((GenericEditView) op).cancelEntry();
				}
				else
				{
					tabbedPane.closeTab(op);
				}
			}
		});
		utilityMenu.add(cancelMenuItem);
		utilityMenu.addSeparator();
		cutMenuItem.addActionListener(new DefaultEditorKit.CutAction());
		utilityMenu.add(cutMenuItem);
		copyMenuItem.addActionListener(new DefaultEditorKit.CopyAction());
		utilityMenu.add(copyMenuItem);
		pasteMenuItem.addActionListener(new DefaultEditorKit.PasteAction());
		utilityMenu.add(pasteMenuItem);
		selectAllMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{

				Component c = BooksView.getInstance().getFocusOwner();
				if (c instanceof JTextComponent)
				{
					((JTextComponent) c).selectAll();
				}
			}
		});
		utilityMenu.add(selectAllMenuItem);

		printMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				JPanel op = (JPanel) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
				if (op instanceof GenericListView)
				{
					PrintTable.printTable(((GenericListView) op).listTable);
				}
				else
				{
					new PrintThis(op);
				}
			}
		});
		utilityMenu.add(printMenuItem);
		menuBar.add(utilityMenu);

		aboutMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tabbedPane.openTab(AboutBox.getInstance());
			}
		});
		helpMenu.add(aboutMenuItem);

		manageFilesItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tabbedPane.openTab(ManageFilesView.getInstance());
			}
		});
		helpMenu.add(manageFilesItem);

		showImagesItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				ShowImageView.getInstance().showImages("");
			}
		});
		helpMenu.add(showImagesItem);

		settingsMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				SettingsView.getInstance().populateFields();
				tabbedPane.openTab(SettingsView.getInstance());
			}
		});
		helpMenu.add(settingsMenuItem);

		helpMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				BooksView.getInstance().tabbedPane.openTab(HelpView.getInstance());
			}
		});
		helpMenu.add(helpMenuItem);

		menuBar.add(helpMenu);

		add(tabbedPane);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent ev)
			{
				exitBooks();
			}
		});
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setJMenuBar(menuBar);
		setTitle("Cato the Younger");
		setMinimumSize(new Dimension(800, 600));
		setSize(800, 600);
		setLocationRelativeTo(null);
		setIconImage(new ImageIcon(getClass().getResource("/resources/book.png")).getImage());
		setVisible(true);
	}
}
