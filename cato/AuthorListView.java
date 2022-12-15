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

public class AuthorListView extends GenericListView
{
	public static String[] authorNames = { "Tag", "Author/Illustrator", "Real Name", "Biography" };
	private static AuthorListView instance = null;

	public static AuthorListView getInstance()
	{
		if (instance == null)
		{
			instance = new AuthorListView();
		}
		return instance;
	}

	public AuthorListView()
	{
		super("AUT", "Author", authorNames, AuthorEditView.getInstance(), 2);
		Constants.windowNames.put(this, "Authors");
		Constants.windowIcons.put(this, "author.png");
	}

	public void deleteRecord()
	{
		super.deleteRecord();
		BookEditView.getInstance().updateAuthors();
	}
}