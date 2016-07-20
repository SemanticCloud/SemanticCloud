package org.semanticcloud.semanticEngine.model.conditions;

import org.semanticcloud.semanticEngine.model.ontology.properties.OwlDatatypeProperty;

public class DatatypePropertyCondition extends PropertyCondition<String,OwlDatatypeProperty> {
    private String value;
    private String operator;

    public DatatypePropertyCondition(){

    }

    public DatatypePropertyCondition(String propertyUri, String operator, String value) {
        super(propertyUri);
        this.operator = operator;
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public void setUri(String propertyUri) {
        super.setUri(propertyUri);
    }

    @Override
    public String getUri() {
        return super.getUri();
    }
}
