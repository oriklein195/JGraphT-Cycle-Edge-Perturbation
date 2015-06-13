import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;


public class DiscreteMathTest {

	@Test
	public void testFactorial5() {
		assertEquals(120, DiscreteMath.factorial(5));
	}
	
	@Test
	public void testFactorial20() {
		BigInteger factorial = DiscreteMath.factorial(50);
		System.out.println(factorial);
	}
	
	@Test
	public void test5Choose2() {
		assertEquals(10, DiscreteMath.combination(5, 2));
	}
	
	@Test
	public void testNumCyclesIn4Clique() {
		assertEquals(BigInteger.valueOf(7), DiscreteMath.numCyclesInClique(4));
	}
	
	@Test
	public void testNumCyclesIn5Clique() {
		DiscreteMath.numCyclesInClique(5);
	}
	
	@Test
	public void testNumCyclesIn50Clique() {
		DiscreteMath.numCyclesInClique(50);
	}
	
	// This is 1,225 edges, approximately how large our graphs will be. Similar to a 50-clique? Probably not because a 
	// 50-clique would be much more dense than a 500-node graph, given that they have the same number of edges.
	@Test
	public void calculate50Choose2() {
		System.out.println(DiscreteMath.combination(50, 2));
	}
	
	// If our graph has 500 nodes, it can have at most 124,750 edges. With 1,000 edges, this is only a tiny fraction
	// of how large the graph *could* be.
	@Test
	public void calculate500Choose2() {
		System.out.println(DiscreteMath.combination(500, 2));
	}

	@Test
	public void testNumCyclesIn20Clique() {
		DiscreteMath.numCyclesInClique(20);
	}
	
	@Test
	public void testNumEdgesIn20Clique() {
		System.out.println(DiscreteMath.combination(20, 2));
	}
	
	@Test
	public void testNumCyclesIn10Clique() {
		DiscreteMath.numCyclesInClique(10);
	}
	
	@Test
	public void testNumEdgesIn10Clique() {
		System.out.println(DiscreteMath.combination(10, 2));
	}
	
}
