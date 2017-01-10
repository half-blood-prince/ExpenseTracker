package fire.half_blood_prince.myapplication;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import fire.half_blood_prince.myapplication.adapters.LoginVPAdapter;

public class Login extends AppCompatActivity implements View.OnClickListener {

    //    [START Views used in the layout]
    private ViewPager mViewPager;
    private TextView tvMessage, tvActionBtn;
//    [END Views used in the layout]

    private LoginVPAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findingViews();

        mAdapter = new LoginVPAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);


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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_login_tv_msg:
                break;
            case R.id.ac_login_tv_action_btn:
                break;

        }
    }
}
