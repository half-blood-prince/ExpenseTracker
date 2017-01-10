package fire.half_blood_prince.myapplication.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import fire.half_blood_prince.myapplication.HomeActivity;
import fire.half_blood_prince.myapplication.R;
import fire.half_blood_prince.myapplication.adapters.LoginVPAdapter;
import fire.half_blood_prince.myapplication.utility.SharedFunctions;

public class Login extends AppCompatActivity implements View.OnClickListener, LoginHandler {

    private ViewPager mViewPager;
    private TextView tvMessage, tvActionBtn;
    private FrameLayout mRootView;

    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;

    private static final String[] MSG = new String[]{"New to Expense Tracker", "Already has a account"};
    private static final String[] ACTION_BTN_TEXT = new String[]{"Register Now", "Sign in"};

    public static final int MIN_PASSWORD_LENGTH = 3;
    public static final int MAX_PASSWORD_LENGTH = 12;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findingViews();
        settingListeners();

        updateBottomPanel(0);

        LoginVPAdapter mAdapter = new LoginVPAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mPageChangeListener = getmPageChangeListener();

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewPager.removeOnPageChangeListener(mPageChangeListener);
    }

    /**
     * This method is used to change the textview according to the page selection
     *
     * @return a instance of the ViewPager.SimpleOnPageChangeListener
     */
    private ViewPager.SimpleOnPageChangeListener getmPageChangeListener() {
        return new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateBottomPanel(position);
            }
        };
    }

    /**
     * method to find all the views used in this activity
     */
    private void findingViews() {
        mRootView = (FrameLayout) findViewById(R.id.ac_login_fl_root);

        mViewPager = (ViewPager) findViewById(R.id.ac_login_vp);
        tvMessage = (TextView) findViewById(R.id.ac_login_tv_msg);
        tvActionBtn = (TextView) findViewById(R.id.ac_login_tv_action_btn);
    }

    /**
     * Setting Listeners for the views
     */
    private void settingListeners() {
        tvActionBtn.setOnClickListener(this);
    }

    private void updateBottomPanel(int currentPos) {
        tvMessage.setText(MSG[currentPos]);
        tvActionBtn.setText(ACTION_BTN_TEXT[currentPos]);
    }

    /**
     * @param v view that is clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_login_tv_action_btn:
                showNextPage();
                break;

        }
    }

    /**
     * Show the next page in circular motion
     */
    private void showNextPage() {
        mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1) % LoginVPAdapter.SIZE);
    }

    private ProgressDialog dialog = null;

    @Override
    public void launchProgressDialog(String title, String message) {

        SharedFunctions.hideKeypad(Login.this, mRootView);

        dialog = ProgressDialog.show(Login.this, title, message, true);
        dialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if (null != dialog && dialog.isShowing()) dialog.dismiss();
    }

    @Override
    public void login(String email, String password) {
        launchProgressDialog("Please wait", "Logging in");
        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(new SimpleLoginTask(SimpleLoginTask.LOGIN_TASK));
    }

    @Override
    public void register(String name, String email, String password) {
        launchProgressDialog("Please wait", "Registering in");
        mAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new SimpleLoginTask(SimpleLoginTask.REGISTER_TASK));

    }


    private class SimpleLoginTask implements OnCompleteListener<AuthResult> {

        static final String LOGIN_TASK = "Login";
        static final String REGISTER_TASK = "Register";

        private String completedTask;

        SimpleLoginTask(String task) {
            this.completedTask = task;
        }

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

            Login.this.dismissProgressDialog();
            if (task.isSuccessful()) {
                showToast(String.format(Locale.getDefault(), "%s Success", completedTask));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(Login.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                }, 250);
            } else {
                showLongToast(String.format(Locale.getDefault(), "%s Failure %s", completedTask, task.getException().getMessage()));
            }

        }

    }

    /**
     * @param msg toast to show
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param msg toast to show
     */
    private void showLongToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void printLog(String log) {
        SharedFunctions.printLog(log);
    }
}
