package pt.up.fe.twinterest.content.db;


public class DbTable {
	private String mName;
	private String[][] mFields;
	private String[] mPrimary;
	private String[][] mForeign;
	
	public DbTable (String name, String[][] fields, String[] primary, String[][] foreign) {
		mName = name;
		mFields = fields;
		mPrimary = primary;
		mForeign = foreign;
	}
	
	private String getFields() {
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < mFields.length; i++) {
			for(int j = 0; j < mFields[i].length; j++) {
				if(j > 0)
					builder.append(" ");
				
				builder.append(mFields[i][j]);
			}
			
			if((i + 1) < mFields.length)
				builder.append(",");
		}
		
		return builder.toString();
	}
	
	private String getPrimary() {
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < mPrimary.length; i++) {
			if(i == 0)
				builder.append("PRIMARY KEY (");
			
			builder.append(mPrimary[i]);
			
			if((i + 1) < mPrimary.length)
				builder.append(",");
			else
				builder.append(")");
		}
		
		return builder.toString();
	}
	
	private String getForeign() {
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < mForeign.length; i++) {
			builder.append("FOREIGN KEY (").append(mForeign[i][0]).append(") REFERENCES ")
				.append(mForeign[i][1]).append("(").append(mForeign[i][2]).append(")");
			
			if(mForeign[i].length > 3)
				builder.append(" ").append(mForeign[i][3]);
			
			if((i + 1) < mForeign.length)
				builder.append(",");
		}
		
		return builder.toString();
	}
	
	public String getName() {
		return mName;
	}
	
	public String createTableSQL() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("CREATE TABLE ").append(mName).append("(").append(getFields());
		builder.append(((mPrimary != null) ? "," + getPrimary() : ""));
		builder.append(((mForeign != null) ? "," + getForeign() : ""));
		builder.append(");");
    	
		return builder.toString();
	}
}