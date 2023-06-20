package fr.neyuux.uhc.util;

import java.util.Random;

public class Interval<T> {
    private final Random random;
    private final T min;
    private final T max;

    public Interval(T min, T max) {
        this.min = min;
        this.max = max;
        this.random = new Random();
    }

    public Integer getAsRandomInt() {
        return random.nextInt((Integer) max - (Integer) min + 1) + (Integer) min;
    }

    public Double getAsRandomDouble() {
        return (Double) min + random.nextDouble() * ((Double) max - (Double) min);
    }
}
