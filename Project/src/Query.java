import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Utility class for performing query functionality
 * @author chrislee
 *
 */
public class Query {

	/**
	 * A set of parsed words
	 */
	private final ArrayList<TreeSet<String>> list;

	/**
	 * A map of queries
	 */
	private final TreeMap<String, ArrayList<LinkedHashMap<String, String>>> map;
	// TODO stemmed word --> results (keys (where, score, count) to values as strings)
	
	/**
	 * Constructor
	 */
	public Query() {

		list = new ArrayList<>();

		map = new TreeMap<>();

	}

	/**
	 * Adding word in order
	 * @param words stemmed words to be added
	 */
	public void addlist(TreeSet<String> words) {


		for(var element : list) {

			if(element.toString().equals(words.toString())) {

				return;
			}
		}

		list.add(words);

	}

	/**
	 * @param path path of a file
	 * @param totalCounts the total number of a file
	 * @param currentCounts the number of a word show in the file
	 * @param words a treeSet of words from a parsed single line
	 */
	private void addMap(String path, Integer totalCounts, Integer currentCounts, TreeSet<String> words) {

		String queries = String.join(" ", words);

		if(path == null) {

			map.putIfAbsent(queries, new ArrayList<>());

		}
		else {

			String file = path.toString();

			double percentage = (double)currentCounts / totalCounts;

			String currentScore = String.format("%.8f", percentage);

			map.putIfAbsent(queries, new ArrayList<>());

			if(map.get(queries).isEmpty()) {

				LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();

				linkedHashMap.put("where", file);

				linkedHashMap.put("count", currentCounts.toString());

				linkedHashMap.put("score", currentScore);

				map.get(queries).add(linkedHashMap);

			}
			else {

				var elements = map.get(queries);

				for(var element : elements) {

					if(element.get("where").equals(file)) {

						Integer lastCounts = Integer.parseInt(element.get("count"));

						Integer tempCounts = lastCounts + currentCounts;

						String newCounts = tempCounts.toString();

						element.put("count", newCounts);

						Double tempScore = (double) tempCounts / totalCounts;

						String newScore = String.format("%.8f", tempScore);

						element.put("score", newScore);

						return;

					}

				}

				LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();

				linkedHashMap.put("where", file);

				linkedHashMap.put("count", currentCounts.toString());

				linkedHashMap.put("score", currentScore);

				map.get(queries).add(linkedHashMap);

			}
		}


	}

	/**
	 * Finding the exact number of stemmed word in files
	 * @param elements InvertedIndex object
	 */
	public void exactSearch(InvertedIndex elements) {

		var iterator = list.iterator();

		while(iterator.hasNext()) {

			var words = iterator.next();

			Set<String> wordSet = elements.getWords();

			for(String word : words) {

				if(wordSet.contains(word)) {

					loop(words, elements, word);

				}

				else {

					addMap(null, 0, 0, words);
				}

			}

		}

		sortArrayList(map);

	}

	/**
	 * Finding the number of times a word stem starts with one of the query words.
	 * @param elements InvertedIndex object
	 */
	public void partialSearch(InvertedIndex elements) {

		var iterator = list.iterator();

		Set<String> invertedWords = elements.getWords();

		while(iterator.hasNext()) {

			var words = iterator.next();

			for(String word: words) {

				ArrayList<String> matchedWords = invertedWords
						.stream().filter(s->s.startsWith(word))
						.collect(Collectors.toCollection(ArrayList::new));

				if(matchedWords.isEmpty()) {

					addMap(null, 0, 0, words);

				}
				else {

					for(String matchedword : matchedWords) {

						loop(words, elements, matchedword);

					}
				}

			}

		}

		sortArrayList(map);

	}

	/**
	 * Utility function
	 * @param words a treeSet of words from a parsed single line
	 * @param word stemmed word
	 * @param elements InvertedIndex object
	 */

	//instead passing InvetedIndex obj, create a local one maybe??
	private void loop(TreeSet<String> words, InvertedIndex elements, String word) {

		Set<String> paths = elements.getLocations(word);

		for(String path: paths) {

			int totalCounts =  elements.getTotalWords(path);

			int currentCounts = elements.getPositions(word, path).size();

			addMap(path, totalCounts, currentCounts, words);


		}

	}


	/**
	 * Method for sorting an array
	 * @param map a map storing queries
	 */
	private void sortArrayList(TreeMap<String, ArrayList<LinkedHashMap<String, String>>> map) {

		var elements = map.entrySet();

		MyComparator comparator = new MyComparator();

		for(var element: elements) {

			element.getValue().sort(comparator);
		}

	}


	/**
	 * method for writing json object
	 * @param path a path of file
	 * @throws IOException
	 */
	public void queryToJson(Path path) throws IOException {

		SimpleJsonWriter.asNestedQueryObject(path, map);

	}

}
