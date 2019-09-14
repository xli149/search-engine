import java.io.IOException;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Unity Class for parsing the file and store the word index and word count
 *
 * @author chrislee
 * @version Fall 2019
 *
 */
public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements;

	private final TreeMap<String, Integer> counts;

	public InvertedIndex() {
		elements = new TreeMap<>();
		counts = new TreeMap<>();
	}

	public void addIndex(String stemmedWords, String filePath, int position) {

		if(!elements.containsKey(stemmedWords.toLowerCase())) {
			TreeMap<String, TreeSet<Integer>> newPair = new TreeMap<>();
			TreeSet<Integer> pos = new TreeSet<>();
			pos.add(position);
			newPair.put(filePath, pos);
			elements.put(stemmedWords.toLowerCase(), newPair);

		}
		else  {
			if(!elements.get(stemmedWords.toLowerCase()).containsKey(filePath)) {
				TreeSet<Integer> pos = new TreeSet<>();
				pos.add(position);
				elements.get(stemmedWords.toLowerCase()).put(filePath, pos);
			}
			else {
				elements.get(stemmedWords.toLowerCase()).get(filePath).add(position);
			}
		}

	}

	public void addWordCounts(String filePath, int wordCounts) {
		counts.put(filePath, wordCounts);
	}

	public void indexToJson(Path path) {

		try {
			SimpleJsonWriter.asNestedObject(elements, path);

		} catch (IOException e) {
			System.out.println("Unable to write in the file");
		}
	}

	public void wordCountToJson(Path path) {

		try {
			SimpleJsonWriter.wordCountsPrinter(counts, path);

		} catch (IOException e) {
			System.out.println("Unable to write in the file");
		}
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

}
