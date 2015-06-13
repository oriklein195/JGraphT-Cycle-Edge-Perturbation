import static org.junit.Assert.*;

import java.util.BitSet;
import java.util.List;

import org.junit.Test;


public class CyclesTest {

	@Test
	public void testCycles() {
		Graph graph = new Graph("scc2.txt");
		graph.getOneCycle();
	}
	
	@Test
	public void testCyclesCustomGraph() {
		Graph graph = new Graph("5-nodes-5-edges.txt");
		graph.getOneCycle();
	}
	
	@Test
	public void testCyclesSCC2() {
		Graph graph = new Graph("scc2.txt");
		graph.getCycles(4); // M = 4 is the maximum for a 4-clique b/c each edge can only be a part of 4 cycles.
	}
	
	@Test
	public void testCyclesSCC3() {
		Graph graph = new Graph("scc3.txt");
		graph.getCycles(2);
	}
	
	@Test
	public void testCyclesSCC4() {
		Graph graph = new Graph("scc4.txt");
		//graph.getCycles(3); // 3 doesn't run completely, edges (429 : 266) and (266 : 429) are only in 2 cycles
		graph.getCycles(3);
	}
	
	@Test
	public void testCyclesSCC5() {
		Graph graph = new Graph("scc5.txt");
		List<BitSet> cycles = graph.getCycles(2); // edges (132 : 140) and (140 : 132) are problems, no cycles found
		//graph.verifyCycles(cycles);
	}
	
	@Test
	public void testCyclesSCC6() {
		Graph graph = new Graph("scc6.txt");
		graph.getCycles(3); // edges (187 : 545) and (187 : 341) are only in 2 cycles
		// edge (187 : 124) is only in 2 cycles
	}
	
	
<<<<<<< Updated upstream
	@Test
	public void test3Clique() {
		Graph graph = new Graph("3-clique.txt");
		graph.getCycles(1); // M = 1 is the maximum for a 3-clique (triangle) b/c each edge can only be a part of 1 cycle.
	}
=======
>>>>>>> Stashed changes
	

}
