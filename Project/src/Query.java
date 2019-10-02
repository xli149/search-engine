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
	public void addlist(TreeSet<String> words) {



		System.out.println("words: " + words);
		System.out.println();
		System.out.println("previous list: " + list);
		System.out.println();

		if(!list.contains(words)) {

			for(var word : words) {

				for(var set : list) {

					if(set.contains(word)) {

						return;

					}

				}
			}
			list.add(words);
		}
		//		for(var element : list) {
		//
		//			if(element.toString().equals(words.toString())) {
		//
		//				System.out.println(element.toString());
		//				return;
		//			}
		//		}
		//
		//		list.add(words);

		System.out.println("after list: " + list);
		System.out.println();


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

			//System.out.println(file);

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

				//System.out.println("First time adding " + currentCounts);

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

						//System.out.println("second time adding " + tempCounts);

						return;

					}


				}

				//System.out.println("got here?");
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

					//System.out.println(words);

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
	 * @return the number of the exact stemmed word in files
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

	//do some test here

	/**
	 * Utility function
	 * @param word stemmed word
	 * @param elements InvertedIndex object
	 */

	//instead passing InvetedIndex obj, create a local one maybe??
	private void loop(TreeSet<String> words, InvertedIndex elements, String word) {




		Set<String> paths = elements.getLocations(word);


		for(String path: paths) {

			//TODO: fix the getTotalwords function to return 0 if word is not in here
			int totalCounts =  elements.getTotalWords(path);

			int currentCounts = elements.getPositions(word, path).size();

			addMap(path, totalCounts, currentCounts, words);


		}

	}


	private void sortArrayList(TreeMap<String, ArrayList<LinkedHashMap<String, String>>> map) {

		var elements = map.entrySet();

		MyComparator comparator = new MyComparator();

		for(var element: elements) {

			element.getValue().sort(comparator);
		}

	}


	public void queryToJson(Path path) throws IOException {

		SimpleJsonWriter.asNestedQueryObject(path, map);

	}













}
