package fire.half_blood_prince.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import fire.half_blood_prince.myapplication.adapters.TransactionVPAdapter;
import fire.half_blood_prince.myapplication.database.DatabaseManager;
import fire.half_blood_prince.myapplication.dialogs.ProcessorPipeline;
import fire.half_blood_prince.myapplication.model.Category;
import fire.half_blood_prince.myapplication.model.Transaction;
import fire.half_blood_prince.myapplication.utility.SharedConstants;
import fire.half_blood_prince.myapplication.utility.SharedFunctions;

public class HomeActivity extends AppCompatActivity
        implements SharedConstants, NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, ProcessorPipeline {


    private Toolbar mToolBar;

    private DrawerLayout mDrawerLayout;

    private ViewPager mPager;

    private TextView tvNoTransDetails;

    private TransactionVPAdapter mAdapter;

    private ArrayList<String> mDataSet = new ArrayList<String>();

//    private TextView tvAddExpense, tvAddIncome, tvBalance;

    private Handler mHandler;

    private DatabaseManager mDBManager;

    private String lastSelectedFrequency = TransactionBatchLoader.FREQUENCY_DAILY;

    private static final String KEY_CAT_SEED_STATUS = "is_cat_seeded";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDBManager = new DatabaseManager(this);
        mHandler = getHandler();


        findingViews();

        setSupportActionBar(mToolBar);
        setTitle(getString(R.string.app_name));
        setUpDrawerLayout();

        findingViews();
        settingListeners();

        mAdapter = new TransactionVPAdapter(getSupportFragmentManager(), mDataSet);
        mPager.setAdapter(mAdapter);

        new TransactionBatchLoader(lastSelectedFrequency).start();

        SharedPreferences preferences = getSharedPreferences(MAIN_PREF, MODE_PRIVATE);
        if (!preferences.getBoolean(KEY_CAT_SEED_STATUS, false)) {
            Category.seed(new DatabaseManager(this).getWritableDatabase(), true);
            preferences.edit().putBoolean(KEY_CAT_SEED_STATUS, true).apply();
        }

    }


    private Handler getHandler() {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case H_KEY_UPDATE_DS:
                        if (mDataSet.isEmpty()) {
                            tvNoTransDetails.setVisibility(View.VISIBLE);
                            mPager.setVisibility(View.GONE);
                        } else {
                            tvNoTransDetails.setVisibility(View.GONE);
                            mPager.setVisibility(View.VISIBLE);
                        }
                        mAdapter.setDataSet(mDataSet);
                        break;
                    default:
                        super.handleMessage(msg);
                }

            }
        };
    }

    private void setUpDrawerLayout() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void findingViews() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mPager = (ViewPager) findViewById(R.id.ac_home_vp);
        tvNoTransDetails = (TextView) findViewById(R.id.ac_home_tv_no_trans_details);
//        tvAddExpense = (TextView) findViewById(R.id.ac_home_tv_add_expense);
//        tvAddIncome = (TextView) findViewById(R.id.ac_home_tv_add_income);
//        tvBalance = (TextView) findViewById(R.id.ac_home_tv_balance);

    }

    private void settingListeners() {
//        tvAddExpense.setOnClickListener(this);
//        tvAddIncome.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        Bundle bundle;
//        switch (v.getId()) {
//            case R.id.ac_home_tv_add_expense:
//                bundle = new Bundle();
//                bundle.putString(TransactionProcessor.KEY_CAT_TYPE, CategorySchema.CATEGORY_TYPES.EXPENSE.toString());
//                TransactionProcessor.show(getSupportFragmentManager(), bundle, this);
//                break;
//            case R.id.ac_home_tv_add_income:
//                bundle = new Bundle();
//                bundle.putString(TransactionProcessor.KEY_CAT_TYPE, CategorySchema.CATEGORY_TYPES.INCOME.toString());
//                TransactionProcessor.show(getSupportFragmentManager(), bundle, this);
//                break;
//        }
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.menu_home_drawer_daily:
                new TransactionBatchLoader(TransactionBatchLoader.FREQUENCY_DAILY).start();
                lastSelectedFrequency = TransactionBatchLoader.FREQUENCY_DAILY;
                break;
            case R.id.menu_home_drawer_weekly:
                new TransactionBatchLoader(TransactionBatchLoader.FREQUENCY_WEEKLY).start();
                lastSelectedFrequency = TransactionBatchLoader.FREQUENCY_WEEKLY;
                break;
            case R.id.menu_home_drawer_monthly:
                new TransactionBatchLoader(TransactionBatchLoader.FREQUENCY_MONTHLY).start();
                lastSelectedFrequency = TransactionBatchLoader.FREQUENCY_MONTHLY;
                break;

            case R.id.menu_home_drawer_categories:
                break;


        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onProcessComplete() {
        new TransactionBatchLoader(lastSelectedFrequency).start();
    }



    private class TransactionBatchLoader extends Thread {

        static final String FREQUENCY_DAILY = "frequency_daily";
        static final String FREQUENCY_WEEKLY = "frequency_weekly";
        static final String FREQUENCY_MONTHLY = "frequency_monthly";

        private String frequency;

        TransactionBatchLoader(String frequency) {
            this.frequency = frequency;
        }

        @Override
        public void run() {
            switch (frequency) {
                case FREQUENCY_DAILY:
                    mDataSet = Transaction.getAllDaysWithTransaction(mDBManager.getReadableDatabase(), true);
                    break;
                case FREQUENCY_WEEKLY:
                    mDataSet = Transaction.getAllWeeksWithTransaction(mDBManager.getReadableDatabase(), true);
                    break;
                case FREQUENCY_MONTHLY:
                    mDataSet = Transaction.getAllMonthWithTransaction(mDBManager.getReadableDatabase(), true);
                    break;

            }
            mHandler.obtainMessage(H_KEY_UPDATE_DS).sendToTarget();
        }
    }

    private void printLog(String log) {
        SharedFunctions.printLog(log);
    }


}
