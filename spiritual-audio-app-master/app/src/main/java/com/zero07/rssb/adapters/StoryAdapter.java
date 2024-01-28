package dummydata.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import dummydata.R;
import dummydata.models.StoryModel;
import dummydata.activity.WebViewActivity;
import dummydata.userModels.UserContext;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    Context context;
    List<StoryModel> storyModels;

    public StoryAdapter(Context context, List<StoryModel> storyModels) {
        this.context = context;
        this.storyModels = storyModels;
    }

    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_rv_story_activity, null);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        StoryModel model = storyModels.get(position);

        holder.textView.setText(model.getName());

        Glide.with(context)
                .load(model.getImageUrl())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserContext.getOpenStoryInsideApp()) {

                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("url", storyModels.get(position).getContentUrl());
                    context.startActivity(intent);
                } else {

                    int cctToolbarColor = context.getResources().getColor(R.color.red_app_theme);
                    CustomTabColorSchemeParams setCCTBarColors = new CustomTabColorSchemeParams.Builder()
                            .setToolbarColor(cctToolbarColor).build();
                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                            .setDefaultColorSchemeParams(setCCTBarColors).build();
                    customTabsIntent.launchUrl(context, Uri.parse(storyModels.get(position).getContentUrl()));

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return storyModels.size();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.story_image);
            textView = itemView.findViewById(R.id.story_name);
        }
    }

}
