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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import adasim.filter.AdasimFilter;
import adasim.filter.FakeFilter;
import adasim.filter.IdentityFilter;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class PrivacyFilterMapTest {
	
	private PrivacyFilterMap map;
	
	@Before
	public void setUp() {
		map = new PrivacyFilterMap();
	}

	@Test
	public void hasIdentityFilterDefault() {
		assertEquals( IdentityFilter.class, map.getFilter( Object.class).getClass() );
	}
	
	@Test
	public void returnsDefaultIfNoMap() {
		assertEquals( IdentityFilter.class, map.getFilter( this.getClass()).getClass() );
	}
	
	@Test
	public void storesFilter() {
		AdasimFilter f = map.addFilter(new FakeFilter(), this.getClass() );
		assertNull( "should be null", f);
		assertEquals( FakeFilter.class, map.getFilter(getClass()).getClass() );
	}

	@Test
	public void updatesFilter() {
		AdasimFilter f = map.addFilter(new FakeFilter(), this.getClass() );
		assertNull( "should be null", f);
		assertEquals( FakeFilter.class, map.getFilter(this.getClass()).getClass() );
		f = map.addFilter( new IdentityFilter(), this.getClass() );
		assertEquals( FakeFilter.class, f.getClass() );
		assertEquals( IdentityFilter.class, map.getFilter(this.getClass()).getClass() );
	}
	
	@Test
	public void updatesDefaultFilter() {
		map.addFilter(new FakeFilter(), Object.class );
		//check default explicitly
		assertEquals( FakeFilter.class, map.getFilter(Object.class).getClass() );
		//check default returned for non-existent mapping
		assertEquals( FakeFilter.class, map.getFilter(FakeFilter.class).getClass() );
	}

	@Test
	public void removesFilter() {
		AdasimFilter f = map.addFilter(new FakeFilter(), this.getClass() );
		assertNull( "should be null", f);
		assertEquals( FakeFilter.class, map.getFilter(getClass()).getClass() );
		map.removeFilter( this.getClass() );
		//filter is removed, so the default should be returned
		assertEquals( IdentityFilter.class, map.getFilter(getClass()).getClass() );		
	}

}
