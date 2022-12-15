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

public class WantEntry
{
	public String id;
	public String client;
	public String author;
	public String title;
	public String publisher;
	public String keywords;
	public String binding;
	public String comments;
	public String priceMin;
	public String priceMax;
	public String firstEdition;
	public String dustJacket;
	public String signed;
	public String status;
	public String addedDate;
	public String changedDate;
	public String includeInUpdate;

	public WantEntry()
	{
		this.id = "-1";
		this.client = "";
		this.author = "";
		this.title = "";
		this.publisher = "";
		this.keywords = "";
		this.binding = "";
		this.comments = "";
		this.priceMin = "";
		this.priceMax = "";
		this.firstEdition = "false";
		this.dustJacket = "false";
		this.signed = "false";
		this.status = "";
		this.addedDate = "";
		this.changedDate = "";
		this.includeInUpdate = "true";
	}

	public WantEntry(int i)
	{
		this.id = Integer.toString(i);;
		this.client = DBFunctions.getInstance().get(i, Constants.WANT_CLIENT);
		this.author = DBFunctions.getInstance().get(i, Constants.WANT_AUTHOR);
		this.title = DBFunctions.getInstance().get(i, Constants.WANT_TITLE);
		this.publisher = DBFunctions.getInstance().get(i, Constants.WANT_PUBLISHER);
		this.keywords = DBFunctions.getInstance().get(i, Constants.WANT_KEYWORDS);
		this.binding = DBFunctions.getInstance().get(i, Constants.WANT_BINDING);
		this.comments = DBFunctions.getInstance().get(i, Constants.WANT_DESCRIPTION);
		this.priceMin = DBFunctions.getInstance().get(i, Constants.WANT_PRICE_MIN);
		this.priceMax = DBFunctions.getInstance().get(i, Constants.WANT_PRICE_MAX);
		this.firstEdition = DBFunctions.getInstance().get(i, Constants.WANT_FIRST_EDITION);
		this.dustJacket = DBFunctions.getInstance().get(i, Constants.WANT_DUST_JACKET);
		this.signed = DBFunctions.getInstance().get(i, Constants.WANT_SIGNED);
		this.status = DBFunctions.getInstance().get(i, Constants.WANT_STATUS);
		this.addedDate = DBFunctions.getInstance().get(i, Constants.WANT_ADDED_DATE);
		this.changedDate = DBFunctions.getInstance().get(i, Constants.WANT_CHANGED_DATE);
		this.includeInUpdate = "true";
	}

	public void saveWantsEntry()
	{
		String[] nw = { "WNT", this.client, this.author, this.title, this.publisher, this.keywords, this.binding,
				this.comments, this.priceMin, this.priceMax, this.firstEdition, this.dustJacket, this.signed,
				this.status, this.addedDate, this.changedDate, this.includeInUpdate };
		DBFunctions.getInstance().update(Integer.parseInt(this.id), nw);
	}
}