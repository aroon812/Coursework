import java.util.Scanner;

/**
 * A standard bubble sort
 * @author Aaron Thompson
 * @version 2/21/17
 */
class BubbleSorter extends Sorter
{
	/**
	 * Uses a bubble sort to sort the provided array
	 */
	@Override
	public <E extends Comparable<E>> void sort(E[] array) 
	{
		int max = array.length; //the upper bound of the sort
		if(Sorter.isSorted(array)) //check to see if the array if already sorted
		{
			return;
		}
		
		for(int i = 0; i < array.length + 1; i++)
		{
			for(int j = 0; j < max-1; j++)
			{
				if(array[j].compareTo(array[j+1]) > 0)
				{
					//Swap the elements if they are out of order
					E temp = array[j+1];
					array[j+1] = array[j];
					array[j] = temp;
					
				}
			}
			max--;
		}
	}
	
	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("Testing bubble sort.");
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
		
		BubbleSorter sorter = new BubbleSorter();
		int sort = sorter.timeSort(size);
		System.out.println("Array of size " + size + " took " + sort + " ms to sort.");
	}

}
