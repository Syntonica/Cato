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

public class ClientEntry
{
	public String address1;
	public String address2;
	public String address3;
	public String city;
	public String id;
	public String comments;
	public String contact;
	public String country;
	public String email;
	public String fax;
	public String name;
	public String phone1;
	public String phone2;
	public String postalCode;
	public String state;

	public ClientEntry()
	{
		this.address1 = "";
		this.address2 = "";
		this.address3 = "";
		this.city = "";
		this.id = "-1";
		this.comments = "";
		this.contact = "";
		this.country = "";
		this.email = "";
		this.fax = "";
		this.name = "";
		this.phone1 = "";
		this.phone2 = "";
		this.postalCode = "";
		this.state = "";
	}

	public ClientEntry(int i)
	{
		this.id = Integer.toString(i);
		this.name = DBFunctions.getInstance().get(i, Constants.CLIENT_NAME);
		this.address1 = DBFunctions.getInstance().get(i, Constants.CLIENT_ADDRESS1);
		this.address2 = DBFunctions.getInstance().get(i, Constants.CLIENT_ADDRESS2);
		this.address3 = DBFunctions.getInstance().get(i, Constants.CLIENT_ADDRESS3);
		this.city = DBFunctions.getInstance().get(i, Constants.CLIENT_CITY);
		this.postalCode = DBFunctions.getInstance().get(i, Constants.CLIENT_POSTAL_CODE);
		this.phone1 = DBFunctions.getInstance().get(i, Constants.CLIENT_PHONE1);
		this.phone2 = DBFunctions.getInstance().get(i, Constants.CLIENT_PHONE2);
		this.fax = DBFunctions.getInstance().get(i, Constants.CLIENT_FAX);
		this.email = DBFunctions.getInstance().get(i, Constants.CLIENT_EMAIL);
		this.contact = DBFunctions.getInstance().get(i, Constants.CLIENT_CONTACT_NAME);
		this.state = DBFunctions.getInstance().get(i, Constants.CLIENT_STATE);
		this.country = DBFunctions.getInstance().get(i, Constants.CLIENT_COUNTRY);
		this.comments = DBFunctions.getInstance().get(i, Constants.CLIENT_COMMENTS);
	}

	public void saveClientEntry()
	{
		String[] nc = { "CLI", this.name, this.address1, this.address2, this.address3, this.city, this.postalCode,
				this.phone1, this.phone2, this.fax, this.email, this.contact, this.state, this.country, this.comments };
		DBFunctions.getInstance().update(Integer.parseInt(this.id), nc);
	}
}
