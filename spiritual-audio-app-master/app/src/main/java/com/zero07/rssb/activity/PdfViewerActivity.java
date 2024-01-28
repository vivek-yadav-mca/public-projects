package dummydata.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.tapdaq.sdk.TMBannerAdView;
import com.tapdaq.sdk.common.TMBannerAdSizes;
import com.tapdaq.sdk.listeners.TMAdListener;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import dummydata.IronsourceBannerListener;
import dummydata.R;
import dummydata.databinding.ActivityPdfViewerBinding;
import dummydata.userModels.UserContext;

public class PdfViewerActivity extends AppCompatActivity {

    private ActivityPdfViewerBinding binding;
    private AdView admobBanner;
//    private AdManagerAdView admobBanner;
    private TMBannerAdView tapdaqBanner;
    private MaxAdView applovinBanner1;
    private IronSourceBannerLayout ironSourceBanner;
    private BannerView unityBanner;
    private BannerView unityBanner2;
    private String unityAdUnitId = "Banner_Android";

    AlertDialog.Builder progressDialog;
    AlertDialog dialog;

    private static final String TAG = "PdfViewerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        binding = ActivityPdfViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadProgressDialog();
        loadBannerAdFrom();

        WebView pdfWebView = (WebView) findViewById(R.id.pdfWebView);

        pdfWebView.getSettings().setJavaScriptEnabled(true);
        pdfWebView.getSettings().setBuiltInZoomControls(true);
        pdfWebView.getSettings().setDisplayZoomControls(false);

        pdfWebView.setWebChromeClient(new WebChromeClient());

        String url = getIntent().getStringExtra("url");

        pdfWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView pdfWebView, String url) {
                pdfWebView.loadUrl("javascript: (function() { " + "document.querySelector('[role=\"toolbar\"]').remove();}) ()");

                dialog.dismiss();

                super.onPageFinished(pdfWebView, url);
            }
        });

        binding.pdfWebView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + url);


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
        binding.pdfAdContainer.addView(unityBanner);
        unityBanner.load();
    }

    public void loadAdmobBanner() {
        admobBanner = new AdView(this);
        binding.pdfAdContainer.addView(admobBanner);
        admobBanner.setAdUnitId(getString(R.string.admob_default_banner));
        AdSize adaptiveSize = getAdSize();
        admobBanner.setAdSize(adaptiveSize);
        AdRequest adRequest = new AdRequest.Builder().build();
        admobBanner.loadAd(adRequest);
    }

    private void loadTapdaqBanner() {
        tapdaqBanner = new TMBannerAdView(this);
        binding.pdfAdContainer.addView(tapdaqBanner);
        tapdaqBanner.load(this, getString(R.string.tapdaq_banner_main), TMBannerAdSizes.STANDARD, new TMAdListener());
    }

    private void loadApplovinBanner() {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heightDp = MaxAdFormat.BANNER.getAdaptiveSize(PdfViewerActivity.this).getHeight();
        int heightPx = AppLovinSdkUtils.dpToPx(PdfViewerActivity.this, heightDp);

        applovinBanner1 = new MaxAdView(getString(R.string.appl_banner_default), PdfViewerActivity.this);
        applovinBanner1.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
        applovinBanner1.setExtraParameter("adaptive_banner", "true");
        binding.pdfAdContainer.addView(applovinBanner1);
        applovinBanner1.loadAd();
    }

    private void loadIronSourceBanner() {
//        IronSource.init(this, getString(R.string.ironSource_app_id), IronSource.AD_UNIT.BANNER);
        ironSourceBanner = IronSource.createBanner(this, ISBannerSize.BANNER);
        binding.pdfAdContainer.addView(ironSourceBanner);

        binding.pdfAdContainer.setVisibility(View.VISIBLE);
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


    private void loadProgressDialog() {
/*** Progress Dialog ***/
//        AlertDialog.Builder progressDialog = new AlertDialog.Builder(StoryActivity.this);
        progressDialog = new AlertDialog.Builder(PdfViewerActivity.this);
        View rootView1 = LayoutInflater.from(PdfViewerActivity.this).inflate(R.layout.layout_dialog_progress,
                (ConstraintLayout) findViewById(R.id.dialog_progress_constraint));
        progressDialog.setView(rootView1);
        progressDialog.setCancelable(false);

//        AlertDialog dialog = progressDialog.create();
        dialog = progressDialog.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();
        /** show dialog below ***/
/*** Progress Dialog ***/
    }


}