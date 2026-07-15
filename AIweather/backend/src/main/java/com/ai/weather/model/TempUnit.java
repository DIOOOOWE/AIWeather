
package com.ai.weather.model;

public enum TempUnit {
    CELSIUS("\u00B0C", "摄氏度"),
    FAHRENHEIT("\u00B0F", "华氏度");

    private final String symbol;
    private final String label;

    TempUnit(String symbol, String label) {
        this.symbol = symbol;
        this.label = label;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getLabel() {
        return label;
    }

    public int convert(int celsius) {
        if (this == FAHRENHEIT) {
            return celsius * 9 / 5 + 32;
        }
        return celsius;
    }
}
