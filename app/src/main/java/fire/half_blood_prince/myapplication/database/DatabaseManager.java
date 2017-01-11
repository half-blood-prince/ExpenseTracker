package fire.half_blood_prince.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Half-Blood-Prince on 1/11/2017.
 * DatabaseManager class
 */

public class DatabaseManager extends SQLiteOpenHelper implements TransactionSchema, CategorySchema {

    private static final String DATABASE_NAME = "ExpenseTracker";
    private static final int DATABASE_VERSION = 1;

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TRANSACTION_TABLE);
        db.execSQL(CREATE_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
