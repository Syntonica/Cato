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

public class FTPEntry
{
	public String id;
	public String name;
	public String hostName;
	public String userName;
	public String password;
	public String list;
	public String format;

	public FTPEntry()
	{
		this.id = "-1";
		this.name = "";
		this.hostName = "";
		this.userName = "";
		this.password = "";
		this.list = "";
		this.format = "";
	}

	public FTPEntry(int i)
	{
		this.id = Integer.toString(i);;
		this.name = DBFunctions.getInstance().get(i, Constants.FTP_SETTINGS_NAME);
		this.hostName = DBFunctions.getInstance().get(i, Constants.FTP_SETTINGS_HOSTNAME);
		this.userName = DBFunctions.getInstance().get(i, Constants.FTP_SETTINGS_USERNAME);
		this.password = DBFunctions.getInstance().get(i, Constants.FTP_SETTINGS_PASSWORD);
		this.list = DBFunctions.getInstance().get(i, Constants.FTP_SETTINGS_LIST);
		this.format = DBFunctions.getInstance().get(i, Constants.FTP_SETTINGS_FORMAT);
	}

	public void saveFTPEntry()
	{
		String[] nu = { "FTP", this.name, this.hostName, this.userName, this.password, this.list, this.format };
		DBFunctions.getInstance().update(Integer.parseInt(this.id), nu);
	}
}
