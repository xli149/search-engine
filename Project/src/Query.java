import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
	private final ArrayList<TreeSet<String>> list;

	private final TreeMap<String, ArrayList<LinkedHashMap<String, String>>> map;

	/**
	 * Constructor
	 */
	public Query() {

		list = new ArrayList<>();

		map = new TreeMap<>();

	}

	/**
	 * Adding word in order
	 * @param word stemmed word to be added
	 */
	public void addlist(TreeSet<String> word) {

		if(!list.contains(word)) {
			list.add(word);
		}
	}


	/**
	 * @param path path of a file
	 * @param scores percentage of total matches over total words
	 * @param counts total words in a file
	 */
	private void addMap(String path, Integer totalCounts, Integer currentCounts, TreeSet<String> words) {



		String queries = String.join(" ", words);

		if(path == null) {

			map.putIfAbsent(queries, new ArrayList<>());

		}


		else {

			String file = path.toString();

			System.out.println(file);

			double percentage = (double)currentCounts / totalCounts;

			String currentScore = String.format("%.8f", percentage);

			//TODO: ADDING comparator
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

			loop(words, elements);


		}

	}

	/**
	 * Finding the number of times a word stem starts with one of the query words.
	 * @param elements InvertedIndex object
	 * @return the number of the exact stemmed word in files
	 */
	//	public void partialSearch(InvertedIndex elements) {
	//
	//		var itr = set.iterator();
	//
	//		Set<String> words = elements.getWords();
	//
	//		while(itr.hasNext()) {
	//
	//			String word = itr.next();
	//
	//			for(String wd: words) {
	//
	//				if(wd.startsWith(word)) {
	//
	//					loop(wd, elements);
	//
	//				}
	//			}
	//
	//		}
	//
	//	}

	//do some test here

	/**
	 * Utility function
	 * @param word stemmed word
	 * @param elements InvertedIndex object
	 */
	private void loop(TreeSet<String> words, InvertedIndex elements) {

		for(String word: words) {
			//System.out.println(word);
			if(elements.getWords().contains(word)) {

				Set<String> paths = elements.getLocations(word);

				for(String path: paths) {

					//TODO: fix the getTotalwords function to return 0 if word is not in here
					int totalCounts =  elements.getTotalWords(path);

					int currentCounts = elements.getPositions(word, path).size();

					addMap(path, totalCounts, currentCounts, words);



				}
			}

			else {

				addMap(null, 0, 0, words);

			}
		}

	}




	public void queryToJson(Path path) throws IOException {

		SimpleJsonWriter.asNestedQueryObject(path, map);

	}













}
