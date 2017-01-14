package fire.half_blood_prince.myapplication.fragments;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import fire.half_blood_prince.myapplication.R;
import fire.half_blood_prince.myapplication.adapters.TransactionExpListAdapter;
import fire.half_blood_prince.myapplication.database.CategorySchema;
import fire.half_blood_prince.myapplication.database.DatabaseManager;
import fire.half_blood_prince.myapplication.database.TransactionSchema;
import fire.half_blood_prince.myapplication.dialogs.ProcessorPipeline;
import fire.half_blood_prince.myapplication.dialogs.TransactionProcessor;
import fire.half_blood_prince.myapplication.model.Category;
import fire.half_blood_prince.myapplication.model.Transaction;
import fire.half_blood_prince.myapplication.utility.SharedConstants;
import fire.half_blood_prince.myapplication.utility.SharedFunctions;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionViewer extends Fragment implements SharedConstants,
        TransactionExpListAdapter.TransactionAudience, View.OnClickListener, ProcessorPipeline {

    public static final String KEY_DATE = "DATE";

    private Activity mActivity;
    private Handler mHandler;

    private ExpandableListView mTransactionList;

    private ImageView imgAddExpense, imgAddIncome;

    private TextView tvBalance;

    private TextView tvNoTransDetails;

    private DatabaseManager mDBManager;

    private double income = 0, expense = 0, balance;

    private boolean showBalanceOnly = true;

    private TransactionExpListAdapter mAdapter;
    private HashMap<Integer, ArrayList<Transaction>> transactionMap = new HashMap<>();
    private ArrayList<Category> categoriesList = new ArrayList<>();

    private String moneySymbol = "₹"; //Default case


    public TransactionViewer() {
    }

    public static TransactionViewer newInstance(String date) {

        Bundle args = new Bundle();
        args.putString(KEY_DATE, date);
        TransactionViewer fragment = new TransactionViewer();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_transaction_viewer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String date = getArguments().getString(KEY_DATE);

        mDBManager = new DatabaseManager(mActivity);
        mHandler = getHandler();
        moneySymbol = getString(R.string.money_symbol);

        findingViews(view);
        settingListeners();
        mAdapter = new TransactionExpListAdapter(mActivity, categoriesList, transactionMap, this);
        mTransactionList.setAdapter(mAdapter);

        new TransactionLoader(getQuery(date)).start();

    }

    private String lastQuery;

    private String getQuery(String date) {

        int toIndex = date.indexOf("to");
        if (-1 != toIndex) {
            String startDate = date.substring(0, toIndex - 1);
            String endDate = date.substring((toIndex + 3));

            lastQuery = "SELECT * FROM "
                    + TransactionSchema.TABLE_TRANSACTION + " AS T "
                    + " INNER JOIN " + CategorySchema.TABLE_CATEGORY + " AS C"
                    + " ON " + "C." + CategorySchema.C_ID + " = " + "T." + TransactionSchema.CID
                    + " WHERE T." + TransactionSchema.DATE + " >= Date('" + startDate + "')"
                    + " AND T." + TransactionSchema.DATE + " <= Date('" + endDate + "')";

        } else {
            lastQuery = "SELECT * FROM "
                    + TransactionSchema.TABLE_TRANSACTION + " AS T "
                    + " INNER JOIN " + CategorySchema.TABLE_CATEGORY + " AS C"
                    + " ON " + "C." + CategorySchema.C_ID + " = " + "T." + TransactionSchema.CID
                    + " WHERE T." + TransactionSchema.DATE + " = Date('" + date + "')";

        }


        return lastQuery;
    }

    private void findingViews(View view) {
        mTransactionList = (ExpandableListView) view.findViewById(R.id.ac_ha_elv_transaction);
        tvNoTransDetails = (TextView) view.findViewById(R.id.ac_ha_tv_no_trans);
        imgAddExpense = (ImageView) view.findViewById(R.id.frag_trans_viewer_img_add_expense);
        imgAddIncome = (ImageView) view.findViewById(R.id.frag_trans_viewer_img_add_income);
        tvBalance = (TextView) view.findViewById(R.id.frag_trans_viewer_tv_balance);

    }

    private void settingListeners() {
        imgAddExpense.setOnClickListener(this);
        imgAddIncome.setOnClickListener(this);
        tvBalance.setOnClickListener(this);
    }


    private Handler getHandler() {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case H_KEY_UPDATE_DS:

                        tvNoTransDetails.setVisibility(categoriesList.isEmpty()?View.VISIBLE:View.GONE);
                        mAdapter.setDataSet(categoriesList, transactionMap);
                        updateBalanceInfo();

                        break;
                }
                super.handleMessage(msg);
            }
        };
    }



    private void updateBalanceInfo() {

        final String incomeType = CategorySchema.CATEGORY_TYPES.INCOME.toString();
        final String expenceType = CategorySchema.CATEGORY_TYPES.EXPENSE.toString();
        for (Category category : categoriesList) {
            if (incomeType.equals(category.getCatType())) {
                income += SharedFunctions.parseDouble(category.getTotalAmount());
            } else if (expenceType.equals(category.getCatType())) {
                expense += SharedFunctions.parseDouble(category.getTotalAmount());
            }
        }
        balance = income - expense;

        toggleBalanceView();
    }

    /**
     * This method called when transaction is clicked in expendable list view
     *
     * @param transaction Transaction object
     * @param v           view clicked
     */
    @Override
    public void onTransactionFocused(Transaction transaction, View v) {
        Bundle bundle = new Bundle();
        bundle.putString(TransactionProcessor.KEY_CAT_TYPE, null);
        bundle.putString(TransactionProcessor.KEY_MODE, MODE_EDIT);
        bundle.putLong(TransactionProcessor.KEY_ID, transaction.getId());
        TransactionProcessor.show(getFragmentManager(), bundle, (ProcessorPipeline) mActivity);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.frag_trans_viewer_img_add_expense:
                bundle = new Bundle();
                bundle.putString(TransactionProcessor.KEY_CAT_TYPE, CategorySchema.CATEGORY_TYPES.EXPENSE.toString());
                TransactionProcessor.show(getFragmentManager(), bundle, this);
                break;
            case R.id.frag_trans_viewer_img_add_income:
                bundle = new Bundle();
                bundle.putString(TransactionProcessor.KEY_CAT_TYPE, CategorySchema.CATEGORY_TYPES.INCOME.toString());
                TransactionProcessor.show(getFragmentManager(), bundle, this);
                break;
            case R.id.frag_trans_viewer_tv_balance:
                toggleBalanceView();
                break;
        }
    }



    private void toggleBalanceView() {
        if (showBalanceOnly) {
            tvBalance.setText(String.format(Locale.getDefault(), "Balance : %s %.2f", moneySymbol, balance));
        } else {

            tvBalance.setText(
                    String.format(Locale.getDefault(), "Income  : %4s %.2f\nExpence : %4s %.2f\nBalance : %4s %.2f", moneySymbol, income, moneySymbol, expense, moneySymbol, balance)
            );
        }

        showBalanceOnly = !showBalanceOnly;
    }


    @Override
    public void onProcessComplete() {
        new TransactionLoader(lastQuery).start();
        ((ProcessorPipeline) mActivity).onProcessComplete();
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
                    printLog("Trans " + transModel);
                    int id = transModel.getCid();

                    if (tMap.containsKey(id)) {
                        ArrayList<Transaction> transList = tMap.get(id);
                        transList.add(transModel);
                        tMap.remove(id);
                        tMap.put(id, transList); // tMap.replace requires java 1.8

                        Category category = cMap.get(id);
                        if (null != category) {

                            category.setTotalAmount(String.valueOf(SharedFunctions.parseDouble(category.getTotalAmount()) +
                                    SharedFunctions.parseDouble(transModel.getAmount())));
                            cMap.remove(id);
                            cMap.put(id, category);

                            DecimalFormat format = new DecimalFormat(".##");
                            category.setTotalAmount(format.format(SharedFunctions.parseDouble(category.getTotalAmount())));
                        }

                    } else {
                        ArrayList<Transaction> transList = new ArrayList<>();
                        transList.add(transModel);
                        tMap.put(id, transList);

                        Category category = new Category();
                        category.setId(id);
                        category.setCatName(cursor.getString(cursor.getColumnIndex(CategorySchema.CAT_NAME)));
                        category.setCatType(cursor.getString(cursor.getColumnIndex(CategorySchema.CAT_TYPE)));
                        category.setTotalAmount(String.valueOf(SharedFunctions.parseDouble(transModel.getAmount())));
                        cMap.put(id, category);
                    }


                } while (cursor.moveToNext());

                cursor.close();
            } else printLog("null/empty cursor");

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
}
