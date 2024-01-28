package dummydata.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dummydata.android.R;
import dummydata.android.model.HomeFragItemModel;

import java.util.ArrayList;

public class HomeFragItemAdapter extends RecyclerView.Adapter<HomeFragItemAdapter.HomeFragItemViewHolder> {

    Context context;
    ArrayList<HomeFragItemModel> homeFragItemList;

    public HomeFragItemAdapter(Context context, ArrayList<HomeFragItemModel> homeFragItemList){
        this.context = context;
        this.homeFragItemList = homeFragItemList;
    }

    @NonNull
    @Override
    public HomeFragItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_rv_notice_home_frag, null);
        return new HomeFragItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeFragItemViewHolder holder, int position) {
        HomeFragItemModel model = homeFragItemList.get(position);

        holder.textView.setText(model.getItemName());
        holder.imageView.setImageResource(model.getItemImage());

    }

    @Override
    public int getItemCount() {
        return homeFragItemList.size();
    }

    public class HomeFragItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public HomeFragItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
