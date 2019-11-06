import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for build query map
 * @author chrislee
 *
 */
public class MultiThreadQueryBuilder extends QueryBuilder {

	/**
	 * Logger object for logging purpose
	 */
	private final static Logger logger = LogManager.getLogger();

	/**
	 * The object of InvertedIndex
	 */
	private final InvertedIndex index;

	/**
	 * Constructor
	 * @param index invertedIndex instance
	 */
	public MultiThreadQueryBuilder(InvertedIndex index) {

		super(index);

		this.index = index;

	}

	/**
	 * Parse query file line by line
	 * @param line the line to be parsed
	 * @param exact flag for choosing searching method
	 */
	@Override
	public void parseLine(String line, boolean exact){

		super.parseLine(line, exact);
	}

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
	@Override
	public void queryToJson(Path path) throws IOException {

		super.queryToJson(path);

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

			synchronized(index) {

				parseLine(line, exact);

			}

			logger.debug("Thread: "+ Thread.currentThread().getId() + " is finished");
		}

	}

}
