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

// TODO Capitalize consistently in Javadoc

/**
 * Utility class for build query map
 * @author chrislee
 *
 */
public class QueryBuilder {

	/**
	 * declaration of invertedIndex type obj
	 */
	private final InvertedIndex index;

	/**
	 * query map
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
	 * parse query file line by line
	 * @param line TODO describe
	 * @param exact TODO describe
	 */
	public void parseLine(String line, boolean exact) {

		Stemmer stemmer = new SnowballStemmer(DEFAULT);

		TreeSet<String> words = new TreeSet<>();

		String [] tokens = TextParser.parse(line);

		String stemmedWords;

		for (int i = 0; i < tokens.length; i++) {

			stemmedWords = stemmer.stem(tokens[i]).toString();

			words.add(stemmedWords);

		}

		ArrayList<InvertedIndex.SearchResult> result = index.search(words, exact);

		String queries = String.join(" ", words);

		if(!words.isEmpty()) {

			map.putIfAbsent(queries, result);

		}

	}
	
//	TODO Better efficency
//	public void parseLine2(String line, boolean exact) {
//
//		Stemmer stemmer = new SnowballStemmer(DEFAULT);
//
//		TreeSet<String> words = new TreeSet<>();
//
//		String [] tokens = TextParser.parse(line);
//
//		String stemmedWords;
//
//		for (int i = 0; i < tokens.length; i++) {
//
//			stemmedWords = stemmer.stem(tokens[i]).toString();
//
//			words.add(stemmedWords);
//
//		}
//
//		String queries = String.join(" ", words);
//		
//		if (!words.isEmpty() && !map.containsKey(queries)) {
//		
//			ArrayList<InvertedIndex.SearchResult> result = index.search(words, exact);
//			map.putIfAbsent(queries, result);
//		}
//	}

	/**
	 *  The default stemmer algorithm used by this class.
	 */
	public final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Method for parsing the file and store the words into a treeSet
	 * @param filePath path of a file
	 * @param exact flag to determine using exact search or partial search
	 * @throws IOException if the file is unable to read
	 * @throws NullPointerException if the path is null
	 */
	public void parseFile(Path filePath, boolean exact) throws IOException , NullPointerException {

		try(BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)){

			String line = null;

			while((line = reader.readLine()) != null) {
				parseLine(line, exact);

			}
		}
	}

	/**
	 * method for writing json object
	 * @param path a path of file
	 * @throws IOException
	 */
	public void queryToJson(Path path) throws IOException {

		SimpleJsonWriter.asNestedQueryObject(path, map);

	}

}
