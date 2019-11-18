import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * Reads in database files and can perform various natural joins on the supplied relations.
 * @author Aaron Thompson
 *
 */
public class JoinEngine {
	public static void main(String[] args) {
		File data = new File("src/data");
		Hashtable<String, Relation> relations = new Hashtable<String, Relation>();

		for (final File fileEntry : data.listFiles()) {
			if(fileEntry.getName().charAt(0) != '.') {
				String str = fileEntry.getName();
				String fileName = str.substring(0, str.length()-4);
				String path = fileEntry.getPath();

				relations.put(fileName.toLowerCase(), new Relation(fileName.toLowerCase(), path));
			}
		}
		
		Scanner scanner = new Scanner(System.in);
		Object[] relationNames = relations.keySet().toArray();
			
		System.out.println("Availiable Relations: ");
		for (int i = 0; i < relationNames.length; i++) {
			System.out.print(relationNames[i] + " ");
		}
		System.out.println();
		System.out.print("Your selection (separated by space): ");
		String relationSelection = scanner.nextLine();
		String[] chosenRelations = relationSelection.split(" ");
		
		for (int i = 0; i < chosenRelations.length; i++) {
			chosenRelations[i] = chosenRelations[i].toLowerCase();
		} 
		
		for (int i = 0; i < chosenRelations.length; i++) {
			if (!relations.containsKey(chosenRelations[i])) {
				throw new IllegalArgumentException("Please enter a relation that exists");
			}
		}
		
		if (chosenRelations.length != 2) {
			throw new IllegalArgumentException("Please enter the names of two relations");
		}
		
		Relation rel1 = relations.get(chosenRelations[0]);
		Relation rel2 = relations.get(chosenRelations[1]);
		
		System.out.println("Choose a join algorithm:");
		System.out.println("1. Nested loop join");
		System.out.println("2. Hash Join");
		System.out.println("3. Sort-Merge Join");
		double time1;
		double time2;
		int algorithmChoice = scanner.nextInt();
		scanner.close();
		if (algorithmChoice == 1) {
			time1 = System.nanoTime();
			Relation r = nestedLoopJoin(rel1, rel2);
			time2 = System.nanoTime();
			
			if (r == null) {
				return;
			}
			
			System.out.println(r);
			System.out.println("Time: " + ((time2-time1)/1000000) + " ms");
			System.out.println("The resulting relation has " + r.size() + " rows");
		}	
		else if (algorithmChoice == 2) {
			try {
				time1 = System.nanoTime();
				Relation r = hashJoin(rel1, rel2);
				time2 = System.nanoTime();
				
				if (r == null) {
					return;
				}
				
				System.out.println(r);
				System.out.println("Time: " + ((time2-time1)/1000000) + " ms");
				System.out.println("The resulting relation has " + r.size() + " rows");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (algorithmChoice == 3) {
			time1 = System.nanoTime();
			Relation r = sortMergeJoin(rel1, rel2);
			time2 = System.nanoTime();
			
			if (r == null) {
				return;
			}
			
			System.out.println(r);
			System.out.println("Time: " + ((time2-time1)/1000000) + " ms");
			System.out.println("The resulting relation has " + r.size() + " rows");
		}
	}
	
	/**
	 * Perform a natural join on two relations using the nested join algorithm.
	 * @param rel1 The relation on the left hand side.
	 * @param rel2 The relation on the right hand side.
	 * @return The new relation
	 */
	static Relation nestedLoopJoin(Relation rel1, Relation rel2){
		String attribute = rel1.commonAttribute(rel2);
		if (attribute == null) {
			return null;
		}
		
		Relation result = new Relation(rel1.getAttributes(), rel2.getAttributes(), attribute);
		for (int i = 0; i < rel1.size(); i++) {
			for (int j = 0; j < rel2.size(); j++) {
				if (rel1.getTuple(i).getAttributeValue(rel1.getAttributeLocation(attribute)).equals(rel2.getTuple(j).getAttributeValue(rel2.getAttributeLocation(attribute)))) {
					Tuple addition = new Tuple(rel1.getTuple(i), rel2.getTuple(j), attribute);
					result.addTuple(addition);
				}
			}
		}
		return result;
	}
	
	/**
	 * Perform a natural join on two relations using the hash join algorithm.
	 * @param rel1 The relation on the left hand side.
	 * @param rel2 The relation on the right hand side.
	 * @return The new relation
	 */
	static Relation hashJoin(Relation rel1, Relation rel2) throws Exception {
		String attribute = rel1.commonAttribute(rel2);	
		if (attribute == null) {
			return null;
		}
		
		Relation result = new Relation(rel1.getAttributes(), rel2.getAttributes(), attribute);
		HashMap<String, Tuple> map = new HashMap<String, Tuple>();
		
		for (int i = 0; i < rel1.size(); i++) {
			String attributeValue = rel1.getTuple(i).getAttributeValue(rel1.getAttributeLocation(attribute));
			if(!map.containsKey(attributeValue)) {
				map.put(attributeValue, rel1.getTuple(i));
			}
			else {
				throw new Exception("Hash join cannot be performed. The common attribute in "
						+ rel1.relationName + " must be unique.");
			}
		}
		
		for (int j = 0; j < rel2.size(); j++) {
			String attributeValue = rel2.getTuple(j).getAttributeValue(rel2.getAttributeLocation(attribute));
			if (map.containsKey(attributeValue)) {
				Tuple t = map.get(attributeValue);
				Tuple addition = new Tuple(t, rel2.getTuple(j), attribute);
				result.addTuple(addition);
			}
		}
		return result;
	}
	
	/**
	 * Perform a natural join on two relations using the sort merge join algorithm.
	 * @param rel1 The relation on the left hand side.
	 * @param rel2 The relation on the right hand side.
	 * @return The new relation
	 */
	static Relation sortMergeJoin(Relation rel1, Relation rel2){
		String attribute = rel1.commonAttribute(rel2);
		if (attribute == null) {
			return null;
		}
		
		if (!rel1.sortedBy().equals(attribute)) {
			rel1.sort(attribute);
		}
		if (!rel2.sortedBy().equals(attribute)) {
			rel2.sort(attribute);
		}
		Relation result = new Relation(rel1.getAttributes(), rel2.getAttributes(), attribute);
		int i = 0;
		int j = 0;
		while (i < rel1.size() && j < rel2.size()) {
			if (rel1.getTuple(i).getAttributeValue(rel1.getAttributeLocation(attribute)).equals(rel2.getTuple(j).getAttributeValue(rel2.getAttributeLocation(attribute)))) {
				while ((rel1.getTuple(i).getAttributeValue(rel1.getAttributeLocation(attribute)).equals(rel2.getTuple(j).getAttributeValue(rel2.getAttributeLocation(attribute)))) && (i < rel1.size())) {
					int k = j;
	
					while ((k < rel2.size()) && (rel1.getTuple(i).getAttributeValue(rel1.getAttributeLocation(attribute)).equals(rel2.getTuple(k).getAttributeValue(rel2.getAttributeLocation(attribute))))) {
						Tuple addition = new Tuple(rel1.getTuple(i), rel2.getTuple(k), attribute);
						result.addTuple(addition);
						k++;
					}
				}
			}
			else if (rel1.getTuple(i).getAttributeValue(rel1.getAttributeLocation(attribute)).compareTo(rel2.getTuple(j).getAttributeValue(rel2.getAttributeLocation(attribute))) == -1) {
				i++;
			}
			else {
				j++;
			}
		}
		return result;
	}	
}
