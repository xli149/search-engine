
import java.util.ConcurrentModificationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Maintains a pair of associated locks, one for read-only operations and one
 * for writing. The read lock may be held simultaneously by multiple reader
 * threads, so long as there are no writers. The write lock is exclusive, but
 * also tracks which thread holds the lock. If unlock is called by any other
 * thread, a {@link ConcurrentModificationException} is thrown.
 *
 * @see SimpleLock
 * @see SimpleReadWriteLock
 */
public class SimpleReadWriteLock {

	/** The lock used for reading. */
	private final SimpleLock readerLock;

	/** The lock used for writing. */
	private final SimpleLock writerLock;

	/**
	 * lock for locking purpose
	 */
	private final Object lock;

	/**
	 * The number of writes
	 */
	private int writer;

	/**
	 * The number of readers
	 */
	private int reader;

	/**
	 * Logger object for logging purpose
	 */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * The process that holds the lock
	 */
	private long whoHolds;

	/**
	 * Initializes a new simple read/write lock.
	 */
	public SimpleReadWriteLock() {

		readerLock = new ReadLock();

		writerLock = new WriteLock();

		writer = 0;

		reader = 0;

		lock = new Object();

	}

	/**
	 * Returns the reader lock.
	 *
	 * @return the reader lock
	 */
	public SimpleLock readLock() {

		return readerLock;
	}

	/**
	 * Returns the writer lock.
	 *
	 * @return the writer lock
	 */
	public SimpleLock writeLock() {

		return writerLock;
	}

	/**
	 * Determines whether the thread running this code and the other thread are
	 * in fact the same thread.
	 *
	 * @param other the other thread to compare
	 * @return true if the thread running this code and the other thread are not
	 * null and have the same ID
	 *
	 * @see Thread#getId()
	 * @see Thread#currentThread()
	 */
	public static boolean sameThread(Thread other) {

		return other != null && other.getId() == Thread.currentThread().getId();
	}

	/**
	 * Used to maintain simultaneous read operations.
	 */
	private class ReadLock implements SimpleLock {

		/**
		 * Will wait until there are no active writers in the system, and then will
		 * increase the number of active readers.
		 */
		@Override
		public void lock() {

			synchronized (lock) {

				while(writer > 0) {

					try {

						lock.wait();

					} catch (InterruptedException e) {

						Thread.currentThread().interrupt();

					}

				}

				reader++;
			}

		}

		/**
		 * Will decrease the number of active readers, and notify any waiting threads if
		 * necessary.
		 */
		@Override
		public void unlock() {

			synchronized (lock) {

				reader--;

				if(reader == 0) {

					lock.notifyAll();

				}
			}

		}

	}

	/**
	 * Used to maintain exclusive write operations.
	 */
	private class WriteLock implements SimpleLock {

		/**
		 * Will wait until there are no active readers or writers in the system, and
		 * then will increase the number of active writers and update which thread
		 * holds the write lock.
		 */
		@Override
		public void lock() {

			synchronized(lock) {

				logger.debug("Thread: " + Thread.currentThread().getName());

				while(reader > 0 || writer > 0) {

					try {

						lock.wait();

					} catch (InterruptedException e) {

						logger.debug("Thread: " + Thread.currentThread().getName());

						Thread.currentThread().interrupt();

					}

				}

				writer++;

				whoHolds = Thread.currentThread().getId();
			}

		}

		/**
		 * Will decrease the number of active writers, and notify any waiting threads if
		 * necessary. If unlock is called by a thread that does not hold the lock, then
		 * a {@link ConcurrentModificationException} is thrown.
		 *
		 * @see #sameThread(Thread)
		 *
		 * @throws ConcurrentModificationException if unlock is called without previously
		 * calling lock or if unlock is called by a thread that does not hold the write lock
		 */
		@Override
		public void unlock() throws ConcurrentModificationException {

			synchronized(lock) {

				if(!SimpleReadWriteLock.sameThread(Thread.currentThread()) || whoHolds != Thread.currentThread().getId()) {

					throw new ConcurrentModificationException();

				}

				writer--;

				whoHolds = 0;

				lock.notifyAll();

			}
		}
	}
}