import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
public class MultiThreadQueryBuilder {

	/**
	 * Declaration of invertedIndex type object
	 */
	private final MultiThreadInvertedIndex index;

	/**
	 * Query map
	 */
	private final TreeMap<String, ArrayList<MultiThreadInvertedIndex.SearchResult>> map;

	/**
	 * Logger object for logging purpose
	 */
	private final static Logger logger = LogManager.getLogger();

	/**
	 * Constructor
	 * @param index invertedIndex instance
	 */
	public MultiThreadQueryBuilder(MultiThreadInvertedIndex index) {

		this.index = index;

		map = new TreeMap<>();

	}

	/**
	 * Parse query file line by line
	 * @param line the line to be parsed
	 * @param exact flag for choosing searching method
	 * @throws IOException if the file is not readable
	 */
	public void parseLine(String line, boolean exact) throws IOException{

		Stemmer stemmer = new SnowballStemmer(DEFAULT);

		TreeSet<String> words = new TreeSet<>();

		String [] tokens = TextParser.parse(line);

		String stemmedWords;

		for (int i = 0; i < tokens.length; i++) {

			stemmedWords = stemmer.stem(tokens[i]).toString();

			words.add(stemmedWords);

		}

		String queries = String.join(" ", words);

		if (!words.isEmpty() && !map.containsKey(queries)) {

			ArrayList<MultiThreadInvertedIndex.SearchResult> result = index.search(words, exact);

			map.putIfAbsent(queries, result);
		}
	}

	/**
	 *  The default stemmer algorithm used by this class.
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Method for parsing the file and store the words into a treeSet
	 * @param filePath path of a file
	 * @param exact flag to determine using exact search or partial search
	 * @param threads the number of threads to be used
	 * @throws IOException if the file is unable to read
	 * @throws NullPointerException if the path is null
	 */
	public void parseFile(Path filePath, boolean exact, int threads) throws IOException , NullPointerException {

		logger.debug("Query start parsing file: " + Thread.currentThread().getId());

		try(BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)){

			String line = null;

			WorkQueue queue = new WorkQueue(threads);

			while((line = reader.readLine()) != null) {

				queue.execute(new Task(line, exact));

			}

			try {

				queue.finished();

				queue.shutDown();

			} catch (InterruptedException e) {

				logger.debug("Thread: " + Thread.currentThread().getId() + " get interrupted");

			}

		}

		logger.debug("Query finished parsing file: " + Thread.currentThread().getId());

	}

	/**
	 * Method for writing json object
	 * @param path a path of file
	 * @throws IOException
	 */
	public void queryToJson(Path path) throws IOException {

		MultiThreadJsonWriter.asNestedQueryObject(path, map);

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

			logger.debug("Thread: "+ Thread.currentThread().getId() + " is runninng");

			try {

				synchronized(map) {

					parseLine(line, exact);

				}

			} catch (IOException e) {

				logger.debug("Thread: " + Thread.currentThread().getId() + "gets IOException");

			}

			logger.debug("Thread: "+ Thread.currentThread().getId() + " is finished");
		}

	}

}
