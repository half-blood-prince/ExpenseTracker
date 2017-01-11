package fire.half_blood_prince.myapplication.database;

/**
 * Created by Half-Blood-Prince on 1/11/2017.
 */

public interface TransactionSchema {

    String TABLE_TRANSACTION = "TransactionInfo";

    String T_ID = "transaction_id";
    String TITLE = "title";
    String AMOUNT = "amount";
    String DATE = "date";
    String NOTES = "notes";
    String CID = "category_id";

    String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_TRANSACTION + " ( "
            + T_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TITLE + " VARCHAR(120),"
            + AMOUNT + " VARCHAR(20),"
            + DATE + " DATE,"
            + NOTES + " VARCHAR(120),"
            + CID + " INTEGER NOT NULL "
            + " )";


}
