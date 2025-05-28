package ru.itmo;

abstract public class SuperTest {
    int a;
    int b;

    public SuperTest(int a, int b) {
        this.a = a;
        this.b = b;
    }

    abstract public void superTestFunction1();
    abstract public void superTestFunction2(int z);
}
