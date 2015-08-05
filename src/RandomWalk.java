import java.util.Random;


public class RandomWalk {
	
	public RandomWalk() {
		
	}
	
	public static void generateRandomExact(int num) {
		System.out.println("Random Exact Numbers");
		int sum = 0;
		for (int i = 0; i < num; i++) {
			Random r = new Random();
			int randomInt = r.nextInt(2); // random int, either 0 or 1
			if (randomInt == 0) {
				System.out.println(5.0);
				sum += 5;
			} else {
				System.out.println(-5.0);
				sum -= 5;
			}
		}
		System.out.println("sum: " + sum);
	}
	
	public static void generateRandomUniform(int num) {
		System.out.println("Random Uniform Numbers");
		double sum = 0.0;
		for (int i = 0; i < num; i++) {
			Random r = new Random();
			double randomUniform = -10.0 + 20.0 * r.nextDouble(); // random double between -10.0 and 10.0 inclusive
			sum += Math.abs(randomUniform); // sum of edge magnitudes
			System.out.println(randomUniform);
		}
		System.out.println("Sum: " + sum);
	}
}
