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
 * Created: Jun 26, 2012
 */

package adasim.model.internal;

import java.util.HashMap;
import java.util.Map;

import adasim.filter.IdentityFilter;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class FilterMap {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	
	private Map<Class<?>, Filters> filterMap;
	
	/**
	 * Creates a new filter map that contains only the default mappings for 
	 * <code>java.lang.Object</code>:
	 * <ul>
	 * <li><code>uncertaintyFilter</code>: <code>adasim.filter.Identityfilter</code>
	 * <li><code>privacyFilter</code>: <code>adasim.filter.IndentityFilter</code>
	 */
	public FilterMap() {
		this.filterMap = new HashMap<Class<?>, Filters>();
		Filters f = new Filters();
		f.uncertaintyFilter = new IdentityFilter();
		f.pMap.addFilter(new IdentityFilter(), Object.class );
		filterMap.put(Object.class, f);
	}

	/**
	 * Updates the filter map for class <code>clazz</code>. 
	 * Updating can only add filterMap, but cannot remove them.
	 * 
	 * @param clazz
	 * @param filterMap
	 */
	void update( Class<?> clazz, Filters filters ) {
		Filters f = filterMap.get(clazz);
		if ( f == filters ) return; //no updating needed
		if ( f == null ) {
			filterMap.put( clazz, filters );
		} else {
			if ( filters.uncertaintyFilter != null ) {
				f.uncertaintyFilter = filters.uncertaintyFilter;
			}
			for ( Class<?> c : filters.pMap ) {
				f.pMap.addFilter(filters.pMap.getFilter(c), c);
			}
		}
	}
	
	/**
	 * Updates this map with all elements in the other map. Deleting
	 * elements is not possible.
	 * 
	 * @param other
	 */
	void updateAll( FilterMap other ) {
		for ( Class<?> c : other.filterMap.keySet() ) {
			this.update(c, other.get(c) );
		}
	}
	
	/**
	 * @param clazz
	 * @return the filter mapping for the class, or the default mapping
	 */
	public Filters get(Class<?> clazz) {
		Filters f = filterMap.get(clazz);
		if ( f == null ) {
			Filters t = filterMap.get( Object.class );
			f = new Filters();
			f.uncertaintyFilter = t.uncertaintyFilter;
			f.pMap = t.pMap.deepCopy();
		}
		assert f != null;
		return f;
	}
}
