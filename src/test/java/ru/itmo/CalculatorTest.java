package ru.itmo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {
	public CalculatorImpl calc = new CalculatorImpl();
	@Test
	public void testAdd() {
		assertEquals(8, calc.sum(3, 5));
	}
	@Test
	public void testNegAdd() {
		assertEquals(315, calc.sum(645, -330));
	}
	@Test
	public void testZeroAdd() {
		assertEquals(10, calc.sum(0, 10));
	}
	@Test
	public void testMul() {
		assertEquals(69, calc.mul(3, 23));
	}
	@Test
	public void testZeroMul() {
		assertEquals(0, calc.mul(15162, 0));
	}
	@Test
	public void testNegativeMul() {
		assertEquals(-815024, calc.mul(532, -1532));
	}
	@Test
	public void testMulNegNeg() {
		assertEquals(8056, calc.mul(-53, -152));
	}
	@Test
	public void testSub() {
		assertEquals(372, calc.sub(1634, 1262));
	}
	@Test
	public void testNegativeSub() {
		assertEquals(2064, calc.sub(532, -1532));
	}
	@Test
	public void testSubNegate() {
		assertEquals(1233, calc.sub(0, -1233));
	}
	@Test
	public void testSubZero() {
		assertEquals(15235, calc.sub(15235, 0));
	}
	@Test
	public void testDiv() {
		assertEquals(3047, calc.div(15235, 5));
	}
	@Test
	public void testZeroDiv() {
		assertEquals(0, calc.div(0, 5123));
	}
	@Test
	public void testDivNegate() {
		assertEquals(-1235, calc.div(1235, -1));
	}
	@Test
	public void testDivNotInt() {
		assertEquals(0.625, calc.div(5, 8));
	}
}
