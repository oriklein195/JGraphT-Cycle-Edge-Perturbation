import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public class ConnectedComponentSeparator {
	
	private Graph graph;
	private Set<Integer> connectedComponent;
	
	public ConnectedComponentSeparator(Graph graph) {
		this.graph = graph;
	}
	
	//ccNumber tells the method which connected component to split
	public void splitConnectedComponent(int ccNumber, String fileName) {
		List<Set> connectedComponents = graph.getConnectedComponents();
		connectedComponent = connectedComponents.get(ccNumber);
		System.out.println(connectedComponent);
		
		int startNode; // Nodes can also be represented as Strings, or whichever class we decide to implement.
		int endNode;
		double weight;
		
		try {
			String line;
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split("\\s+");
				startNode = Integer.parseInt(tokens[0]);
				endNode = Integer.parseInt(tokens[1]);
				//weight = Double.parseDouble(tokens[2]);
				if (connectedComponent.contains(startNode) || connectedComponent.contains(endNode)) {
					System.out.println(line);
				}
			}
		} catch (IOException e) {
			System.out.println("IO Exception.");
		}
	}

}
