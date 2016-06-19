package org.semanticcloud.agents.broker;

import java.util.ArrayList;
import java.util.List;

public class Criterion {

    private double weight = 0.0;

    private Object value;

    private Object minValue;

    private Object maxValue;

    private String propertyName;
    private String description;
    private Criterion parent;
    private List<Criterion> nestedParams = new ArrayList<>();
    private CriterionType type = CriterionType.BENEFIT;

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getMinValue() {
        return minValue;
    }

    public void setMinValue(Object minValue) {
        this.minValue = minValue;
    }

    public Object getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Object maxValue) {
        this.maxValue = maxValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Criterion getParent() {
        return parent;
    }

    public void setParent(Criterion parent) {
        this.parent = parent;
    }

    public List<Criterion> getNestedParams() {
        return nestedParams;
    }

    public void setNestedParams(List<Criterion> nestedParams) {
        this.nestedParams = nestedParams;
    }

    public void addNestedParam(Criterion nestedParam){
        nestedParams.add(nestedParam);
        nestedParam.setParent(this);
    }
}
