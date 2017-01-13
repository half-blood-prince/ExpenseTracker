package fire.half_blood_prince.myapplication.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import fire.half_blood_prince.myapplication.R;
import fire.half_blood_prince.myapplication.model.Category;
import fire.half_blood_prince.myapplication.model.Transaction;

/**
 * Created by Half-Blood-Prince on 1/11/2017.
 */

public class TransactionExpListAdapter extends BaseExpandableListAdapter {


    private ArrayList<Category> catList; //header
    private HashMap<Integer, ArrayList<Transaction>> transMap; //child

    private LayoutInflater mInflater;

    // Money symnbol used to show different symbol according to user location
    private String moneySymbol = "₹"; //Default value indian Rupees

    public interface TransactionAudience {
        void onTransactionFocused(Transaction transaction, View v);
    }

    private TransactionAudience mAudience;

    public TransactionExpListAdapter(Context context,
                                     ArrayList<Category> categoryArrayList, HashMap<Integer,
            ArrayList<Transaction>> transMap, TransactionAudience mAudience) {
        this.catList = categoryArrayList;
        this.transMap = transMap;

        this.mAudience =  mAudience;

        mInflater = LayoutInflater.from(context);

        moneySymbol = context.getString(R.string.money_symbol);
    }

    public void setDataSet(ArrayList<Category> categoryArrayList, HashMap<Integer, ArrayList<Transaction>> transMap) {
        this.catList = categoryArrayList;
        this.transMap = transMap;
        notifyDataSetChanged();
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

        GroupHolder mHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_cat_group, parent, false);
            mHolder = new GroupHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (GroupHolder) convertView.getTag();
        }
        Category categoryInfo = (Category) getGroup(groupPosition);
        mHolder.tvCatName.setText(categoryInfo.getCatName());
        mHolder.tvCatTotalAmount.setText(String.format(Locale.getDefault(), "%s %s", moneySymbol, categoryInfo.getTotalAmount()));

        return convertView;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildHolder mHolder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.layout_trans_child, parent, false);
            mHolder = new ChildHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ChildHolder) convertView.getTag();
        }

        final Transaction transaction = (Transaction) getChild(groupPosition, childPosition);
        mHolder.tvTitle.setText(transaction.getTitle());
        mHolder.tvAmount.setText(String.format(Locale.getDefault(), "%s %s", moneySymbol, transaction.getAmount()));
        mHolder.tvNotes.setText(transaction.getNotes());
        mHolder.tvDate.setText(transaction.getDate());

        mHolder.rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudience.onTransactionFocused(transaction, v);
            }
        });


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * View Holder class for Group View
     */
    private static class GroupHolder {
        TextView tvCatName, tvCatTotalAmount;

        GroupHolder(View view) {
            tvCatName = (TextView) view.findViewById(R.id.la_cg_tv_header);
            tvCatTotalAmount = (TextView) view.findViewById(R.id.la_cg_tv_total_amount);
        }
    }

    /**
     * View Holder class for Child View
     */
    private static class ChildHolder {
        RelativeLayout rlRoot;
        TextView tvTitle, tvAmount, tvNotes, tvDate;

        ChildHolder(View view) {
            rlRoot = (RelativeLayout) view.findViewById(R.id.la_tc_rl_root);
            tvTitle = (TextView) view.findViewById(R.id.la_tc_tv_title);
            tvAmount = (TextView) view.findViewById(R.id.la_tc_tv_amount);
            tvNotes = (TextView) view.findViewById(R.id.la_tc_tv_notes);
            tvDate = (TextView) view.findViewById(R.id.la_tc_tv_date);
        }
    }

}
