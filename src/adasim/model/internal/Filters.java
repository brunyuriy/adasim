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
 * Created: Apr 23, 2012
 */

package adasim.model.internal;

import adasim.agent.PrivacyFilterMap;
import adasim.filter.AdasimFilter;
import adasim.filter.IdentityFilter;

/**
 * 
 * This class is used internally to load and manage
 * default filters.
 * <p>
 * This class also hard-codes required defaults.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class Filters {
	
	AdasimFilter uncertaintyFilter;
	PrivacyFilterMap pMap;
	
	public Filters() {
		uncertaintyFilter = new IdentityFilter();
		pMap = new PrivacyFilterMap();
	}
	
	/**
	 * Copy constructor makes a deep copy of the passed map.
	 * @param map
	 */
	public Filters( Filters map) {
		this.uncertaintyFilter = map.uncertaintyFilter;
		this.pMap = map.pMap.deepCopy();
	}
	
}