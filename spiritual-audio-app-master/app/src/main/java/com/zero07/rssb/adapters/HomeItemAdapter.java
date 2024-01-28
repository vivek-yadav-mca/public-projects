package dummydata.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.tapdaq.sdk.Tapdaq;
import com.tapdaq.sdk.adnetworks.TDMediatedNativeAd;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.listeners.TMAdListener;
import com.tapdaq.sdk.listeners.TMAdListenerBase;
import dummydata.activity.SocialPostActivity;
import dummydata.models.HomeItemModel;
import dummydata.R;
import dummydata.activity.BookActivity;
import dummydata.activity.PhotoActivity;
import dummydata.activity.SatsangActivity;
import dummydata.activity.ShabadActivity;
import dummydata.activity.StoryActivity;
import dummydata.userModels.UserContext;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class HomeItemAdapter extends RecyclerView.Adapter<HomeItemAdapter.HomeItemViewHolder> {

    ConnectivityManager connectivityManager;

    Activity context;
    ArrayList<HomeItemModel> homeItemModels;
    AlertDialog progressDialog;

    private boolean isAdmobInterstitialEnabled;
    private boolean isTapdaqInterstitialEnabled;
    private boolean isAppLovinInterstitialEnabled;

    private InterstitialAd admobInterstitial;
    private MaxInterstitialAd applovinInterstitial;

    public HomeItemAdapter(Activity context, ArrayList<HomeItemModel> homeItemModels) {
        this.context = context;
        this.homeItemModels = homeItemModels;
    }

    @Override
    public HomeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_rv_home_item, null);
        return new HomeItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeItemAdapter.HomeItemViewHolder holder, int position) {

        HomeItemModel model = homeItemModels.get(position);

        holder.textView.setText(model.getItemName());

        Glide.with(context)
                .load(model.getItemImage())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        if (!internetConnected(context)) {
                            noInternetDialog();
                        } else {
                            openNextActivity(v, 0);
                        }
                        break;
                    case 1:
                        if (!internetConnected(context)) {
                            noInternetDialog();
                        } else {
                            openNextActivity(v, 1);
                        }
                        break;
                    case 2:
                        if (!internetConnected(context)) {
                            noInternetDialog();
                        } else {
                            openNextActivity(v, 2);
                        }
                        break;
                    case 3:
                        intent = new Intent(v.getContext(), PhotoActivity.class);
                        Toast.makeText(context, "Please wait. Loading....", Toast.LENGTH_LONG).show();
                        context.startActivity(intent);
                        break;
                    case 4:
                        if (!internetConnected(context)) {
                            noInternetDialog();
                        } else {
                            openNextActivity(v, 4);
                        }
                        break;
                    case 5:
                        if (!internetConnected(context)) {
                            noInternetDialog();
                        } else {
                            showToastComingSoon(v);
                        }
                        break;
                    default:
                        break;
                }

            }
        });
    }

    private void showToastComingSoon(View rootView1) {
        rootView1 = LayoutInflater.from(context).inflate(R.layout.layout_toast_custom,
                (ConstraintLayout) rootView1.findViewById(R.id.custom_toast_constraint));
        CardView toastCardView = rootView1.findViewById(R.id.toast_custom_card_view);
        TextView toastText = rootView1.findViewById(R.id.toast_custom_text);
        ImageView toastIcon = rootView1.findViewById(R.id.toast_custom_image);

        toastCardView.setBackgroundResource(R.drawable.bg_custom_toast);
        toastText.setText("COMING SOON");
        toastIcon.setImageResource(R.drawable.coming_soon);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 500);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(rootView1);

        toast.show();
    }

    @Override
    public int getItemCount() {
        return homeItemModels.size();
    }

    public class HomeItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public HomeItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.home_item_photo);
            textView = itemView.findViewById(R.id.home_item_name);
        }
    }

    private void toBlink(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.blink);

    }

    private void noInternetDialog() {
        AlertDialog.Builder noInternetDialog = new AlertDialog.Builder(context);
        noInternetDialog.setMessage("Please check your internet connection");

        AlertDialog alertDialog = noInternetDialog.create();

        alertDialog.show();
    }


    /*****Checking Internet Connectivity****/
    private boolean internetConnected(Context context) {

        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
        NetworkInfo mobileDataConnection = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);

        if ((wifiConnection != null && wifiConnection.isConnected()
                || (mobileDataConnection != null && mobileDataConnection.isConnected()))) {
            return true;
        } else {
            return false;
        }
    }

    /*****Checking Internet Connectivity****/

    private void showProgressDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View rootView1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_progress,
                (ConstraintLayout) v.findViewById(R.id.dialog_progress_constraint));
        builder.setView(rootView1);
        builder.setCancelable(false);

        progressDialog = builder.create();

        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
    }


    private void openNextActivity(View v, int s0s1s2p3b4s5) {
        if (s0s1s2p3b4s5 == 0) {
            Intent intent = new Intent(v.getContext(), ShabadActivity.class);
            Toast.makeText(context, "Please wait. Loading....", Toast.LENGTH_LONG).show();
            context.startActivity(intent);
        }
        if (s0s1s2p3b4s5 == 1) {
            Intent intent = new Intent(v.getContext(), SatsangActivity.class);
            Toast.makeText(context, "Please wait. Loading....", Toast.LENGTH_LONG).show();
            context.startActivity(intent);
        }
        if (s0s1s2p3b4s5 == 2) {
            Intent intent = new Intent(v.getContext(), StoryActivity.class);
            Toast.makeText(context, "Please wait. Loading....", Toast.LENGTH_LONG).show();
            context.startActivity(intent);
        }
        if (s0s1s2p3b4s5 == 3) {
            Intent intent = new Intent(v.getContext(), PhotoActivity.class);
            Toast.makeText(context, "Please wait. Loading....", Toast.LENGTH_LONG).show();
            context.startActivity(intent);
        }
        if (s0s1s2p3b4s5 == 4) {
            Intent intent = new Intent(v.getContext(), BookActivity.class);
            Toast.makeText(context, "Please wait. Loading....", Toast.LENGTH_LONG).show();
            context.startActivity(intent);
        }
        if (s0s1s2p3b4s5 == 5) {
            showToastComingSoon(v);
        }
    }

}
