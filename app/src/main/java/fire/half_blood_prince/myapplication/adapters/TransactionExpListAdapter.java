package fire.half_blood_prince.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import fire.half_blood_prince.myapplication.model.Category;
import fire.half_blood_prince.myapplication.model.Transaction;

/**
 * Created by Half-Blood-Prince on 1/11/2017.
 */

public class TransactionExpListAdapter extends BaseExpandableListAdapter {


    private ArrayList<Category> catList; //header
    private HashMap<Integer, ArrayList<Transaction>> transMap = new HashMap<>(); //child

    private LayoutInflater mInflater;

    public TransactionExpListAdapter(Context context, ArrayList<Category> categoryArrayList, HashMap<Integer, ArrayList<Transaction>> transMap) {
        this.catList = categoryArrayList;
        this.transMap = transMap;

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return catList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return transMap.get(catList.get(groupPosition).getId()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return catList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return transMap.get(catList.get(groupPosition).getId()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        ((TextView) convertView).setText(((Category) getGroup(groupPosition)).getCatName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        ((TextView) convertView).setText(((Transaction) getChild(groupPosition, childPosition)).getTitle());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
