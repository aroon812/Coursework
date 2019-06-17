import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Loads names of items and prices into an SSST, saves it, and calculates the total price of all the items the user enters
 * @author Aaron Thompson
 */
class Restaurant {
	
	@SuppressWarnings("rawtypes")
	SequentialSearchSymbolTable table = new SequentialSearchSymbolTable<String, Integer>();
	@SuppressWarnings("unchecked")
	private void loadMenu()
	{
		String fileName = "menu.tsv";
		Scanner scanner = null;
		
		File file = new File(fileName);
		
		try {
			scanner = new Scanner(file);
		} 
		catch (FileNotFoundException e) {
			System.err.println("File not found");
		}
		
		String line;
		String[] strArray;
		
		//load all of the contents from the file into the table
		while (scanner.hasNextLine())
		{
			
			line = scanner.nextLine();
			strArray = line.split("\t");
			
			table.put(strArray[0], strArray[1]);
		}
		scanner.close();
	}
	
	/**
	 * Takes input from the user
	 */
	@SuppressWarnings({ "unchecked", "resource" })
	public void query()
	{
		Scanner scanner = new Scanner(System.in);
		loadMenu();
		int subtotal = 0;
		System.out.println("Welcome to the restaurant!");
		String response;
		
		//loop until user hits enter
		while(true)
		{
			System.out.println("Enter a menu item to add the cost to your subtotal, or press enter to calculate tax and tip.");
			response = scanner.nextLine();
			if(response.equals(""))
			{
				
				if(subtotal == 0)
				{
					System.out.println("Tax: 0.00");
					System.out.println("Tip: 0.00");
					System.out.println("Total: 0.00");
				}
				
				else{
					int tax = subtotal / 100 * 10;
					int tip = subtotal /100 * 15;
					System.out.print("Tax: ");
					printMoney(tax);
					System.out.println();
					System.out.print("Tip: ");
					printMoney(tip);
					System.out.println();
					System.out.print("Total: ");
					printMoney(subtotal + tip + tax);
					System.out.println();
				}
				
				System.out.println("Goodbye!");
				System.exit(0);
				
			}
			else if(table.get(response) == null)
			{
				System.out.println("Sorry. I was not able to understand your response.");
			}
			else{
				int cost = Integer.parseInt((String)table.get(response));
				subtotal += cost;
				System.out.print("Subtotal: ");
				printMoney(cost);
				System.out.println();
			}
		}
	
	}
	
	//convert number of cents into a dollar value
	private void printMoney(int money)
	{
		String moneyString = money + "";
		int cents = Integer.parseInt(moneyString.substring(moneyString.length()-2, moneyString.length()-1));
		int dollars = Integer.parseInt(moneyString)/100;
		System.out.printf("$%d.%02d", dollars, cents);
	}
	
	public static void main(String[] args)
	{
		Restaurant restaurant = new Restaurant();
		restaurant.query();
	}
}
