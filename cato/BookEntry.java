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

public class BookEntry
{
	String key;
	String addedDate;
	String author;
	String binding;
	String bindingStyle;
	String bookCondition;
	String bookID;
	String bookType;
	String catalog1;
	String catalog2;
	String catalog3;
	String changedDate;
	String description;
	String cost;
	String date;
	String djCondition;
	String edition;
	String firstEdition;
	String hasDJ;
	String unused;
	String height;
	String illustrator;
	String includeInUpdate;
	String isbn;
	String keywords;
	String language;
	String list1;
	String list2;
	String list3;
	String list4;
	String list5;
	String location;
	String pages;
	String place;
	String printing;
	String privateField;
	String publisher;
	String quantity;
	String bookNumber;
	String signed;
	String size;
	String status;
	String title;
	String user1;
	String user2;
	String validatedDate;
	String weight;
	String width;

	public BookEntry(int i)
	{
		if (i < 0)
		{
			this.key = "-1";
			this.addedDate = Constants.getTimestamp();
			this.author = "";
			this.binding = "";
			this.bindingStyle = "";
			this.bookCondition = "";

			this.bookID = "";
			if (SettingsView.getInstance().get("autoInc").equalsIgnoreCase("true"))
			{
				String idx = SettingsView.getInstance().get("autoIncNumber");
				this.bookID = idx;
			}

			this.bookType = "";
			this.catalog1 = "";
			this.catalog2 = "";
			this.catalog3 = "";
			this.changedDate = "";
			this.description = "";
			this.cost = "";
			this.date = "";
			this.djCondition = "";
			this.edition = "";
			this.firstEdition = "false";
			this.hasDJ = "false";
			this.unused = "";
			this.height = "";
			this.illustrator = "";
			this.includeInUpdate = "true";
			this.isbn = "";
			this.keywords = "";
			this.language = "";
			this.list1 = "";
			this.list2 = "";
			this.list3 = "";
			this.list4 = "";
			this.list5 = "";
			this.location = "";
			this.pages = "";
			this.place = "";
			this.printing = "";
			this.privateField = "";
			this.publisher = "";
			this.quantity = "1";
			this.bookNumber = "";
			this.signed = "";
			this.size = "";
			this.status = "For Sale";
			this.title = "";
			this.user1 = "";
			this.user2 = "";
			this.validatedDate = "";
			this.weight = "";
			this.width = "";
		}
		else
		{
			this.key = Integer.toString(i);
			this.addedDate = DBFunctions.getInstance().get(i, Constants.BOOK_ADDED_DATE);
			this.author = DBFunctions.getInstance().get(i, Constants.BOOK_AUTHOR);
			this.bindingStyle = DBFunctions.getInstance().get(i, Constants.BOOK_BINDING_STYLE);
			this.binding = DBFunctions.getInstance().get(i, Constants.BOOK_BINDING_TYPE);
			this.catalog1 = DBFunctions.getInstance().get(i, Constants.BOOK_CATALOG1);
			this.catalog2 = DBFunctions.getInstance().get(i, Constants.BOOK_CATALOG2);
			this.catalog3 = DBFunctions.getInstance().get(i, Constants.BOOK_CATALOG3);
			this.changedDate = DBFunctions.getInstance().get(i, Constants.BOOK_CHANGED_DATE);
			this.description = DBFunctions.getInstance().get(i, Constants.BOOK_DESCRIPTION);
			this.bookCondition = DBFunctions.getInstance().get(i, Constants.BOOK_CONDITION);
			this.cost = DBFunctions.getInstance().get(i, Constants.BOOK_COST);
			this.date = DBFunctions.getInstance().get(i, Constants.BOOK_DATE);
			this.djCondition = DBFunctions.getInstance().get(i, Constants.BOOK_DJ_CONDITION);
			this.edition = DBFunctions.getInstance().get(i, Constants.BOOK_EDITION);
			this.firstEdition = DBFunctions.getInstance().get(i, Constants.BOOK_FIRST_EDITION);
			this.hasDJ = DBFunctions.getInstance().get(i, Constants.BOOK_HAS_DJ);
			this.unused = DBFunctions.getInstance().get(i, Constants.BOOK_UNUSED);
			this.height = DBFunctions.getInstance().get(i, Constants.BOOK_HEIGHT);
			this.bookID = DBFunctions.getInstance().get(i, Constants.BOOK_ID);
			this.illustrator = DBFunctions.getInstance().get(i, Constants.BOOK_ILLUSTRATOR);
			this.includeInUpdate = DBFunctions.getInstance().get(i, Constants.BOOK_INCLUDE_IN_UPDATE);
			this.isbn = DBFunctions.getInstance().get(i, Constants.BOOK_ISBN);
			this.keywords = DBFunctions.getInstance().get(i, Constants.BOOK_KEYWORDS);
			this.language = DBFunctions.getInstance().get(i, Constants.BOOK_LANGUAGE);
			this.list1 = DBFunctions.getInstance().get(i, Constants.BOOK_LIST1);
			this.list2 = DBFunctions.getInstance().get(i, Constants.BOOK_LIST2);
			this.list3 = DBFunctions.getInstance().get(i, Constants.BOOK_LIST3);
			this.list4 = DBFunctions.getInstance().get(i, Constants.BOOK_LIST4);
			this.list5 = DBFunctions.getInstance().get(i, Constants.BOOK_LIST5);
			this.location = DBFunctions.getInstance().get(i, Constants.BOOK_LOCATION);
			this.pages = DBFunctions.getInstance().get(i, Constants.BOOK_PAGES);
			this.place = DBFunctions.getInstance().get(i, Constants.BOOK_PLACE);
			this.printing = DBFunctions.getInstance().get(i, Constants.BOOK_PRINTING);
			this.privateField = DBFunctions.getInstance().get(i, Constants.BOOK_PRIVATE);
			this.publisher = DBFunctions.getInstance().get(i, Constants.BOOK_PUBLISHER);
			this.quantity = DBFunctions.getInstance().get(i, Constants.BOOK_QUANTITY);
			this.bookNumber = DBFunctions.getInstance().get(i, Constants.BOOK_NUMBER);
			this.signed = DBFunctions.getInstance().get(i, Constants.BOOK_SIGNED);
			this.size = DBFunctions.getInstance().get(i, Constants.BOOK_SIZE);
			this.status = DBFunctions.getInstance().get(i, Constants.BOOK_STATUS);
			this.title = DBFunctions.getInstance().get(i, Constants.BOOK_TITLE);
			this.bookType = DBFunctions.getInstance().get(i, Constants.BOOK_TYPE);
			this.user1 = DBFunctions.getInstance().get(i, Constants.BOOK_USER1);
			this.user2 = DBFunctions.getInstance().get(i, Constants.BOOK_USER2);
			this.validatedDate = DBFunctions.getInstance().get(i, Constants.BOOK_VALIDATED_DATE);
			this.weight = DBFunctions.getInstance().get(i, Constants.BOOK_WEIGHT);
			this.width = DBFunctions.getInstance().get(i, Constants.BOOK_WIDTH);
		}
	}

	public void saveBookEntry()
	{
		String[] nb = { "BKS", bookID, isbn, author, title, illustrator, publisher, place, date, edition, printing,
				pages, language, keywords, bookCondition, firstEdition, binding, djCondition, bindingStyle, hasDJ,
				bookType, signed, location, includeInUpdate, user1, user2, cost, list1, list2, list3, list4, list5,
				size, weight, height, width, quantity, unused, status, description, privateField, catalog1, catalog2,
				catalog3, addedDate, changedDate, validatedDate, bookNumber };
		DBFunctions.getInstance().update(Integer.parseInt(this.key), nb);
	}
}
