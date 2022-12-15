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

import java.util.Comparator;

public class Comparators
{
	public static final Comparator<String> NUMBER_ORDER = new Comparator<String>()
	{ // Numeric sort in list tables
		public int compare(String s1, String s2)
		{
			Double d1 = (s1.equals("")) ? 0.0 : Double.parseDouble(s1);
			Double d2 = (s2.equals("")) ? 0.0 : Double.parseDouble(s2);
			return d1.compareTo(d2);
		}
	};

	public static final Comparator<String[]> ARRAY_SORT = new Comparator<String[]>()
	{ // sort the database before saving
		public int compare(String[] s1, String[] s2)
		{
			int result = s1[0].compareTo(s2[0]);
			if (result == 0) result = s1[1].compareTo(s2[1]);
			return result;
		}
	};

	static final Comparator<String> SIZE_ORDER = new Comparator<String>()
	{ // sort tables in Manager
		public int compare(String s1, String s2)
		{
			if (s1.equals("") || s2.equals("")) return 0;
			if (s1.equals("Up")) s1 = "-2";
			if (s2.equals("Up")) s2 = "-2";
			if (s1.equals("<DIR>")) s1 = "-1";
			if (s2.equals("<DIR>")) s2 = "-1";

			long d1 = Long.parseLong(s1);
			long d2 = Long.parseLong(s2);
			return (int) Math.signum(d2 - d1);
		}
	};
}
