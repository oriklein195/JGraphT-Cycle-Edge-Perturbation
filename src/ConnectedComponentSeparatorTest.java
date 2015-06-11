import static org.junit.Assert.*;

import org.junit.Test;


public class ConnectedComponentSeparatorTest {

	@Test
	public void testSeparateConnectedComponent() {
		Graph graph = new Graph("s1.txt");
		ConnectedComponentSeparator ccSeparator = new ConnectedComponentSeparator(graph);
		ccSeparator.splitConnectedComponent(2, "s1.txt");
	}
	
	@Test
	public void testConnectedComponentSize() {
		Graph graph = new Graph("scc1.txt");
		graph.getConnectedComponents();
	}

}
