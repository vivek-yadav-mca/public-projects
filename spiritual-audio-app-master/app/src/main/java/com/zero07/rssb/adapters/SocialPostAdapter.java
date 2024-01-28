package dummydata.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import dummydata.R;
import dummydata.activity.MainActivity;
import dummydata.models.SocialPostModel;
import dummydata.userModels.UserContext;

import java.util.List;

public class SocialPostAdapter extends RecyclerView.Adapter<SocialPostAdapter.SocialPostViewHolder> {

    Context context;
    List<SocialPostModel> socialPostsList;

    public SocialPostAdapter(Context context, List<SocialPostModel> socialPostsList) {
        this.context = context;
        this.socialPostsList = socialPostsList;
    }

    @Override
    public SocialPostAdapter.SocialPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_rv_social_post, null);
        return new SocialPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialPostAdapter.SocialPostViewHolder holder, int position) {
        SocialPostModel model = socialPostsList.get(position);

        RequestOptions loadProfileImage = new RequestOptions()
                .centerCrop()
                .circleCrop()  //to crop image in circle view
                .placeholder(R.drawable.user_profile)
                .error(R.drawable.user_profile);

        Glide.with(context)
                .load(model.getPost_user_photo())
                .apply(loadProfileImage)
                .into(holder.post_user_photo);

        holder.post_user_name.setText(UserContext.getLoggedInUser().getUserName());

        holder.post_date.setText(model.getPost_date());
        holder.post_text.setText(model.getPost_text());

        RequestOptions loadImage = new RequestOptions()
                .placeholder(R.drawable.add_photo)
                .error(R.drawable.add_photo);

        Glide.with(context)
                .load(model.getPost_image())
                .apply(loadImage)
                .into(holder.post_image);
    }

    @Override
    public int getItemCount() {
        return socialPostsList.size();
    }

    public class SocialPostViewHolder extends RecyclerView.ViewHolder {
        ImageView post_user_photo;
        TextView post_user_name;
        TextView post_date;
        TextView post_text;
        ImageView post_image;

        public SocialPostViewHolder(@NonNull View itemView) {
            super(itemView);
            post_user_photo = itemView.findViewById(R.id.post_user_photo);
            post_user_name = itemView.findViewById(R.id.post_user_name);
            post_date = itemView.findViewById(R.id.post_date);
            post_text = itemView.findViewById(R.id.post_text);
            post_image = itemView.findViewById(R.id.post_image);
        }
    }

    private void customDeleteDialog(View rootView1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        rootView1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_delete_post,
                (ConstraintLayout) rootView1.findViewById(R.id.delete_dialog_constraint));
        builder.setView(rootView1);

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();

        Button yesDeleteButton = rootView1.findViewById(R.id.exit_cancel_button);
        Button noDeleteButton = rootView1.findViewById(R.id.exit_button);

        yesDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        noDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

}
