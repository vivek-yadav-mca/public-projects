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
import dummydata.models.SatsangModel;

import java.util.ArrayList;

public class SatsangAdapter extends RecyclerView.Adapter<SatsangAdapter.SatsangViewHolder> {

    private Context context;
    private ArrayList<SatsangModel> satsangModels;
    private CustomMediaPlayer customMediaPlayer;

    public SatsangAdapter(Context context, ArrayList<SatsangModel> satsangModels, CustomMediaPlayer customMediaPlayer) {
        this.context = context;
        this.satsangModels = satsangModels;
        this.customMediaPlayer = customMediaPlayer;
    }

    @NonNull
    @Override
    public SatsangAdapter.SatsangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_rv_satsang_activity, null);
        return new SatsangAdapter.SatsangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SatsangAdapter.SatsangViewHolder holder, int position) {

        SatsangModel model = satsangModels.get(position);

        holder.satsangName.setText(model.getSatsangName());
        holder.satsangSubName.setText(model.getSatsangSubName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customMediaPlayer.initSong(v.getContext(), Uri.parse(satsangModels.get(position).getSatsangUrl()), position);
                Toast.makeText(context, "Please wait. Loading....", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return satsangModels.size();
    }

    public class SatsangViewHolder extends RecyclerView.ViewHolder {
        TextView satsangName;
        TextView satsangSubName;

        public SatsangViewHolder(@NonNull View itemView) {
            super(itemView);
            satsangName = itemView.findViewById(R.id.satsang_name);
            satsangSubName = itemView.findViewById(R.id.satsang_sub_name);
        }
    }
}