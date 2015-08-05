import static org.junit.Assert.*;

import org.junit.Test;


public class RandomWalkTest {

	@Test
	public void testGenerateRandomExact() {
		RandomWalk.generateRandomExact(1000);
	}
	
	@Test
	public void testGenerateRandomUniform() {
		RandomWalk.generateRandomUniform(1000);
	}

}
