package ru.itmo;

abstract public class Test extends SuperTest implements Comparable {
    private int a;
    private int b;
    public int c;

    public Test(int x, int z, int a, int b, int c) {
        super(x, z);
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public void set(int a, int b) {
        this.a = a;
        this.b = b;
    }

    abstract public int add(int a, int b);
    abstract public int mul(int a, int b);
}
