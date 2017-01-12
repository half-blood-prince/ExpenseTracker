package fire.half_blood_prince.myapplication.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;

/**
 * Created by Half-Blood-Prince on 1/12/2017.
 */

public class CategoryProcessor extends AppCompatDialogFragment {

    private static final String TAG = "CategoryProcessor";

    public static void show(FragmentManager fManager, Bundle bundle) {
        CategoryProcessor thisObj = new CategoryProcessor();
        thisObj.setArguments(bundle);
        thisObj.show(fManager, TAG);
    }

    private Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AppCompatDialog dialog = new AppCompatDialog(mActivity);



        return dialog;

    }
}
