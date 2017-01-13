package fire.half_blood_prince.myapplication.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;

import fire.half_blood_prince.myapplication.fragments.TransactionViewer;

/**
 * Created by Half-Blood-Prince on 1/13/2017.
 * TransactionVPAdapter
 */

public class TransactionVPAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> mDataSet = new ArrayList<>();

    public TransactionVPAdapter(FragmentManager fm,  ArrayList<String> arrayList) {
        super(fm);
        this.mDataSet = arrayList;
    }

    @Override
    public Fragment getItem(int position) {
        return TransactionViewer.newInstance(mDataSet.get(position));
    }

    @Override
    public int getCount() {
        return mDataSet.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mDataSet.get(position);
    }

    public void setDataSet(ArrayList<String> arrayList){

        this.mDataSet = arrayList;

        this.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
