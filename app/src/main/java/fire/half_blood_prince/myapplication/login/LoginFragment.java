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
public class LoginFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText tipEmail, tipPassword;

    private Activity mActivity;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViewAndSetListeners(view);
    }

    private void findViewAndSetListeners(View view) {
        tipEmail = (TextInputEditText) view.findViewById(R.id.fm_login_tip_email);
        tipPassword = (TextInputEditText) view.findViewById(R.id.fm_login_tip_password);

        tilEmail = (TextInputLayout) view.findViewById(R.id.fm_login_til_email);
        tilPassword = (TextInputLayout) view.findViewById(R.id.fm_login_til_password);

        // Login Button
        view.findViewById(R.id.fm_login_btn_login).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fm_login_btn_login:
                //validate and login
                if (SharedFunctions.isDataNetworkConnected(mActivity)) {
                    if (validateCredentials()) {
                        ((Login)mActivity).login(getText(tipEmail),getText(tipPassword));
                    }
                } else {
                    showToast("You are not connected to the internet");
                }
                break;
        }
    }

    private boolean validateCredentials() {
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
