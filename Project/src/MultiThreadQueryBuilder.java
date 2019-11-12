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
public class MultiThreadQueryBuilder implements QueryBuilderInterface {

	/**
	 * Logger object for logging purpose
	 */
	private final static Logger logger = LogManager.getLogger();

	/**
	 * The object of InvertedIndex
	 */
	private final MultiThreadInvertedIndex index;

	private final WorkQueue queue;


	/**
	 *  The default stemmer algorithm used by this class.
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;


	/**
	 * Query map
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.SearchResult>> map;

	/**
	 * Constructor
	 * @param index invertedIndex instance
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
	 * @param threads the number of threads to be used
	 * @throws IOException if the file is unable to read
	 * @throws NullPointerException if the path is null
	 */
	@Override
	public void parseFile(Path filePath, boolean exact) throws IOException , NullPointerException {

		logger.debug("Query start parsing file: " + Thread.currentThread().getId());

		try(BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)){

			String line = null;

			while((line = reader.readLine()) != null) {

				parseLine( line, exact);

			}

		}

		try {

			queue.finished();

			queue.shutDown();

		} catch (InterruptedException e) {

			logger.debug("Thread: " + Thread.currentThread().getId() + " get interrupted");

		}

		logger.debug("Query finished parsing file: " + Thread.currentThread().getId());

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

			logger.debug("Thread: "+ Thread.currentThread().getId() + " is runninng");

			Stemmer stemmer = new SnowballStemmer(DEFAULT);

			TreeSet<String> words = new TreeSet<>();

			String [] tokens = TextParser.parse(line);

			String stemmedWords;

			for (int i = 0; i < tokens.length; i++) {

				stemmedWords = stemmer.stem(tokens[i]).toString();

				words.add(stemmedWords);

			}

			String queries = String.join(" ", words);

			synchronized(map) {

				if (!words.isEmpty() && !map.containsKey(queries)) {

					ArrayList<InvertedIndex.SearchResult> result = index.search(words, exact);

					map.put(queries, result);

				}
			}

			logger.debug("Thread: "+ Thread.currentThread().getId() + " is finished");
		}

	}

}