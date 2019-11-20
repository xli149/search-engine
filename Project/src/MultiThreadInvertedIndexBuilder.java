import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Unity Class for parsing the file and store the word index and word count
 *
 * @author chrislee
 * @version Fall 2019
 *
 */
public class MultiThreadInvertedIndexBuilder extends InvertedIndexBuilder{

	/**
	 * Declaration of invetedIndex object
	 */
	private final MultiThreadInvertedIndex index;

	/**
	 * Logger Object for logging purpose
	 */
	private final static Logger logger = LogManager.getLogger();

	/**
	 * The queue to be used for MultiThreading usage
	 */
	private final WorkQueue queue;

	/**
	 * @param index MultiThreadInvertedIndex object
	 * @param queue passed by Driver class for MultiThreading usage
	 */
	public MultiThreadInvertedIndexBuilder(MultiThreadInvertedIndex index, WorkQueue queue) {

		super(index);

		this.index = index;

		this.queue = queue;
	}

	@Override
	public void traversDirectory(Path start) throws IOException, NullPointerException{

		super.traversDirectory(start);

		try {

			queue.finished();

		} catch (InterruptedException e) {

			logger.debug("Thread: " + Thread.currentThread().getId() + "gets interrupted");
		}

	}

	@Override
	public void addStem(Path filePath) throws IOException {

		logger.debug("travers dirctory starts: Thread: " + Thread.currentThread().getId() );

		queue.execute(new Task(filePath));

		logger.debug("travers dirctory finished: Thread: " + Thread.currentThread().getId() );
	}

	/**
	 * Nested class for creating Tasks
	 * @author chrislee
	 */
	private class Task implements Runnable{

		/**
		 * The path of directory
		 */
		private Path path;

		/**
		 * Constructor
		 * @param path Directory to traverse
		 */
		public Task(Path path) {

			this.path = path;

		}

		@Override
		public void run() {

			logger.debug("Query start parsing file: " + Thread.currentThread().getId());

			try {

				InvertedIndex local = new InvertedIndex();

				InvertedIndexBuilder.addStem(path, local);

				index.addAll(local);

			} catch (IOException e) {

				logger.debug("Thread: " + Thread.currentThread().getId() + "gets IOException");

			}

			logger.debug("Query finished parsing file: " + Thread.currentThread().getId());
		}

	}

}