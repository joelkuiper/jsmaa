/*
	This file is part of JSMAA.
	(c) Tommi Tervonen, 2009	

    JSMAA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JSMAA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JSMAA.  If not, see <http://www.gnu.org/licenses/>.
*/

package fi.smaa.jsmaa.model.test;

import org.junit.Before;
import org.junit.Test;

import fi.smaa.common.JUnitUtil;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.CardinalCriterion;

public class CardinalCriterionTest {
	
	private CardinalCriterion crit;
	
	@SuppressWarnings("serial")
	@Before
	public void setUp() {
		crit = new CardinalCriterion("crit", true) {
			@Override
			public String getTypeLabel() {
				return null;
			}
			public Criterion deepCopy() {
				return null;
			}			
		};
	}
	
	@Test
	public void testSetAscending() {
		JUnitUtil.testSetter(crit, CardinalCriterion.PROPERTY_ASCENDING, true, false);
	}

}
