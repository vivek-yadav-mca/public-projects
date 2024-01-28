package dummydata;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.tapdaq.sdk.TMBannerAdView;
import com.tapdaq.sdk.common.TMBannerAdSizes;

public class AdsActivity extends AppCompatActivity {
    AdManagerAdView adManagerAdView;
    AdManagerAdView adView;
    TMBannerAdView tapdaqBanner;

    FrameLayout adContainerLayout50x3, adContainerLayout50x4;
    AdView testAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);

        Button rewardedAdButton = findViewById(R.id.rewarded_ad_button);

        MobileAds.initialize(this);

        adManagerAdView = findViewById(R.id.adManagerAdView);
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();

        adView = new AdManagerAdView(this);
        adContainerLayout50x3 = findViewById(R.id.ads_activity_banner_50x3);
        adContainerLayout50x3.addView(adView);
        adView.setAdUnitId("dummydata");
        AdSize adaptiveSize = getAdSize();
        adView.setAdSizes(adaptiveSize, AdSize.BANNER);
        AdManagerAdRequest adRequest1 = new AdManagerAdRequest.Builder().build();

        tapdaqBanner = new TMBannerAdView(this);
        adContainerLayout50x4 = findViewById(R.id.ads_activity_banner_50x4);
        adContainerLayout50x4.addView(tapdaqBanner);
        tapdaqBanner.load(AdsActivity.this, "dummydata", TMBannerAdSizes.STANDARD, new TapdaqBannerListener());

        rewardedAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

}