package fire.half_blood_prince.myapplication.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import fire.half_blood_prince.myapplication.R;
import fire.half_blood_prince.myapplication.adapters.CategoryRVAdapter;
import fire.half_blood_prince.myapplication.database.DatabaseManager;
import fire.half_blood_prince.myapplication.dialogs.CategoryProcessor;
import fire.half_blood_prince.myapplication.dialogs.ProcessorPipeline;
import fire.half_blood_prince.myapplication.model.Category;
import fire.half_blood_prince.myapplication.utility.SharedConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryViewer extends AppCompatDialogFragment implements View.OnClickListener, SharedConstants, CategoryRVAdapter.Communicator {

    private static final String TAG = "CategoryViewer";

    private Activity mActivity;

    private CategoryRVAdapter mAdapter;

    private ArrayList<Category> mDataSet = new ArrayList<>();

    private Handler mHandler;

    private DatabaseManager mDBManager;

    private TextView tvNoCategories;

    public static void show(FragmentManager fManager) {
        CategoryViewer thisObj = new CategoryViewer();
        thisObj.show(fManager, TAG);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mHandler = getHandler();
        mDBManager = new DatabaseManager(mActivity);

        AppCompatDialog dialog = new AppCompatDialog(mActivity, R.style.AppTheme);

        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_category_viewer, ((ViewGroup) mActivity.findViewById(android.R.id.content)), false);

        dialog.setContentView(view);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.frag_cat_viewer_rv);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, getResources().getInteger(R.integer.cat_col_span)));

        mAdapter = new CategoryRVAdapter(mActivity, mDataSet, this);

        mRecyclerView.setAdapter(mAdapter);

        tvNoCategories = (TextView) view.findViewById(R.id.frag_cat_viewer_tv_no_cat);

        FloatingActionButton fabAddCat = (FloatingActionButton) view.findViewById(R.id.frag_cat_viewer_fab);
        fabAddCat.setOnClickListener(this);
        view.findViewById(R.id.frag_cat_viewer_img_close).setOnClickListener(this);

        new CategoryLoader().start();

        return dialog;
    }


    private Handler getHandler() {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case H_KEY_UPDATE_DS:

                        tvNoCategories.setVisibility(mDataSet.isEmpty() ? View.VISIBLE : View.GONE);
                        mAdapter.setDataSet(mDataSet);

                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void onCategoryClick(Category category, View view) {
        updateCategory(category.getId());
    }


    private class CategoryLoader extends Thread {

        @Override
        public void run() {

            mDataSet = Category.getAll(mDBManager.getReadableDatabase(), true);

            mHandler.obtainMessage(H_KEY_UPDATE_DS).sendToTarget();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frag_cat_viewer_img_close:
                dismiss();
                break;

            case R.id.frag_cat_viewer_fab:
                addCategory();
                break;

        }
    }

    private void updateCategory(int id) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_MODE, MODE_EDIT);
        bundle.putInt(KEY_ID, id);
        CategoryProcessor.show(getFragmentManager(), bundle, new ProcessorPipeline() {
            @Override
            public void onProcessComplete() {
                new CategoryLoader().start();
                ((ProcessorPipeline) mActivity).onProcessComplete();
            }
        });
    }

    private void addCategory() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_MODE, MODE_INSERT);
        CategoryProcessor.show(getFragmentManager(), bundle, new ProcessorPipeline() {
            @Override
            public void onProcessComplete() {
                new CategoryLoader().start();
            }
        });
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
//        mDBManager.close();
        super.onDismiss(dialog);
    }
}
