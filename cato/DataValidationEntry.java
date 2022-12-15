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

public class DataValidationEntry
{
	String id;
	String incorrect;
	String correct;
	String field;

	public DataValidationEntry()
	{
		this.id = "-1";
		this.incorrect = "";
		this.correct = "";
		this.field = "";
	}

	public DataValidationEntry(int i)
	{
		this.id = Integer.toString(i);;
		this.incorrect = DBFunctions.getInstance().get(i, Constants.DATA_VALIDATION_INCORRECT);
		this.correct = DBFunctions.getInstance().get(i, Constants.DATA_VALIDATION_CORRECT);
		this.field = DBFunctions.getInstance().get(i, Constants.DATA_VALIDATION_FIELD);
	}

	public void saveDataValidationEntry()
	{
		String[] nd = { "DVA", this.incorrect, this.correct, this.field };
		DBFunctions.getInstance().update(Integer.parseInt(this.id), nd);
	}
}
