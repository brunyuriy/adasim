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

package adasim.agent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import adasim.filter.AdasimFilter;
import adasim.filter.IdentityFilter;

/**
 * This class maps classes to privacy filters. It supports the privacy
 * filter framework by providing simple access to multiple
 * privacy filters based on classes they apply to.
 * <p>
 * The map is mostly intended to map agent classes to filters, but
 * supports other types of classes as well. 
 * <code>null</code> keys and values are <em>forbidden</em> and attempts
 * to add such values throw {@link IllegalArgumentException}s.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public final class PrivacyFilterMap implements Iterable<Class<?>> {

	private Map<Class<?>, AdasimFilter> filters;
	
	public PrivacyFilterMap() {
		this( new IdentityFilter() );
	}
	
	/**
	 * Constructs a new map with <code>deflt</code> as the default filter.
	 * The key for accessing or updating the default filter is Object.class.
	 * 
	 * @param deflt
	 * 
	 * @throws IllegalArgumentException if <code>deflt</code> is <code>null</code>.
	 */
	public PrivacyFilterMap( AdasimFilter deflt ) {
		if ( deflt == null )
			throw new IllegalArgumentException( "default filter must not be null" );
		filters = new HashMap<Class<?>, AdasimFilter>();
		filters.put(Object.class, deflt );
	}
	
	/**
	 * This method will return the filter mapped to the 
	 * <code>criterion</code> class, or the default filter configured.
	 * 
	 * @param criterion
	 * @return a matching filter or the default filter
	 */
	public AdasimFilter getFilter( Class<?> criterion ) {
		AdasimFilter f = filters.get(criterion);
		if ( f == null ) {
			f = filters.get( Object.class);
		}
		return f;
	}
	
	/**
	 * Adds the new filter for the the <code>criterion</code> to 
	 * the map.
	 * 
	 * @param filter the filter to be added.
	 * @param criterion the criterion the filter should be mapped to
	 * @return The filter that was previously mapped for the criterion or <code>null</code>.
	 * 
	 * @throws IllegalArgumentException if either <code>filter</code> or
	 * <code>criterion</code> are null
	 */
	public AdasimFilter addFilter( AdasimFilter filter, Class<?> criterion ) {
		if ( filter == null || criterion == null ) 
			throw new IllegalArgumentException( "filter and criterion must not be null" );
		AdasimFilter old = filters.get(criterion);
		filters.put(criterion, filter);
		return old;
	}
	
	/**
	 * Removes the filter mapped by the criterion if it exists.
	 * <p>
	 * You should <em>never</em> remove the default filter.
	 * 
	 * @param criterion
	 * 
	 * @throws IllegalArgumentException if the criterion is null
	 */
	public void removeFilter( Class<?> criterion) {
		if ( criterion == null )
			throw new IllegalArgumentException( "criterion must not be null" );
		filters.remove(criterion);
	}

	/**
	 * @return a deep copy of this filter map. <em>Note:</em> The filter instances are not copied.
	 */
	public PrivacyFilterMap deepCopy() {
		PrivacyFilterMap newMap = new PrivacyFilterMap();
		newMap.filters = new HashMap<Class<?>, AdasimFilter>(this.filters);
		return newMap;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Class<?>> iterator() {
		return filters.keySet().iterator();
	}
}
