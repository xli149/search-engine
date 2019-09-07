import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where
 * newlines are used to separate elements and nested elements are indented.
 * test..
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class SimpleJsonWriter {
	
	/**
	 * Writes the word count of each file as a pretty JSON object to a file
	 * @param counts the number of words of each file
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if the file is unable to write or read
	 */
	public static void wordsCountsPrinter(TreeMap<String, Integer> counts, Writer writer, int level)throws IOException{
		
		int flag = 0;
		writer.write("{\n");
		for(String count: counts.keySet()) {
			quote(count, writer, 1);
			writer.write(": ");
			writer.write(counts.get(count).toString());
			if(flag != counts.size() - 1) {
				writer.write(",");
			}
			writer.write("\n");
			flag++;
			
		}
		System.out.println("Get here");
		writer.write("}");
		
	}

	/**
	 * Writes the word count of each file as a pretty JSON object to a file
	 * @param counts the number of words of each file
	 * @param path 	 the path of a file
	 * @throws IOException if the file is unable to write or read
	 * 
	 * @see #wordCountsPrinter()
	 */
	public static void wordCountsPrinter(TreeMap<String, Integer> counts, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			System.out.println("path: " + path.toString());
			wordsCountsPrinter(counts, writer,0);
		}catch(FileNotFoundException e) {
			
			System.out.println("No such files");
		}catch(NullPointerException e) {
			System.out.println("Null");
		}
	}
	/**
	 * Writes the elements as a nested pretty JSON object. The generic notation used
	 * allows this method to be used for any type of map with any type of nested
	 * collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if the file is unable to read or write
	 */
	public static void asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer, int level) throws IOException {
		int flag1 = 0;
		writer.write("{\n");
		for (var e : elements.entrySet()) {
			int flag2 = 0;
			quote(e.getKey(), writer, 1);
			writer.write(": {\n");
			for(var filename : e.getValue().entrySet()) {
				int flag3 = 0;
				quote(filename.getKey(), writer, 2);
				writer.write(": [\n");
				for(int i: filename.getValue()) {
					indent(i, writer, 3);
					if(flag3 != filename.getValue().size() - 1) {
						writer.write(",");
					}
				
					flag3++;
					writer.write("\n");
				}
				writer.write("\t\t]");
				if(flag2 != e.getValue().size() - 1) {
					writer.write(",");
				}
				flag2++;
				writer.write("\n");
			}
			writer.write("\t}");
			if(flag1 != elements.size() - 1) {
				writer.write(",");
			}	
			flag1++;
			writer.write("\n");
		} 
		writer.write("}");
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path) throws IOException {
	
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}catch(FileNotFoundException e) {
			System.out.println("No such files");
		}catch(NullPointerException e) {
			System.out.println("Null");
		}
	}
	/**
	 * Writes the {@code \t} tab symbol by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException
	 */
	public static void indent(Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(String, Writer, int)
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(element.toString(), writer, times);
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		writer.write(element);
	}

	/**
	 * Writes the element surrounded by {@code " "} quotation marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @throws IOException
	 */
	public static void quote(String element, Writer writer) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Indents and then writes the element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 * @see #quote(String, Writer)
	 */
	public static void quote(String element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		quote(element, writer);
	}


}
