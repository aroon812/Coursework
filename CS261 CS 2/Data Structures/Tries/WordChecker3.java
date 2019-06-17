import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

/**
 * Uses a Trie to check if an entered word is valid
 * @author Aaron Thompson
 * @version 4/23/2017
 */
class WordChecker3 {
	String fileName;
	
	private Trie createTrie()
	{
		Trie trie = new Trie();
		File file = null;
		Scanner fileScanner = null;
		
		//create the new file
		try{
			file = new File(fileName);
			throw new IOException();
		} 
		catch (IOException e) {
			
		}
		
		//create the scanner
		try{
			fileScanner = new Scanner(file);
		}
		catch(FileNotFoundException e)
		{
			System.err.println("File not found");
		}
		
		if(fileScanner == null)
		{
			System.exit(1);
		}
		
		//add all of the words into the Trie
		while(fileScanner.hasNextLine())
		{
			trie.insert(fileScanner.nextLine());
		}
		fileScanner.close();
		
		return trie;
	}
	
	/**
	 * Get input from the user
	 */
	public void queryUser()
	{
		Trie trie = null;
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Welcome to the Word Checker 3.0!");
		System.out.print("Please enter a .lex or .trie file: ");
		fileName = scanner.nextLine();
		
		if(fileName.contains(".lex"))
		{
			//save the trie from the .lex file to the disk
			try{
				trie = createTrie();
				System.out.println("Loading file \"" + fileName + "\", which contains " + trie.getSize() + " words.");
				
				fileName = fileName.substring(0, fileName.length()-4) + ".trie";
				System.out.println("Saving new file \"" + fileName + "\"...done.");
				ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(new File(fileName)));
				outStream.writeObject(trie);
				outStream.close();
				
				System.out.println("Goodbye!");
				System.exit(0);
			}
			catch (FileNotFoundException e) 
			{
				System.err.println("File not found");
				System.exit(1);
			} 
			catch (IOException e) 
			{
				System.err.println("IOException");
			}
		}
		
		else if (fileName.contains(".trie"))
		{
			try {
				//loads the trie file
				ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(fileName));
				trie = (Trie) inStream.readObject();
				inStream.close();
				System.out.println("Loading file \"" + fileName + "\", which contains " + trie.getSize() + " words.");
				
				System.out.println("Please enter a word, or hit enter to quit:");
				String word;
				//check to see if the words entered by the user are valid
				while (true)
				{
					System.out.print("> ");
					word = scanner.nextLine();
					
					if (word.equals(""))
					{
						System.out.println("Goodbye!");
						System.exit(0);
					}
					else{
						if (trie.has(word))
						{
							System.out.println("\"" + word + "\"" + " is a valid word.");
						}
						else{
							System.out.println("\"" + word + "\"" + " is NOT a valid word.");
						}
					}
				}
			} 
			catch (FileNotFoundException e) 
			{
				System.err.println("File not found");
			} 
			catch (IOException e) 
			{
				System.err.println("IOException");
			}
			catch (ClassNotFoundException e) 
			{
				System.err.println("Class not found");
			}
		}
	}
	
	public static void main(String[] args) 
	{
		WordChecker3 wordChecker = new WordChecker3();
		wordChecker.queryUser();
	}
}

