package fire.half_blood_prince.myapplication.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
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
import fire.half_blood_prince.myapplication.utility.Validation;

/**
 * Created by Half-Blood-Prince on 1/12/2017.
 */

public class CategoryProcessor extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = "CategoryProcessor";


    private DatabaseManager mDBManger;
    private Activity mActivity;
    private ProcessorPipeline mPipeline;

    private ImageView imgClose;
    private TextView tvToolbarTitle;
    private TextView tvAction;
    private Spinner spCatTypes;

    private TextInputLayout tilCategory;
    private TextInputEditText tieCategory;

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

    @Override
    public void setupDialog(Dialog dialog, int style) {

        View view = LayoutInflater.from(mActivity).
                inflate(R.layout.layout_dialog_cat_processor, ((ViewGroup) mActivity.findViewById(android.R.id.content)), false);

        dialog.setContentView(view);

        findingViews(view);
        populatingCategoriesTypes();
        settingListeners();


        super.setupDialog(dialog, style);
    }

    private void findingViews(View view) {

        imgClose = (ImageView) view.findViewById(R.id.la_df_cp_img_close);
        tvToolbarTitle = (TextView) view.findViewById(R.id.la_df_cp_tv_toolbar_title);
        tvAction = (TextView) view.findViewById(R.id.la_df_cp_tv_action);

        spCatTypes = (Spinner) view.findViewById(R.id.la_df_cp_sp_ct);
        tilCategory = (TextInputLayout) view.findViewById(R.id.la_df_cp_til_category);
        tieCategory = (TextInputEditText) view.findViewById(R.id.la_df_cp_tie_category);
    }

    private void populatingCategoriesTypes() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mActivity, R.layout.layout_custom_list_item, Category.getCategoriesTypes());
        spCatTypes.setAdapter(arrayAdapter);
    }

    private void settingListeners() {
        imgClose.setOnClickListener(this);
        tvAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.la_df_cp_img_close:
                break;
            case R.id.la_df_cp_tv_action:
                saveCategory();
                break;
        }
    }

    private void saveCategory() {

        String catType = spCatTypes.getSelectedItem().toString();
        String catName = tieCategory.getText().toString().trim();

        if (!Validation.isEmpty(catName, getString(R.string.cat_req), tilCategory)) {

            Category category = new Category();
            category.setCatType(catType);
            category.setCatName(catName);
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
        if (mDBManger != null) mDBManger.close();
        super.onDismiss(dialog);
    }


    //
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        AppCompatDialog dialog = new AppCompatDialog(mActivity);
//
//        return dialog;
//
//    }
}
