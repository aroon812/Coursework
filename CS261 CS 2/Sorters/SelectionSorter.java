import java.util.Scanner;

/**
 * A standard selection sort
 * @author Aaron Thompson
 * @version 2/21/2017
 */
class SelectionSorter extends Sorter 
{
	/**
	 * Uses selection sort to sort the provided array
	 */
	@Override
	public <E extends Comparable<E>> void sort(E[] array) 
	{
		int upperBound = array.length-1; //the upper bound of the sore
		if(Sorter.isSorted(array)) //check to see if the array is already sorted
		{
			return;
		}
			
		for (int i = upperBound; i > 0; i--)
		{
			int currentMax = 0;
			
			//for loop which finds the current maximum element in the unsorted portion of the array
			for (int j = 1; j <= i; j++)
			{
				if(array[currentMax].compareTo(array[j]) < 0)
				{
					currentMax = j;
				}
			}
			//swaps the end element with the maximum element
			E temp = array[upperBound];
			array[upperBound] = array[currentMax];
			array[currentMax] = temp;
			
			upperBound--;
		}
		
	}
	
	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("Testing selection sort.");
		System.out.print("Please enter the array size: ");
		String input = scan.nextLine();
		int size = 0;
		
		try{
			size = Integer.parseInt(input);
		}
		catch(NumberFormatException e){
			System.out.println("Bad number. Exiting the program...");
			System.exit(1);
		}
		
		SelectionSorter sorter = new SelectionSorter();
		int sort = sorter.timeSort(size);
		System.out.println("Array of size " + size + " took " + sort + " ms to sort.");
	}

}

