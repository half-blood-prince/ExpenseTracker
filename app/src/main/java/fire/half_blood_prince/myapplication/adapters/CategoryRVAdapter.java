package fire.half_blood_prince.myapplication.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fire.half_blood_prince.myapplication.R;
import fire.half_blood_prince.myapplication.model.Category;

/**
 * Created by Half-Blood-Prince on 1/14/2017.
 */

public class CategoryRVAdapter extends RecyclerView.Adapter<CategoryRVAdapter.ViewHolder> {

    private ArrayList<Category> mDataSet;

    private LayoutInflater mInflater;

    public interface Communicator {
        void onCategoryClick(Category category, View view);
    }

    private Communicator mComm;

    public CategoryRVAdapter(Context context, ArrayList<Category> mDataSet, Communicator mComm) {
        this.mDataSet = mDataSet;
        this.mComm = mComm;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.layout_cat_view, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mComm.onCategoryClick(mDataSet.get(holder.getLayoutPosition()),v);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = mDataSet.get(position);
        holder.tvCatName.setText(category.getCatName());
        holder.tvCatType.setText(category.getCatType());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llRoot;
        TextView tvCatName, tvCatType;

        ViewHolder(View itemView) {
            super(itemView);
            llRoot = (LinearLayout) itemView.findViewById(R.id.layout_cat_view_tv_root);
            tvCatName = (TextView) itemView.findViewById(R.id.layout_cat_view_tv_cat_name);
            tvCatType = (TextView) itemView.findViewById(R.id.layout_cat_view_tv_cat_type);
        }
    }


    public void setDataSet(ArrayList<Category> newDataSet) {
        this.mDataSet = newDataSet;
        notifyDataSetChanged();
    }

}
