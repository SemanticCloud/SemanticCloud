package org.semanticcloud.semanticEngine.model.ontology.properties;

public enum PropertyOperatorType {
    EQUAL_TO("equalTo"),
    GREATER_THAN("greaterThan"),
    LESS_THAN("lessThan"),
    EQUAL_TO_INDIVIDUAL("equalToIndividual"),
    DESCRIBED_WITH("describedWith"),
    CONSTRAINED_BY("constrainedBy");

    private final String name;

    PropertyOperatorType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
