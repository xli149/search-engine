import java.io.IOException;
import java.nio.file.Path;

/**
 * Utility interface inherited by both QueryBuilder
 * and MultithreadQueryBuilder class
 * @author chrislee
 *
 */
public interface QueryBuilderInterface {

	/**
	 * Method for parsing a single line to be inherited
	 * @param line the line to be parsed
	 * @param exact boolean value for choosing between exact/partial search
	 * @throws IOException if the file is not valid to read/write
	 * @throws NullPointerException if the file does not exist
	 */
	public abstract void parseLine(String line, boolean exact)throws IOException , NullPointerException;

	/**
	 * Method for parsing files to be inherited
	 * @param filePath the path of a file
	 * @param exact the boolean value for choosing exact/partial search
	 * @throws IOException if the file is not valid to read/write
	 * @throws NullPointerException  if the file does not exist
	 */
	public abstract void parseFile(Path filePath, boolean exact)throws IOException , NullPointerException;

	/**
	 * Method for writing query to json format
	 * @param path the path of the file to be written in
	 * @throws IOException if the file is not allowed to be written in
	 */
	public abstract void queryToJson(Path path) throws IOException ;

}