import java.util.Arrays;
import java.util.Random;

/**
 * This class implements a Dynamic Programming solution for the Skiing Problem
 * 
 * @author Aaron Thompson and Jared Polonitza
 *
 */
public class Skiing {
	// All measurements are given in inches
	private static final int MAX_HEIGHT = 77;
	private static final int MIN_HEIGHT = 61;

	// Feel free to change these values as long as NUM_SKIS > NUM_SKIERS
	private static final int NUM_SKIS = 5;
	private static final int NUM_SKIERS = 3;

	// These will contain the actual human and ski lengths
	private static int[] skiHeights;
	private static int[] skierHeights;

	/**
	 * Randomly populates the skier and ski arrays with realistic human heights
	 * and ski lengths
	 */
	private static void populateArrays() {
		Random rng = new Random();
		for (int i = 0; i < skierHeights.length; i++) {
			skierHeights[i] = rng.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1) + MIN_HEIGHT;
		}
		for (int i = 0; i < skiHeights.length; i++) {
			skiHeights[i] = rng.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1) + MIN_HEIGHT;
		}
	}

	/**
	 * Returns an optimal solution to an instance of the Skiing Problem.
	 * 
	 * @param assignments
	 *            An array whose length is the number of skiers. The ith entry
	 *            in the array is the ski assignment for person i.
	 */
	public static void computeSolution(int[] assignments) {

		int[][] solutions = new int[skierHeights.length + 1][skiHeights.length + 1];

		for (int i = 1; i < skierHeights.length + 1; i++) {
			for (int j = 1; j < skiHeights.length + 1; j++) {
				if (j <= i) {
					solutions[i][j] = Math.abs(skierHeights[i - 1] - skiHeights[j - 1] + solutions[i - 1][j - 1]);
				} else {
					solutions[i][j] = Math.min(
							Math.abs(skierHeights[i - 1] - skiHeights[j - 1] + solutions[i - 1][j - 1]),
							(solutions[i][j - 1]));
				}
			}
		}
		findIndexes(solutions.length - 1, solutions[0].length - 1, assignments, solutions);
	}

	// uses the results from computeSolution to find each person's corresponding
	// ski assignment
	private static void findIndexes(int i, int j, int[] assignments, int[][] solutions) {
		if (i == 0) {
			return;
		} else if (j <= i) {
			assignments[i - 1] = j - 1;
			findIndexes(i - 1, j, assignments, solutions);
		} else if ((Math.abs(skierHeights[i - 1] - skiHeights[j - 1] + solutions[i - 1][j - 1])) <= solutions[i][j
				- 1]) {
			assignments[i - 1] = j - 1;
			findIndexes(i - 1, j - 1, assignments, solutions);
		} else {
			findIndexes(i, j - 1, assignments, solutions);
		}
	}

	/**
	 * Once you're done, you can run this main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		skiHeights = new int[NUM_SKIS];
		skierHeights = new int[NUM_SKIERS];
		populateArrays();
		// Note: sorting is crucial for this problem...why is that?
		Arrays.sort(skiHeights);
		Arrays.sort(skierHeights);
		int[] assignments = new int[NUM_SKIERS];
		computeSolution(assignments);
		System.out.println("The optimal ski assignment is: ");
		System.out.println(Arrays.toString(assignments));
	}
}