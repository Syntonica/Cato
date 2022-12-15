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

public class WantListView extends GenericListView
{
	public static String[] wantNames = { "Tag", "Client", "Author", "Title", "Publisher", "Keywords", "Binding",
			"Comments", "Price Min", "Price Max", "FE?", "DJ?", "Signed?", "Status", "Added Date", "Changed Date",
			"Include in Update?" };

	private static WantListView instance = null;

	public static WantListView getInstance()
	{
		if (instance == null)
		{
			instance = new WantListView();
		}
		return instance;
	}

	public WantListView()
	{
		super("WNT", "Want", wantNames, WantEditView.getInstance(), 4);
		Constants.windowNames.put(this, "Wants");
		Constants.windowIcons.put(this, "want.png");
	}
}
