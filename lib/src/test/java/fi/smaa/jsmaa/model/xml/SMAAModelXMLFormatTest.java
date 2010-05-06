package fi.smaa.jsmaa.model.xml;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javolution.xml.XMLObjectReader;
import javolution.xml.stream.XMLStreamException;

import org.junit.Test;

import fi.smaa.common.JUnitUtil;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.CardinalPreferenceInformation;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.ExactMeasurement;
import fi.smaa.jsmaa.model.Interval;
import fi.smaa.jsmaa.model.ModelChangeEvent;
import fi.smaa.jsmaa.model.OrdinalPreferenceInformation;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.model.SMAAModelListener;
import fi.smaa.jsmaa.model.ScaleCriterion;

public class SMAAModelXMLFormatTest {

	@Test
	public void testMarshalSMAAModel() throws XMLStreamException {	
		ScaleCriterion c = new ScaleCriterion("c");
		Alternative a = new Alternative("a");
		
		SMAAModel model = new SMAAModel("model");
		model.addAlternative(a);
		model.addCriterion(c);
		CardinalPreferenceInformation pref = new CardinalPreferenceInformation(model.getCriteria());
		pref.setMeasurement(c, new Interval(0.0, 1.0));
		model.setPreferenceInformation(pref);
		model.setMeasurement(c, a, new ExactMeasurement(2.0));
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JSMAABinding.writeModel(model, bos);
		SMAAModel nmodel = JSMAABinding.readModel(new ByteArrayInputStream(bos.toByteArray()));
		
		Criterion nCrit = nmodel.getCriteria().get(0);
		Alternative nAlt = nmodel.getAlternatives().get(0);
				
		assertEquals(model.getName(), nmodel.getName());
		assertEquals(1, model.getAlternatives().size());
		assertEquals(1, model.getCriteria().size());
		assertEquals(c.getName(), nCrit.getName());
		assertEquals(a.getName(), nAlt.getName());
	
		assertEquals(model.getMeasurement(c, a), nmodel.getMeasurement(nCrit, nAlt));
		CardinalPreferenceInformation npref = (CardinalPreferenceInformation) nmodel.getPreferenceInformation();
		assertEquals(new Interval(0.0, 1.0), npref.getMeasurement(nCrit));
	}
	
	@Test
	public void testPreferenceListenerConnects() throws XMLStreamException {
		ScaleCriterion c = new ScaleCriterion("c");
		ScaleCriterion c2 = new ScaleCriterion("c");
		
		SMAAModel model = new SMAAModel("model");
		model.addCriterion(c);
		model.addCriterion(c2);
		OrdinalPreferenceInformation pref = new OrdinalPreferenceInformation(model.getCriteria());
		model.setPreferenceInformation(pref);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JSMAABinding.writeModel(model, bos);
		SMAAModel nmodel = JSMAABinding.readModel(new ByteArrayInputStream(bos.toByteArray()));		
		
		SMAAModelListener mock = createMock(SMAAModelListener.class);
		mock.modelChanged((ModelChangeEvent) JUnitUtil.eqEventObject(new ModelChangeEvent(nmodel, ModelChangeEvent.PREFERENCES)));
		expectLastCall().atLeastOnce();		
		replay(mock);
		nmodel.addModelListener(mock);		
		OrdinalPreferenceInformation npref = (OrdinalPreferenceInformation) nmodel.getPreferenceInformation();
		npref.getMeasurement(npref.getCriteria().get(0)).setRank(2);
		verify(mock);
	}
	
	@Test(expected=InvalidModelVersionException.class)
	public void testReadInvalidModelVersion() throws XMLStreamException {
		String smaaMod = "<SMAA-2-model name=\"model\" modelVersion=\"1000\"/>";
		XMLObjectReader reader = new XMLObjectReader().setInput(new StringReader(smaaMod)).setBinding(new JSMAABinding());
		@SuppressWarnings("unused")
		SMAAModel mod = reader.read();
	}
}