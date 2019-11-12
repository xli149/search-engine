import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

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
	private final InvertedIndex index;

	/**
	 * Logger Object for logging purpose
	 */
	private final static Logger logger = LogManager.getLogger();

	// TODO Take a MultiThreadInvertedIndex as a parameter
	/**
	 * @param index MultiThreadInvertedIndex object
	 */
	public MultiThreadInvertedIndexBuilder(InvertedIndex index) {

		super(index);

		this.index = index;
	}

	/*
	 * TODO No longer overrides the original traverseDirectory...
	 * so remove the threads parameter here and move it to the constructor
	 * 
	 * Even better, instead of passing a threads param, pass to the constructor
	 * a work queue that is used by both your threaded builder and query builder 
	 * classes.
	 * 
	 * If you pass a work queue, traverseDirectory is basically:
	 * 
	 * super.traverseDirectory(start);
	 * queue.finish();
	 * 
	 * And then override addStem (which is called by traverseDirectory)
	 * to add a task to the work queue.
	 */
	
	/**
	 * Recursively checking if a file descriptor points to a file or not
	 * pass it to parse if it is a file, otherwise get a list of sub-folders
	 * and call itself again
	 *
	 * @param start starting path of stream
	 * @param threads number of threads to be used
	 * @throws IOException if the file is unable to read
	 * @throws NullPointerException if the path is null
	 */
	public void traversDirectory(Path start, int threads) throws IOException, NullPointerException {

		logger.debug("travers dirctory starts: Thread: " + Thread.currentThread().getId() );

		ArrayList<Path> paths = super.getPaths(start);

		Iterator<Path> itr = paths.iterator();

		WorkQueue queue = new WorkQueue(threads);

		while(itr.hasNext()) {

			queue.execute(new Task(itr.next(), index));

		}

		try {

			queue.finished();

			queue.shutDown();

		} catch (InterruptedException e) {

			logger.debug("Thread: " + Thread.currentThread().getId() + "gets interrupted");
		}

		logger.debug("travers dirctory finished: Thread: " + Thread.currentThread().getId() );
	}

	/**
	 * Nested class for creating Tasks
	 * @author chrislee
	 */
	private static class Task implements Runnable{

		/**
		 * The path of directory
		 */
		private Path path;

		/**
		 * Object of MultiThreadInvertedIndex
		 */
		private InvertedIndex index;

		/**
		 * Constructor
		 * @param path Directory to traverse
		 * @param index object to MultiThreadInvertedIndex
		 */
		public Task(Path path, InvertedIndex index) {

			this.path = path;

			this.index = index;

		}

		@Override
		public void run() {

			logger.debug("Query start parsing file: " + Thread.currentThread().getId());

			try {


				InvertedIndex local = new InvertedIndex();

				InvertedIndexBuilder.addStem(path, local);

				synchronized(index) { // TODO Remove

					index.addAll(local);

				}





			} catch (IOException e) {

				logger.debug("Thread: " + Thread.currentThread().getId() + "gets IOException");

			}

			logger.debug("Query finished parsing file: " + Thread.currentThread().getId());
		}

	}

}