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
	 * Return the total number words of a file
	 * @param path
	 * @return the number of words in a file
	 */
	public int getTotalWords(String path) {

		return counts.get(path);
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
	 * exact search method for checking the one-to-one word from query to invertedindex
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

			Set<String> wordSet = getWords(); // TODO Access directly
			
//			TODO
//			if (elements.containsKey(word)) {
//				update(results,lookUp, word);
//			}

			if(wordSet.contains(word)) {
				update(results,lookUp, word);

			}

		}

		Collections.sort(results);

		return results;

	}

	/**
	 * partial search method for checking the starwith word from query to invertedindex
	 * @param queries  collection of query words
	 * @return an arraylist contains SearchResult to be added in map
	 */
	private ArrayList<SearchResult> partialSearch(Collection<String> queries) {

		ArrayList<SearchResult> results = new ArrayList<>();

		Map<String, SearchResult> lookUp = new HashMap<>();

		var list = queries;

		var iterator = list.iterator();

		Set<String> invertedWords = getWords(); // TODO Access directly

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

		Set<String> paths = getLocations(matchedWord); // TODO Don't use public method here, access the elements directly

		// TODO for(String path : elements.get(matchedWord).keySet()) {
		for(String path: paths) {
			
			// TODO Change all of these to access your data directly

			int totalCounts = getTotalWords(path);

			int currentCounts = getPositions(matchedWord, path).size();

			String file = path.toString();

			double percentage = (double)currentCounts / totalCounts; // TODO Remove---let the result object take care of this for you

			String currentScore = String.format("%.8f", percentage);

			if(lookUp.containsKey(path)) {

				var element = lookUp.get(path);
				
				// TODO element.updateCount(path); (and remove the rest of this work)

				Integer lastCounts = element.getCount();

				Integer tempCounts = lastCounts + currentCounts;

				element.setCount(tempCounts);

				Double tempScore = (double) tempCounts / totalCounts;

				String newScore = String.format("%.8f", tempScore);

				element.setScore(newScore);

			}
			else {

				SearchResult searchReasult = new SearchResult(file, currentCounts, currentScore);

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
	public static class SearchResult implements Comparable<SearchResult> { // TODO public class SearchResult (not static)

		/**
		 * file that holds the word
		 */
		private String where; // TODO final

		/**
		 * times the word shows up in that file
		 */
		private int count;

		/**
		 * frequency of the word in that file
		 */
		private String score; // TODO Remove

		/**
		 * Constructor
		 * @param where file that holds the word
		 * @param count times the word shows up in that file
		 * @param score frequency of the word in that file
		 */
		// TODO public SearchResult(String where), set the count to 0
		public SearchResult(String where, int count, String score) {

			this.where = where;

			this.count = count;

			this.score = score;

		}

		/**
		 * set value of where
		 * @param location file that holds the word
		 */
		public void setLocation(String location) { // TODO Remove

			this.where = location;

		}

		/*
		 * TODO Instead of set count, set score...
		 * 
		 * public void updateCount(String word) {
		 * 		count += elements.get(word).get(where).size();
		 * }
		 */
		
		/**
		 * set value of count
		 * @param newCount times the word shows up in that file
		 */
		public void setCount (int newCount) {

			this.count = newCount;

		}

		/**
		 * set value of score
		 * @param score frequency of the word in that file
		 */
		public void setScore (String score) {

			this.score = score;

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
		public String getScore() { // TODO Return double

			return score;
			
			// TODO return (double) count / counts.get(where); (option 1)
			
			/*
			 * TODO 
			 * 1) Calculating on the fly.
			 * 2) The other option is to calculate and save every time count changes.
			 * 3) To save when a change has been made ("dirty") and if the data is "dirty" when you access the score recalculate it
			 */

		}
		
		// TODO public String getFormattedScore() { returns the formatted 8 decimal point score }

		@Override
		public int compareTo(SearchResult o) {

			if(Double.parseDouble(this.getScore()) < Double.parseDouble(o.getScore())) {

				return 1;

			}

			else if(Double.parseDouble(this.getScore()) == Double.parseDouble(o.getScore())){

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




