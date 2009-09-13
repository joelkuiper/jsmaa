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

package fi.smaa.jsmaa.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SMAATRIModel extends SMAAModel {
	
	private static final long serialVersionUID = -739020656344899318L;
	private ImpactMatrix profileMatrix;
	private List<Alternative> categories = new ArrayList<Alternative>();
	private boolean optimistic;
	private Interval lambda;
	private transient LambdaListener lambdaListener;
	
	public static final String PROPERTY_RULE = "rule";
	public static final String PROPERTY_LAMBDA = "lambda";

	public SMAATRIModel(String name) {
		super(name);
		profileMatrix = new ImpactMatrix();
		optimistic = true;
		lambda = new Interval(0.6, 0.85);
		connectProfileListener();
		connectLambdaListener();
	}
	
	public void setRule(boolean optimistic) {
		this.optimistic = optimistic;
		fireModelChange(ModelChangeEvent.PARAMETER);
	}
		
	public Interval getLambda() {
		return lambda;
	}
		
	public boolean getRule() {
		return optimistic;
	}
	
	public void setCategories(List<Alternative> categories) {
		this.categories = categories;
		profileMatrix.setAlternatives(this.categories);
		fireModelChange(ModelChangeEvent.CATEGORIES);
	}
	
	public void addCategory(Alternative cat) {
		List<Alternative> newCats = new ArrayList<Alternative>(categories);
		newCats.add(cat);
		setCategories(newCats);
	}
	
	public void deleteCategory(Alternative cat) {
		List<Alternative> newCats = new ArrayList<Alternative>(categories);
		newCats.remove(cat);
		setCategories(newCats);		
	}
	
	public List<Alternative> getCategories() {
		return categories;
	}
		
	/**
	 * All criteria should be outranking-criteria.
	 */
	@Override
	public synchronized void setCriteria(Collection<Criterion> crit) {
		super.setCriteria(crit);
		for (Criterion c : crit) {
			if (!(c instanceof OutrankingCriterion)) {
				throw new IllegalArgumentException("All criteria should be outranking-criteria");
			}
		}
		profileMatrix.setCriteria(getCriteria());
	}
	
	@Override
	public void setMeasurement(CardinalCriterion crit, Alternative alt, CardinalMeasurement meas) {
		if (profileMatrix.getAlternatives().contains(alt)) {
			setCategoryUpperBound((OutrankingCriterion) crit, alt, meas);
		} else {
			super.setMeasurement(crit, alt, meas);
		}
	}
	
	@Override
	public CardinalMeasurement getMeasurement(CardinalCriterion crit, Alternative alt) {
		if (profileMatrix.getAlternatives().contains(alt)) {
			return getCategoryUpperBound((OutrankingCriterion) crit, alt);
		} else {
			return super.getMeasurement(crit, alt);
		}		
	}
		
	public void setCategoryUpperBound(OutrankingCriterion crit, 
			Alternative category, CardinalMeasurement meas) {
		profileMatrix.setMeasurement(crit, category, meas);
	}
	
	public CardinalMeasurement getCategoryUpperBound(OutrankingCriterion crit, Alternative category) {
		return profileMatrix.getMeasurement(crit, category);
	}
	
	private void connectProfileListener() {
		profileMatrix.addListener(impactListener);	
	}
	
	private void connectLambdaListener() {
		lambdaListener = new LambdaListener();
		lambda.addPropertyChangeListener(lambdaListener);
	}	

	private void readObject(ObjectInputStream i) throws IOException, ClassNotFoundException {
		i.defaultReadObject();
		connectProfileListener();
		connectLambdaListener();
	}
	
	private class LambdaListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			fireModelChange(ModelChangeEvent.PARAMETER);
		}
	}
	
	@Override
	synchronized public SMAATRIModel deepCopy() {
		SMAATRIModel model = new SMAATRIModel(getName());
		super.deepCopyContents(model);
		List<Alternative> cats = new ArrayList<Alternative>();
		for (Alternative cat : categories) {
			cats.add(cat.deepCopy());
		}
		model.categories = cats;
		model.profileMatrix = profileMatrix.deepCopy(model.getCategories(), model.getCriteria());
		model.setRule(optimistic);		
		model.getLambda().setStart(getLambda().getStart());
		model.getLambda().setEnd(getLambda().getEnd());
		return model;
	}	
}