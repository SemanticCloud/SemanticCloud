package org.semanticcloud.semanticEngine.controll.owlGeneration.restrictionFactories;

import org.semanticcloud.semanticEngine.entity.models.ConfigurationException;
import org.semanticcloud.semanticEngine.controll.owlGeneration.RestrictionFactory;
import org.semanticcloud.semanticEngine.model.conditions.DatatypePropertyCondition;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.semanticweb.owlapi.model.OWLClassExpression;

public class DatetimeRestrictionFactoryDecorator extends DatatypeRestrictionFactory{

	private final RestrictionFactory<DatatypePropertyCondition> restrictionFactory;

	public DatetimeRestrictionFactoryDecorator(RestrictionFactory<DatatypePropertyCondition> restrictionFactory) {
		this.restrictionFactory = restrictionFactory;		
	}

	@Override
	public OWLClassExpression createRestriction(DatatypePropertyCondition condition) throws ConfigurationException {
		DateTime datetime = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm").parseDateTime(condition.getValue().trim());
		
		//10/26/2001 00:00 
		condition.setValue(datetime.toString(ISODateTimeFormat.dateHourMinuteSecond()));
		return restrictionFactory.createRestriction(condition);
	}

}
