import static org.junit.Assert.*;

import org.junit.Test;


public class CyclesTest {

	@Test
	public void testCycles() {
		Graph graph = new Graph("scc2.txt");
		graph.testCycles();
	}

}
