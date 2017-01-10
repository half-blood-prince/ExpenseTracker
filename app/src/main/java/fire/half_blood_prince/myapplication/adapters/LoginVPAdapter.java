package fire.half_blood_prince.myapplication.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import fire.half_blood_prince.myapplication.login.LoginFragment;
import fire.half_blood_prince.myapplication.login.RegisterFragment;

/**
 * Created by Half-Blood-Prince on 1/10/2017.
 * Adapter used to provide login and register fragment when necessary
 */

public class LoginVPAdapter extends FragmentStatePagerAdapter {

    public static int SIZE = 2;

    public LoginVPAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LoginFragment.newInstance();
            case 1:
                return RegisterFragment.newInstance();
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return SIZE;
    }
}
