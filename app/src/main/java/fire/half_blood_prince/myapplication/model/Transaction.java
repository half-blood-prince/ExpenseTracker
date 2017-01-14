package fire.half_blood_prince.myapplication.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;

import fire.half_blood_prince.myapplication.database.CategorySchema;
import fire.half_blood_prince.myapplication.database.TransactionSchema;
import fire.half_blood_prince.myapplication.utility.SharedConstants;

/**
 * Created by Half-Blood-Prince on 1/11/2017.
 * Transaction Model class
 */

public class Transaction implements TransactionSchema, SharedConstants {

    private long id;
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
        id = cursor.getLong(cursor.getColumnIndex(T_ID));
        title = cursor.getString(cursor.getColumnIndex(TITLE));
        amount = cursor.getString(cursor.getColumnIndex(AMOUNT));
        date = cursor.getString(cursor.getColumnIndex(DATE));
        notes = cursor.getString(cursor.getColumnIndex(NOTES));
        cid = cursor.getInt(cursor.getColumnIndex(CID));
    }

    /**
     * @param database database Object
     * @param transID  id to get the transaction
     * @param canClose flag used to determine wheather to close the database connection or not
     * @return transaction object upon success , null otherwise
     */
    @Nullable
    public static Transaction get(SQLiteDatabase database, long transID, boolean canClose) {

        Transaction transaction = null;

        String q = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + T_ID + " = " + transID;

        Cursor cursor = database.rawQuery(q, null);
        if (null != cursor && cursor.moveToFirst()) {
            transaction = new Transaction(cursor);
            cursor.close();
        }

        if (canClose) database.close();

        return transaction;
    }

    /**
     * @param database database Object
     * @param canClose flag used to determine wheather to close the database connection or not
     * @return CategorySchema.CATEGORY_TYPES object upon successful find , null otherwise
     */
    public CategorySchema.CATEGORY_TYPES getCatType(SQLiteDatabase database, boolean canClose) {

        CategorySchema.CATEGORY_TYPES categoryType = null;

        Category category = Category.get(database, cid, false);
        if (null != category)
            categoryType = CategorySchema.CATEGORY_TYPES.valueOf(category.getCatType());

        if (canClose) database.close();
        return categoryType;
    }

    /**
     * @param database database Object
     * @param canClose flag used to determine wheather to close the database connection or not
     * @return id
     */
    public long save(SQLiteDatabase database, boolean canClose) {

        long primaryKey = database.insert(TABLE_TRANSACTION, null, getValues());

        if (canClose) database.close();

        return primaryKey;
    }

    /**
     * @param database database Object
     * @param canClose flag used to determine wheather to close the database connection or not
     * @return no of rows affected
     */
    public int update(SQLiteDatabase database, boolean canClose) {

        int rowsAffected = database.update(TABLE_TRANSACTION, getValues(), T_ID + " = " + id, null);

        if (canClose) database.close();

        return rowsAffected;

    }

    public int delete(SQLiteDatabase database, boolean canClose) {

        int rowsAffected = database.delete(TABLE_TRANSACTION, T_ID + " = " + id, null);

        if (canClose) database.close();

        return rowsAffected;
    }

    /**
     * @param database database Object
     * @param canClose flag used to determine wheather to close the database connection or not
     * @return maxDate , null when no date to be found
     */
    private static String getMaxDate(SQLiteDatabase database, boolean canClose) {

        String maxDate = null;

        String query = "SELECT MAX(" + TransactionSchema.DATE + ") FROM " + TransactionSchema.TABLE_TRANSACTION;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            maxDate = cursor.getString(0);
            cursor.close();
        }

        if (canClose) database.close();

        return maxDate;
    }

    /**
     * @param database database Object
     * @param canClose flag used to determine wheather to close the database connection or not
     * @return minDate , null when no date to be found
     */
    private static String getMinDate(SQLiteDatabase database, boolean canClose) {

        String minDate = null;

        String query = "SELECT MIN(" + TransactionSchema.DATE + ") FROM " + TransactionSchema.TABLE_TRANSACTION;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            minDate = cursor.getString(0);
            cursor.close();
        }

        if (canClose) database.close();

        return minDate;
    }

    /**
     * @return Transaction Object as ContentValues Object
     */
    private ContentValues getValues() {
        ContentValues values = new ContentValues();

//        values.put(T_ID, id);
        values.put(TITLE, title);
        values.put(AMOUNT, amount);
        values.put(DATE, date);
        values.put(NOTES, notes);
        values.put(CID, cid);

        return values;
    }


    public static ArrayList<String> getAllMonthWithTransaction(SQLiteDatabase database, boolean canClose) {
//        printLog("---------------MONTH------------------");

        ArrayList<String> monthList = new ArrayList<>();

        String maxDate = Transaction.getMaxDate(database, false);
        String minDate = Transaction.getMinDate(database, false);

        Calendar calendar = Calendar.getInstance();

        Calendar endCalender = Calendar.getInstance();

        try {
            calendar.setTime(dateFormat.parse(minDate));
            endCalender.setTime(dateFormat.parse(maxDate));
        } catch (Exception e) {
            e.printStackTrace();
            calendar = Calendar.getInstance();
            endCalender = Calendar.getInstance();
            endCalender.set(Calendar.YEAR, 1);
        }

        int endYear = endCalender.get(Calendar.YEAR) + 1;

        calendar.set(Calendar.DATE, 1);

        String startDate, endDate;
        do {

            startDate = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            endDate = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);

            String q = "SELECT " + TransactionSchema.T_ID + " FROM " + TransactionSchema.TABLE_TRANSACTION
                    + " WHERE " + Transaction.DATE + " >= Date('" + startDate + "') AND " + Transaction.DATE + " < Date('" + endDate + "')";

            Cursor cursor = database.rawQuery(q, null);

            if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
                cursor.close();
                monthList.add(String.format(Locale.getDefault(), "%s to %s", startDate, endDate));
//                printLog(String.format("%s - %s", startDate, endDate));
            }


        } while (calendar.get(Calendar.YEAR) < endYear);

        if (canClose) database.close();

//        printLog("--------------MONTH----------------");
        return monthList;
    }

    public static ArrayList<String> getAllWeeksWithTransaction(SQLiteDatabase database, boolean canClose) {

//        printLog("-------------WEEK-----------------");

        ArrayList<String> weekList = new ArrayList<>();

        String maxDate = Transaction.getMaxDate(database, false);
        String minDate = Transaction.getMinDate(database, false);

        Calendar calendar = Calendar.getInstance();

        Calendar endCalender = Calendar.getInstance();

        try {
            calendar.setTime(dateFormat.parse(minDate));
            endCalender.setTime(dateFormat.parse(maxDate));
        } catch (Exception e) {
            e.printStackTrace();
            calendar = Calendar.getInstance();
            endCalender = Calendar.getInstance();
            endCalender.set(Calendar.YEAR, 1);
        }

        int endYear = endCalender.get(Calendar.YEAR) + 1;

        calendar.set(Calendar.DATE, calendar.getFirstDayOfWeek());

        String startDate, endDate;
        do {

            startDate = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DATE, 6);
            endDate = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);

            String q = "SELECT " + TransactionSchema.T_ID + " FROM " + TransactionSchema.TABLE_TRANSACTION
                    + " WHERE " + Transaction.DATE + " >= Date('" + startDate + "') AND " + Transaction.DATE + " <= Date('" + endDate + "')";

            Cursor cursor = database.rawQuery(q, null);

            if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
                cursor.close();
                weekList.add(String.format(Locale.getDefault(), "%s to %s", startDate, endDate));
//                printLog(String.format("%s - %s", startDate, endDate));
            }


        } while (calendar.get(Calendar.YEAR) < endYear);

        if (canClose) database.close();

//        printLog("-------------WEEK-----------------");
        return weekList;
    }

    public static ArrayList<String> getAllDaysWithTransaction(SQLiteDatabase database, boolean canClose) {

//        printLog("-------------DAY-----------------");

        ArrayList<String> dayList;

        LinkedHashMap<String, String> daysMap = new LinkedHashMap<>();

        String q = "SELECT " + TransactionSchema.DATE + " FROM " + TransactionSchema.TABLE_TRANSACTION
                + " ORDER BY " + TransactionSchema.DATE;

        Cursor cursor = database.rawQuery(q, null);

        if (null != cursor && cursor.moveToFirst()) {

            do {

                String date = cursor.getString(cursor.getColumnIndex(TransactionSchema.DATE));
                daysMap.put(date, date);

//                printLog("DATE IN DB" + date);

            } while (cursor.moveToNext());

            cursor.close();
        }
        if (canClose) database.close();
//        printLog("-------------DAY-----------------");
        dayList = new ArrayList<>(daysMap.values());
        return dayList;
    }

//    private static void printLog(String log) {
//        SharedFunctions.printLog(log);
//    }


    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "id = %d title = %s amount = %s date = %s notes = %s cid = %d"
                , id, title, amount, date, notes, cid);
    }


    // getters and setters


    public long getId() {
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

    public void setId(long id) {
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
