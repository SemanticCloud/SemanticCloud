package org.semanticcloud.semanticEngine.model.conditions;

import org.semanticcloud.semanticEngine.model.ontology.properties.OwlDatatypeProperty;

public class DatatypePropertyCondition extends PropertyCondition<OwlDatatypeProperty> {
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
    public void setPropertyUri(String propertyUri) {
        super.setPropertyUri(propertyUri);
    }

    @Override
    public String getPropertyUri() {
        return super.getPropertyUri();
    }
}
