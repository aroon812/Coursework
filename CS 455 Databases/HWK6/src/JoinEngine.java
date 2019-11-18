import java.io.File;
import java.util.Hashtable;
import java.util.Scanner;

public class JoinEngine {
	public static void main(String[] args) {
		File data = new File("src/data");
		int numFiles = data.listFiles().length;
		//Relation[] relations = new Relation[numFiles];
		Hashtable<String, Relation> relations = new Hashtable<String, Relation>();
		int currentRelation = 0;
		for (final File fileEntry : data.listFiles()) {
			if(fileEntry.getName().charAt(0) != '.') {
				String str = fileEntry.getName();
				String fileName = str.substring(0, str.length()-4);
				String path = fileEntry.getPath();
				
				//relations[currentRelation] = new Relation(fileName, path);
				relations.put(fileName, new Relation(fileName, path));
				currentRelation++;
			}
			
		}
		//relations[1].sort("state");
		//relations[1].printRelation();
		
		
		
	
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
			if (!relations.containsKey(chosenRelations[i])) {
				throw new IllegalArgumentException("Please enter a relation that exists");
			}
		}
		Relation rel1 = relations.get(chosenRelations[0]);
		Relation rel2 = relations.get(chosenRelations[1]);
		
		System.out.println("Choose a join algorithm:");
		System.out.println("1. Nested loop join");
		System.out.println("2. Hash Join");
		System.out.println("3. Sort-Merge Join");
		int algorithmChoice = scanner.nextInt();
		if (algorithmChoice == 1) {
			System.out.println(nestedLoopJoin(rel1, rel2));
		}	
	}
	
	static Relation nestedLoopJoin(Relation rel1, Relation rel2){
		String attribute = rel1.commonAttribute(rel2);
		Relation result = new Relation(rel1.attributes, rel2.attributes, attribute);
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
	
	static Relation HashJoin(Relation rel1, Relation rel2) {
		return null;
	}
	
	
}
