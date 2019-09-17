import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
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
	 * @param word word has stemmed
	 * @param location path of the file
	 * @param position position of the stemmed word in that file
	 */
	public void add(String word, String location, int position) {

		elements.putIfAbsent(word, new TreeMap<>());

		elements.get(word).putIfAbsent(location, new TreeSet<>());

		elements.get(word).get(location).add(position);

		counts.putIfAbsent(location, position);

		counts.replace(location, position);

	}

	/**
	 * @return an unmodifiable treeSet of stemmed
	 */
	public TreeSet<String> getWords(){

		return (TreeSet<String>) Collections.unmodifiableSet(elements.keySet());

	}

	/**
	 * @param word stemmed word
	 * @param file the location of a file
	 * @return an unmodifiable treeSet of locations
	 */
	public TreeSet<Integer> getLocations(String word, String file){

		TreeMap<String, TreeSet<Integer>> files = elements.get(word);

		TreeSet<Integer> locations = files.get(file);

		return (TreeSet<Integer>) Collections.unmodifiableSet(locations);
	}

	/**
	 * Function for writing word index to Json object
	 * @param path the path of a file
	 * @throws IOException if unable to write in the file
	 */
	public void indexToJson(Path path) throws IOException {

		SimpleJsonWriter.asNestedObject(elements, path);

	}

	/**
	 * Function for writing total word count to Json object
	 * @param path the path of a file
	 * @throws IOException if unable to write in the file
	 */
	public void wordCountToJson(Path path) throws IOException{

		SimpleJsonWriter.wordCountsPrinter(counts, path);

	}

	@Override
	public String toString() {

		return this.elements.toString();

	}

}
