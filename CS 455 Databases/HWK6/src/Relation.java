import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Scanner;
/**
 * Represents a relation in database management systems.
 * @author Aaron Thompson
 *
 */
public class Relation {
	private String[] attributes;
	private String sortedBy;
	private ArrayList<Tuple> tuples;
	private Hashtable<String, Integer> attributeLocations;
	public String relationName;
	
	/**
	 * Create a new relation.
	 * @param relationName The name of the new relation
	 * @param filePath The file path to the data file
	 */
	public Relation(String relationName, String filePath){
		this.relationName = relationName;
		File file = new File(filePath);
		Scanner sc;
		
		try {
			tuples = new ArrayList<Tuple>();
			attributeLocations = new Hashtable<String, Integer>();
			sc = new Scanner(file);
			int line = 0;
			String currentLine;
			while(sc.hasNextLine()) {
				currentLine = sc.nextLine();
				if (line == 0) {
					attributes = currentLine.replaceAll("#", "").split("\\|");
					for (int i = 0; i < attributes.length; i++) {
						attributeLocations.put(attributes[i], i);
					}
					
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
			e.printStackTrace();
		}
	}
	
	/**
	 * Create a new, empty relation from a combination of attributes
	 */
	public Relation(String[] atts1, String[] atts2, String commonAttribute) {
		attributes = new String[(atts1.length+atts2.length)-1];
		
		int currentIndex = 0;
		for (int i = 0; i < atts1.length; i++) {
			attributes[currentIndex] = atts1[i];
			currentIndex++;
		}
		for (int j = 0; j < atts2.length; j++) {
			if(!atts2[j].equals(commonAttribute)) {
				attributes[currentIndex] = atts2[j];
				currentIndex++;
			}
		}
		attributeLocations = new Hashtable<String, Integer>();
		for (int i = 0; i < attributes.length; i++) {
			attributeLocations.put(attributes[i], i);
		}
		tuples = new ArrayList<Tuple>();
	}
	
	/**
	 * Get the attributes associated with the relation
	 * @return an array of the relations attributes
	 */
	public String[] getAttributes() {
		return attributes;
	}
	
	/**
	 * Returns the attribute on which the relation is sorted.
	 * @return The attribute on which the relation is sorted, or null if the relation is not sorted.
	 */
	public String sortedBy() {
		return sortedBy;
	}
	
	/**
	 * Add a tuple to the relation.
	 * @param t The tuple to be added.
	 */
	public void addTuple(Tuple t) {
		tuples.add(t);
	}
	
	/**
	 * Get a Tuple at a specific index in a relation
	 * @param i The index to retrieve the tuple from.
	 * @return The tuple to be returned from the specified index.
	 */
	public Tuple getTuple(int i) {
		return tuples.get(i);
	}
	
	/**
	 * Returns the number of tuples in a relation.
	 * @return The number of tuples in a relation.
	 */
	public int size() {
		return tuples.size();
	}
	
	/**
	 * Sorts the relation on an attribute
	 * @param attribute The attribute to be sorted on.
	 */
	public void sort(String attribute) {
		Collections.sort(tuples, new SortByAttribute(this, attribute));
	}
	
	/**
	 * Find a common attribute in another relation.
	 * @param r The other relation to compare.
	 * @return The common attribute if one exists, or null if there are no common attributes.
	 */
	public String commonAttribute(Relation r) {
		for (int i = 0; i < attributes.length; i++) {
			for (int j = 0; j < r.attributes.length; j++) {
				if (attributes[i].equals(r.attributes[j])) {
					return attributes[i];
				}
			}
		}
		return null;
	}
	
	/**
	 * Get the index of an attribute from the attribute name.
	 * @param attributeName The name of the desired attribute.
	 * @return The index of the desired attribute.
	 */
	public int getAttributeLocation(String attributeName) {
		return attributeLocations.get(attributeName);
	}
	
	/**
	 * Return the relation as a string. 
	 * @return The relation as a string
	 */
	public String toString() {
		String strBuilder = "";
		for (int i = 0; i < attributes.length; i++) {
			strBuilder += attributes[i];
			if (i != attributes.length-1) {
				strBuilder += "|";
			}
		}
		strBuilder += "\n";
		for (int i = 0; i < tuples.size(); i++) {
			for (int j = 0; j < attributes.length; j++) {
				strBuilder += tuples.get(i).getAttributeValue(getAttributeLocation(attributes[j]));
				if (j != attributes.length-1) {
					strBuilder += "|";
				}
			}
			strBuilder += "\n";
		}
		return strBuilder;
	}
}
