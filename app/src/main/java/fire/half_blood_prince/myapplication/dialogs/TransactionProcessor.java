package fire.half_blood_prince.myapplication.dialogs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import fire.half_blood_prince.myapplication.R;
import fire.half_blood_prince.myapplication.database.CategorySchema;
import fire.half_blood_prince.myapplication.database.DatabaseManager;
import fire.half_blood_prince.myapplication.model.Category;
import fire.half_blood_prince.myapplication.model.Transaction;
import fire.half_blood_prince.myapplication.utility.SharedConstants;
import fire.half_blood_prince.myapplication.utility.SharedFunctions;
import fire.half_blood_prince.myapplication.utility.Validation;

/**
 * Created by Half-Blood-Prince on 1/12/2017.
 * TransactionProcessor will take care of inserting , editing , updating and deleting the transaction
 */

public class TransactionProcessor extends AppCompatDialogFragment implements View.OnClickListener, SharedConstants {

    private static final String TAG = "TransactionProcessor";
    public static final String KEY_CAT_TYPE = "cat_type";

    private Activity mActivity;

    private ImageView imgClose, imgSave, imgEdit, imgDelete;
    private TextView tvToolbarTitle;
    private TextInputLayout tilTitle, tilAmount, tilDate, tilCategory;
    private TextInputEditText tieTitle, tieAmount, tieNotes, tieDate, tieCategory;
    private ImageView imgAddCategory;

    private DatabaseManager mDBManager;

    private ArrayList<String> categories;

    private CategorySchema.CATEGORY_TYPES catType;

    private ProcessorPipeline mPipeline;

    private String mode;
    private long id;

    public static void show(FragmentManager fManager, Bundle args, ProcessorPipeline mPipeline) {

        TransactionProcessor thisObj = new TransactionProcessor();
        thisObj.setArguments(args);
        thisObj.mPipeline = mPipeline;
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

        mDBManager = new DatabaseManager(mActivity);

        Bundle arguments = getArguments();
        mode = arguments.getString(KEY_MODE, MODE_INSERT);
        id = arguments.getLong(KEY_ID, -1);

        String strCatType = arguments.getString(KEY_CAT_TYPE, null);
        if (null != strCatType)
            catType = CategorySchema.CATEGORY_TYPES.valueOf(
                    strCatType);
        else catType = null;

        categories = Category.getCatNames(mDBManager.getReadableDatabase(), catType, true);

        AppCompatDialog dialog = new AppCompatDialog(mActivity, R.style.AppTheme);
        View dialogView = LayoutInflater.from(mActivity).
                inflate(R.layout.layout_trans_processor, ((ViewGroup) mActivity.findViewById(android.R.id.content)), false);

        dialog.setContentView(dialogView);

        findingViews(dialogView);
        settingListeners();

        if (mode.equals(MODE_EDIT)) {

            tvToolbarTitle.setText(getString(R.string.edit_trans));
            changeActionVisibility(0, 1, 1);
            disableAllFields();

            SQLiteDatabase database = mDBManager.getReadableDatabase();
            Transaction transaction = Transaction.get(database, id, false);
            if (null != transaction) {
                catType = transaction.getCatType(database, true);
                setFields(transaction);
            } else {
                showToast(getString(R.string.problem_open_trans));
                dismiss();
            }

        } else {
            tvToolbarTitle.setText(getString(R.string.add_trans));
            changeActionVisibility(1, 0, 0);
        }

        return dialog;
    }

    private void changeActionVisibility(int a, int b, int c) {

        imgSave.setVisibility(a == 1 ? View.VISIBLE : View.GONE);
        imgEdit.setVisibility(b == 1 ? View.VISIBLE : View.GONE);
        imgDelete.setVisibility(c == 1 ? View.VISIBLE : View.GONE);

    }

    private void enableAllFields() {
        changeFieldState(true);
    }

    private void disableAllFields() {
        changeFieldState(false);
    }

    private void changeFieldState(boolean state) {
        tieTitle.setEnabled(state);
        tieAmount.setEnabled(state);
        tieNotes.setEnabled(state);
        tieDate.setEnabled(state);
        tieCategory.setEnabled(state);
        imgAddCategory.setEnabled(state);
    }

    private void findingViews(View view) {

        imgClose = (ImageView) view.findViewById(R.id.frag_cat_viewer_img_close);
        imgSave = (ImageView) view.findViewById(R.id.la_df_tp_img_save);
        imgEdit = (ImageView) view.findViewById(R.id.la_df_tp_img_edit);
        imgDelete = (ImageView) view.findViewById(R.id.la_df_tp_img_delete);
        tvToolbarTitle = (TextView) view.findViewById(R.id.frag_cat_viewer_tv_toolbar_title);

        tilTitle = (TextInputLayout) view.findViewById(R.id.la_df_tp_til_title);
        tilAmount = (TextInputLayout) view.findViewById(R.id.la_df_tp_til_amount);
        tilDate = (TextInputLayout) view.findViewById(R.id.la_df_tp_til_date);
        tilCategory = (TextInputLayout) view.findViewById(R.id.la_df_tp_til_category);

        tieTitle = (TextInputEditText) view.findViewById(R.id.la_df_tp_tie_title);
        tieAmount = (TextInputEditText) view.findViewById(R.id.la_df_tp_tie_amount);
        tieNotes = (TextInputEditText) view.findViewById(R.id.la_df_tp_tie_notes);
        tieDate = (TextInputEditText) view.findViewById(R.id.la_df_tp_tie_date);
        tieCategory = (TextInputEditText) view.findViewById(R.id.la_df_tp_tie_category);

        imgAddCategory = (ImageView) view.findViewById(R.id.la_df_tp_img_add_category);
    }

    private void settingListeners() {
        imgClose.setOnClickListener(this);
        imgSave.setOnClickListener(this);
        imgEdit.setOnClickListener(this);
        imgDelete.setOnClickListener(this);

        tieDate.setOnClickListener(this);
        tieCategory.setOnClickListener(this);
        imgAddCategory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        SharedFunctions.hideKeypad(mActivity, v);
        switch (v.getId()) {
            case R.id.frag_cat_viewer_img_close:
                dismiss();
                break;
            case R.id.la_df_tp_img_save:
                switch (mode) {
                    case MODE_INSERT:
                        saveTransaction();
                        break;
                    case MODE_EDIT:
                        updateTransaction();
                        break;

                }
                break;
            case R.id.la_df_tp_img_edit:
                changeActionVisibility(1, 0, 0);
                enableAllFields();
                break;
            case R.id.la_df_tp_img_delete:

                SharedFunctions.showAlertDialog(mActivity, null, "Are you sure want to delete this transaction", "Delete", "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_POSITIVE) {
                            if (deleteTransaction()) {
                                showToast(getString(R.string.trans_deleted));
                                mPipeline.onProcessComplete();
                                dismiss();
                            } else showToast(getString(R.string.prob_deleting_trans));
                        }

                        dialog.dismiss();
                    }
                });


                break;


            case R.id.la_df_tp_tie_date:
                setDate(tieDate);
                break;
            case R.id.la_df_tp_tie_category:
                setSingleChoiceItem(getString(R.string.choose_category), categories.toArray(new String[]{}), tieCategory);
                break;
            case R.id.la_df_tp_img_add_category:
                addCategory();
                break;

        }
    }

    private void setFields(Transaction transaction) {
        tieTitle.setText(transaction.getTitle());
        tieAmount.setText(transaction.getAmount());
        tieNotes.setText(transaction.getNotes());
        tieDate.setText(transaction.getDate());

        Category category = Category.get(mDBManager.getReadableDatabase(), transaction.getCid(), true);
        tieCategory.setText(category != null ? category.getCatName() : "");
    }

    private void setDate(final TextInputEditText editText) {
        SharedFunctions.datePicker(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editText.setText(
                        String.format(Locale.getDefault(),
                                "%d-%s-%s", year,
                                ((month + 1) < 9 ? "0" + (month + 1) : (month + 1)),
                                (dayOfMonth <= 9 ? "0" + dayOfMonth : String.valueOf(dayOfMonth))
                        )
                );
            }
        });
    }

    private <T extends TextView> void setSingleChoiceItem(String title, final String[] mDataSet, final T view) {
        SharedFunctions.showSingleChoiceDialog(mActivity, title, mDataSet, view.getText().toString(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                view.setText(mDataSet[which]);
                dialog.dismiss();
            }
        });
    }

    private void showToast(String toast) {
        Toast.makeText(mActivity, toast, Toast.LENGTH_SHORT).show();
    }

    private void addCategory() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_MODE, MODE_INSERT);
        CategoryProcessor.show(getFragmentManager(), bundle, new ProcessorPipeline() {
            @Override
            public void onProcessComplete() {
                categories = Category.getCatNames(mDBManager.getReadableDatabase(), catType, true);
            }
        });
    }

    private void saveTransaction() {

        Transaction transaction = validateAndConstruct();
        if (null != transaction) {
            long insert = transaction.save(mDBManager.getWritableDatabase(), true);
            if (insert != -1) {
                showToast(getString(R.string.trans_inserted));
                mPipeline.onProcessComplete();
            } else showToast(getString(R.string.problem_inserting_trans));
            dismiss();
        }
    }

    private void updateTransaction() {
        Transaction transaction = validateAndConstruct();
        if (null != transaction) {
            if (transaction.update(mDBManager.getWritableDatabase(), true) > 0) {
                showToast("Transaction updated");
                mPipeline.onProcessComplete();
            } else showToast("Problem occured while updating transaction");
            dismiss();
        }
    }

    private boolean deleteTransaction() {
        Transaction transaction = validateAndConstruct();
        return null != transaction && (transaction.delete(mDBManager.getWritableDatabase(), true) > 0);
    }

    private Transaction validateAndConstruct() {

        Transaction transaction = new Transaction();
        transaction.setTitle(getStirngFromView(tieTitle));
        transaction.setAmount(getStirngFromView(tieAmount));
        transaction.setNotes(getStirngFromView(tieNotes));
        transaction.setDate(getStirngFromView(tieDate));


        if (!Validation.isEmpty(getStirngFromView(tieTitle), getString(R.string.title_req), tilTitle))
            if (!Validation.isEmpty(getStirngFromView(tieAmount), getString(R.string.amount_req), tilAmount))
                if (!Validation.isEmpty(getStirngFromView(tieDate), getString(R.string.date_req), tilDate))
                    if (!Validation.isEmpty(getStirngFromView(tieCategory), getString(R.string.cat_req), tilCategory)) {

                        transaction.setCid(Category.getId(mDBManager.getReadableDatabase(), getStirngFromView(tieCategory), true));
                        transaction.setId(id); // only used while deleting and updating
                        return transaction;
                    }

        return null;

    }

    private <T extends TextView> String getStirngFromView(T view) {
        return SharedFunctions.getText(view);
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
//        if (mDBManager != null) mDBManager.close();
        super.onDismiss(dialog);
    }
}
