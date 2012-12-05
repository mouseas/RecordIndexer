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
public class Batch {
	
	public BatchImage batchImage;
	public BatchImage getBatchImage() { return batchImage; }
	public void setBatchImage(BatchImage newBI) { batchImage = newBI; }
	
	protected List<Record> records;
	public List<Record> getRecords() { return records; }
	public void setRecords(List<Record> records) { this.records = records; }
	
	protected List<Field> fields;
	public List<Field> getFields() { return fields; }
	public void setFields(List<Field> fields) { this.fields = fields; }
	
	public Batch(BatchImage batch) {
		this.batchImage = batch;
		records = new ArrayList<Record>();
		fields = new ArrayList<Field>();
	}
	
	public void addField(Field field) {
		if (field != null) {
			fields.add(field);
		}
	}
	
	public void addRecord(Record record) {
		if (record != null) {
			records.add(record);
		}
	}
	
	public static String serialize(Batch fb) {
		XStream xstream = new XStream(new DomDriver());
		return xstream.toXML(fb);
	}
	
}
