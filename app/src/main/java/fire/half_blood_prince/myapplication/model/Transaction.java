package fire.half_blood_prince.myapplication.model;

import android.database.Cursor;

import java.util.Locale;

import fire.half_blood_prince.myapplication.database.TransactionSchema;

/**
 * Created by Half-Blood-Prince on 1/11/2017.
 * Transaction Model class
 */

public class Transaction implements TransactionSchema {

    int id;
    String title;
    String amount;
    String date;
    String notes;
    int cid;

    public Transaction() {
    }

    /**
     * Cursor must be initialized and should have every column in it , otherwise error will be thrown
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
