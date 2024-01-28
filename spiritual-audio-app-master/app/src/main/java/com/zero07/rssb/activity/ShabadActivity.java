package dummydata.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.tapdaq.sdk.TMBannerAdView;
import com.tapdaq.sdk.common.TMBannerAdSizes;
import com.tapdaq.sdk.listeners.TMAdListener;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import dummydata.CustomMediaPlayer;
import dummydata.IronsourceBannerListener;
import dummydata.R;
import dummydata.adapters.ShabadAdapter;
import dummydata.databinding.ActivityShabadBinding;
import dummydata.models.ShabadModel;
import dummydata.userModels.UserContext;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ShabadActivity extends AppCompatActivity {

    private AdView admobBanner1;
    private AdView admobBanner2;
    TMBannerAdView tapdaqBanner1;
    TMBannerAdView tapdaqBanner2;
    MaxAdView applovinBanner1;
    MaxAdView applovinBanner2;
    IronSourceBannerLayout ironSourceBanner;
    private BannerView unityBanner1;
    private BannerView unityBanner2;
    private String unityAdUnitId = "Banner_Android";

    private static final String TAG = "ShabadActivity";

    TextView shabad_start_position;
    TextView shabad_end_position;
    SeekBar seekBar;
    ImageView shabad_play_button;
    ImageView shabad_pause_button;
    ImageView shabad_fast_forward_button;
    ImageView shabad_rewind_button;
    ImageView shabad_next_button;
    ImageView shabad_previous_button;
    ImageView shabad_musical_note;

    Handler handler = new Handler();
    Runnable runnable;

    ActivityShabadBinding binding;
    FirebaseFirestore database;

    CustomMediaPlayer customMediaPlayer;

    ObjectAnimator animator = null;
    AnimatorSet animatorSet = new AnimatorSet();
    TextView scrollertextview;
    ArrayList<ShabadModel> shabadAudios;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shabad);
        binding = ActivityShabadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mediaPlayer = customMediaPlayer.getMediaPlayer();
        binding.shabadRecyclerView.setLayoutManager(new GridLayoutManager(this, 1) {
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                customMediaPlayer.setbackAudio(getApplicationContext(), Uri.parse(shabadAudios.get(0).getShabadUrl()), 0);
            }
        });
        binding.shabadRecyclerView.setAdapter(adapter);




        // Initialize runnable
        runnable = new Runnable() {
            @Override
            public void run() {
                // Set progress on seekbar
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                // Handler post delay for 0.5 second
                handler.postDelayed(this, 500);
            }
        };

        shabad_play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shabad_play_button.setVisibility(View.GONE);
                shabad_pause_button.setVisibility(View.VISIBLE);
                mediaPlayer.start();

                scrollertextview.setText(shabadAudios.get(customMediaPlayer.getCurrentAudioPosition()).getShabadName());
                // Set max on seek bar
                seekBar.setMax(mediaPlayer.getDuration());
                // Start handler
                handler.postDelayed(runnable, 0);

                // Get duration of media player
                int duration = mediaPlayer.getDuration();
                // Convert millisecond to minute and second
                String sDuration = converFormat(duration);
                // Set duration on text view
                shabad_end_position.setText(sDuration);

                playerAnimation();
            }
        });

        shabad_pause_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shabad_pause_button.setVisibility(View.GONE);
                shabad_play_button.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                // Stop handler
                handler.removeCallbacks(runnable);

                stopAnimation();
            }
        });

        shabad_fast_forward_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current position of media player
                int currentPosition = mediaPlayer.getCurrentPosition();
                // Get duration of media player
                int duration = mediaPlayer.getDuration();
                // Check condition
                if (mediaPlayer.isPlaying() && duration != currentPosition) {
                    // When media is playing and duration is not equal to current position
                    // Fast forward for 5 seconds
                    currentPosition = currentPosition + 5000;
                }
                // Set current position on text view
                shabad_start_position.setText(converFormat(currentPosition));
                // Set progress on seekbar
                mediaPlayer.seekTo(currentPosition);
            }
        });

        shabad_rewind_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current position of media player
                int currentPosition = mediaPlayer.getCurrentPosition();
                // Get duration of media player
                int duration = mediaPlayer.getDuration();
                // Check condition
                if (mediaPlayer.isPlaying() && currentPosition > 5000) {
                    // When media is playing and current position is more than 5 seconds
                    // Rewind for 5 seconds
                    currentPosition = currentPosition - 5000;
                }
                // Set current position on text view
                shabad_start_position.setText(converFormat(currentPosition));
                // Set progress on seekbar
                mediaPlayer.seekTo(currentPosition);
            }
        });

        shabad_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customMediaPlayer.playNextAudio(v.getContext(), shabadAudios);
            }
        });

        shabad_previous_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customMediaPlayer.playPreviousAudio(v.getContext(), shabadAudios);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                // Set current position on text view
                shabad_start_position.setText(converFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int bufferingPercent) {
                seekBar.setSecondaryProgress(bufferingPercent);
            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.reset();
                shabad_pause_button.setVisibility(View.INVISIBLE);
                shabad_play_button.setVisibility(View.VISIBLE);
                // Set media player to initial position
                mediaPlayer.seekTo(0);
                int duration = mediaPlayer.getDuration();
                // Convert millisecond to minute and second
                String sDuration = converFormat(duration);
                // Set duration on text view
                shabad_start_position.setText(sDuration);
                customMediaPlayer.playNextAudio(getApplicationContext(), shabadAudios);
            }
        });

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
        unityBanner1 = new BannerView(this, unityAdUnitId, new UnityBannerSize(320, 50));
        binding.shabadAdContainer1.addView(unityBanner1);
        unityBanner1.load();

        unityBanner2 = new BannerView(this, unityAdUnitId, new UnityBannerSize(320, 50));
        binding.shabadAdContainer2.addView(unityBanner2);
        unityBanner2.load();
    }

    public void loadAdmobBanner() {
        binding.admobShabadAdContainer.setVisibility(View.VISIBLE);
        binding.shabadMusicalNote.getLayoutParams().height = 200;

        admobBanner1 = findViewById(R.id.admob_shabad_ad_container);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        admobBanner1.loadAd(adRequest1);
    }

    private void loadTapdaqBanner() {
        tapdaqBanner1 = new TMBannerAdView(this);
        binding.shabadAdContainer1.addView(tapdaqBanner1);
        tapdaqBanner1.load(this, getString(R.string.tapdaq_banner_default), TMBannerAdSizes.STANDARD, new TMAdListener());

        tapdaqBanner2 = new TMBannerAdView(this);
        binding.shabadAdContainer2.addView(tapdaqBanner2);
        tapdaqBanner2.load(this, getString(R.string.tapdaq_banner_main), TMBannerAdSizes.STANDARD, new TMAdListener());
    }

    private void loadApplovinBanner() {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heightDp = MaxAdFormat.BANNER.getAdaptiveSize(ShabadActivity.this).getHeight();
        int heightPx = AppLovinSdkUtils.dpToPx(ShabadActivity.this, heightDp);

        applovinBanner1 = new MaxAdView(getString(R.string.appl_banner_default), ShabadActivity.this);
        applovinBanner1.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
        applovinBanner1.setExtraParameter("adaptive_banner", "true");
        binding.shabadAdContainer1.addView(applovinBanner1);
        applovinBanner1.loadAd();

        applovinBanner2 = new MaxAdView(getString(R.string.appl_banner_default), ShabadActivity.this);
        applovinBanner2.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
        applovinBanner2.setExtraParameter("adaptive_banner", "true");
        binding.shabadAdContainer2.addView(applovinBanner2);
        applovinBanner2.loadAd();
    }

    private void loadIronSourceBanner() {
//        IronSource.init(this, getString(R.string.ironSource_app_id), IronSource.AD_UNIT.BANNER);
        ironSourceBanner = IronSource.createBanner(this, ISBannerSize.RECTANGLE);
        binding.shabadAdContainer1.addView(ironSourceBanner);

        binding.shabadAdContainer1.setVisibility(View.VISIBLE);
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
        if (admobBanner1 != null) {
            admobBanner1.pause();
        }
        if (admobBanner2 != null) {
            admobBanner2.pause();
        }
        if(customMediaPlayer != null ) {
            customMediaPlayer.pause();
        }

        IronSource.onPause(this);
        IronSource.destroyBanner(ironSourceBanner);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (admobBanner1 != null) {
            admobBanner1.resume();
        }
        if (admobBanner2 != null) {
            admobBanner2.resume();
        }

        IronSource.onResume(this);
        loadBannerAdFrom();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (unityBanner1 != null){
            unityBanner1.destroy();
        }
        if (unityBanner2 != null){
            unityBanner2.destroy();
        }

        if (admobBanner1 != null) {
            admobBanner1.destroy();
        }
        if (admobBanner2 != null) {
            admobBanner2.destroy();
        }

        if (tapdaqBanner1 != null) {
            tapdaqBanner1.destroy(this);
        }
        if (tapdaqBanner2 != null) {
            tapdaqBanner2.destroy(this);
        }

        IronSource.destroyBanner(ironSourceBanner);
        super.onDestroy();
    }

    private void playerAnimation() {
        View view = findViewById(R.id.shabad_musical_note);

        animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        animator.setDuration(10000);
        animator.setRepeatCount(Animation.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animatorSet.playTogether(animator);
        animatorSet.start();

    }

    private void stopAnimation() {
        animator.cancel();
        animatorSet.cancel();
    }

    private void initializeMediaPlayerButtons() {
        shabad_start_position = findViewById(R.id.shabad_start_position);
        shabad_end_position = findViewById(R.id.shabad_end_position);
        seekBar = findViewById(R.id.shabad_seekbar);
        shabad_play_button = findViewById(R.id.shabad_play_button);
        shabad_pause_button = findViewById(R.id.shabad_pause_button);
        shabad_fast_forward_button = findViewById(R.id.shabad_fast_forward_button);
        shabad_rewind_button = findViewById(R.id.shabad_rewind_button);
        shabad_next_button = findViewById(R.id.shabad_next_button);
        shabad_previous_button = findViewById(R.id.shabad_previous_button);
        shabad_musical_note = findViewById(R.id.shabad_musical_note);
    }

    @SuppressLint("DefaultLocale")
    private String converFormat(int duration) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customMediaPlayer.getMediaPlayer().stop();
        customMediaPlayer.getMediaPlayer().reset();
        customMediaPlayer.clear();
    }


    public ArrayList<ShabadModel> populateShabad() {
        ArrayList<ShabadModel> shabadaudio = new ArrayList<>();

        return shabadaudio;
    }
