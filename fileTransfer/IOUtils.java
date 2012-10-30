
import java.io.*;

public class IOUtils {
	
	private IOUtils() {
		return;
	}
	
	/**
	 * @param filePath Path of file (relative or absolute)
	 * @return Name of file without directory names
	 */
	public static String getFileName(String filePath) {
		int lastForwardSlash = filePath.lastIndexOf('/');
		int lastBackSlash = filePath.lastIndexOf('\\');
		int lastSlash = Math.max(lastForwardSlash, lastBackSlash);
		if (lastSlash == -1) {
			return filePath;
		}
		else {
			return filePath.substring(lastSlash + 1);
		}
	}
	
	/**
	 * @param input The InputStream to read from
	 * @return The next line of text from the specified InputStream
	 * @throws IOException
	 */
	public static String readLine(InputStream input) throws IOException {
		
		StringBuilder result = new StringBuilder();
		
		int b;
		while ((b = input.read()) >= 0) {
			char c = (char)b;
			if (c == '\r') {
				continue;
			}
			else if (c == '\n') {
				break;
			}
			else {
				result.append(c);
			}
		}
		
		return result.toString();
	}
	
	/**
	 * Safely closes the specified Closeable object, checking for null and
	 * catching expections
	 * 
	 * @param obj The Closeable object to be safely closed
	 */
	public static void safeClose(Closeable obj) {
		if (obj != null) {
			try {
				obj.close();
			}
			catch (IOException e) {
				// There's nothing we can do, so ignore it
			}
		}
	}

}
