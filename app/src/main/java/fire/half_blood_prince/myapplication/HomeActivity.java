package fire.half_blood_prince.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import fire.half_blood_prince.myapplication.dialogs.ProcessorPipeline;
import fire.half_blood_prince.myapplication.dialogs.TransactionProcessor;
import fire.half_blood_prince.myapplication.model.Category;
import fire.half_blood_prince.myapplication.model.Transaction;
import fire.half_blood_prince.myapplication.utility.SharedConstants;
import fire.half_blood_prince.myapplication.utility.SharedFunctions;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ProcessorPipeline, SharedConstants {


    private ExpandableListView mTransactionList;

    private TextView tvAddExpense, tvAddIncome;

    private DatabaseManager mDBManager = new DatabaseManager(this);

    private Handler mHandler;

    private TransactionExpListAdapter mAdapter;
    private HashMap<Integer, ArrayList<Transaction>> transactionMap = new HashMap<>();
    private ArrayList<Category> categoriesList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getString(R.string.app_name));

        mHandler = getHandler();

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

        mAdapter = new TransactionExpListAdapter(HomeActivity.this, categoriesList, transactionMap);
        mTransactionList.setAdapter(mAdapter);


//        String s = "SELECT * FROM " + TransactionSchema.TABLE_TRANSACTION + " AS T " + " INNER JOIN " + CategorySchema.TABLE_CATEGORY + " AS C"
//                + " ON " + "C." + CategorySchema.C_ID + " = " + "T." + TransactionSchema.CID
//                + " WHERE " + "C." + CategorySchema.CAT_TYPE + " LIKE " + "'" + CategorySchema.CATEGORY_TYPES.EXPENSE + "'";

        new TransactionLoader(Transaction.QUERY_ALL_TRANSACTION).start();

    }

    private Handler getHandler() {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case H_KEY_UPDATE_DS:
                        mAdapter.setDataSet(categoriesList, transactionMap);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    private class TransactionLoader extends Thread {

        String query;

        TransactionLoader(String query) {
            this.query = query;
        }

        @Override
        public void run() {

            SQLiteDatabase db = mDBManager.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            HashMap<Integer, ArrayList<Transaction>> tMap = new HashMap<>();
            HashMap<Integer, Category> cMap = new HashMap<>();
            ArrayList<Category> cList;

            if (cursor != null && cursor.moveToFirst()) {
                do {

                    Transaction transModel = new Transaction(cursor);

                    int id = transModel.getCid();

                    if (tMap.containsKey(id)) {
                        ArrayList<Transaction> transList = tMap.get(id);
                        transList.add(transModel);
                        tMap.remove(id);
                        tMap.put(id, transList); // tMap.replace requires java 1.8

                        Category category = cMap.get(id);
                        if (null != category) {

                            category.setTotalAmount(String.valueOf(SharedFunctions.parseInt(category.getTotalAmount()) +
                                    SharedFunctions.parseInt(transModel.getAmount())));
                            cMap.remove(id);
                            cMap.put(id, category);
                        }

                    } else {
                        ArrayList<Transaction> transList = new ArrayList<>();
                        transList.add(transModel);
                        tMap.put(id, transList);

                        Category category = new Category();
                        category.setId(id);
                        category.setCatName(cursor.getString(cursor.getColumnIndex(CategorySchema.CAT_NAME)));
                        category.setCatType(cursor.getString(cursor.getColumnIndex(CategorySchema.CAT_TYPE)));
                        category.setTotalAmount(transModel.getAmount());
                        cMap.put(id, category);
                    }


                } while (cursor.moveToNext());

                cursor.close();
            }

            db.close();

            cList = new ArrayList<>(cMap.values());
            Collections.sort(cList, new Comparator<Category>() {
                @Override
                public int compare(Category o1, Category o2) {
                    CategorySchema.CATEGORY_TYPES category_types = CategorySchema.CATEGORY_TYPES.valueOf(o1.getCatType());
                    return category_types.compareTo(CategorySchema.CATEGORY_TYPES.valueOf(o2.getCatType()));
                }
            });

            cMap.clear(); // don't want this anymore

            transactionMap = tMap;
            categoriesList = cList;

            mHandler.obtainMessage(H_KEY_UPDATE_DS).sendToTarget();

        }
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
                TransactionProcessor.show(getSupportFragmentManager(), bundle, this);
                break;
            case R.id.ac_home_tv_add_income:
                bundle = new Bundle();
                bundle.putString(TransactionProcessor.KEY_CAT_TYPE, CategorySchema.CATEGORY_TYPES.INCOME.toString());
                TransactionProcessor.show(getSupportFragmentManager(), bundle, this);
                break;
        }
    }

    @Override
    public void onProcessComplete() {
        new TransactionLoader(Transaction.QUERY_ALL_TRANSACTION).start();
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
