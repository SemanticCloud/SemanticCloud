package org.semanticcloud.semanticEngine.controll.reading.jena;

import org.semanticcloud.semanticEngine.entity.models.ConfigurationException;
import org.semanticcloud.semanticEngine.entity.models.InvalidConfigurationException;
import org.semanticcloud.semanticEngine.model.conditions.PropertyCondition;
import org.semanticcloud.semanticEngine.model.ontology.OwlClass;
import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;
import org.semanticcloud.semanticEngine.model.ontology.OwlIndividual;
import org.semanticcloud.semanticEngine.controll.reading.OntologyReader;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class JenaOwlReader extends OntologyReader{
	private OntModel model;
	private String uri;
	private boolean ignorePropsWithNoDomain;



	private JenaOwlReader(OntModel model) {
		this.model = model;
		String namespace = model.getNsPrefixURI("");
		this.uri = namespace.substring(0, namespace.length() - 1);
	}

	public JenaOwlReader(OntModel model, boolean ignorePropsWithNoDomain) {
		this(model);
		this.ignorePropsWithNoDomain = ignorePropsWithNoDomain;
	}

	/* (non-Javadoc)
	 * @see models.ontologyReading.jena.OntologyReader#getOwlClass(java.lang.String)
	 */
	public OwlClass getOwlClass(String className) {
		if (!(className.contains("#"))) {
			className = String.format("%s#%s", uri, className);
		}
		OntClass ontClass = model.getOntClass(className);
		if (ontClass == null)
			return null;

		OwlClass owlClass = new OwlClass(ontClass.getNameSpace(), ontClass.getLocalName(), getProperties(ontClass));
		return owlClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.PropertyProvider#getProperty(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see models.ontologyReading.jena.OntologyReader#getProperty(java.lang.String)
	 */
    public OntoProperty getProperty(String propertyUri) throws ConfigurationException {
        if (!(propertyUri.contains("#"))) {
            propertyUri = String.format("%s#%s", uri, propertyUri);
        }
        OntProperty ontProperty = model.getOntProperty(propertyUri);
        if(ontProperty == null){
            throw new ConfigurationException(String.format("Property %s not found in ontology", propertyUri));
        }
        return createProperty(ontProperty);
    }

	private List<OntoProperty> getProperties(OntClass ontClass) {
		List<OntoProperty> props = new ArrayList<OntoProperty>();


		for (Iterator<OntProperty> i = ontClass.listDeclaredProperties(); i.hasNext();) {
			OntProperty prop = i.next();
			if (prop.getDomain() != null || !ignorePropsWithNoDomain)
			try{
				props.add(createProperty(prop));
			}
			catch(InvalidConfigurationException ex){
				ex.printStackTrace();
			}			
		}

		// The below code should not be needed, but it may depend on the
		// reasoner used (how smart it is).
		// for (Iterator<OntProperty> i = model.listOntProperties();
		// i.hasNext();) {
		// OntProperty prop = i.next();
		//
		// if (isInUnionDomain(ontClass, prop)) {
		// props.add(createProperty(prop));
		// }
		// }
		return props;
	}

	private OntoProperty createProperty(OntProperty prop) {
		return OwlPropertyFactory.createOwlProperty(prop);
	}

	/* (non-Javadoc)
	 * @see models.ontologyReading.jena.OntologyReader#getIndividualsInRange(models.ontologyModel.OntoClass, models.ontologyModel.OntoProperty)
	 */
	public List<OwlIndividual> getIndividualsInRange(OwlClass owlClass, OntoProperty property) {
		List<OwlIndividual> individuals = new ArrayList<OwlIndividual>();

		for (OntClass rangeClass : getAllClassesFromRange(property)) {
			for (ExtendedIterator<? extends OntResource> i = rangeClass.listInstances(); i.hasNext();) {
				Individual individual = i.next().asIndividual();

				OwlIndividual owlIndividual = new OwlIndividual(individual, new ArrayList<PropertyCondition>());
				if (!individuals.contains(owlIndividual))
					individuals.add(owlIndividual);
			}
		}
		return individuals;
	}

	/* (non-Javadoc)
	 * @see models.ontologyReading.jena.OntologyReader#getClassesInRange(models.ontologyModel.OntoClass, models.ontologyModel.OntoProperty)
	 */
	public List<OwlClass> getClassesInRange2(OwlClass owlClass, OntoProperty property) {
		List<OwlClass> classes = new ArrayList<OwlClass>();

		for (OntClass ontClass : getAllClassesFromRange(property)) {
			classes.add(new OwlClass(ontClass));
		}

		return classes;
	}

    /* (non-Javadoc)
     * @see models.ontologyReading.jena.OntologyReader#getClassesInRange(models.ontologyModel.OntoClass, models.ontologyModel.OntoProperty)
     */
    public List<OwlClass> getClassesInRange(OwlClass owlClass, OntoProperty property) {
        List<OwlClass> classes = new ArrayList<OwlClass>();

        OntProperty ontProp = model.getOntProperty(property.getUri());
        for (ExtendedIterator<? extends OntResource> r = ontProp.listRange(); r.hasNext();) {
            OntResource res = r.next();
            if (ontProp.hasRange(res)) {
                if (res.isClass()) {
                    OntClass rangeClass = res.asClass();

                    classes.add(new OwlClass(rangeClass));
                    fillWithSubClasses(classes, rangeClass);
                }
            }
        }
        return classes;
    }

    @Override
    public List<OwlClass> getOwlSubclasses(String classUri) {
        return null;
    }

    private List<OntClass> getAllClassesFromRange(OntoProperty property) {
		List<OntClass> classes = new ArrayList<OntClass>();

		OntProperty ontProp = model.getOntProperty(property.getUri());
		for (ExtendedIterator<? extends OntResource> r = ontProp.listRange(); r.hasNext();) {
			OntResource res = r.next();
			if (ontProp.hasRange(res)) {
				if (res.isClass()) {
					OntClass rangeClass = res.asClass();
					
					classes.add(rangeClass);
					for (ExtendedIterator<? extends OntResource> s = rangeClass.listSubClasses(); s.hasNext();) {
						OntClass subclass = s.next().asClass();
						if (!subclass.getURI().equals("http://www.w3.org/2002/07/owl#Nothing"))
							classes.add(subclass);
					}
				}
			}
		}

		return classes;
	}




    private void fillWithSubClasses(List<OwlClass> classes, OntClass superClass){
        for (ExtendedIterator<? extends OntResource> s = superClass.listSubClasses(true); s.hasNext();) {
            OntClass subclass = s.next().asClass();
            if (!subclass.getURI().equals("http://www.w3.org/2002/07/owl#Nothing")){
                classes.add(new OwlClass(subclass, superClass));
                fillWithSubClasses(classes, subclass);
            }
        }
    }
}
