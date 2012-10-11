package shared.dataTransfer;

public class XferProject {
	
	public int id;
	public String title;
	public int recordsPerImage;
	public int firstYCoord;
	public int fieldHeight;
	public int numRows;
	
	public XferProject(int id, int recordsPerImage, int firstYCoord, int fieldHeight,
			int numRows, String title) {
		this.id = id;
		this.title = title;
		this.recordsPerImage = recordsPerImage;
		this.firstYCoord = firstYCoord;
		this.fieldHeight = fieldHeight;
		this.numRows = numRows;
	}
}