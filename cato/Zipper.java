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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;

public class Zipper
{
	/**
	 * ***************************************** Zip a single file
	 * ******************************************
	 */
	public static void zipFile(File fn)
	{
		try
		{
			BufferedInputStream origin = null;

			if (new File(fn + ".zip").exists())
			{
				if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(BooksView.getInstance(),
						"File already exists.  OK to overwrite?", "Zipping", JOptionPane.OK_CANCEL_OPTION))
				{
					return;
				}
			}
			FileOutputStream dest = new FileOutputStream(fn + ".zip");
			CheckedOutputStream checksum = new CheckedOutputStream(dest, new Adler32());
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(checksum));

			byte data[] = new byte[2048];
			// get a list of files from current directory
			FileInputStream fi = new FileInputStream(fn);
			origin = new BufferedInputStream(fi, 2048);
			ZipEntry entry = new ZipEntry(fn.toString());
			out.putNextEntry(entry);
			int count;
			while ((count = origin.read(data, 0, 2048)) != -1)
			{
				out.write(data, 0, count);
			}
			origin.close();
			out.close();
		}
		catch (Exception ex)
		{
			Constants.writeBlog("Zipper > zipFile > " + ex);
		}
	}

	/**
	 * **************************************** Zip directory
	 *******************************************
	 */
	public static void zipDir(File fn)
	{
		ArrayList<File> fileList = new ArrayList<File>();
		getAllFiles(fn, fileList);
		writeZipFile(fn, fileList);
	}

	public static void getAllFiles(File dir, ArrayList<File> fileList)
	{
		try
		{
			File[] files = dir.listFiles();
			for (File file : files)
			{
				fileList.add(file);
				if (file.isDirectory())
				{
					getAllFiles(file, fileList);
				}
			}
		}
		catch (Exception ex)
		{
			Constants.writeBlog("Zipper > getAllFiles > " + ex);
		}
	}

	public static void writeZipFile(File directoryToZip, ArrayList<File> fileList)
	{
		try
		{
			String f = directoryToZip.getParent() + System.getProperty("file.separator") + directoryToZip.getName()
					+ ".zip";
			File ff = new File(f);
			if (ff.exists())
			{
				if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(BooksView.getInstance(),
						"File already exists.  OK to overwrite?", "Zipping", JOptionPane.OK_CANCEL_OPTION))
				{
					return;
				}
			}
			FileOutputStream fos = new FileOutputStream(f);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList)
			{
				if (!file.isDirectory())
				{ // we only zip files, not directories
					addToZip(directoryToZip, file, zos);
				}
			}
			zos.close();
			fos.close();
		}
		catch (Exception ex)
		{
			Constants.writeBlog("Zipper > writeZipFile > " + ex);
		}
	}

	public static void addToZip(File directoryToZip, File file, ZipOutputStream zos)
	{
		try
		{
			FileInputStream fis = new FileInputStream(file);
			String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
					file.getCanonicalPath().length());
			ZipEntry zipEntry = new ZipEntry(zipFilePath);
			zos.putNextEntry(zipEntry);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0)
			{
				zos.write(bytes, 0, length);
			}
			zos.closeEntry();
			fis.close();
		}
		catch (Exception ex)
		{
			Constants.writeBlog("Zipper > addToZip > " + ex);
		}
	}

	/**
	 * ***************************************** Unzip .zip file
	 * ****************************************
	 */
	@SuppressWarnings("resource") // zipFile never closed?
	public static boolean unzipFile(File fn)
	{
		ZipFile zipFile = null;
		boolean retVal = false;
		try
		{
			zipFile = new ZipFile(fn);
			Enumeration<?> enu = zipFile.entries();
			if (zipFile.size() > 1)
			{
				String ffnn = fn.getName();
				ffnn = ffnn.substring(0, ffnn.length() - 4);
				ffnn = fn.getParent().toString() + System.getProperty("file.separator") + ffnn;
				fn = new File(ffnn);
				fn.mkdir();
			}
			else
			{
				fn = new File(fn.getParent());
			}
			while (enu.hasMoreElements())
			{
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();
				String name = fn + System.getProperty("file.separator") + zipEntry.getName();

				File file = new File(name);
				int really = JOptionPane.NO_OPTION;
				if (file.exists())
				{
					really = JOptionPane.showConfirmDialog(BooksView.getInstance(),
							"File already exists.  OK to overwrite?", "UnZipping", JOptionPane.YES_NO_CANCEL_OPTION);
					if (really == JOptionPane.CANCEL_OPTION) return false;
				}
				if (!file.exists() || (really == JOptionPane.YES_OPTION))
				{
					File parent = file.getParentFile();
					if (parent != null)
					{
						parent.mkdirs();
					}
					InputStream is = zipFile.getInputStream(zipEntry);
					FileOutputStream fos = new FileOutputStream(file);
					byte[] bytes = new byte[1024];
					int length;
					while ((length = is.read(bytes)) >= 0)
					{
						fos.write(bytes, 0, length);
					}
					is.close();
					fos.close();
				}
				retVal = true;
			}
		}
		catch (Exception ex)
		{
			Constants.writeBlog("Zipper > unzipFile > " + ex);
		}
		try
		{
			zipFile.close();
		}
		catch (Exception ex)
		{
			Constants.writeBlog("Zipper > unzipFile2 > " + ex);
		}
		return retVal;
	}
}
