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

package fi.smaa.jsmaa.gui;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;


import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fi.smaa.common.gui.LayoutUtil;
import fi.smaa.common.gui.ViewBuilder;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.Rank;

public class PreferenceInformationView implements ViewBuilder {
	private SMAAModelPreferencePresentationModel model;
	private RankSelectorGroup selectorGroup;
	
	public PreferenceInformationView(SMAAModelPreferencePresentationModel model) {
		this.model = model;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref, 3dlu, left:pref:grow",
				"p, 3dlu, p, 3dlu, p" );
		
		int fullWidth = 5;
		
		ValueModel enabledModel 
			= model.getModel(SMAAModelPreferencePresentationModel.ORDINAL_ENABLED);

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Preferences", cc.xyw(1, 1, fullWidth));

		JCheckBox checkBox = BasicComponentFactory.createCheckBox(enabledModel,"");
		builder.add(checkBox, cc.xy(1, 3));

		builder.addLabel("Enable ordinal preferences", cc.xyw(3, 3, fullWidth-2));

		JLabel critLabel = new JLabel("Criterion");
		Bindings.bind(critLabel, "enabled", enabledModel);
		builder.add(critLabel, cc.xy(3, 5));
		JLabel rankLabel = new JLabel("Rank");
		Bindings.bind(rankLabel, "enabled", enabledModel);
		builder.add(rankLabel, cc.xy(5, 5));
		
		int row = 5;
		
		selectorGroup = new RankSelectorGroup(model.getOrdinalRanks());
		
		List<Rank> ordinalRanks = model.getOrdinalRanks();
		int i=0;
		for (Criterion c : model.getBean().getCriteria()) {			
			JLabel label = new JLabel();

			Bindings.bind(label, "text",
					new PresentationModel<Criterion>(c).getModel(
							Criterion.PROPERTY_NAME)
							);
			Bindings.bind(label, "enabled", enabledModel);
			row += 2;			
			LayoutUtil.addRow(layout);
			builder.add(label, cc.xy(3, row));
			
			JComboBox selector = createSelector(enabledModel, ordinalRanks, i);
			
			builder.add(selector, cc.xy(5, row));
			i++;
		}

		return builder.getPanel();
	}

	private JComboBox createSelector(ValueModel enabledModel,
			List<Rank> ordinalRanks, int i) {
		JComboBox selector = selectorGroup.getSelectors().get(i);
		Bindings.bind(selector, "enabled", enabledModel);
		return selector;
	}

}
