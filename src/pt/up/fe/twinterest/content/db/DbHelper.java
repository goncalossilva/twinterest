package pt.up.fe.twinterest.content.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "remember_to_call.db";
	private static final int DATABASE_VERSION = 1;
	
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		List<DbTable> tables = new ArrayList<DbTable>();
		
		populateTables(tables);

		for(DbTable table : tables)
			db.execSQL(table.createTableSQL());
	}
	
	@Override
	public void onConfigure(SQLiteDatabase db) {
		db.enableWriteAheadLogging();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Nothing for now.
	}
	
	private void populateTables(List<DbTable> tables) {
		tables.add(DbSchema.getTweetsTable());
		tables.add(DbSchema.getMediaTable());
	}
}
