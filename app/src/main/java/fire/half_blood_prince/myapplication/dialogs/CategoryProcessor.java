package fire.half_blood_prince.myapplication.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import fire.half_blood_prince.myapplication.R;
import fire.half_blood_prince.myapplication.database.DatabaseManager;
import fire.half_blood_prince.myapplication.model.Category;
import fire.half_blood_prince.myapplication.utility.SharedConstants;
import fire.half_blood_prince.myapplication.utility.SharedFunctions;
import fire.half_blood_prince.myapplication.utility.Validation;

/**
 * Created by Half-Blood-Prince on 1/12/2017.
 * CategoryProcessor will take care of inserting , editing , updating and deleting the category
 */

public class CategoryProcessor extends BottomSheetDialogFragment implements View.OnClickListener, SharedConstants {

    private static final String TAG = "CategoryProcessor";


    private DatabaseManager mDBManger;
    private Activity mActivity;
    private ProcessorPipeline mPipeline;

    private ImageView imgClose, imgSave, imgEdit, imgDelete;
    private TextView tvToolbarTitle;
    private Spinner spCatTypes;

    private TextInputLayout tilCategory;
    private TextInputEditText tieCategory;

    private String mode;


    public static void show(FragmentManager fManager, Bundle bundle, ProcessorPipeline mPipeline) {
        CategoryProcessor thisObj = new CategoryProcessor();
        thisObj.setArguments(bundle);
        thisObj.mPipeline = mPipeline;
        thisObj.show(fManager, TAG);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mDBManger = new DatabaseManager(mActivity);
    }

    private int id = -1;

    @Override
    public void setupDialog(Dialog dialog, int style) {

        Bundle arguments = getArguments();
        mode = arguments.getString(KEY_MODE, MODE_INSERT);

        if (mode.equals(MODE_EDIT)) id = arguments.getInt(KEY_ID);

        View view = LayoutInflater.from(mActivity).
                inflate(R.layout.layout_dialog_cat_processor, ((ViewGroup) mActivity.findViewById(android.R.id.content)), false);

        dialog.setContentView(view);

        findingViews(view);
        populatingCategoriesTypes();
        settingListeners();

        if (mode.equals(MODE_EDIT)) {

            tvToolbarTitle.setText(getString(R.string.edit_category));

            changeActionVisibility(0, 1, 1);
            disableAllFields();

            SQLiteDatabase database = mDBManger.getReadableDatabase();
            Category category = Category.get(database, id, true);
            if (null != category) {
                setFields(category);
            } else {
                showToast(getString(R.string.problem_open_catergory));
                dismiss();
            }

        } else {
            tvToolbarTitle.setText(getString(R.string.add_category));
            changeActionVisibility(1, 0, 0);
        }


        super.setupDialog(dialog, style);
    }


    private void findingViews(View view) {

        imgClose = (ImageView) view.findViewById(R.id.la_df_cp_img_close);
        imgSave = (ImageView) view.findViewById(R.id.la_df_cp_img_save);
        imgEdit = (ImageView) view.findViewById(R.id.la_df_cp_img_edit);
        imgDelete = (ImageView) view.findViewById(R.id.la_df_cp_img_delete);
        tvToolbarTitle = (TextView) view.findViewById(R.id.la_df_cp_tv_toolbar_title);

        spCatTypes = (Spinner) view.findViewById(R.id.la_df_cp_sp_ct);
        tilCategory = (TextInputLayout) view.findViewById(R.id.la_df_cp_til_category);
        tieCategory = (TextInputEditText) view.findViewById(R.id.la_df_cp_tie_category);
    }

    private void populatingCategoriesTypes() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mActivity, R.layout.layout_custom_list_item, Category.getCategoriesTypes());
        spCatTypes.setAdapter(arrayAdapter);
    }

    private void settingListeners() {
        imgClose.setOnClickListener(this);
        imgSave.setOnClickListener(this);
        imgEdit.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
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
        tieCategory.setEnabled(state);
        spCatTypes.setEnabled(state);
    }

    private void setFields(Category category) {
        tieCategory.setText(category.getCatName());
        spCatTypes.setSelection(Category.CATEGORY_TYPES.valueOf(category.getCatType()).ordinal());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.la_df_cp_img_close:
                dismiss();
                break;
            case R.id.la_df_cp_img_save:
                switch (mode) {
                    case MODE_INSERT:
                        saveCategory();
                        break;
                    case MODE_EDIT:
                        updateCategory();
                        break;

                }
                break;
            case R.id.la_df_cp_img_edit:
                changeActionVisibility(1, 0, 0);
                enableAllFields();
                break;
            case R.id.la_df_cp_img_delete:

                SharedFunctions.showAlertDialog(mActivity, null, getString(R.string.cat_delete_confirm), getString(R.string.delete), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_POSITIVE) {
                            if (deleteCategory()) {
                                showToast(getString(R.string.cat_deleted));
                                mPipeline.onProcessComplete();
                                ((ProcessorPipeline) mActivity).onProcessComplete(); // to load View Pager again
                                dismiss();
                            } else showToast(getString(R.string.prob_deleting_cat));
                        }

                        dialog.dismiss();
                    }
                });


                break;

        }
    }

    private boolean deleteCategory() {
        Category category = validateAndConstruct();
        return null != category && (category.delete(mDBManger.getWritableDatabase(), true) > 0);
    }

    private void updateCategory() {
        Category transaction = validateAndConstruct();
        if (null != transaction) {
            if (transaction.update(mDBManger.getWritableDatabase(), true) > 0) {
                showToast("Category updated");
                mPipeline.onProcessComplete();
            } else showToast("Problem occured while updating category");
            dismiss();
        }
    }

    private Category validateAndConstruct() {
        Category category = null;

        String catType = spCatTypes.getSelectedItem().toString();
        String catName = tieCategory.getText().toString().trim();

        if (!Validation.isEmpty(catName, getString(R.string.cat_req), tilCategory)) {
            category = new Category();
            category.setCatType(catType);
            category.setCatName(catName);

            category.setId(id); // only used when deleting and updating
        }
        return category;
    }

    private void saveCategory() {

        Category category = validateAndConstruct();
        if (null != category) {
            if (category.save(mDBManger.getWritableDatabase(), true) != -1) {
                showToast(getString(R.string.cat_inserted));
                mPipeline.onProcessComplete();
            } else
                showToast(getString(R.string.problem_ins_cat));

            dismiss();
        }
    }

    private void showToast(String toast) {
        Toast.makeText(mActivity, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
//        if (mDBManger != null) mDBManger.close();
        super.onDismiss(dialog);
    }


}
