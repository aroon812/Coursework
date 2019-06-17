import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Animalpedia {

	HashTable<String, String> typeTable, numFeetTable;
	String fileName = "animals.tsv";
	
	private void loadTables()
	{
		File file = new File(fileName);
		typeTable = new HashTable<String, String>(101);
		numFeetTable = new HashTable<String, String>(101);
		
		String[] strArray;
		
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine())
			{
				strArray = scanner.nextLine().split("\t");
				
				//load all of the file contents into the HashTables
				typeTable.put(strArray[0], strArray[1]);
				numFeetTable.put(strArray[0], strArray[2]);
			}
		} 
		catch (FileNotFoundException e) {
			System.err.println("File not found");
		}
		
	}
	
	/**
	 * Get input from the user
	 */
	public void query()
	{
		Scanner scanner = new Scanner(System.in);
		String word;
		loadTables();
		System.out.println("Welcome to Animalpedia!");
		System.out.println("File \""  + fileName + "\" loaded.");
		System.out.println();
		System.out.println("Please enter an animal, or hit enter to quit.");

		while(true)
		{
			System.out.println();
			System.out.print("Your animal: ");
			word = scanner.nextLine();
			
			if(word.equals(""))
			{
				System.out.println("Goodbye!");
				System.exit(0);
			}
			else if(typeTable.get(word) != null)
			{
				System.out.println("A " + word + " is a " + typeTable.get(word) + ", that has " + numFeetTable.get(word) + " legs.");
			}
			else{
				System.out.println("I'm sorry. I dont know what a " + word + " is.");
			}
		}
	}
	
	public static void main(String[] args) 
	{
		Animalpedia animalpedia = new Animalpedia();
		animalpedia.query();
	}

}
