package address_book;
import java.io.*;

public class Entry {

	public static final int MAXDETAILS = 6;
	private String[] details;
	
	public Entry() {
		this.details = new String[MAXDETAILS];
	}
	
	public Entry(String[] details) {
		this();
		for (int i = 0; i < MAXDETAILS; i++) {
			this.details[i] = details[i];
		}
	}
	
	public String getDetails(int i) {
		return details[i];
	}
	
	public void setDetails(String s, int i) {
		details[i] = s;
	}
	
	public void writeEntry(BufferedWriter out) throws IOException {
		for (int i = 0; i < MAXDETAILS; i++) {
			out.write(details[i] + "\n");
		}
	}
	
	public boolean readEntry(BufferedReader in) throws IOException {
		String line = in.readLine();
		if (line == null) {
			return false;
		}
		details[0] = line;
		for (int i = 1; i < MAXDETAILS; i++) {
			details[i] = in.readLine();
		}
		return true;
	}
}
