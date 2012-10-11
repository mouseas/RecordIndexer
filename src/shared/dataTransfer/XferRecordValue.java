package shared.dataTransfer;

public class XferRecordValue {
	
	public XferRecordValue(int id, int batchID, int fieldID, int rowNumber, 
			String value) {
		
	}
	
	public int id;
	public int batchID;
	public int fieldID;
	public int rowNumber;
	public String value;
}
//DROP TABLE record_values;
//CREATE TABLE record_values
//(
//	id integer not null primary key autoincrement,
//	batch_id integer not null,
//	field_id integer not null,
//	row_number integer not null,
//	value varchar(255),
//	foreign key(batch_id) references batches(id),
//	foreign key(field_id) references fields(id)
//);