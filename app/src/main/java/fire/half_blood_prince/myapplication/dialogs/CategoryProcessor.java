package fire.half_blood_prince.myapplication.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import fire.half_blood_prince.myapplication.R;
import fire.half_blood_prince.myapplication.database.DatabaseManager;
import fire.half_blood_prince.myapplication.model.Category;

/**
 * Created by Half-Blood-Prince on 1/12/2017.
 */

public class CategoryProcessor extends BottomSheetDialogFragment {

    private static final String TAG = "CategoryProcessor";

    public static void show(FragmentManager fManager, Bundle bundle) {
        CategoryProcessor thisObj = new CategoryProcessor();
        thisObj.setArguments(bundle);
        thisObj.show(fManager, TAG);
    }

    private DatabaseManager mDBManger ;
    private Activity mActivity;

    private Spinner spCatTypes;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mDBManger = new DatabaseManager(mActivity);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {

        View view = LayoutInflater.from(mActivity).
                inflate(R.layout.layout_dialog_cat_processor,((ViewGroup) mActivity.findViewById(android.R.id.content)),false);

        dialog.setContentView(view);

        findingViews(view);

        super.setupDialog(dialog, style);
    }

    private void findingViews(View view){
        spCatTypes = (Spinner) view.findViewById(R.id.la_df_cp_sp_ct);
        String[] s = Category.getCategoriesTypes();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_list_item_1,s);
        spCatTypes.setAdapter(arrayAdapter);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mDBManger !=null) mDBManger.close();
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
