package shared.dataTransfer;

import java.util.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Collection of a Batch, its Fields, and the Records that have been created from it.
 * Used to transfer a finished batch from client to server.
 * @author Martin
 *
 */
public class FinishedBatch {
	
	public Batch batch;
	protected List<Record> records;
	public List<Record> getRecords() { return records; }
	public void setRecords(List<Record> records) { this.records = records; }
	
	protected List<Field> fields;
	public List<Field> getFields() { return fields; }
	public void setFields(List<Field> fields) { this.fields = fields; }
	
	public FinishedBatch(Batch batch) {
		this.batch = batch;
		records = new ArrayList<Record>();
		fields = new ArrayList<Field>();
	}
	
	public void add(Field field) {
		if (field != null) {
			fields.add(field);
		}
	}
	
	public void add (Record record) {
		if (record != null) {
			records.add(record);
		}
	}
	
	public static String serialize(FinishedBatch fb) {
		XStream xstream = new XStream(new DomDriver());
		return xstream.toXML(fb);
	}
	
}
