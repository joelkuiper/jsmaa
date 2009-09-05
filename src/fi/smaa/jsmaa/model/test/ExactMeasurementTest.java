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

import static org.junit.Assert.*;

import org.junit.Test;

import fi.smaa.common.JUnitUtil;
import fi.smaa.jsmaa.model.ExactMeasurement;

public class ExactMeasurementTest {

	@Test
	public void testConstructor() {
		ExactMeasurement m = new ExactMeasurement(2.0);
		assertEquals(2.0, m.getValue(), 0.0000001);
	}
	
	@Test
	public void testSetValue() {
		JUnitUtil.testSetter(new ExactMeasurement(0.0), ExactMeasurement.PROPERTY_VALUE,
				new Double(0.0), new Double(2.0));
	}
}
