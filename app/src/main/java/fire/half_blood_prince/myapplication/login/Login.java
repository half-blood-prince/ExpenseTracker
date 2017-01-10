package fire.half_blood_prince.myapplication.login;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import fire.half_blood_prince.myapplication.R;
import fire.half_blood_prince.myapplication.adapters.LoginVPAdapter;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private TextView tvMessage, tvActionBtn;

    private LoginVPAdapter mAdapter;

    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;

    private static final String[] MSG = new String[]{"New to Expense Tracker", "Already has a account"};
    private static final String[] ACTION_BTN_TEXT = new String[]{"Register Now", "Sign in"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findingViews();
        settingListeners();

        updateBottomPanel(0);

        mAdapter = new LoginVPAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mPageChangeListener = getmPageChangeListener();


    }

    private ViewPager.SimpleOnPageChangeListener getmPageChangeListener() {
        return new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateBottomPanel(position);
            }
        };
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
     * method to find all the views used in this activity
     */
    private void findingViews() {
        mViewPager = (ViewPager) findViewById(R.id.ac_login_vp);
        tvMessage = (TextView) findViewById(R.id.ac_login_tv_msg);
        tvActionBtn = (TextView) findViewById(R.id.ac_login_tv_action_btn);
    }

    /**
     * Setting Listeners for the views
     */
    private void settingListeners() {
        tvMessage.setOnClickListener(this);
        tvActionBtn.setOnClickListener(this);
    }

    private void updateBottomPanel(int currentPos) {
        tvMessage.setText(MSG[currentPos]);
        tvActionBtn.setText(ACTION_BTN_TEXT[currentPos]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_login_tv_msg:
                break;
            case R.id.ac_login_tv_action_btn:
                showNextPage();
                break;

        }
    }

    private void showNextPage() {
        mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1) % LoginVPAdapter.SIZE);
    }
}
