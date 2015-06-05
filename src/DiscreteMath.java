import java.math.BigInteger;

public class DiscreteMath {
	
	public static BigInteger numCyclesInClique(int size) {
		BigInteger numCycles = BigInteger.valueOf(0);
		for (int i = 3; i <= size; i++) {
			BigInteger numCyclesLengthI = combination(size, i).multiply(factorial(i)).divide((BigInteger.valueOf(2 * i)));
			System.out.println(i + "-cycles: " + numCyclesLengthI);
			numCycles = numCycles.add(numCyclesLengthI);
		}
		System.out.println("Total number cycles in " + size + "-clique: " + numCycles);
		return numCycles;
	}
	
	public static BigInteger combination(int n, int k) {
		return factorial(n).divide((factorial(k).multiply(factorial(n - k))));
	}
	
	public static BigInteger factorial(int n) {
		BigInteger fact = BigInteger.valueOf(1);
		for (int i = 1; i <= n; i++) {
			fact = fact.multiply(BigInteger.valueOf(i));
		}
		return fact;
	}
}
