import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for build query map
 * @author chrislee
 *
 */
public class QueryBuilder {

	/**
	 *  The default stemmer algorithm used by this class.
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Method for parsing the file and store the words into a treeSet
	 * @param filePath
	 * @param query
	 * @throws IOException if the file is unable to read
	 * @throws NullPointerException if the path is null
	 */
	public static void queryParser(Path filePath, Query query) throws IOException , NullPointerException {

		Stemmer stemmer = new SnowballStemmer(DEFAULT);

		try(BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)){

			String line = null;

			while((line = reader.readLine()) != null) {

				TreeSet<String> words = new TreeSet<>();

				String [] tokens = TextParser.parse(line);

				String stemmedWords;

				for (int i = 0; i < tokens.length; i++) {

					stemmedWords = stemmer.stem(tokens[i]).toString();

					words.add(stemmedWords);

				}

				query.addlist(words);
			}
		}
	}
}
