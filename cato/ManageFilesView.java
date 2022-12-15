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
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManageFilesView extends JPanel
{
	// ***************** ACTIONS ***********************
	private Action copyAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			copyFile(currentTable);
		}
	};

	private Action renameAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			renameFile(currentTable);
		}
	};

	private Action pasteAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			pasteFile(currentTable);
		}
	};

	private Action cutAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			cutFile(currentTable);
		}
	};

	private Action deleteAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			deleteFile(currentTable);
		}
	};

	private Action zipAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			final WaitBox frame = new WaitBox();
			class Zipp extends SwingWorker
			{
				public Object doInBackground()
				{
					zipFile(currentTable);
					return true;
				}

				public void done()
				{
					frame.dispose();
				}

			};
			(new Zipp()).execute();
		}
	};

	private Action memoryAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			Long t = Runtime.getRuntime().totalMemory() / 1048576L;
			Long u = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L;
			Long f = Runtime.getRuntime().freeMemory() / 1048576L;
			JOptionPane.showMessageDialog(BooksView.getInstance(), "Total: " + Long.toString(t) + "Mb Used: "
					+ Long.toString(u) + "Mb Free: " + Long.toString(f) + "Mb");
			System.gc();
		}
	};

	private Action hideAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			hidden[1] = !hidden[1];
			hidden[2] = !hidden[2];
			if (hidden[1])
			{
				hideButton.setToolTipText("Show hidden files and directory.");
				hideButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("resources/show.png")));
			}
			else
			{
				hideButton.setToolTipText("Hide hidden files and directory.");
				hideButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("resources/hide.png")));
			}
			populateTable(1);
			populateTable(2);
		}
	};

	private Action makeDirAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			makeDir(currentTable);
		}
	};

	// ******************** BUTTONS ***********************
	private JButton copyButton = new GenericButton("copy.png", "Copy selected file.", copyAction);
	private JButton renameButton = new GenericButton("rename.png", "Rename selected file.", renameAction);
	private JButton pasteButton = new GenericButton("paste.png", "Paste selected file.", pasteAction);
	private JButton cutButton = new GenericButton("cut.png", "Cut selected file.", cutAction);
	private JButton deleteButton = new GenericButton("delete.png", "Delete selected file.", deleteAction);
	private JButton zipButton = new GenericButton("zip.png", "Zip selected file or directory.", zipAction);
	private JButton memoryButton = new GenericButton("memory.png", "Show current memory usage.", memoryAction);
	private JButton makeDirButton = new GenericButton("newdir.png", "Make new directory.", makeDirAction);
	private JButton hideButton = new GenericButton("show.png", "Show hidden files and directory.", hideAction);

	JTable[] tables = { null, null, null };
	String[] curdirs = { "", "", "" };
	JLabel[] messages = { null, new JLabel(), new JLabel() };
	boolean[] hidden = { false, true, true };
	File fileInQuestion;
	int currentTable = 1;

	private JLabel errorLabel = new JLabel("             File Manager            ");
	Color naturalColor;
	private JScrollPane manageFilesScrollPane1 = new JScrollPane(tables[1]);
	private JScrollPane manageFilesScrollPane2 = new JScrollPane(tables[2]);
	private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, manageFilesScrollPane1,
			manageFilesScrollPane2);

	private boolean cutting = false;

	private Object[] columnNames = { "Filename", "Size", "Modified Date" };

	private static ManageFilesView instance = null;

	public static ManageFilesView getInstance()
	{
		if (instance == null)
		{
			instance = new ManageFilesView();
		}
		return instance;
	}

	public ManageFilesView()
	{
		Constants.windowNames.put(this, "File Manager");
		Constants.windowIcons.put(this, "manager.png");
		naturalColor = errorLabel.getForeground();
		curdirs[1] = SettingsView.getInstance().get("ManageFiles.dir");
		if (curdirs[1].equals(""))
		{
			curdirs[1] = Constants.HOME_DIR;
		}
		curdirs[2] = SettingsView.getInstance().get("ManageFiles.dir");
		if (curdirs[2].equals(""))
		{
			curdirs[2] = Constants.HOME_DIR;
		}
		initComponents();
		populateTable(1);
		populateTable(2);
	}

	private void populateTable(int i)
	{
		((DefaultTableModel) (tables[i].getModel())).setRowCount(0);

		File dir = new File(curdirs[i]);
		String filename = "";
		String filesize = "";
		String filedate = "";
		int noOfFiles = 0;
		int noOfFolders = 0;

		File[] children = dir.listFiles();
		if (children != null)
		{
			for (File f : children)
			{
				if (!f.isHidden() || hidden[i] == false)
				{
					filename = f.getName();
					if (f.isFile())
					{
						noOfFiles++;
						Long l = f.length();
						filesize = l.toString();
					}
					else
					{
						filesize = "<DIR>";
						noOfFolders++;
					}
					DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					filedate = df.format(f.lastModified());
					((DefaultTableModel) (tables[i].getModel())).addRow(new Object[] { filename, filesize, filedate });
				}
			}
		}
		String pop = "Files: " + noOfFiles + "|Fldrs: " + noOfFolders + "|Dir: " + curdirs[i];
		messages[i].setText(pop);

		if (!curdirs[i].equals("/"))
		{
			((DefaultTableModel) tables[i].getModel()).insertRow(0, new Object[] { ".", "", "" });
		}
	}

	private void zipFile(int i)
	{
		int index = tables[i].getSelectedRow();
		if (index != -1)
		{
			fileInQuestion = new File(curdirs[i] + Constants.ps + tables[i].getValueAt(index, 0));
			if (fileInQuestion.isDirectory())
			{
				try
				{
					Zipper.zipDir(fileInQuestion);
					errorLabel.setForeground(naturalColor);
					errorLabel.setText("Directory zipped: " + fileInQuestion);
					populateTable(i);
				}
				catch (Exception ex)
				{
					errorLabel.setForeground(Color.RED);
					errorLabel.setText("Zip failed.");
				}
			}
			else if (fileInQuestion.toString().endsWith(".zip"))
			{
				if (Zipper.unzipFile(fileInQuestion))
				{
					errorLabel.setForeground(naturalColor);
					errorLabel.setText("Unzipped: " + fileInQuestion);
					populateTable(i);
				}
				else
				{
					errorLabel.setForeground(Color.RED);
					errorLabel.setText("Unzip failed.");
				}
			}
			else if (fileInQuestion.isFile())
			{
				try
				{
					Zipper.zipFile(fileInQuestion);
					errorLabel.setForeground(naturalColor);
					errorLabel.setText("File zipped: " + fileInQuestion);
					populateTable(i);
				}
				catch (Exception ex)
				{
					errorLabel.setForeground(Color.RED);
					errorLabel.setText("Zip failed.");
				}
			}
		}
	}

	private void copyFile(int i)
	{
		int index = tables[i].getSelectedRow();
		if (index != -1)
		{
			fileInQuestion = new File(curdirs[i] + Constants.ps + tables[i].getValueAt(index, 0));
		}
		errorLabel.setForeground(naturalColor);
		errorLabel.setText("Copying file: " + fileInQuestion);
		cutting = false;
	}

	private void makeDir(int i)
	{
		boolean success = false;
		String dirName = JOptionPane.showInputDialog(BooksView.getInstance(), "Name of the new Directory?");
		if ((dirName != null) && (dirName.length() > 0))
		{
			File f = new File(curdirs[i] + Constants.ps + dirName);
			success = f.mkdir();
		}
		if (success)
		{
			errorLabel.setForeground(naturalColor);
			errorLabel.setText(dirName + " successfully created.");
			Constants.writeBlog(dirName + " successfully created.");
			populateTable(i);
		}
		else
		{
			errorLabel.setForeground(Color.RED);
			errorLabel.setText(dirName + " failed creation.");
		}
		cutting = false;
		fileInQuestion = null;
	}

	private void deleteFile(int i)
	{
		boolean success = false;
		int index = tables[i].getSelectedRow();
		if (index != -1)
		{
			fileInQuestion = new File(curdirs[i] + Constants.ps + tables[i].getValueAt(index, 0));
			if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(BooksView.getInstance(),
					"Delete " + fileInQuestion + "?", "Deleting", JOptionPane.OK_CANCEL_OPTION))
			{
				success = fileInQuestion.delete();
			}
		}
		if (success)
		{
			errorLabel.setForeground(naturalColor);
			errorLabel.setText("File/Directory deleted: " + fileInQuestion);
			Constants.writeBlog("File/Directory deleted: " + fileInQuestion);
			populateTable(i);
		}
		else
		{
			errorLabel.setForeground(Color.RED);
			errorLabel.setText("Failed delete: " + fileInQuestion);
		}
		cutting = false;
		fileInQuestion = null;
	}

	private void cutFile(int i)
	{
		int index = tables[i].getSelectedRow();
		if (index != -1)
		{
			fileInQuestion = new File(curdirs[i] + Constants.ps + tables[i].getValueAt(index, 0));
		}
		errorLabel.setForeground(naturalColor);
		errorLabel.setText("Cutting file: " + fileInQuestion);
		cutting = true;
	}

	private void pasteFile(int i)
	{
		if (fileInQuestion == null)
		{
			errorLabel.setForeground(Color.RED);
			errorLabel.setText("No file on pasteboard.");
			return;
		}
		File nn = new File(curdirs[i] + Constants.ps + fileInQuestion.getName());
		try
		{
			if (fileInQuestion.toString().equals(nn.toString()))
			{
				errorLabel.setForeground(Color.RED);
				errorLabel.setText("Can't paste a file onto itself.");
				return;
			}
			if (nn.exists())
			{
				if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(BooksView.getInstance(),
						"File already exists.  OK to overwrite?", "Overwriting", JOptionPane.OK_CANCEL_OPTION))
					return;
			}
			InputStream in = new FileInputStream(fileInQuestion);
			OutputStream out = new FileOutputStream(nn);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			errorLabel.setForeground(naturalColor);
			errorLabel.setText("File pasted from: " + fileInQuestion + " to: " + nn);
			Constants.writeBlog("File pasted from: " + fileInQuestion + " to: " + nn);
			if (cutting)
			{
				fileInQuestion.delete();
				Constants.writeBlog("File/Directory deleted: " + fileInQuestion);
			}
			populateTable(1);
			populateTable(2);
		}
		catch (Exception ex)
		{
			errorLabel.setForeground(Color.RED);
			errorLabel.setText("File paste failed.");
		}
		fileInQuestion = null;
		cutting = false;
	}

	private void renameFile(int i)
	{
		int index = tables[i].getSelectedRow();
		if (index != -1)
		{
			fileInQuestion = new File(curdirs[i] + Constants.ps + tables[i].getValueAt(index, 0));
			String newName = JOptionPane.showInputDialog(BooksView.getInstance(), "New name?");
			if ((newName != null) && (newName.length() > 0))
			{
				File nn = new File(curdirs[i] + Constants.ps + newName);
				boolean success = fileInQuestion.renameTo(nn);
				if (success)
				{
					populateTable(i);
					errorLabel.setForeground(naturalColor);
					errorLabel.setText("File/Directory renamed from: " + fileInQuestion + " to: " + nn);
					Constants.writeBlog("File/Directory renamed from: " + fileInQuestion + " to: " + nn);
				}
				else
				{
					errorLabel.setForeground(Color.RED);
					errorLabel.setText("File/Directory rename failed.");
				}
			}
		}
		cutting = false;
		fileInQuestion = null;
	}

	private void changeDirectory(int i)
	{
		int index = tables[i].getSelectedRow();
		if (index != -1)
		{
			File f = new File(curdirs[i] + Constants.ps + (String) tables[i].getValueAt(index, 0));
			if (f.isDirectory() && !tables[i].getValueAt(index, 0).equals("."))
			{
				curdirs[i] = curdirs[i] + Constants.ps + (String) tables[i].getValueAt(index, 0);
			}
			else if (tables[i].getValueAt(index, 0).equals("."))
			{
				File newCurDir = new File(curdirs[i]);
				curdirs[i] = newCurDir.getParent();
			}
		}
		if (!SettingsView.getInstance().get("dbName").equals("")) SettingsView.getInstance().setNoLog("ManageFiles.dir", curdirs[i]);
		populateTable(i);
	}

	private void initComponents()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent e)
			{
				populateTable(1);
				populateTable(2);
			}
		});

		messages[1].setHorizontalAlignment(SwingConstants.LEFT);
		messages[1].setText(" ");
		messages[1].setFont(Cato.catoFont.deriveFont(0, 10));
		messages[1].setForeground(naturalColor);
		messages[1].setBackground(Color.WHITE);

		messages[2].setHorizontalAlignment(SwingConstants.RIGHT);
		messages[2].setText(" ");
		messages[2].setFont(Cato.catoFont.deriveFont(0, 10));
		messages[2].setForeground(naturalColor);
		messages[2].setBackground(Color.WHITE);

		splitPane.setResizeWeight(0.5);

		final Color onColor = Color.gray;
		final Color offColor = Cato.whiteAndBlack;

		tables[1] = new JTable(new DefaultTableModel(columnNames, 0))
		{
			public boolean isCellEditable(int row, int column)
			{
				return (false);
			}
		};
		tables[2] = new JTable(new DefaultTableModel(columnNames, 0))
		{
			public boolean isCellEditable(int row, int column)
			{
				return (false);
			}
		};

		tables[1].setShowGrid(true);
		tables[1].setGridColor(onColor);
		tables[1].setRowHeight(18);
		tables[1].setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tables[1].setAutoCreateRowSorter(true);
		((DefaultRowSorter) tables[1].getRowSorter()).setComparator(1, Comparators.SIZE_ORDER);
		tables[1].addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent evt)
			{
				currentTable = 1;
				tables[2].clearSelection();
				tables[1].setGridColor(onColor);
				tables[2].setGridColor(offColor);
				if (evt.getClickCount() > 1) changeDirectory(currentTable);
			}
		});
		manageFilesScrollPane1.setViewportView(tables[1]);
		manageFilesScrollPane1.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				currentTable = 1;
				tables[2].clearSelection();
				tables[1].setGridColor(onColor);
				tables[2].setGridColor(offColor);
			}
		});

		tables[2].setShowGrid(true);
		tables[2].setGridColor(offColor);
		tables[2].setRowHeight(18);
		tables[2].setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tables[2].setAutoCreateRowSorter(true);
		((DefaultRowSorter) tables[2].getRowSorter()).setComparator(1, Comparators.SIZE_ORDER);
		tables[2].addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				currentTable = 2;
				tables[1].clearSelection();
				tables[2].setGridColor(onColor);
				tables[1].setGridColor(offColor);
				if (evt.getClickCount() > 1) changeDirectory(currentTable);
			}
		});
		manageFilesScrollPane2.setViewportView(tables[2]);
		manageFilesScrollPane2.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				currentTable = 2;
				tables[1].clearSelection();
				tables[2].setGridColor(onColor);
				tables[1].setGridColor(offColor);
			}
		});

		JPanel messagePanel = new JPanel(new ColumnLayout(0, 0));
		messagePanel.add("hlx", messages[1]);
		messagePanel.add("hrxw", messages[2]);

		setLayout(new ColumnLayout(1, 1));
		add("9x", copyButton);
		add("", cutButton);
		add("", pasteButton);
		add("", renameButton);
		add("", makeDirButton);
		add("", deleteButton);
		add("", hideButton);
		add("", zipButton);
		add("w", memoryButton);
		add("hvxw", splitPane);
		add("hxw", messagePanel);
		add("kcxw", errorLabel);
	}
}
