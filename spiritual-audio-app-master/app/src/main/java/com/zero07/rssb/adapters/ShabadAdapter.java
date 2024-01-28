package dummydata.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dummydata.CustomMediaPlayer;
import dummydata.R;
import dummydata.models.ShabadModel;

import java.util.ArrayList;

public class ShabadAdapter extends RecyclerView.Adapter<ShabadAdapter.ShabadViewHolder> {

    private Context context;
    private ArrayList<ShabadModel> shabadModels;
    private CustomMediaPlayer customMediaPlayer;

    public ShabadAdapter(Context context, ArrayList<ShabadModel> shabadModels, CustomMediaPlayer customMediaPlayer) {
        this.context = context;
        this.shabadModels = shabadModels;
        this.customMediaPlayer = customMediaPlayer;
    }

    @NonNull
    @Override
    public ShabadAdapter.ShabadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_rv_shabad_activity, null);
        return new ShabadAdapter.ShabadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShabadAdapter.ShabadViewHolder holder, int position) {

        ShabadModel model = shabadModels.get(position);

        holder.shabadName.setText(model.getShabadName());
        holder.shabadSubName.setText(model.getShabadSubName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Please wait. Loading....", Toast.LENGTH_LONG).show();
                customMediaPlayer.initSong(v.getContext(), Uri.parse(shabadModels.get(position).getShabadUrl()), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shabadModels.size();
    }

    public class ShabadViewHolder extends RecyclerView.ViewHolder {
        TextView shabadName;
        TextView shabadSubName;

        public ShabadViewHolder(@NonNull View itemView) {
            super(itemView);
            shabadName = itemView.findViewById(R.id.shabad_name);
            shabadSubName = itemView.findViewById(R.id.shabad_sub_name);
        }
    }
}