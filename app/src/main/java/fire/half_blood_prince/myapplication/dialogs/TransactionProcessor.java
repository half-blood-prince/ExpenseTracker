package fire.half_blood_prince.myapplication.dialogs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
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
import fire.half_blood_prince.myapplication.utility.SharedFunctions;
import fire.half_blood_prince.myapplication.utility.Validation;

/**
 * Created by Half-Blood-Prince on 1/12/2017.
 */

public class TransactionProcessor extends AppCompatDialogFragment implements View.OnClickListener {

    private static final String TAG = "TransactionProcessor";
    public static final String KEY_CAT_TYPE = "cat_type";
    public static final String KEY_ID = "id";

    private Activity mActivity;

    private ImageView imgClose;
    private TextView tvToolbarTitle, tvAction;
    private TextInputLayout tilTitle, tilAmount, tilDate, tilCategory;
    private TextInputEditText tieTitle, tieAmount, tieNotes, tieDate, tieCategory;
    private ImageView imgAddCategory;

    private DatabaseManager mDatabaseManager;

    private ArrayList<String> categories;

    private CategorySchema.CATEGORY_TYPES catType;

    public static void show(FragmentManager fManager, Bundle args) {

        TransactionProcessor thisObj = new TransactionProcessor();
        thisObj.setArguments(args);
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

        mDatabaseManager = new DatabaseManager(mActivity);

        Bundle arguments = getArguments();

        catType = CategorySchema.CATEGORY_TYPES.valueOf(
                arguments.getString(KEY_CAT_TYPE, CategorySchema.CATEGORY_TYPES.EXPENSE.toString()
                ));

        categories = Category.getCatNames(mDatabaseManager.getReadableDatabase(), catType, true);

        AppCompatDialog dialog = new AppCompatDialog(mActivity, R.style.AppTheme);
        View dialogView = LayoutInflater.from(mActivity).
                inflate(R.layout.layout_trans_processor, ((ViewGroup) mActivity.findViewById(android.R.id.content)), false);

        dialog.setContentView(dialogView);

        findingViews(dialogView);
        settingListeners();

        return dialog;
    }


    private void findingViews(View view) {

        imgClose = (ImageView) view.findViewById(R.id.la_df_tp_img_close);
        tvToolbarTitle = (TextView) view.findViewById(R.id.la_df_tp_tv_toolbar_title);
        tvAction = (TextView) view.findViewById(R.id.la_df_tp_tv_action);

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
        tvAction.setOnClickListener(this);

        tieDate.setOnClickListener(this);
        tieCategory.setOnClickListener(this);
        imgAddCategory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.la_df_tp_img_close:
                dismiss();
                break;
            case R.id.la_df_tp_tv_action:
                saveTransaction();
                break;

            case R.id.la_df_tp_tie_date:
                SharedFunctions.hideKeypad(mActivity, v);
                setDate(tieDate);
                break;
            case R.id.la_df_tp_tie_category:
                SharedFunctions.hideKeypad(mActivity, v);
                setSingleChoiceItem(getString(R.string.choose_category), categories.toArray(new String[]{}), tieCategory);
                break;
            case R.id.la_df_tp_img_add_category:

                break;

        }
    }

    private void setDate(final TextInputEditText editText) {
        SharedFunctions.datePicker(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editText.setText(
                        String.format(Locale.getDefault(),
                                "%s-%s-%d", (dayOfMonth <= 9 ? "0" + dayOfMonth : String.valueOf(dayOfMonth)), ((month + 1) < 9 ? "0" + (month + 1) : (month + 1)), year)
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
        String[] catTypes = Category.getCategoriesTypes();




    }

    private void saveTransaction() {

        Transaction transaction = validateAndConstruct();
        if (null != transaction) {
            long insert = transaction.insert(mDatabaseManager.getReadableDatabase(), true);
            if (insert != 1) showToast(getString(R.string.trans_inserted));
            else showToast(getString(R.string.problem_inserting_trans));
            dismiss();
        }
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
                        transaction.setCid(Category.getId(mDatabaseManager.getReadableDatabase(), getStirngFromView(tieCategory), true));
                        return transaction;
                    }

        return null;

    }

    private <T extends TextView> String getStirngFromView(T view) {
        return SharedFunctions.getText(view);
    }

}
