package dummydata.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.tapdaq.sdk.TMBannerAdView;
import com.tapdaq.sdk.common.TMBannerAdSizes;
import com.tapdaq.sdk.listeners.TMAdListener;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import dummydata.Constants;
import dummydata.IronsourceBannerListener;
import dummydata.R;
import dummydata.adapters.StoryAdapter;
import dummydata.databinding.ActivityStoryBinding;
import dummydata.models.StoryModel;
import dummydata.userModels.UserContext;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StoryActivity extends AppCompatActivity {

    private AdView admobBanner;
//    private AdManagerAdView admobBanner;
    private TMBannerAdView tapdaqBanner;
    private MaxAdView applovinBanner1;
    IronSourceBannerLayout ironSourceBanner;
    private BannerView unityBanner;
    private BannerView unityBanner2;
    private String unityAdUnitId = "Banner_Android";

    private static final String TAG = "StoryActivity";

    private ActivityStoryBinding binding;

    private List<StoryModel> storyModelList = new ArrayList<>();
    private StoryAdapter storyAdapter;

    private FirebaseFirestore firestore;

    AlertDialog.Builder progressDialog;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        binding = ActivityStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();

        populateStoryModels();
        loadProgressDialog();

        loadBannerAdFrom();

        storyAdapter = new StoryAdapter(this, storyModelList);
        binding.storyRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        binding.storyRecyclerView.setAdapter(storyAdapter);

    }

    private void loadBannerAdFrom() {
        if (UserContext.getIsAdmobEnabled()) {
            loadAdmobBanner();
        }
        if (UserContext.getIsTapdaqEnabled()) {
            loadTapdaqBanner();
        }
        if (UserContext.getIsApplovinEnabled()) {
            loadApplovinBanner();
        }
        if (UserContext.getIsUnityEnabled()) {
            loadUnityBanner();
        }
        if (UserContext.getIsIronsource_banner()) {
            loadIronSourceBanner();
        }
    }

    private void loadUnityBanner() {
/**** Unity Ads ****/
        unityBanner = new BannerView(this, unityAdUnitId, new UnityBannerSize(320, 50));
        binding.storyAdContainer.addView(unityBanner);
        unityBanner.load();
    }

    public void loadAdmobBanner() {
        admobBanner = new AdView(this);
        binding.storyAdContainer.addView(admobBanner);
        admobBanner.setAdUnitId(getString(R.string.admob_default_banner));
        AdSize adaptiveSize = getAdSize();
        admobBanner.setAdSize(adaptiveSize);
        AdRequest adRequest = new AdRequest.Builder().build();
        admobBanner.loadAd(adRequest);
    }

    private void loadTapdaqBanner() {
        tapdaqBanner = new TMBannerAdView(this);
        binding.storyAdContainer.addView(tapdaqBanner);
        tapdaqBanner.load(this, getString(R.string.tapdaq_banner_main), TMBannerAdSizes.STANDARD, new TMAdListener());
    }

    private void loadApplovinBanner() {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heightDp = MaxAdFormat.BANNER.getAdaptiveSize(StoryActivity.this).getHeight();
        int heightPx = AppLovinSdkUtils.dpToPx(StoryActivity.this, heightDp);

        applovinBanner1 = new MaxAdView(getString(R.string.appl_banner_default), StoryActivity.this);
        applovinBanner1.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
        applovinBanner1.setExtraParameter("adaptive_banner", "true");
        binding.storyAdContainer.addView(applovinBanner1);
        applovinBanner1.loadAd();
    }

    private void loadIronSourceBanner() {
//        IronSource.init(this, getString(R.string.ironSource_app_id), IronSource.AD_UNIT.BANNER);
        ironSourceBanner = IronSource.createBanner(this, ISBannerSize.BANNER);
        binding.storyAdContainer.addView(ironSourceBanner);

        binding.storyAdContainer.setVisibility(View.VISIBLE);
        ironSourceBanner.setBannerListener(new IronsourceBannerListener(this));
        IronSource.loadBanner(ironSourceBanner);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @Override
    protected void onPause() {
        if (admobBanner != null) {
            admobBanner.pause();
        }

        IronSource.onPause(this);
        IronSource.destroyBanner(ironSourceBanner);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (admobBanner != null) {
            admobBanner.resume();
        }

        IronSource.onResume(this);
        loadBannerAdFrom();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (unityBanner != null) {
            unityBanner.destroy();
        }
        if (admobBanner != null) {
            admobBanner.destroy();
        }
        if (tapdaqBanner != null) {
            tapdaqBanner.destroy(this);
        }
        IronSource.destroyBanner(ironSourceBanner);
        super.onDestroy();
    }


    public void generateWebView(String url) {
        //binding.storyWebView.loadUrl(url);
    }


    private void loadProgressDialog() {
/*** Progress Dialog ***/
        progressDialog = new AlertDialog.Builder(StoryActivity.this);
        View rootView1 = LayoutInflater.from(StoryActivity.this).inflate(R.layout.layout_dialog_progress,
                (ConstraintLayout) findViewById(R.id.dialog_progress_constraint));
        progressDialog.setView(rootView1);
        progressDialog.setCancelable(false);

        dialog = progressDialog.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();
        /** show dialog below ***/
/*** Progress Dialog ***/
    }


    private void populateStoryModels() {

        firestore.collection(Constants.STORY_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        storyModelList.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            StoryModel model = snapshot.toObject(StoryModel.class);
                            model.setId(snapshot.getId());
                            storyModelList.add(model);

                            dialog.dismiss();
                        }
                        storyModelList.sort(new Comparator<StoryModel>() {
                            @Override
                            public int compare(StoryModel o1, StoryModel o2) {
                                return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                            }
                        });
                        storyAdapter.notifyDataSetChanged();
                    }
                });

        firestore.collection(Constants.STORY_COLLECTION)
                .orderBy("createdAt",
           Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NewApi")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    storyModelList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        StoryModel model = document.toObject(StoryModel.class);
                        model.setId(document.getId());
                        storyModelList.add(model);

                        dialog.dismiss();
                    }
                    storyModelList.sort(new Comparator<StoryModel>() {
                        @Override
                        public int compare(StoryModel o1, StoryModel o2) {
                            return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                        }
                    });
                } else {
                    Log.e("database", "Error getting documents: ", task.getException());
                }
            }
        });


    }


}