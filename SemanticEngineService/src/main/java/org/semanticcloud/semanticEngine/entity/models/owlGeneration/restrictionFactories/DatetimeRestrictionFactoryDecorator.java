package org.semanticcloud.semanticEngine.entity.models.owlGeneration.restrictionFactories;

import org.semanticcloud.semanticEngine.entity.models.ConfigurationException;
import org.semanticcloud.semanticEngine.entity.models.owlGeneration.RestrictionFactory;
import org.semanticcloud.semanticEngine.entity.models.propertyConditions.DatatypePropertyCondition;
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
		DateTime datetime = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm").parseDateTime(condition.getDatatypeValue().trim());
		
		//10/26/2001 00:00 
		condition.setDatatypeValue(datetime.toString(ISODateTimeFormat.dateHourMinuteSecond()));
		return restrictionFactory.createRestriction(condition);
	}

}
