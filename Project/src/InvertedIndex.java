import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class InvertedIndex{
	
	//Reading the text files in directories and its sub-directories
	/** test
	 * @param relativePath
	 * @throws FileNotFoundException 
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;
	private static int iterateNum = 1;
	private static int numOfWorlds = 0;
	private static String fileName;
	
	/**
	 * 
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static void uniqueStems(String line, TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) {
		uniqueStems(line, new SnowballStemmer(DEFAULT), elements);
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @param elements the collection of stemming the words and its position
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static void uniqueStems(String line, Stemmer stemmer, TreeMap<String, TreeMap<String, TreeSet<Integer>>>elements) {
		String [] s = TextParser.parse(line);
		String stemmedWords;
		for(int i = 0; i < s.length; i++) {
			stemmedWords = stemmer.stem(s[i]).toString();
			 if(!elements.containsKey(stemmedWords.toLowerCase())) {
				 TreeMap<String, TreeSet<Integer>> newPair = new TreeMap<>();
				 TreeSet<Integer> pos = new TreeSet<>();
				 pos.add(iterateNum);
				 newPair.put(fileName, pos);
				 elements.put(stemmedWords.toLowerCase(), newPair);
			 }
			 else  {
				 if(!elements.get(stemmedWords.toLowerCase()).containsKey(fileName)) {
					 TreeSet<Integer> pos = new TreeSet<>();
					 pos.add(iterateNum);
					 elements.get(stemmedWords.toLowerCase()).put(fileName, pos);
				 }
				 else {
					 elements.get(stemmedWords.toLowerCase()).get(fileName).add(iterateNum);
				 }
			 }
			 numOfWorlds++;
			 iterateNum++;		 
		}	
	}
	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @return a sorted set of stems from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #uniqueStems(String)
	 * @see TextParser#parse(String)
	 */
	public static void uniqueStems(Path inputFile,  TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) throws IOException {
		try(
				
				BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
		){
			fileName = inputFile.toString();
			String line = null;
			while((line = reader.readLine()) != null) {
				uniqueStems(line, elements);
				
			}
			
			iterateNum = 1;
		}catch(IOException e) {
			System.out.print("Please check if the file exist");
		}
	}
	public void ProcessFiles(Path relativePath, TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, TreeMap<String, Integer> counts) throws FileNotFoundException, IOException{
		try {
				File file = relativePath.toFile();
				iterateFiles(file, elements, counts);
		}catch(NullPointerException e) {
			System.out.println("The path is not valid");
		}
		
		

		
//		System.out.println("size of the array: " + arr.size());
//		System.out.println("size of elements " + elements.size());
	}

	/**
	 * @param files
	 */
	private void iterateFiles(File file, TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements,  TreeMap<String, Integer> counts) throws IOException{
		
		if(file != null) {
			if(file.isDirectory()) {
				File[] files = file.listFiles();
				for(File f: files) {
					//System.out.println("dir name: " + fileName);
					iterateFiles(f, elements, counts);
				}
				
			}
			else if(file.isFile()) {
				if(file.getName().toLowerCase().endsWith(".txt") || file.getName().toLowerCase().endsWith(".text")) {
					uniqueStems(file.toPath(), elements);
					//System.out.println(fileName + " " +  numOfWorlds);
					if(numOfWorlds != 0) {
						counts.put(fileName, numOfWorlds);
					}
					numOfWorlds = 0;
				}
			}

		}
		
	
	
	}
	
}
