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

package fi.smaa.jsmaa.simulator;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import fi.smaa.common.JUnitUtil;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.ExactMeasurement;
import fi.smaa.jsmaa.model.Interval;
import fi.smaa.jsmaa.model.OutrankingCriterion;
import fi.smaa.jsmaa.model.SMAATRIModel;

public class SMAATRISimulatorTest {

	private SMAATRIModel model;
	private Alternative alt1 = new Alternative("alt1");
	private Alternative alt2 = new Alternative("alt2");
	private OutrankingCriterion c1 = new OutrankingCriterion("c1", true, 
			new Interval(0.0, 0.0), new Interval(1.0, 1.0));
	private OutrankingCriterion c2 = new OutrankingCriterion("c2", true,
			new Interval(0.0, 0.0), new Interval(1.0, 1.0));
	private Alternative cat1 = new Alternative("cat1");
	private Alternative cat2 = new Alternative("cat2");
	private Set<Alternative> alts;
	private Set<Criterion> crit;		
	private List<Alternative> cats;
	
	@Before
	public void setUp() {
		alts = new HashSet<Alternative>();
		crit = new HashSet<Criterion>();
		cats = new ArrayList<Alternative>();
		alts.add(alt1);
		alts.add(alt2);
		crit.add(c1);
		crit.add(c2);
		cats.add(cat1);
		cats.add(cat2);
		model = new SMAATRIModel("model");
		model.addAlternative(alt1);
		model.addAlternative(alt2);
		model.addCriterion(c1);
		model.addCriterion(c2);
		model.addCategory(cat1);
		model.addCategory(cat2);
		model.setMeasurement(c1, alt1, new ExactMeasurement(2.0));
		model.setMeasurement(c2, alt1, new ExactMeasurement(2.0));
		model.setMeasurement(c1, alt2, new ExactMeasurement(0.0));
		model.setMeasurement(c2, alt2, new ExactMeasurement(0.0));
		model.setCategoryUpperBound(c1, cat1, new ExactMeasurement(1.0));
		model.setCategoryUpperBound(c2, cat1, new ExactMeasurement(1.0));
		model.setRule(true);
	}
	
	@Test
	public void testOneCategory() throws InterruptedException {
		model.deleteCategory(cat1);
		SMAASimulator simulator = new SMAASimulator(model, new SMAATRISimulationThread(model, 10000));		
		simulator.restart();
		while (simulator.isRunning()) {
			Thread.sleep(10);
		}
		
		SMAATRIResults res = (SMAATRIResults) simulator.getResults();
		Map<Alternative, List<Double>> accs = res.getCategoryAcceptabilities();		
		assertEquals(1.0, accs.get(alt1).get(0), 0.00001);
		assertEquals(1.0, accs.get(alt2).get(0), 0.00001);
	}
	
	@Test
	public void testConstructor() {
		SMAASimulator simulator = new SMAASimulator(model, new SMAATRISimulationThread(model, 100));		
		assertEquals(100, simulator.getTotalIterations().intValue());
	}
	
	@Test
	public void testCorrectResults() throws InterruptedException {
		SMAASimulator simulator = new SMAASimulator(model, new SMAATRISimulationThread(model, 10000));		
		simulator.restart();
		while (simulator.isRunning()) {
			Thread.sleep(10);
		}
		
		SMAATRIResults res = (SMAATRIResults) simulator.getResults();		
		Map<Alternative, List<Double>> accs = res.getCategoryAcceptabilities();
		
		assertEquals(0.0, accs.get(alt1).get(0), 0.00001);
		assertEquals(1.0, accs.get(alt1).get(1), 0.00001);
		
		assertEquals(1.0, accs.get(alt2).get(0), 0.00001);
		assertEquals(0.0, accs.get(alt2).get(1), 0.00001);	
	}
	
	@Test
	public void testCorrectResultsPessimistic() throws InterruptedException {
		model.setRule(false);
		SMAASimulator simulator = new SMAASimulator(model, new SMAATRISimulationThread(model, 10000));
		simulator.restart();
		while (simulator.isRunning()) {
			Thread.sleep(10);
		}
		
		SMAATRIResults res = (SMAATRIResults) simulator.getResults();		
		Map<Alternative, List<Double>> accs = res.getCategoryAcceptabilities();
		
		assertEquals(1.0, accs.get(alt1).get(0), 0.00001);
		assertEquals(0.0, accs.get(alt1).get(1), 0.00001);
		
		assertEquals(1.0, accs.get(alt2).get(0), 0.00001);
		assertEquals(0.0, accs.get(alt2).get(1), 0.00001);	
	}
	
	// ignore because mock doesnt match the event
	@Test
	public void testInvalidUpperBoundsFire() throws InterruptedException {
		Alternative cat3 = new Alternative("cat3");
		model.addCategory(cat3);
		model.setCategoryUpperBound(c1, cat1, new ExactMeasurement(1.0));
		model.setCategoryUpperBound(c1, cat2, new ExactMeasurement(0.0));
		SMAASimulator simulator = new SMAASimulator(model, new SMAATRISimulationThread(model, 10000));		
		
		SMAAResultsListener mock = createMock(SMAAResultsListener.class);
		mock.resultsChanged((ResultsEvent) JUnitUtil.eqEventObject(new ResultsEvent(simulator.getResults(),
				new IterationException(""))));
		
		replay(mock);
		simulator.getResults().addResultsListener(mock);		
		simulator.restart();
		Thread.sleep(100);
		verify(mock);
	}

	// ignore because mock doesnt match the event
	@Test
	public void testInvalidThresholdsFire() throws InterruptedException {
		c1.setIndifMeasurement(new ExactMeasurement(3.0));
		c1.setPrefMeasurement(new ExactMeasurement(2.0));
		SMAASimulator simulator = new SMAASimulator(model, new SMAATRISimulationThread(model, 10000));		
		
		SMAAResultsListener mock = createMock(SMAAResultsListener.class);
		mock.resultsChanged((ResultsEvent) JUnitUtil.eqEventObject(new ResultsEvent(simulator.getResults(),
				new IterationException(""))));
		
		replay(mock);
		simulator.getResults().addResultsListener(mock);		
		simulator.restart();
		Thread.sleep(100);
		verify(mock);
	}
	
	@Test
	public void testOneCriterionZeroBoundUpperBound() throws InterruptedException {
		model.setCategoryUpperBound(c1, cat1, new ExactMeasurement(0.0));
		model.setCategoryUpperBound(c2, cat1, new ExactMeasurement(0.0));

		SMAASimulator simulator = new SMAASimulator(model, new SMAATRISimulationThread(model, 1));		
		
		simulator.restart();
		Thread.sleep(10);
		assertEquals(new Double(0.0), ((SMAATRIResults)simulator.getResults()).getCategoryAcceptabilities().get(alt1).get(0));
	}
}