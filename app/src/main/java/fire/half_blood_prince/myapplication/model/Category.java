package fire.half_blood_prince.myapplication.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Locale;

import fire.half_blood_prince.myapplication.database.CategorySchema;

/**
 * Created by Half-Blood-Prince on 1/11/2017.
 * Category Model class
 */

public class Category implements CategorySchema {

    private int id;
    private String catName;
    private String catType;

    private String totalAmount;


    public Category() {
    }

    /**
     * constrcuts the Category object from cursor
     * Note Cursor must contain all column values
     *
     * @param cursor cursor obj
     */
    public Category(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(C_ID));
        catName = cursor.getString(cursor.getColumnIndex(CAT_NAME));
        catType = cursor.getString(cursor.getColumnIndex(CAT_TYPE));
    }

    /**
     * @param database database object
     * @param canClose flag to check whether to close database after work done
     * @return list of categories
     */
    public static ArrayList<String> getCatNames(SQLiteDatabase database, CATEGORY_TYPES catType, boolean canClose) {
        ArrayList<String> categoryList = new ArrayList<>();

        Cursor cursor = database.rawQuery(catType.equals(CATEGORY_TYPES.INCOME) ?
                        QUERY_GET_ALL_INCONE_CATEGORY : QUERY_GET_ALL_EXPENSE_CATEGORY
                , null);

        if (null != cursor && cursor.moveToFirst()) {
            do {
                categoryList.add(cursor.getString(cursor.getColumnIndex(CAT_NAME)));
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (canClose) database.close();
        return categoryList;
    }

    /**
     * @param database database object
     * @param catName  category name for which the id is needed
     * @param canClose flag to check whether to close database after work done
     * @return id associated with the catName
     */
    public static int getId(SQLiteDatabase database, String catName, boolean canClose) {
        int id = -1;

        String q = "SELECT " + C_ID + " FROM " + TABLE_CATEGORY + " WHERE " + CAT_NAME + " LIKE " + "'" + catName + "'";

        Cursor cursor = database.rawQuery(q, null);

        if (null != cursor && cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex(C_ID));
            cursor.close();
        }

        if (canClose) database.close();
        return id;
    }

    public static  String[] getCategoriesTypes() {

        String[] catTypes = new String[CategorySchema.CATEGORY_TYPES.values().length];
        int i = 0;
        for (CategorySchema.CATEGORY_TYPES ct : CategorySchema.CATEGORY_TYPES.values())
            catTypes[i++] = ct.toString();

        return catTypes;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "id = %d catName = %s catType = %s totalAmount = %s"
                , id, catName, catType, totalAmount);
    }


    // getters and setters


    public int getId() {
        return id;
    }

    public String getCatName() {
        return catName;
    }

    public String getCatType() {
        return catType;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public void setCatType(String catType) {
        this.catType = catType;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
