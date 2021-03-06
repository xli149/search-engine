import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

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
	public abstract void parseLine(String line, boolean exact) throws IOException , NullPointerException;

	/**
	 * Method for parsing files to be inherited
	 * @param filePath the path of a file
	 * @param exact the boolean value for choosing exact/partial search
	 * @throws IOException if the file is not valid to read/write
	 * @throws NullPointerException  if the file does not exist
	 */
	public default void parseFile(Path filePath, boolean exact) throws IOException , NullPointerException {

		try(BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)){

			String line = null;

			while((line = reader.readLine()) != null) {

				parseLine(line, exact);

			}
		}
	}

	/**
	 * Method for writing query to json format
	 * @param path the path of the file to be written in
	 * @throws IOException if the file is not allowed to be written in
	 */
	public abstract void queryToJson(Path path) throws IOException;

	/**
	 *  The default stemmer algorithm used by this class.
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Functionfor returning unmodifiable list
	 * @param query the query to be searched
	 * @return an unmodifiable list
	 * @throws IOException if the query is not able to be written
	 */
	public List<InvertedIndex.SearchResult> results(String query) throws IOException;

	/**
	 * Function for parsing the links from a webSite
	 * @param line the line to be parsed
	 * @param exact the search method according to true or false
	 * @return a list of InvertedIndex.SearchResult
	 * @throws IOException
	 */
	public List<InvertedIndex.SearchResult> parseLinks(String line, boolean exact) throws IOException;

}