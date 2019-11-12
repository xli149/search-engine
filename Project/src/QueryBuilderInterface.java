import java.io.IOException;
import java.nio.file.Path;

public interface QueryBuilderInterface {

	public abstract void parseLine(String line, boolean exact)throws IOException , NullPointerException;

	public abstract void parseFile(Path filePath, boolean exact)throws IOException , NullPointerException;

	public abstract void queryToJson(Path path) throws IOException ;


}
