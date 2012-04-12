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
 * Created: Apr 4, 2012
 */

package adasim.filter;

/**
 * The abstract interface for uncertainty and
 * privacy filters.
 * <p>
 * Implementers are free to implement the singleton pattern.
 * If there is a method getInstance(), the simulation loader
 * will use this method to obtain instances of the implemented
 * filter. Otherwise, the default constructor will be called.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public interface AdasimFilter {
		
	public byte filter(byte b );
	public char filter(char b );
	public short filter(short b );
	public int filter(int b );
	public long filter(long b );
	public float filter(float b );
	public double filter(double b );
	public boolean filter(boolean b );
	public <T> T filter(T b );

}
