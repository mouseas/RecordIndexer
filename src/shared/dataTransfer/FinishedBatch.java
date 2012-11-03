package shared.dataTransfer;

import java.util.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class FinishedBatch {
	
	public Batch batch;
	protected List<Record> records;
	public List<Record> getRecords() { return records; }
	
	protected List<Field> fields;
	public List<Field> getFields() { return fields; }
	
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
