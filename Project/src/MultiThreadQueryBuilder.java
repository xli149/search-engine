import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for build query map
 * @author chrislee
 *
 */
public class MultiThreadQueryBuilder implements QueryBuilderInterface {

	/**
	 * Logger object for logging purpose
	 */
	private final static Logger logger = LogManager.getLogger();

	/**
	 * The object of InvertedIndex
	 */
	private final MultiThreadInvertedIndex index;

	/**
	 * The queue to be used for MultiThreads
	 */
	private final WorkQueue queue;

	/**
	 * Query map
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.SearchResult>> map;

	/**
	 * Constructor
	 * @param index invertedIndex instance
	 * @param queue queue passed from Driver class for MultiThreading usage
	 */
	public MultiThreadQueryBuilder(MultiThreadInvertedIndex index, WorkQueue queue) {

		this.index = index;

		this.queue = queue;

		map = new TreeMap<>();

	}

	/**
	 * Parse query file line by line
	 * @param line the line to be parsed
	 * @param exact flag for choosing searching method
	 */
	@Override
	public void parseLine(String line, boolean exact){

		queue.execute(new Task(line, exact));

	}

	/**
	 * Method for parsing the file and store the words into a treeSet
	 * @param filePath path of a file
	 * @param exact flag to determine using exact search or partial search
	 * @throws IOException if the file is unable to read
	 * @throws NullPointerException if the path is null
	 */
	@Override
	public void parseFile(Path filePath, boolean exact) throws IOException , NullPointerException {

		logger.debug("Query start parsing file: " + Thread.currentThread().getId());

		QueryBuilderInterface.super.parseFile(filePath, exact);

		try {

			queue.finished();

		} catch (InterruptedException e) {

			logger.debug("Thread: " + Thread.currentThread().getId() + " get interrupted");

		}

		logger.debug("Query finished parsing file: " + Thread.currentThread().getId());

	}

	@Override
	public void parseLinks(String line, boolean exact) {

		parseLine(line, exact);

		try {

			queue.finished();

		} catch (InterruptedException e) {

			logger.debug("Thread: " + Thread.currentThread().getId() + " get interrupted");

		}

		logger.debug("Query finished parsing file: " + Thread.currentThread().getId());

	}

	@Override
	public List<InvertedIndex.SearchResult> results(String query) throws IOException{

		if(map.containsKey(query)) {

			return Collections.unmodifiableList(map.get(query));

		}

		return null;

	}

	/**
	 * Method for writing json object
	 * @param path a path of file
	 * @throws IOException
	 */
	@Override
	public void queryToJson(Path path) throws IOException {

		SimpleJsonWriter.asNestedQueryObject(path, map);

	}

	/**
	 * Nested class for creating Task object
	 * @author chrislee
	 */
	private class Task implements Runnable{

		/**
		 * The line to be parsed
		 */
		private String line;

		/**
		 * The function to be used for parsing
		 */
		private boolean exact;

		/**
		 * Constructor
		 * @param line the line to be parsed
		 * @param exact boolean value for choosing search function
		 */
		public Task(String line, boolean exact) {

			this.line = line;

			this.exact = exact;

		}

		@Override
		public void run() {

			String [] tokens = TextParser.parse(line);

			if(tokens.length == 0) {

				return;

			}

			Stemmer stemmer = new SnowballStemmer(DEFAULT);

			TreeSet<String> words = new TreeSet<>();

			String stemmedWords;

			for (int i = 0; i < tokens.length; i++) {

				stemmedWords = stemmer.stem(tokens[i]).toString();

				words.add(stemmedWords);

			}

			String queries = String.join(" ", words);

			synchronized (map) {

				if (map.containsKey(queries)) {

					return;

				}

			}

			ArrayList<InvertedIndex.SearchResult> result = index.search(words, exact);

			synchronized(map) {

				map.put(queries, result);

			}

			logger.debug("Thread: "+ Thread.currentThread().getId() + " is finished");

		}

	}

}