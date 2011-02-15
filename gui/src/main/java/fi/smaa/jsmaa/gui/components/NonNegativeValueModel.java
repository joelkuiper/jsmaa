/*
    This file is part of JSMAA.
    JSMAA is distributed from http://smaa.fi/.

    (c) Tommi Tervonen, 2009-2010.
    (c) Tommi Tervonen, Gert van Valkenhoef 2011.

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
package fi.smaa.jsmaa.gui.components;

import javax.swing.JComponent;

import com.jgoodies.binding.value.ValueModel;

import fi.smaa.jsmaa.gui.presentation.MultiVetoableValueModel;

@SuppressWarnings("serial")
public class NonNegativeValueModel extends MultiVetoableValueModel {
	
	private String typeLabel;

	public NonNegativeValueModel(JComponent parent, ValueModel subject, String typeLabel) {
		super(parent, subject);
		this.typeLabel = typeLabel;
	}
	
	@Override
	public boolean proposedChange(Object oldVal, Object newVal) {
		if ((Double) newVal < 0.0) {
			errorMessage(typeLabel + " cannot be negative");
			return false;
		}
		return super.proposedChange(oldVal, newVal);
	}
}
