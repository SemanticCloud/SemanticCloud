package org.semanticcloud.semanticEngine.controll.reading.owlApi.propertyFactories;

import org.semanticcloud.semanticEngine.controll.reading.owlApi.OwlPropertyFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SimpleDatatypePropertyFactory extends OwlPropertyFactory {

	private final String[] rangeUris;
	
	public SimpleDatatypePropertyFactory(String... rangeUris) {
		super();
		this.rangeUris = rangeUris;
	}

	@Override
	public boolean canCreateProperty(OWLOntology onto, OWLProperty property) {
		if(!property.isOWLDataProperty())
			return false;
		OWLDataProperty dataProperty = property.asOWLDataProperty();

		
		Collection<OWLDataRange> ranges = EntitySearcher.getRanges(dataProperty, onto.getImports().stream()).collect(Collectors.toList());
		if(ranges.size() > 1){
			return false;
		}		
		
		for (OWLPropertyRange range : ranges) {
			Set<OWLDatatype> rangeDatatypes = range.getDatatypesInSignature();
			if(rangeDatatypes.size() > 1)
				return false;
			
			for (OWLDatatype datatype : rangeDatatypes) {
				String rangeUri = datatype.getIRI().toString();
				for (int i = 0; i < rangeUris.length; i++) {
					if(rangeUris[i].equalsIgnoreCase(rangeUri)){
						return true;
					}				
				}								
			}
		}		
		return false;
	}
}