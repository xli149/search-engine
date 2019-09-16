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

	// TODO add(String word, String location, int position)
	/**
	 * Function for adding path and word index to a collection
	 *
	 * @param stemmedWords word has stemmed
	 * @param filePath path of the file
	 * @param position position of the stemmed word in that file
	 */
	public void addIndex(String stemmedWords, String filePath, int position) {

		// TODO Remove all of the toLowerCase... data structures assume another class handles the cleaning/parsing/etc.

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

		/* TODO
		refactor version 1
		if (elements.get(stemmedWords) == null) {
			elements.put(stemmedWords, new TreeMap<>());
		}

		if (elements.get(stemmedWords).get(filePath) == null) {
			elements.get(stemmedWords).put(filePath, new TreeSet<>());
		}

		elements.get(stemmedWords).get(filePath).add(position);

		refactor version 2
		elements.putIfAbsent(stemmedWords, new TreeMap<>());
		elements.get(stemmedWords).putIfAbsent(filePath, new TreeSet<>());
		elements.get(stemmedWords).get(filePath).add(position);

		// put updating word count here so it is always 100% consistent with what your index is storing
		if this is the first time seeing the file
			put the file and current position in your count map
		else
			put the maximum of the stored value and current position for the file into the count map
		*/
	}

	/*
	 * TODO Add more methods... especially getters
	 *
	 * getWords(...) to return safely all the words in the index
	 * getLocations(String word) to return all the locations for that word safely
	 * etc.
	 */

	/*
	 * TODO Should not be public... would be bad if something could decide there are -11 words in a file.
	 */
	/**
	 * Function for adding path of a file and the total word count to a collection
	 * @param filePath the path of a file
	 * @param wordCounts the total number of a file
	 */
	public void addWordCounts(String filePath, int wordCounts) {
		counts.put(filePath, wordCounts);
	}

	/*
	 * TODO Throw the exceptions to Driver, Driver can provide more specific
	 * user-friendly error output.
	 */

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
		// TODO return this.elements.toString();
		return this.getClass().getName();
	}

}
