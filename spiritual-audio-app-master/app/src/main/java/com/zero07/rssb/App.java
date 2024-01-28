package dummydata;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import dummydata.activity.MainActivity;

public class App extends Application {

    Context context;
    public static IronSourceBannerLayout ironSourceBanner;

    @Override
    public void onCreate() {
        super.onCreate();
        IronSource.init((Activity) context, getString(R.string.ironSource_app_id), IronSource.AD_UNIT.BANNER);
        ironSourceBanner = IronSource.createBanner((Activity) context, ISBannerSize.BANNER);
        ironSourceBanner.setBannerListener(new IronsourceBannerListener(this));

    }

    public void showIronsourceBanner(FrameLayout adContainer, Context context) {
        this.context = context;

        adContainer.removeAllViews();

        adContainer.addView(ironSourceBanner);
        IronSource.loadBanner(ironSourceBanner);
    }
}
