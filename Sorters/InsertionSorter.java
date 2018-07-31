package heapsort;
import java.util.Scanner;

/**
 * An implementation of insertion sort
 * @author Aaron Thompson
 */
class InsertionSorter extends Sorter
{
	/**
	 * Uses insertion sort to sort the provided array
	 */
	@Override
	public <E extends Comparable<E>> void sort(E[] array) 
	{
		for(int i = 1; i < array.length; i++)
		{
			for(int j = i; j > 0; j--)
			{
				if(array[j].compareTo(array[j-1]) < 0)
				{
					swap(array, j, j-1);
				}
			}
		}	
	}
	
	private <E extends Comparable<E>> void swap(E[]array, int index1, int index2)
	{
		E temp = array[index1];
		array[index1] = array[index2];
		array[index2] = temp;
	}
	
	public static void main(String[] args) 
	{
		Scanner scanner = new Scanner(System.in);
		System.out.println("Testing insertion sort.");
		System.out.print("Please enter the array size: ");
		String input = scanner.nextLine();
		int size = 0;
		
		try{
			size = Integer.parseInt(input);
		}
		catch(NumberFormatException e){
			System.out.println("Bad number. Exiting the program...");
			System.exit(1);
		}
		
		InsertionSorter sorter = new InsertionSorter();
		
		int sort = sorter.timeSort(size);
		
		System.out.println("Array of size " + size + " took " + sort + " ms to sort.");
	}
}
