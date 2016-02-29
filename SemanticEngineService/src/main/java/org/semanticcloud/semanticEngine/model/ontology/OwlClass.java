package org.semanticcloud.semanticEngine.model.ontology;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntClass;
import org.semanticweb.owlapi.model.IRI;


public class OwlClass extends OwlElement {

    private OwlClass superClass;
	private List<OntoProperty> properties;
    private String label;

	public OwlClass(String namespace, String name, List<OntoProperty> properties) {
		this.namespace = namespace;
		this.localName = name;
		this.properties = properties;
	}

	public OwlClass(OntClass ontClass) {
		this(ontClass.getNameSpace(), ontClass.getLocalName(), new ArrayList<OntoProperty>());
        this.label = ontClass.getLabel(null);
        if(label != null && label.isEmpty())
            label = null;
	}

    public OwlClass(OntClass ontClass, OntClass ontSuperClass) {
        this(ontClass.getNameSpace(), ontClass.getLocalName(), new ArrayList<OntoProperty>());
        this.superClass = new OwlClass(ontSuperClass);
    }

	public OwlClass(IRI iri, List<OntoProperty> properties) {
		this(iri.getScheme(), iri.getFragment(), properties);
	}

	public List<OntoProperty> getProperties() {
		return properties;
	}

	public OntoProperty findProperty(String propertyLocalName) {
		for (OntoProperty prop : getProperties()) {
			if (prop.getLocalName().equals(propertyLocalName)) {
				return prop;
			}
		}
		return null;
	}

    public String getDisplayName() {
        String localDisplayName = label;
        if(localDisplayName == null)
            localDisplayName = localName;
        if(superClass != null)
            return String.format("%s\\%s", superClass.getDisplayName(), localDisplayName);
        else
            return localDisplayName;
    }

    public OwlClass getSuperClass() {
        return superClass;
    }

    public String getLabel() {
        return label;
    }

}
