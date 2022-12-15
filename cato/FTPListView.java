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

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import it.sauronsoftware.ftp4j.FTPClient;

public class FTPListView extends GenericListView
{
	private Action sendAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e) // printList
		{
			enableWindow(false);
			sendFTP();
		}
	};

	private JButton sendButton = new GenericButton("upload.png", "Send updates as designated below.", sendAction);
	private StringBuilder outcome;
	boolean isSecure = false;

	private FTPSend client;
	private JTable listTable2 = new JTable();
	private static String[] ftpNames = { "Tag", "Account Name", "Upload?", "• Book File", "• Additional File" };
	private TableModel tm2 = new DefaultTableModel(ftpNames, 0)
	{
		public Class<?> getColumnClass(int columnIndex)
		{
			if (columnIndex == 2)
			{
				return Boolean.class;
			}
			return String.class;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			if (columnIndex == 1)
			{
				return false;
			}
			return true;
		}
	};

	private static FTPListView instance = null;

	public static GenericListView getInstance()
	{
		if (instance == null)
		{
			instance = new FTPListView();
		}
		return instance;
	}

	public FTPListView()
	{
		super("FTP", "FTP", ftpNames, FTPEditView.getInstance(), 0);
		Constants.windowNames.put(this, "FTP");
		Constants.windowIcons.put(this, "ftp.png");
		initComponents();
	}

	public void populateTable()
	{
		final JComboBox filesComboBox = new JComboBox();
		filesComboBox.removeAllItems();
		filesComboBox.addItem("None");
		File dir = new File(Constants.EXPORTS_DIR);
		String[] children = dir.list();
		if (children != null)
		{
			for (String fn : children)
			{
				if (!fn.equals("") && (!fn.startsWith(".")))
				{
					filesComboBox.addItem(fn);
				}
			}
		}
		listTable2.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(filesComboBox));

		JComboBox filesComboBox2 = new JComboBox();
		filesComboBox2.removeAllItems();
		filesComboBox2.addItem("None");
		dir = new File(Constants.EXPORTS_DIR);
		children = dir.list();
		if (children != null)
		{
			for (String fn : children)
			{
				if (!fn.equals("") && (!fn.startsWith(".")))
				{
					filesComboBox2.addItem(fn);
				}
			}
		}
		listTable2.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(filesComboBox2));

		((DefaultTableModel) tm2).setRowCount(0);
		for (int i = 0; i < DBFunctions.getInstance().size(); i++)
		{
			if (DBFunctions.getInstance().get(i, Constants.RECORD_TAG).equals("FTP"))
			{
				((DefaultTableModel) tm2).insertRow(listTable2.getRowCount(), new Object[] { Integer.toString(i),
						DBFunctions.getInstance().get(i, Constants.FTP_SETTINGS_NAME), true, "None", "None" // old port field holds
																							// new Name field
				});
			}
		}
		listTable2.setModel(tm2);
		listTable2.setRowHeight(30);
		listTable2.setRowMargin(10);
	}

	public void sendFTP()
	{
		outcome = new StringBuilder();
		for (int i = 0; i < listTable2.getRowCount(); i++)
		{
			// set isSecure here!
			boolean up = (Boolean) listTable2.getValueAt(i, 2);
			if (up)
			{
				// ArrayList<Integer> bookList = new ArrayList<Integer>();

				int idx = Integer.parseInt((String) listTable2.getValueAt(i, 0));
				// String format = DBFunctions.getInstance().get(idx)[Constants.FTP_SETTINGS_FORMAT];
				String list = DBFunctions.getInstance().get(idx, Constants.FTP_SETTINGS_LIST);
				String fileSelection1 = (String) listTable2.getValueAt(i, 3);
				String fileSelection2 = (String) listTable2.getValueAt(i, 4);
				String fileWithPath1 = "";
				String fileWithPath2 = "";

				if (!fileSelection1.equals("None"))
				{
					uploadAFile(idx, fileSelection1, fileWithPath1, list);
					outcome.append(listTable2.getValueAt(i, 1));
					outcome.append(": ");
					outcome.append(client.getOutcome());
					outcome.append(Constants.rn);
				}
				else
				{
					outcome.append(listTable2.getValueAt(i, 1));
					outcome.append(": No updated records found.");
					outcome.append(Constants.rn);
				}
				if (!fileSelection2.equals("None"))
				{
					fileWithPath2 = Constants.EXPORTS_DIR + Constants.ps + fileSelection2;
					uploadAFile(idx, fileSelection2, fileWithPath2, list);
					outcome.append(listTable2.getValueAt(i, 1));
					outcome.append(": ");
					outcome.append(client.getOutcome());
					outcome.append(Constants.rn);
				}
			}
		}
		JOptionPane.showMessageDialog(BooksView.getInstance(), outcome);
		enableWindow(true);
	}

	private boolean uploadAFile(int idx, String fileName, String fileWithPath, String list)
	{
		String hostName = DBFunctions.getInstance().get(idx, Constants.FTP_SETTINGS_HOSTNAME);
		String userName = DBFunctions.getInstance().get(idx, Constants.FTP_SETTINGS_USERNAME);
		String password = DBFunctions.getInstance().get(idx, Constants.FTP_SETTINGS_PASSWORD);

		try
		{
			client = new FTPSend();
			return client.store(hostName, 21, userName, password, fileName, fileWithPath);
		}
		catch (Exception ex)
		{
			Constants.writeBlog("FTPListView > uploadAFile > " + ex);
		}
		return false;
	}

	private void enableWindow(boolean on)
	{
		sendButton.setEnabled(on);
		listTable2.setEnabled(on);
	}

	public void addRecord()
	{
		FTPEditView.getInstance().setupEntry(-1);
		BooksView.getInstance().tabbedPane.openTab(FTPEditView.getInstance());
	}

	public void editRecord()
	{
		// get row clicked on
		int index = listTable2.getSelectedRow();
		if (index != -1)
		{
			FTPEditView.getInstance().setupEntry(Integer.parseInt((String) listTable2.getValueAt(index, 0)));
			BooksView.getInstance().tabbedPane.openTab(FTPEditView.getInstance());
		}
	}

	public void deleteRecord()
	{
		int index = listTable2.getSelectedRow();
		if (index != -1)
		{
			if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(BooksView.getInstance(),
					"Delete " + tm2.getValueAt(index, Constants.FTP_SETTINGS_NAME) + "?", "Deleting",
					JOptionPane.OK_CANCEL_OPTION))
			{
				DBFunctions.getInstance().remove(Integer.parseInt((String) tm2.getValueAt(index, 0)));
				populateTable();
			}
		}
	}

	private void initComponents()
	{
		listTable2.setShowGrid(false);
		listTable2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listTable2.getTableHeader().setReorderingAllowed(false);
		listTable2.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() > 1)
				{
					editRecord();
				}
			}
		});
		listTable2.setModel(tm2);
		listScrollPane.setViewportView(listTable2);

		listTable2.getColumnModel().getColumn(0).setMinWidth(1);
		listTable2.getColumnModel().getColumn(0).setPreferredWidth(1);
		listTable2.getColumnModel().getColumn(0).setMaxWidth(1);

		setLayout(new ColumnLayout(1, 1));
		add("6x", clearButton);
		add("", addButton);
		add("", editButton);
		add("", deleteButton);
		add("", printButton);
		add("w", sendButton);
		add("whvx", listScrollPane);
	}

	public class FTPSend
	{

		private String outcome = "Success!";

		public String getOutcome()
		{
			return outcome;
		}

		public synchronized boolean store(String host, int port, String user, String pass, String fileName,
				String fileWithPath)
		{
			TrustManager[] trustManager = new TrustManager[] // accept any certificate since Abe's in invalid.
			{ new X509TrustManager()
			{
				public X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType)
				{}

				public void checkServerTrusted(X509Certificate[] certs, String authType)
				{}
			} };
			String[] pieces = host.split("\\/", 2);
			try
			{
				SSLContext sslContext = null;
				sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, trustManager, new SecureRandom());
				SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
				FTPClient client = new FTPClient();
				client.setSecurity(FTPClient.SECURITY_FTPS);
				client.setSecurity(FTPClient.SECURITY_FTPES);
				client.setSSLSocketFactory(sslSocketFactory);
				client.connect(pieces[0], port);
				client.login(user, pass);
				if (pieces.length > 1)
				{
					client.changeDirectory("/" + pieces[1]);
				}
				client.upload(new File(fileWithPath));
				return true;
			}
			catch (Exception ex)
			{
				outcome = ex.getMessage();
				return false;
			}
		}
	}
}
