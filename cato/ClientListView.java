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

public class ClientListView extends GenericListView
{
	public static String[] clientNames = { "Tag", "Name", "Address1", "Address2", "Address3", "City", "Postal Code",
			"Phone1", "Phone2", "Fax", "Email", "Contact Name", "State", "Country", "Comments" };

	private static ClientListView instance = null;

	public static ClientListView getInstance()
	{
		if (instance == null)
		{
			instance = new ClientListView();
		}
		return instance;
	}

	public ClientListView()
	{
		super("CLI", "Client", clientNames, ClientEditView.getInstance(), 4);
		Constants.windowNames.put(this, "Clients");
		Constants.windowIcons.put(this, "client.png");
	}
}