package org.semanticcloud.agents.broker;

public class NegotiationParameter {

    private double weight;

    private Object value;

    private Object minValue;

    private Object maxValue;

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
}
