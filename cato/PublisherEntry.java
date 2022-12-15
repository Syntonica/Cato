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

public class PublisherEntry
{
	public String id;
	public String publisher;
	public String place;
	public String start;
	public String end;
	public String firstEdition;
	public String notes;

	public PublisherEntry()
	{
		this.id = "-1";
		this.publisher = "";
		this.place = "";
		this.start = "";
		this.end = "";
		this.firstEdition = "";
		this.notes = "";
	}

	public PublisherEntry(int i)
	{
		this.id = Integer.toString(i);;
		this.publisher = DBFunctions.getInstance().get(i, Constants.PUBLISHER_PUBLISHER);
		this.place = DBFunctions.getInstance().get(i, Constants.PUBLISHER_PLACE);
		this.start = DBFunctions.getInstance().get(i, Constants.PUBLISHER_START_DATE);
		this.end = DBFunctions.getInstance().get(i, Constants.PUBLISHER_END_DATE);
		this.firstEdition = DBFunctions.getInstance().get(i, Constants.PUBLISHER_FIRST_EDITION);
		this.notes = DBFunctions.getInstance().get(i, Constants.PUBLISHER_NOTES);
	}

	public void savePublisherEntry()
	{
		String[] np = { "PUB", this.publisher, this.place, this.start, this.end, this.firstEdition, this.notes };
		DBFunctions.getInstance().update(Integer.parseInt(this.id), np);
	}
}
