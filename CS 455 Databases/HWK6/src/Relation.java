import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
/**
 * Represents a relation in database management systems.
 * @author Aaron Thompson
 *
 */
public class Relation {
	String[] attributes;
	String sortedBy;
	ArrayList<Tuple> tuples;
	public String relationName;
	
	/**
	 * 
	 * @param relationName The name of the new relation
	 * @param filePath The file path to the data file
	 */
	public Relation(String relationName, String filePath){
		this.relationName = relationName;
		File file = new File(filePath);
		Scanner sc;
		
		try {
			tuples = new ArrayList<Tuple>();
			sc = new Scanner(file);
			int line = 0;
			String currentLine;
			while(sc.hasNextLine()) {
				currentLine = sc.nextLine();
				if (line == 0) {
					attributes = currentLine.replaceAll("#", "").split("\\|");
				}
				else if (line == 1 && currentLine.charAt(0) == '#') {
					sortedBy = currentLine.replaceAll("#", "");
				}
				else {
					String[] attValues = currentLine.split("\\|");
					Tuple newTuple = new Tuple(attributes, attValues);
					tuples.add(newTuple);
				}
				line++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Create a new, empty relation.
	 */
	public Relation() {
		tuples = new ArrayList<Tuple>();
	}
	
	/**
	 * Sorts the relation on an attribute
	 * @param attribute The attribute to be sorted on.
	 */
	public void sort(String attribute) {
		Collections.sort(tuples, new SortByAttribute(attribute));
	}
	
	/**
	 * Print the relation. (for testing purposes only at the moment)
	 */
	public void printRelation() {
		for (int i = 0; i < attributes.length; i++) {
			System.out.print(attributes[i] + " ");
		}
		System.out.println();
		for (int i = 0; i < tuples.size(); i++) {
			for (int j = 0; j < attributes.length; j++) {
				System.out.print(tuples.get(i).getAttributeValue(attributes[j]) + " ");
			}
			System.out.println();
		}
	}
}
