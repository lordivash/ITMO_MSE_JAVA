package ru.itmo;

public class CalculatorImpl implements Calculator {
    public int sum(int x, int y) {
        return x + y;
    }

    public int sub(int x, int y) {
        return x - y;
    }

    public int mul(int x, int y) {
        return x * y;
    }

    public double div(int x, int y) {
        return (double) x / y;
    }
}
