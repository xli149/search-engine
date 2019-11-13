import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Unity Class for parsing the file and store the word index and word count
 *
 * @author chrislee
 * @version Fall 2019
 *
 */
public class MultiThreadInvertedIndex extends InvertedIndex{

	/**
	 * The lock for read and write
	 */
	private final SimpleReadWriteLock lock;

	/**
	 * Constructor
	 */
	public MultiThreadInvertedIndex() {

		super();

		lock = new SimpleReadWriteLock();

	}

	/**
	 * Function for adding path and word index to a collection
	 *
	 * @param word word has stemmed
	 * @param location path of the file
	 * @param position position of the stemmed word in that file
	 */
	@Override
	public void add(String word, String location, int position) {

		lock.writeLock().lock();

		try {

			super.add(word, location, position);

		}
		finally {

			lock.writeLock().unlock();

		}

	}

	/**
	 * @return an unmodifiable Set of stemmed words
	 */
	@Override
	public Set<String> getWords(){

		lock.readLock().lock();

		try {

			return super.getWords();

		}
		finally {

			lock.readLock().unlock();

		}

	}

	/**
	 * @param word stemmed word
	 * @param file the location of a file
	 * @return an unmodifiable treeSet of locations
	 */
	@Override
	public Set<Integer> getPositions(String word, String file) {

		lock.readLock().lock();

		try {

			return super.getPositions(word, file);

		}
		finally {

			lock.readLock().unlock();

		}

	}

	/**
	 * @param word stemmed word
	 * @return a keyset of files
	 */
	@Override
	public Set<String>  getLocations(String word){

		lock.readLock().lock();

		try {

			return super.getLocations();

		}
		finally{

			lock.readLock().unlock();

		}

	}

	/**
	 *
	 * @return the paths in your counts map
	 */
	@Override
	public Set<String> getLocations(){

		lock.readLock().lock();

		try {

			return super.getLocations();

		}
		finally {

			lock.readLock().unlock();

		}
	}

	/**
	 * @return an unmodifiable map of counts
	 */
	@Override
	public Map<String, Integer> getCounts(){

		lock.readLock().lock();

		try {

			return super.getCounts();

		}
		finally {

			lock.readLock().unlock();

		}

	}

	/**
	 * Function for writing word index to Json object
	 * @param path the path of a file
	 * @throws IOException if unable to write in the file
	 */
	@Override
	public void indexToJson(Path path) throws IOException {

		lock.readLock().lock();

		try {

			super.indexToJson(path);

		}
		finally {

			lock.readLock().lock();

		}

	}

	/**
	 * Function for writing total word count to Json object
	 * @param path the path of a file
	 * @throws IOException if unable to write in the file
	 */
	@Override
	public void wordCountToJson(Path path) throws IOException{

		lock.readLock().lock();

		try {

			super.wordCountToJson(path);

		}
		finally{

			lock.readLock().lock();

		}

	}

	@Override
	public String toString() {

		lock.readLock().lock();

		try {

			return super.toString();

		}
		finally {

			lock.readLock().unlock();

		}

	}

	/**
	 * Exact search method for checking the one-to-one word from query to invertedIndex
	 * @param queries  collection of query words
	 * @return an arrayList contains SearchResult to be added in map
	 */
	@Override
	public ArrayList<SearchResult> exactSearch(Collection<String> queries) {

		lock.readLock().lock();

		try {

			return super.exactSearch(queries);

		}
		finally {

			lock.readLock().unlock();

		}

	}

	/**
	 * Partial search method for checking the star-with word from query to invertedIndex
	 * @param queries  collection of query words
	 * @return an arrayList contains SearchResult to be added in map
	 */
	@Override
	public ArrayList<SearchResult> partialSearch(Collection<String> queries) {

		lock.readLock().lock();

		try {

			return super.partialSearch(queries);

		}
		finally {

			lock.readLock().unlock();

		}

	}

	@Override
	public void addAll(InvertedIndex other) {

		lock.writeLock().lock();

		try {

			super.addAll(other);

		}
		finally {

			lock.writeLock().unlock();

		}

	}

}