package mock;

import java.util.List;

/**
 * Created by Tomasz on 6/5/2015.
 */
public class RateHistory {

    private String name;
    private List<Double> values;
    private List<String> labels;

    public RateHistory(String name, List<Double> values, List<String> labels) {
        this.name = name;
        this.values = values;
        this.labels = labels;
    }

    public RateHistory() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getValues() {
        return values;
    }

    public void setValues(List<Double> values) {
        this.values = values;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "RateHistory{" +
                "name='" + name + '\'' +
                ", values=" + values +
                ", labels=" + labels +
                '}';
    }
}
