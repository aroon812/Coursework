import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Relation {
	String[] attributes;
	public Relation(String relationName, String filePath){
		
		File file = new File(filePath);
		Scanner sc;
		
		try {
			sc = new Scanner(file);
			int line = 0;
			while(sc.hasNextLine()) {
				if (line == 0) {
					attributes = sc.nextLine().replaceAll("#", "").split("\\|");
					System.out.println(Arrays.toString(attributes));
				}
				line++;
				System.out.println(sc.nextLine());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
