/*******************************************************************************
 * Copyright (C) 2011 - 2012 Jochen Wuttke
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.

 *
 * Contributors:
 *    Jochen Wuttke (wuttkej@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Apr 19, 2012
 */

package adasim.filter;

import adasim.filter.AdasimFilter;

public class FakeFilter implements AdasimFilter {
	
	/* (non-Javadoc)
	 * @see adasim.filter.AdasimFilter#filter(byte)
	 */
	@Override
	public byte filter(byte b) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see adasim.filter.AdasimFilter#filter(char)
	 */
	@Override
	public char filter(char b) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see adasim.filter.AdasimFilter#filter(short)
	 */
	@Override
	public short filter(short b) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see adasim.filter.AdasimFilter#filter(int)
	 */
	@Override
	public int filter(int b) {
		return b+1;
	}
	
	/* (non-Javadoc)
	 * @see adasim.filter.AdasimFilter#filter(long)
	 */
	@Override
	public long filter(long b) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see adasim.filter.AdasimFilter#filter(float)
	 */
	@Override
	public float filter(float b) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see adasim.filter.AdasimFilter#filter(double)
	 */
	@Override
	public double filter(double b) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see adasim.filter.AdasimFilter#filter(boolean)
	 */
	@Override
	public boolean filter(boolean b) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/* (non-Javadoc)
	 * @see adasim.filter.AdasimFilter#filter(java.lang.Object)
	 */
	@Override
	public <T> T filter(T b) {
		// TODO Auto-generated method stub
		return null;
	}
	
}