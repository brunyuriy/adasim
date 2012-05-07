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
 * Created: May 4, 2012
 */

package adasim.util;

import pjunit.ProtectedAccessor;

/**
 * @param s the object to call the method on
 * @param propery the name of the zero-argument method to be called
 * 
 * @throws NoSuchMethodException if the passed in object does not declare the 
 * property method specified
 * @throws ReflectionException for any other type of exception caused by 
 * reflection. The original exception is nested for inspection.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class ReflectionUtils {
	public static <T> T getProperty( Object target, String property ) throws NoSuchMethodException, ReflectionException {
		try {
			return ProtectedAccessor.invoke(target, property, new Object[0] );
		} catch (NoSuchMethodException e) {
			throw e;
		} catch ( Exception e ) {
			throw new ReflectionException( e );
		}
	}
}
