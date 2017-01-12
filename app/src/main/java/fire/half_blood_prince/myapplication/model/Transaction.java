package fire.half_blood_prince.myapplication.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Locale;

import fire.half_blood_prince.myapplication.database.TransactionSchema;

/**
 * Created by Half-Blood-Prince on 1/11/2017.
 * Transaction Model class
 */

public class Transaction implements TransactionSchema {

    private int id;
    private String title;
    private String amount;
    private String date;
    private String notes;
    private int cid;

    public Transaction() {
    }

    /**
     * Cursor must be initialized and should have every column in it , otherwise error will be thrown
     *
     * @param cursor cursor from database
     */
    public Transaction(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(T_ID));
        title = cursor.getString(cursor.getColumnIndex(TITLE));
        amount = cursor.getString(cursor.getColumnIndex(AMOUNT));
        date = cursor.getString(cursor.getColumnIndex(DATE));
        notes = cursor.getString(cursor.getColumnIndex(NOTES));
        cid = cursor.getInt(cursor.getColumnIndex(CID));
    }


    /**
     * @param database database Object
     * @param canClose flag used to determine wheather to close the database connection or not
     * @return id
     */
    public long insert(SQLiteDatabase database, boolean canClose) {

        long primaryKey;

        ContentValues values = getValues();
        values.remove(T_ID);

        primaryKey = database.insert(TABLE_TRANSACTION, null, values);

        if (canClose) database.close();

        return primaryKey;
    }

    /**
     * @return Transaction Object as ContentValues Object
     */
    private ContentValues getValues() {
        ContentValues values = new ContentValues();

        values.put(T_ID, id);
        values.put(TITLE, title);
        values.put(AMOUNT, amount);
        values.put(DATE, date);
        values.put(NOTES, notes);
        values.put(CID, cid);

        return values;
    }


    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "id = %d title = %s amount = %s date = %s notes = %s cid = %d"
                , id, title, amount, date, notes, cid);
    }


    // getters and setters


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getNotes() {
        return notes;
    }

    public int getCid() {
        return cid;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }
}
