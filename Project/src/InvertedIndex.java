import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

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

		new ArrayList<>();

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

		if(counts.get(location) < position) {

			counts.put(location, position);

		}

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
	public Set<Integer> getPositions(String word, String file) {

		if (elements.containsKey(word)) {

			TreeMap<String, TreeSet<Integer>> fileMap = elements.get(word);

			if(fileMap.containsKey(file)) {

				TreeSet<Integer> positions = fileMap.get(file);

				return Collections.unmodifiableSet(positions);

			}

			return Collections.emptySet();

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

	/**
	 * Exact search method for checking the one-to-one word from query to invertedindex
	 * @param queries  collection of query words
	 * @return an arraylist contains SearchResult to be added in map
	 */
	private ArrayList<SearchResult> exactSearch(Collection<String> queries) {

		ArrayList<SearchResult> results = new ArrayList<>();

		Map<String, SearchResult> lookUp = new HashMap<>();

		var list  = queries;

		var iterator = list.iterator();

		while(iterator.hasNext()) {

			var word = iterator.next();

			if (elements.containsKey(word)) {
				update(results,lookUp, word);
			}

		}

		Collections.sort(results);

		return results;

	}

	/**
	 * Partial search method for checking the starwith word from query to invertedindex
	 * @param queries  collection of query words
	 * @return an arraylist contains SearchResult to be added in map
	 */
	private ArrayList<SearchResult> partialSearch(Collection<String> queries) {

		ArrayList<SearchResult> results = new ArrayList<>();

		Map<String, SearchResult> lookUp = new HashMap<>();

		var list = queries;

		var iterator = list.iterator();

		Set<String> invertedWords = elements.keySet();

		while(iterator.hasNext()) {

			var word = iterator.next();

			ArrayList<String> matchedWords = invertedWords
					.stream().filter(s->s.startsWith(word))
					.collect(Collectors.toCollection(ArrayList::new));

			for(String matchedWord : matchedWords) {

				update(results,lookUp, matchedWord);

			}

		}

		/* TODO Start this way...
		for (String word : elements.keySet()) {
			then see this: https://github.com/usf-cs212-fall2019/lectures/blob/master/Data%20Structures/src/FindDemo.java#L146-L163
		}
		 */

		Collections.sort(results);

		return results;
	}

	/**
	 * Utility method for update result arraylist and lookUp map
	 * @param results an arrayList of searchResult obj to be add into map
	 * @param lookUp a map for checking if a file has existed
	 * @param matchedWord the matched word from query to invertedIndex
	 */
	private void update(ArrayList<SearchResult> results,Map<String, SearchResult> lookUp, String matchedWord) {


		for(String path : elements.get(matchedWord).keySet()) {

			String file = path.toString();

			if(lookUp.containsKey(path)) {

				var element = lookUp.get(path);

				element.updateCount(matchedWord);

			}
			else {

				SearchResult searchReasult = new SearchResult(file);

				searchReasult.updateCount(matchedWord);

				results.add(searchReasult);

				lookUp.put(file, searchReasult);

			}

		}

	}

	/**
	 * Search method for calling exactSearch and partialSearch methods
	 * @param queries collection of query words
	 * @param exact boolean value to determine using exactSearch or partialSearch
	 * @return an arrayList of SearchResult obj to be add to query map
	 */
	public ArrayList<SearchResult> search(Collection<String> queries, boolean exact){

		if(exact) {

			return exactSearch(queries);

		}

		else {

			return partialSearch(queries);

		}


	}

	/**
	 * Nested inner class
	 * @author chrislee
	 *
	 */
	public class SearchResult implements Comparable<SearchResult> {

		/**
		 * File that holds the word
		 */
		private final String where;

		/**
		 * Times the word shows up in that file
		 */
		private int count;

		/**
		 * Constructor
		 * @param where file that holds the word
		 */
		public SearchResult(String where) {

			this.where = where;

			this.count = 0;

		}

		/**
		 * @param word the word to be updated on its count
		 */
		public void updateCount(String word) {

			count += elements.get(word).get(where).size();
		}

		/**
		 * Set value of count
		 * @param newCount times the word shows up in that file
		 */
		public void setCount (int newCount) {

			this.count = newCount;

		}


		/**
		 * @return the file name that word is in
		 */
		public String getLocation() {

			return where;
		}

		/**
		 * @return the number that the word in that file
		 */
		public int getCount() {

			return count;

		}
		/**
		 * @return the percentage of the frequency of the word
		 */
		public double getScore() {


			return (double) count / counts.get(where);

		}

		/**
		 * @return string formatted score
		 */
		public String getFormattedScore() {

			return String.format("%.8f", (double) count / counts.get(where));

		}


		@Override
		public int compareTo(SearchResult o) {

			if(this.getScore() < o.getScore()) {

				return 1;

			}

			else if(this.getScore() == o.getScore()){

				if(this.getCount() < o.getCount()) {

					return 1;
				}
				else if(this.getCount() == o.getCount()){

					return this.getLocation().compareTo(o.getLocation());

				}
				else {

					return -1;
				}
			}
			else {
				return -1;
			}



		}


	}

}




