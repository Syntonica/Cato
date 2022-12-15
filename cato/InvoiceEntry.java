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

public class InvoiceEntry
{
	public String id;
	public String date;
	public String client;
	public String user;
	public String comments;
	public String shipTo;
	public String paymentMethod;
	public String taxes1;
	public String taxes2;
	public String taxes3;
	public String discount;
	public String shipping;
	public String total;
	public String invoiceNo;

	public InvoiceEntry()
	{
		id = "-1";
		date = Constants.getPrettyDateTime();
		client = "";
		user = SettingsView.getInstance().get("vendorName");
		comments = "";
		shipTo = "";
		paymentMethod = "";
		taxes1 = "0.00";
		taxes2 = "0.00";
		taxes3 = "0.00";
		discount = "0.00";
		shipping = "";
		total = "0.00";
		invoiceNo = DBFunctions.getInstance().getNextID("INV", Constants.INVOICE_NUMBER);
	}

	public InvoiceEntry(int i)
	{
		this.id = Integer.toString(i);
		this.date = DBFunctions.getInstance().get(i, Constants.INVOICE_DATE);
		this.client = DBFunctions.getInstance().get(i, Constants.INVOICE_CLIENT_NAME);
		this.user = DBFunctions.getInstance().get(i, Constants.INVOICE_USER);
		this.comments = DBFunctions.getInstance().get(i, Constants.INVOICE_COMMENTS);
		this.shipTo = DBFunctions.getInstance().get(i, Constants.INVOICE_SHIP_TO);
		this.paymentMethod = DBFunctions.getInstance().get(i, Constants.INVOICE_PAYMENT_METHOD);
		this.taxes1 = DBFunctions.getInstance().get(i, Constants.INVOICE_TAXES1);
		this.taxes2 = DBFunctions.getInstance().get(i, Constants.INVOICE_TAXES2);
		this.taxes3 = DBFunctions.getInstance().get(i, Constants.INVOICE_TAXES3);
		this.discount = DBFunctions.getInstance().get(i, Constants.INVOICE_DISCOUNT);
		this.shipping = DBFunctions.getInstance().get(i, Constants.INVOICE_SHIPPING);
		this.total = DBFunctions.getInstance().get(i, Constants.INVOICE_FINAL_TOTAL);
		this.invoiceNo = DBFunctions.getInstance().get(i, Constants.INVOICE_NUMBER);
	}

	public void saveInvoiceEntry()
	{
		String[] ni = { "INV", this.invoiceNo, this.date, this.client, this.user, this.comments, this.shipTo,
				this.paymentMethod, this.taxes1, this.taxes2, this.taxes3, this.discount, this.shipping, this.total };
		DBFunctions.getInstance().update(Integer.parseInt(this.id), ni);
	}
}
