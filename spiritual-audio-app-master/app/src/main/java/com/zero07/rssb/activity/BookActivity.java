package dummydata.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.tapdaq.sdk.TMBannerAdView;
import com.tapdaq.sdk.common.TMBannerAdSizes;
import com.tapdaq.sdk.listeners.TMAdListener;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import dummydata.IronsourceBannerListener;
import dummydata.R;
import dummydata.adapters.BookAdapter;
import dummydata.databinding.ActivityBookBinding;
import dummydata.models.BookModel;
import dummydata.userModels.UserContext;

import java.util.ArrayList;

public class BookActivity extends AppCompatActivity {

    private ActivityBookBinding binding;
    private AdView admobBanner;
    private TMBannerAdView tapdaqBanner;
    private MaxAdView applovinBanner1;
    private IronSourceBannerLayout ironSourceBanner;
    private BannerView unityBanner;
    private BannerView unityBanner2;
    private String unityAdUnitId = "Banner_Android";

    private static final String TAG = "BookActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        binding = ActivityBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadBannerAdFrom();

        ArrayList<BookModel> bookModels = new ArrayList<>();

        BookAdapter bookAdapter = new BookAdapter(this,bookModels );
        binding.bookRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.bookRecyclerView.setAdapter(bookAdapter);



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
            loadIronsourceBanner();
        }
    }

    private void loadUnityBanner() {
/**** Unity Ads ****/
        unityBanner = new BannerView(this, unityAdUnitId, new UnityBannerSize(320, 50));
        binding.bookAdContainer.addView(unityBanner);
        unityBanner.load();
    }

    public void loadAdmobBanner() {
        admobBanner = new AdView(this);
        binding.bookAdContainer.addView(admobBanner);
        admobBanner.setAdUnitId(getString(R.string.admob_default_banner));
        AdSize adaptiveSize = getAdSize();
        admobBanner.setAdSize(adaptiveSize);
        AdRequest adRequest = new AdRequest.Builder().build();
        admobBanner.loadAd(adRequest);
    }

    private void loadTapdaqBanner() {
        tapdaqBanner = new TMBannerAdView(this);
        binding.bookAdContainer.addView(tapdaqBanner);
        tapdaqBanner.load(this, getString(R.string.tapdaq_banner_main), TMBannerAdSizes.STANDARD, new TMAdListener());
    }

    private void loadApplovinBanner() {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heightDp = MaxAdFormat.BANNER.getAdaptiveSize(BookActivity.this).getHeight();
        int heightPx = AppLovinSdkUtils.dpToPx(BookActivity.this, heightDp);

        applovinBanner1 = new MaxAdView(getString(R.string.appl_banner_default), BookActivity.this);
        applovinBanner1.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
        applovinBanner1.setExtraParameter("adaptive_banner", "true");
        binding.bookAdContainer.addView(applovinBanner1);
        applovinBanner1.loadAd();
    }

    private void loadIronsourceBanner() {
//        IronSource.init(this, getString(R.string.ironSource_app_id), IronSource.AD_UNIT.BANNER);
        ironSourceBanner = IronSource.createBanner(this, ISBannerSize.BANNER);
        binding.bookAdContainer.addView(ironSourceBanner);

        binding.bookAdContainer.setVisibility(View.VISIBLE);
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
        super.onPause();
        if (admobBanner != null) {
            admobBanner.pause();
        }
        IronSource.onPause(this);
        IronSource.destroyBanner(ironSourceBanner);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (admobBanner != null) {
            admobBanner.resume();
        }
        IronSource.onResume(this);
        loadBannerAdFrom();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    }


}