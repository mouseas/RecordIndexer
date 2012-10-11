package shared.dataTransfer;

public class XferField {
	
	public XferField (int id, int projectID, String title, int xCoord,
			int width, String helpHtml, String knownData) {
		
	}
	
	public int id;
	public int projectID;
	public String title;
	public int xCoord;
	public int width;
	public String helpHtml;
	public String knownData;
	
}
//DROP TABLE fields;
//CREATE TABLE fields
//(
//	id integer not null primary key autoincrement,
//	project_id integer not null,
//	title varchar(255) not null,
//	x_coord integer not null,
//	width integer not null,
//	help_html blob,
//	known_data blob,
//	foreign key(project_id) references projects(id)
//)