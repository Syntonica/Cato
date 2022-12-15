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

public class SaleEntry
{
	public String id;
	public String salesperson;
	public String date;
	public String bookID;
	public String author;
	public String title;
	public String quantity;
	public String invoiceNumber;
	public String regularPrice;
	public String list;
	public String saleAmount;
	public String shippingCredit;
	public String commission;
	public String secondCommission;
	public String fee;
	public String shippingCost;
	public String taxes1;
	public String taxes2;
	public String taxes3;
	public String cost;
	public String saleNumber;

	public SaleEntry()
	{
		this.id = "-1";
		this.salesperson = SettingsView.getInstance().get("vendorName");
		this.date = Constants.getPrettyDateTime();
		this.bookID = "";
		this.author = "";
		this.title = "";
		this.quantity = "1";
		this.invoiceNumber = "";
		this.regularPrice = "0.00";
		this.list = "";
		this.saleAmount = "0.00";
		this.shippingCredit = "0.00";
		this.commission = "0.00";
		this.secondCommission = "0.00";
		this.fee = "0.00";
		this.shippingCost = "0.00";
		this.taxes1 = "0.00";
		this.taxes2 = "0.00";
		this.taxes3 = "0.00";
		this.cost = "0.00";
		this.saleNumber = DBFunctions.getInstance().getNextID("SAL", Constants.SALE_NUMBER);;
	}

	public SaleEntry(int i)
	{
		this.id = Integer.toString(i);
		this.salesperson = DBFunctions.getInstance().get(i, Constants.SALE_SALESPERSON);
		this.date = DBFunctions.getInstance().get(i, Constants.SALE_DATE);
		this.bookID = DBFunctions.getInstance().get(i, Constants.SALE_BOOK_ID);
		this.author = DBFunctions.getInstance().get(i, Constants.SALE_AUTHOR);
		this.title = DBFunctions.getInstance().get(i, Constants.SALE_TITLE);
		this.quantity = DBFunctions.getInstance().get(i, Constants.SALE_QUANTITY);
		this.invoiceNumber = DBFunctions.getInstance().get(i, Constants.SALE_INVOICE_NUMBER);
		this.regularPrice = DBFunctions.getInstance().get(i, Constants.SALE_REGULAR_PRICE);
		this.list = DBFunctions.getInstance().get(i, Constants.SALE_LIST);
		this.saleAmount = DBFunctions.getInstance().get(i, Constants.SALE_SALE_AMOUNT);
		this.shippingCredit = DBFunctions.getInstance().get(i, Constants.SALE_SHIPPING_CREDIT);
		this.commission = DBFunctions.getInstance().get(i, Constants.SALE_COMMISSION);
		this.secondCommission = DBFunctions.getInstance().get(i, Constants.SALE_SECOND_COMMISSION);
		this.fee = DBFunctions.getInstance().get(i, Constants.SALE_FEE);
		this.shippingCost = DBFunctions.getInstance().get(i, Constants.SALE_SHIPPING_COST);
		this.taxes1 = DBFunctions.getInstance().get(i, Constants.SALE_TAXES1);
		this.taxes2 = DBFunctions.getInstance().get(i, Constants.SALE_TAXES2);
		this.taxes3 = DBFunctions.getInstance().get(i, Constants.SALE_TAXES3);
		this.cost = DBFunctions.getInstance().get(i, Constants.SALE_COST);
		this.saleNumber = DBFunctions.getInstance().get(i, Constants.SALE_NUMBER);
	}

	public void saveSalesEntry()
	{
		String[] ns = { "SAL", this.saleNumber, this.salesperson, this.date, this.bookID, this.author, this.title,
				this.quantity, this.invoiceNumber, this.regularPrice, this.list, this.saleAmount, this.shippingCredit,
				this.commission, this.secondCommission, this.fee, this.shippingCost, this.taxes1, this.taxes2,
				this.taxes3, this.cost };
		DBFunctions.getInstance().update(Integer.parseInt(this.id), ns);
	}
}
