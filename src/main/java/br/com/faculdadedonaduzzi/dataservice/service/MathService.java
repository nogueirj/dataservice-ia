package br.com.faculdadedonaduzzi.dataservice.service;

public class MathService {

    public double[] normalize(double val1, double val2, double val3) {
    double max = Math.max(Math.max(val1, val2), Math.max(val3, val1));
    double min = Math.min(Math.min(val1, val2), Math.min(val3, val1));
    double median = ((max + min) / 2);
    return new double[]{median, median};
    }
}
