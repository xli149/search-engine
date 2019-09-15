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

	/**
	 * Collection for stemmed word index of a file
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements;

	/**
	 * Collection for total number of words in a file
	 */
	private final TreeMap<String, Integer> counts;

	/**
	 * Constructor
	 */
	public InvertedIndex() {
		elements = new TreeMap<>();
		counts = new TreeMap<>();
	}

	/**
	 * Function for adding path and word index to a collection
	 *
	 * @param stemmedWords word has stemmed
	 * @param filePath path of the file
	 * @param position position of the stemmed word in that file
	 */
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

	/**
	 * Function for adding path of a file and the total word count to a collection
	 * @param filePath the path of a file
	 * @param wordCounts the total number of a file
	 */
	public void addWordCounts(String filePath, int wordCounts) {
		counts.put(filePath, wordCounts);
	}

	/**
	 * Function for writing word index to Json object
	 * @param path the path of a file
	 */
	public void indexToJson(Path path) {

		try {
			SimpleJsonWriter.asNestedObject(elements, path);

		} catch (IOException e) {
			System.out.println("Unable to write in the file");
		}
	}

	/**
	 * Function for writing total word count to Json object
	 * @param path the path of a file
	 */
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
