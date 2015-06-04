import static org.junit.Assert.*;

import org.junit.Test;


public class SearchTest {

	@Test
	public void testSearchConstructor() {
		Search search = new Search();
		search.populateGraph();
		search.print();
		search.findJohnsonCycles();
	}
	
	@Test
	public void testSquareJohnsonCycle() {
		Search search = new Search();
		search.createSquare();
		search.print();
		search.findJohnsonCycles();
		search.findSzwarcfiterLauerCycles();
	}

}
