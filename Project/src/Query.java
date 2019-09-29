import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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

	private final TreeMap<String, TreeMap<String, String>> map;

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

		list.add(word);

	}


	/**
	 * @param path path of a file
	 * @param scores percentage of total matches over total words
	 * @param counts total words in a file
	 */
	private void addMap(String path, Integer totalCounts, Integer currentCounts, TreeSet<String> words) {

		String file = path.toString();

		String queries = String.join(" ", words);

		System.out.println("queries " + queries);

		double percentage = (double)currentCounts / totalCounts;

		String currentScore = String.format("%.8f", percentage);


		//TODO: ADDING comparator
		map.putIfAbsent(queries, new TreeMap<String, String>());

		map.get(queries).putIfAbsent("where", file);

		map.get(queries).putIfAbsent("count", totalCounts.toString());

		Integer lastCounts = Integer.parseInt(map.get(queries).get("count"));

		Integer newCounts = lastCounts + currentCounts;

		map.get(queries).put("count", newCounts.toString());

		map.get(queries).putIfAbsent("score", currentScore);

		double newPercentage = (double) newCounts / totalCounts;

		String newScore = String.format("%.8f", percentage);

		map.get(queries).put("score", newScore);


	}


	/**
	 * Finding the exact number of stemmed word in files
	 * @param elements InvertedIndex object
	 */
	public void exactSearch(InvertedIndex elements) {

		//		System.out.println("Search start");
		//		Iterator<String> itr = set.iterator();
		//
		//		while(itr.hasNext()) {
		//
		//			String word = itr.next();
		//
		//			loop(word, elements);
		//
		//		}

		//		loop(elements);

		//		ArrayList<String> existWords = set.stream()
		//							 .filter(w->elements.getWords().contains(w))
		//							 .collect(Collectors.toCollection(ArrayList::new));

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

	/**
	 * Utility function
	 * @param word stemmed word
	 * @param elements InvertedIndex object
	 */
	private void loop(TreeSet<String> words, InvertedIndex elements) {


		for(String word: words) {

			if(elements.getWords().contains(word)) {

				Set<String> paths = elements.getLocations(word);

				for(String path: paths) {

					int totalCounts = elements.getTotalWords(path);

					int currentCounts = elements.getPositions(word, path).size();

					//					double percentage = (double)currentCounts / totalCounts;
					//
					//					String score = String.format("%.8f", percentage);

					addMap(path, totalCounts, currentCounts, words);


				}


			}

		}


		//		TreeSet<String> words = new TreeSet<>();
		//
		//		Iterator<String> iterator = set.iterator();
		//
		//
		//
		//		while(iterator.hasNext()) {
		//
		//			int totalCounts = 0;
		//
		//			int totalNum = 0;
		//
		//			int score;
		//
		//			String word = iterator.next();
		//
		//			if(elements.getWords().contains(word)) {
		//
		//
		//				var files = elements.getLocations(word);
		//
		//				for(String file: files) {
		//
		//					int currentCount = elements.getPositions(word, file).size();
		//
		//					//System.out.println("Count: " + count);
		//
		//					int totalWords = elements.getTotalWords(file);
		//
		//					//System.out.println("total words " + totalWords );
		//
		//
		//					addMap()
		//
		//
		//				}
		//			}
		//		}
		//
		//		double percentage = (double)count / totalWords;
		//
		//		String score = String.format("%.8f", percentage);
		//
		//		//System.out.println("Score " + score);
		//
		//
		//		addMap(file,score, count);

	}


	public void queryToJson(Path path) throws IOException {
		//
		//		var iterator = map.entrySet().iterator();
		//		while(iterator.hasNext()) {
		//			System.out.println("Key " + iterator.next().getKey());
		//
		//		}

		SimpleJsonWriter.asNestedQueryObject(path, map);

	}













}
