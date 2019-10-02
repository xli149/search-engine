import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class QueryBuilder {

	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;


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

					//System.out.println(stemmedWords);
					words.add(stemmedWords);

					query.addlist(words);

				}
			}
		}
	}




}
