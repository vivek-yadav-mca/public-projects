package dummydata;

import android.content.Context;
import android.widget.Toast;

import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;

public class IronsourceBannerListener implements BannerListener {

    Context context;

    public IronsourceBannerListener(Context context) {
        this.context = context;
    }

    @Override
    public void onBannerAdLoaded() {
    }

    @Override
    public void onBannerAdLoadFailed(IronSourceError ironSourceError) {
    }

    @Override
    public void onBannerAdClicked() {

    }

    @Override
    public void onBannerAdScreenPresented() {

    }

    @Override
    public void onBannerAdScreenDismissed() {

    }

    @Override
    public void onBannerAdLeftApplication() {

    }
}
