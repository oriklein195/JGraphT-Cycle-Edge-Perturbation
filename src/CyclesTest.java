import static org.junit.Assert.*;

import java.util.BitSet;
import java.util.List;

import org.junit.Test;


public class CyclesTest {
	
	@Test
	public void testCyclesSCC2() {
		Graph graph = new Graph("scc2.txt");
		Cycles c = new Cycles(graph);
		c.getCycles(4); // M = 4 is the maximum for a 4-clique b/c each edge can only be a part of 4 cycles.
	}
	
	@Test
	public void testCyclesSCC3() {
		Graph graph = new Graph("scc3.txt");
		Cycles c = new Cycles(graph);
		c.getCycles(2);
	}
	
	@Test
	public void testCyclesSCC4() {
		Graph graph = new Graph("scc4.txt");
		//graph.getCycles(3); // 3 doesn't run completely, edges (429 : 266) and (266 : 429) are only in 2 cycles
		Cycles c = new Cycles(graph);
		c.getCycles(5);
	}
	
	@Test
	public void testCyclesSCC5() {
		Graph graph = new Graph("scc5.txt");
		Cycles c = new Cycles(graph);
		c.getCycles(2);
	}
	
	@Test
	public void testCyclesSCC6() {
		Graph graph = new Graph("scc6.txt");
		Cycles c = new Cycles(graph);
		c.getCycles(20);
	}
	
	
	@Test
	public void test3Clique() {
		Graph graph = new Graph("3-clique.txt");
		Cycles c = new Cycles(graph);
		c.getCycles(1); // M = 1 is the maximum for a 3-clique (triangle) b/c each edge can only be a part of 1 cycle.
	}
	
	@Test
	public void test4Clique() {
		Graph graph = new Graph("scc4.txt");
		Cycles c = new Cycles(graph);
		c.getCycles(3);
	}
	
	@Test
	public void test5Clique() {
		Graph graph = new Graph("5-clique.txt");
		Cycles c = new Cycles(graph);
		c.getCycles(15); // Interesting, M = 15 finds all 37 cycles (74 directed cycles).
	}
	
	@Test
	public void testVerifyCyclesSCC6() {
		Graph graph = new Graph("scc6.txt");
		Cycles c = new Cycles(graph);
		List<BitSet> cycles = c.getCycles(20);
		c.verifyCycles(cycles);
	}

	// increment M from 10 to 1000
	// only print out the last parts of data:
	/*
	 * number of cycles
	 * max cycle length
	 * number of repeated cycles
	 * number of removed edges
	 * priority queue
	 * number if iterations
	 */
	@Test
	public void testIncrementM() {
		Graph graph = new Graph("scc6.txt");
		for (int m = 10; m <= 1000; m += 10) {
			Cycles c = new Cycles(graph);
			c.getCycles(m);
		}
	}
	
	@Test
	public void testIncrementUpperM() {
		Graph graph = new Graph("scc6.txt");
		for (int m = 1000; m <= 2000; m += 10) {
			Cycles c = new Cycles(graph);
			c.getCycles(m);
		}
	}
	
	@Test
	public void testCompareCycleAlgorithms4Clique() {
		Graph graph = new Graph("scc2.txt");
		graph.findSzwarcfiterLauerCycles();
		Cycles c = new Cycles(graph);
		c.getCycles(4);
	}
	
	@Test
	public void testCompareCyclesSCC3() {
		Graph graph = new Graph("scc3.txt");
		//List<List> slCycles = graph.findSzwarcfiterLauerCycles();
		//graph.printCycleLengthHistogram(slCycles);
		
		Cycles c = new Cycles(graph);
		List<BitSet> cycles = c.getCycles(500);
		c.printCycleLengthHistogram(cycles);
	}
	
	public void testCompareCycleAlgorithms5Clique() {
		Graph graph = new Graph("5-clique.txt");
		graph.findSzwarcfiterLauerCycles();
		Cycles c = new Cycles(graph);
		c.getCycles(15);
	}
	
	@Test
	public void testCompareCycleAlgorithms6Clique() {
		Graph graph = new Graph("6-clique.txt");
		graph.findSzwarcfiterLauerCycles();
		Cycles c = new Cycles(graph);
		c.getCycles(64);
	}

	public void testTab() {
		System.out.println("0" + "\t" + "1");
		System.out.println("1" + "\t" + "4");
	}
	
	@Test
	public void testActualHistogram() {
		Graph graph = new Graph("scc6.txt");
		Cycles c = new Cycles(graph);
		c.getCycles(1700);
		c.printHistogram();
		// took 6-7 minutes
	}
	
	@Test
	public void testLargeMValue() {
		Graph graph = new Graph("scc6.txt");
		Cycles c = new Cycles(graph);
		c.getCycles(1600);
	}
	
	@Test
	public void testHistogram() {
		Graph graph = new Graph("scc6.txt");
		Cycles c = new Cycles(graph);
		c.getCycles(50);
		c.printHistogram();
	}
	
	@Test
	public void testSCC6LargeM() {
		Graph graph = new Graph("scc6.txt");
		Cycles c = new Cycles(graph);
		List<BitSet> cycles = c.getCycles(50);
		c.getCyclesWithEdge(303, 305, cycles);
	}
	
	
	
	@Test
	public void testPrintIntegerToEdgeMap() {
		Graph graph = new Graph("scc6.txt");
		Cycles c = new Cycles(graph);
		c.printIntegerToEdgeMap();
	}
}
