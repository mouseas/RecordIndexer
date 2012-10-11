package shared.dataTransfer;

public class XferBatch {
	
	public XferBatch(int id, int projectID, String imageFilename) {
		
	}
	
	public int id;
	public int projectID;
	public String imageFilename;
	
}
//DROP TABLE batches;
//CREATE TABLE batches
//(
//	id integer not null primary key autoincrement,
//	project_id integer not null,
//	filename blob,
//	foreign key(project_id) references projects(id)
//);