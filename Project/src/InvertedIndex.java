import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
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

		counts.replace(location, position); // TODO Address later (ask me about on Piazza)

	}

	/**
	 * @return an unmodifiable Set of stemmed words
	 */
	public Set<String> getWords(){

		return Collections.unmodifiableSet(elements.keySet());

	}

	/**
	 * @param word stemmed word
	 * @param file the location of a file
	 * @return an unmodifiable treeSet of locations
	 */
	public Set<Integer> getPositions(String word, String file){

		if (elements.containsKey(word)) {

			TreeMap<String, TreeSet<Integer>> fileMap = elements.get(word);

			TreeSet<Integer> positions = fileMap.get(file);

			return Collections.unmodifiableSet(positions);
		}

		return Collections.emptySet();

	}

	/**
	 * @param word stemmed word
	 * @return a keyset of files
	 */
	public Set<String>  getLocations(String word){

		if (elements.containsKey(word)) {

			TreeMap<String, TreeSet<Integer>> fileMap = elements.get(word);

			Set<String> files = fileMap.keySet();

			return Collections.unmodifiableSet(files);
		}

		return Collections.emptySet();

	}

	/**
	 *
	 * @return the paths in your counts map
	 */
	public Set<String> getLocations(){

		Set<String> paths = counts.keySet();

		return Collections.unmodifiableSet(paths);
	}

	/**
	 * @return an unmodifiable map of counts
	 */
	public Map<String, Integer> getCounts(){

		return Collections.unmodifiableMap(counts);

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
