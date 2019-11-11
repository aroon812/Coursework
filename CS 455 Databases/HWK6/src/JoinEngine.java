import java.io.File;

public class JoinEngine {
	public static void main(String[] args) {
		File data = new File("src/data");
		int numFiles = data.listFiles().length;
		Relation[] relations = new Relation[numFiles];
		int currentRelation = 0;
		for (final File fileEntry : data.listFiles()) {
			String str = fileEntry.getName();
			String fileName = str.substring(0, str.length()-4);
			String path = fileEntry.getPath();
			
			relations[currentRelation] = new Relation(fileName, path);
			currentRelation++;
		}
	}
}
