package fire.half_blood_prince.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import fire.half_blood_prince.myapplication.adapters.TransactionExpListAdapter;
import fire.half_blood_prince.myapplication.database.CategorySchema;
import fire.half_blood_prince.myapplication.database.DatabaseManager;
import fire.half_blood_prince.myapplication.database.TransactionSchema;
import fire.half_blood_prince.myapplication.dialogs.TransactionProcessor;
import fire.half_blood_prince.myapplication.model.Category;
import fire.half_blood_prince.myapplication.model.Transaction;
import fire.half_blood_prince.myapplication.utility.SharedFunctions;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    private ExpandableListView mTransactionList;

    private TextView tvAddExpense, tvAddIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getString(R.string.app_name));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //start

        findingViews();
        settingListeners();

//        temp();
//        temp1();
        temp2();


    }

    private void temp2() {
        DatabaseManager dbm = new DatabaseManager(this);
        SQLiteDatabase db = dbm.getReadableDatabase();

        String s = "SELECT * FROM " + TransactionSchema.TABLE_TRANSACTION + " AS T " + " INNER JOIN " + CategorySchema.TABLE_CATEGORY + " AS C"
                + " ON " + "C." + CategorySchema.C_ID + " = " + "T." + TransactionSchema.CID
               /* + " WHERE " + "C." + CategorySchema.CAT_TYPE + " LIKE " + "'" + CategorySchema.CATEGORY_TYPES.EXPENSE + "'"*/;

        Cursor cursor = db.rawQuery(s, null);

        HashMap<Integer, ArrayList<Transaction>> map = new HashMap<>();
        HashMap<Integer, Category> catMap = new HashMap<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {

                Transaction transModel = new Transaction(cursor);
                printLog(transModel.toString());

//                String catName = cursor.getString(cursor.getColumnIndex(CategorySchema.CAT_NAME));
                int id = transModel.getCid();

                if (map.containsKey(id)) {
                    ArrayList<Transaction> transList = map.get(id);
                    transList.add(transModel);
                    map.remove(id);
                    map.put(id, transList); // map.replace requires java 1.8

                    Category category = catMap.get(id);
                    if (null != category) {

                        category.setTotalAmount(String.valueOf(SharedFunctions.parseInt(category.getTotalAmount()) +
                                SharedFunctions.parseInt(transModel.getAmount())));
                        catMap.remove(id);
                        catMap.put(id, category);
                    }

                } else {
                    ArrayList<Transaction> transList = new ArrayList<>();
                    transList.add(transModel);
                    map.put(id, transList);

                    Category category = new Category();
                    category.setId(id);
                    category.setCatName(cursor.getString(cursor.getColumnIndex(CategorySchema.CAT_NAME)));
                    category.setCatType(cursor.getString(cursor.getColumnIndex(CategorySchema.CAT_TYPE)));
                    category.setTotalAmount(transModel.getAmount());
                    catMap.put(id, category);
                }


            } while (cursor.moveToNext());

            cursor.close();
        } else {
            printLog("null/empty cursor");
        }

        db.close();

        printLog("MAP " + map);
//        printLog("Cat Map" + catMap);

        ArrayList<Category> categoryArrayList = new ArrayList<>(catMap.values());
        Collections.sort(categoryArrayList, new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                CategorySchema.CATEGORY_TYPES category_types = CategorySchema.CATEGORY_TYPES.valueOf(o1.getCatType());
                return category_types.compareTo(CategorySchema.CATEGORY_TYPES.valueOf(o2.getCatType()));
            }
        });

        printLog("CAT MAP VALUE LIST " + categoryArrayList.toString());

        TransactionExpListAdapter mAdapter = new TransactionExpListAdapter(this, categoryArrayList, map);
        mTransactionList.setAdapter(mAdapter);

    }

    private void temp1() {
        DatabaseManager dbm = new DatabaseManager(this);
        SQLiteDatabase db = dbm.getReadableDatabase();

        String s = "SELECT * FROM " + TransactionSchema.TABLE_TRANSACTION + " AS T " + " INNER JOIN " + CategorySchema.TABLE_CATEGORY + " AS C"
                + " ON " + "C." + CategorySchema.C_ID + " = " + "T." + TransactionSchema.CID
                + " WHERE " + "C." + CategorySchema.CAT_TYPE + " LIKE " + "'" + CategorySchema.CATEGORY_TYPES.EXPENSE + "'";

        Cursor cursor = db.rawQuery(s, null);

        HashMap<Integer, ArrayList<Transaction>> map = new HashMap<>();
        HashMap<Integer, Category> catMap = new HashMap<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {

                Transaction transModel = new Transaction(cursor);
                printLog(transModel.toString());

//                String catName = cursor.getString(cursor.getColumnIndex(CategorySchema.CAT_NAME));
                int id = transModel.getCid();

                if (map.containsKey(id)) {
                    ArrayList<Transaction> transList = map.get(id);
                    transList.add(transModel);
                    map.remove(id);
                    map.put(id, transList); // map.replace requires java 1.8

                    Category category = catMap.get(id);
                    if (null != category) {

                        category.setTotalAmount(String.valueOf(SharedFunctions.parseInt(category.getTotalAmount()) +
                                SharedFunctions.parseInt(transModel.getAmount())));
                        catMap.remove(id);
                        catMap.put(id, category);
                    }

                } else {
                    ArrayList<Transaction> transList = new ArrayList<>();
                    transList.add(transModel);
                    map.put(id, transList);

                    Category category = new Category();
                    category.setId(id);
                    category.setCatName(cursor.getString(cursor.getColumnIndex(CategorySchema.CAT_NAME)));
                    category.setCatType(cursor.getString(cursor.getColumnIndex(CategorySchema.CAT_TYPE)));
                    category.setTotalAmount(transModel.getAmount());
                    catMap.put(id, category);
                }


            } while (cursor.moveToNext());

            cursor.close();
        } else {
            printLog("null/empty cursor");
        }

        db.close();

        printLog("MAP " + map);
//        printLog("Cat Map" + catMap);

        ArrayList<Category> categoryArrayList = new ArrayList<>(catMap.values());

        printLog("CAT MAP VALUE LIST " + categoryArrayList.toString());

        TransactionExpListAdapter mAdapter = new TransactionExpListAdapter(this, categoryArrayList, map);
        mTransactionList.setAdapter(mAdapter);


    }

    private void temp() {
        DatabaseManager dbm = new DatabaseManager(this);
        SQLiteDatabase db = dbm.getReadableDatabase();

//        ContentValues catValues = new ContentValues();
//        catValues.put(CategorySchema.CAT_NAME, "Food");
//        catValues.put(CategorySchema.CAT_TYPE, CategorySchema.CATEGORY_TYPES.EXPENSE.toString());
//
//        db.insert(CategorySchema.TABLE_CATEGORY, null, catValues);
//
//        catValues.put(CategorySchema.CAT_NAME, "Shopping");
//        catValues.put(CategorySchema.CAT_TYPE, CategorySchema.CATEGORY_TYPES.EXPENSE.toString());
//
//        db.insert(CategorySchema.TABLE_CATEGORY, null, catValues);
//
//        catValues.put(CategorySchema.CAT_NAME, "Salary");
//        catValues.put(CategorySchema.CAT_TYPE, CategorySchema.CATEGORY_TYPES.INCOME.toString());
//
//        db.insert(CategorySchema.TABLE_CATEGORY, null, catValues);

        ContentValues transValues = new ContentValues();

        transValues.put(TransactionSchema.TITLE, "Shopping Exp 1");
        transValues.put(TransactionSchema.AMOUNT, "1000");
        transValues.put(TransactionSchema.DATE, "2011-11-13");
        transValues.put(TransactionSchema.NOTES, "Note 1");
        transValues.put(TransactionSchema.CID, 2);

        db.insert(TransactionSchema.TABLE_TRANSACTION, null, transValues);

        transValues.put(TransactionSchema.TITLE, "Salary 2");
        transValues.put(TransactionSchema.AMOUNT, "2000");
        transValues.put(TransactionSchema.DATE, "2016-12-15");
        transValues.put(TransactionSchema.NOTES, "Salary 2");
        transValues.put(TransactionSchema.CID, 3);

        db.insert(TransactionSchema.TABLE_TRANSACTION, null, transValues);

        transValues.put(TransactionSchema.TITLE, "Salary 3");
        transValues.put(TransactionSchema.AMOUNT, "100000");
        transValues.put(TransactionSchema.DATE, "2012-01-11");
        transValues.put(TransactionSchema.NOTES, "Salary 3");
        transValues.put(TransactionSchema.CID, 3);

        db.insert(TransactionSchema.TABLE_TRANSACTION, null, transValues);

    }

    private void printLog(String log) {
        SharedFunctions.printLog(log);
    }


    private void findingViews() {
        mTransactionList = (ExpandableListView) findViewById(R.id.ac_ha_elv_transaction);
        tvAddExpense = (TextView) findViewById(R.id.ac_home_tv_add_expense);
        tvAddIncome = (TextView) findViewById(R.id.ac_home_tv_add_income);
    }

    private void settingListeners() {
        tvAddExpense.setOnClickListener(this);
        tvAddIncome.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.ac_home_tv_add_expense:
                bundle = new Bundle();
                bundle.putString(TransactionProcessor.KEY_CAT_TYPE, CategorySchema.CATEGORY_TYPES.EXPENSE.toString());
                TransactionProcessor.show(getSupportFragmentManager(), bundle);
                break;
            case R.id.ac_home_tv_add_income:
                bundle = new Bundle();
                bundle.putString(TransactionProcessor.KEY_CAT_TYPE, CategorySchema.CATEGORY_TYPES.INCOME.toString());
                TransactionProcessor.show(getSupportFragmentManager(), bundle);
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
