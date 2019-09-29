import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Utility class for performing query functionality
 * @author chrislee
 *
 */
public class Query {

	/**
	 * A set of parsed words
	 */
	private final TreeSet<String> set;

	private final TreeMap<String, TreeMap<String, String>> map;

	/**
	 * Constructor
	 */
	public Query() {

		set = new TreeSet<>();

		map = new TreeMap<>();

	}

	/**
	 * Adding word in order
	 * @param word stemmed word to be added
	 */
	public void addSet(String word) {

		set.add(word);

	}


	private void addMap(String path, String scores, Integer counts) {

		String file = path.toString();

		String queries = String.join(" ", set);

		String count = counts.toString();

		String score = scores.toString();

		map.putIfAbsent(queries, new TreeMap<String, String>());

		map.get(queries).putIfAbsent("where", file);

		map.get(queries).putIfAbsent("count", count);

		map.get(queries).putIfAbsent("score", score);

	}


	/**
	 * Finding the exact number of stemmed word in files
	 * @param elements InvertedIndex object
	 * @return the number of the exact stemmed word in files
	 */
	public void exactSearch(InvertedIndex elements) {



		int count = 0;

		Iterator<String> itr = set.iterator();

		while(itr.hasNext()) {

			String word = itr.next();

			//loop(word, elements);





		}
		//		return count;
	}

	/**
	 * Finding the number of times a word stem starts with one of the query words.
	 * @param elements InvertedIndex object
	 * @return the number of the exact stemmed word in files
	 */
	public int partialSearch(InvertedIndex elements) {

		int count = 0;

		Iterator<String> itr = set.iterator();

		Set<String> words = elements.getWords();

		while(itr.hasNext()) {

			String word = itr.next();

			for(String wd: words) {

				if(wd.startsWith(word)) {

					loop(wd, elements);

				}
			}

		}

		return count;
	}

	/**
	 * Utility function
	 * @param word stemmed word
	 * @param elements InvertedIndex object
	 * @return
	 */
	private void loop(String word, InvertedIndex elements) {



		Set<String> files = elements.getLocations(word);

		for(String file: files) {

			int count = elements.getPositions(word, file).size();

			int totalWords = elements.getTotalWords(file);

			String score = String.format("%.8f", count / totalWords);

			addMap(file,score, count);

		}

		//		return count;
	}













}
