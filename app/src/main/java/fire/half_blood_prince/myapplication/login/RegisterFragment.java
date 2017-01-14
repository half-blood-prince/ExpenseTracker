package fire.half_blood_prince.myapplication.login;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import fire.half_blood_prince.myapplication.R;
import fire.half_blood_prince.myapplication.utility.SharedFunctions;
import fire.half_blood_prince.myapplication.utility.Validation;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout tilName, tilEmail, tilPassword;
    private TextInputEditText tipName, tipEmail, tipPassword;

    private Activity mActivity;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViewAndSetListeners(view);
    }

    private void findViewAndSetListeners(View view) {
        tipName = (TextInputEditText) view.findViewById(R.id.fm_reg_tip_name);
        tipEmail = (TextInputEditText) view.findViewById(R.id.fm_reg_tip_email);
        tipPassword = (TextInputEditText) view.findViewById(R.id.fm_reg_tip_password);

        tilName = (TextInputLayout) view.findViewById(R.id.fm_reg_til_name);
        tilEmail = (TextInputLayout) view.findViewById(R.id.fm_reg_til_email);
        tilPassword = (TextInputLayout) view.findViewById(R.id.fm_reg_til_password);

        //Register Button

        view.findViewById(R.id.fm_reg_btn_register).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fm_reg_btn_register:
                // validate and register
                if (SharedFunctions.isDataNetworkConnected(mActivity)) {
                    if (validateCredentials()) {
                        ((Login) mActivity).register(getText(tipName), getText(tipEmail), getText(tipPassword));
                    }
                } else {
                    showToast("You are not connected to the internet");
                }
                break;
        }
    }

    private boolean validateCredentials() {
        if (Validation.isNameValid(getText(tipName), tilName))
            if (Validation.isEmailValid(getText(tipEmail), tilEmail))
                if (Validation.isPasswordValid(getText(tipPassword), tilPassword, Login.MIN_PASSWORD_LENGTH, Login.MAX_PASSWORD_LENGTH))
                    return true;
        return false;
    }

    private String getText(TextInputEditText tipEdt) {
        return SharedFunctions.getText(tipEdt);
    }

    private void showToast(String toastMsg) {
        Toast.makeText(mActivity, toastMsg, Toast.LENGTH_SHORT).show();
    }
}
