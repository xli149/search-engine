import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for build query map
 * @author chrislee
 *
 */
public class QueryBuilder implements QueryBuilderInterface {

	/**
	 * Declaration of invertedIndex type object
	 */
	private final InvertedIndex index;

	/**
	 * Query map
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.SearchResult>> map;

	/**
	 * Constructor
	 * @param index invertedIndex instance
	 */
	public QueryBuilder(InvertedIndex index) {

		this.index = index;

		map = new TreeMap<>();

	}

	/**
	 * Parse query file line by line
	 * @param line the line to be parsed
	 * @param exact flag for choosing searching method
	 */
	@Override
	public void parseLine(String line, boolean exact) {

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

			ArrayList<InvertedIndex.SearchResult> result = index.search(words, exact);

			map.put(queries, result);

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
	 * @throws IOException if the file is unable to read
	 * @throws NullPointerException if the path is null
	 */
	@Override
	public void parseFile(Path filePath, boolean exact) throws IOException , NullPointerException {

		try(BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)){

			String line = null;

			while((line = reader.readLine()) != null) {

				parseLine(line, exact);

			}
		}
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

}