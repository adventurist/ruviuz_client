package stronglogic.ruviuz.util.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;

import stronglogic.ruviuz.util.db.RuvDBContract.RuvEntry;

import static android.content.ContentValues.TAG;

/**
 * Created by logicp on 3/31/17.
 * RuvDB Helper class
 */

public class RuvDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "ruv_search";

    private Cursor cursor;

    private static final String SQL_CREATE_STATEMENT =
            "CREATE TABLE " + RuvEntry.TABLE_NAME + " (" +
                    RuvEntry._id + " INTEGER, " +
                    RuvEntry.TYPE + " INTEGER, " +
                    RuvEntry.PATTERN + " TEXT, " +
                    "PRIMARY KEY(" +
                    RuvEntry.TYPE + ", " +
                    RuvEntry.PATTERN + "))";

    private static final String SQL_DELETE_RUVENTRIES =
            "DROP TABLE IF EXISTS " + RuvEntry.TABLE_NAME;

    public RuvDBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_RUVENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public void insertSuggestion(int type, String suggestion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(RuvEntry.TYPE, type);
        contentValues.put(RuvEntry.PATTERN, suggestion);

        long id = db.insert(RuvEntry.TABLE_NAME, null, contentValues);
        db.close();

        Log.d(TAG, "insertSuggestion: " + String.valueOf(id));
    }

    public String[] getSuggestions(int type) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { RuvEntry.PATTERN };
        String selection = RuvEntry.TYPE + "=?";
        String [] selectionWhereArgs = { String.valueOf(type) };
        cursor = db.query(
                RuvEntry.TABLE_NAME,
                columns,
                selection,
                selectionWhereArgs,
                null,
                null,
                RuvEntry.PATTERN + " ASC");

        String[] suggestionArray = new String[(int)DatabaseUtils.queryNumEntries(db, RuvEntry.TABLE_NAME)];
        int i = 0;
        while (cursor.moveToNext()) {
            String suggestion = cursor.getString(cursor.getColumnIndex(RuvEntry.PATTERN));
            suggestionArray[i] = suggestion;
            i++;
        }
        db.close();
        return suggestionArray;
    }

    public SQLiteDatabase getReadableDb() {
        return this.getReadableDatabase();
    }

    public Cursor getCursor(int type, String constraint) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { RuvEntry._ID, RuvEntry.TYPE, RuvEntry.PATTERN };
        String selection = RuvEntry.TYPE + "=? AND " + RuvEntry.PATTERN + " LIKE ?";
        String [] selectionWhereArgs = { String.valueOf(type), "%" + constraint + "%"};
        cursor = db.query(
                RuvEntry.TABLE_NAME,
                columns,
                selection,
                selectionWhereArgs,
                null,
                null,
                RuvEntry.PATTERN + " ASC");

        return cursor;
    }

}
