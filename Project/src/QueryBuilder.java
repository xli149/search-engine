import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for build query map
 * @author chrislee
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

		String [] tokens = TextParser.parse(line);

		if(tokens.length == 0) {

			return;

		}

		Stemmer stemmer = new SnowballStemmer(DEFAULT);

		TreeSet<String> words = new TreeSet<>();

		String stemmedWords;

		for (int i = 0; i < tokens.length; i++) {

			stemmedWords = stemmer.stem(tokens[i]).toString();

			words.add(stemmedWords);

		}

		String queries = String.join(" ", words);

		if (map.containsKey(queries)) {

			return;

		}

		ArrayList<InvertedIndex.SearchResult> result = index.search(words, exact);

		map.put(queries, result);

	}

	@Override
	public List<InvertedIndex.SearchResult> results(String query) throws IOException{

		if(map.containsKey(query)) {

			return Collections.unmodifiableList(map.get(query));

		}

		return null;

	}

	@Override
	public List<InvertedIndex.SearchResult> parseLinks(String line, boolean exact) throws IOException{

		parseLine(line, exact);

		return results(line);

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