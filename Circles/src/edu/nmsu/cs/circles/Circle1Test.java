package edu.nmsu.cs.circles;

/***
 * Example JUnit testing class for Circle1 (and Circle)
 *
 * - must have your classpath set to include the JUnit jarfiles - to run the test do: java
 * org.junit.runner.JUnitCore Circle1Test - note that the commented out main is another way to run
 * tests - note that normally you would not have print statements in a JUnit testing class; they are
 * here just so you see what is happening. You should not have them in your test cases.
 ***/

import org.junit.*;

public class Circle1Test
{
	// Data you need for each test case
	private Circle1 circle1;

	//
	// Stuff you want to do before each test case
	//
	@Before
	public void setup()
	{
		System.out.println("\nTest starting...");
		circle1 = new Circle1(1, 2, 3);
	}

	//
	// Stuff you want to do after each test case
	//
	@After
	public void teardown()
	{
		System.out.println("\nTest finished.");
	}

	
	//
	// Tests that two circles that are not intersecting are not intersecting
	//
	@Test
	public void intersectNoIntersection() {
		
		System.out.println("Running test: intersectNoIntersection.");
		
		// next to eachother same x
		System.out.println("intersectNoIntersection - next to eachother");
		Circle1 a = new Circle1(0, 50, 10);
		Circle1 b = new Circle1(0, 0, 5);
		
		Assert.assertFalse(a.intersects(b));
		Assert.assertFalse(b.intersects(a));
		
		// one above the other, almost touching
		System.out.println("intersectNoIntersection - one above, almost touching");
		a = new Circle1(1, 10, 2.99);
		b = new Circle1(1, 5, 2);
		
		Assert.assertFalse(a.intersects(b));
		Assert.assertFalse(b.intersects(a));
		
		// circle above to the right
		System.out.println("intersectNoIntersection - above to the right, very close");
		a = new Circle1(16, 128, 4);
		b = new Circle1(23.45, 121.6, 5);
		
		Assert.assertFalse(a.intersects(b));
		Assert.assertFalse(b.intersects(a));
		
	} // end noIntersection
	
	//
	// Tests that two circles that are comepletely overlapping are intersecting
	//
	@Test
	public void intersectCompleteOverlap() {
		
		System.out.println("Running test: intersectCompleteOverlap.");
		
		// next to eachother same x
		System.out.println("intersectCompleteOverlap - at origin");
		Circle1 a = new Circle1(0, 0, 3);
		Circle1 b = new Circle1(7, 0, 4);
		
		Assert.assertTrue(a.intersects(b));
		Assert.assertTrue(b.intersects(a));
	}
	
	//
	// Tests two circles that are intersecting at a single point are intersecting
	//
	@Test
	public void intersectAtOnePoint() {
		
		System.out.println("Running test: intersectAtOnePoint.");
		
		// next to eachother same x
		System.out.println("intersectAtOnePoint - on x-axis");
		Circle1 a = new Circle1(0, 0, 3);
		Circle1 b = new Circle1(0, 0, 5);
		
		Assert.assertTrue(a.intersects(b));
		Assert.assertTrue(b.intersects(a));
	}
	
	//
	// Test a simple positive move
	//
	@Test
	public void simpleMove()
	{
		Point p;
		System.out.println("Running test simpleMove.");
		p = circle1.moveBy(1, 1);
		Assert.assertTrue(p.x == 2 && p.y == 3);
	}

	//
	// Test a simple negative move
	//
	@Test
	public void simpleMoveNeg()
	{
		Point p;
		System.out.println("Running test simpleMoveNeg.");
		p = circle1.moveBy(-1, -1);
		Assert.assertTrue(p.x == 0 && p.y == 1);
	}

	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle1Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
