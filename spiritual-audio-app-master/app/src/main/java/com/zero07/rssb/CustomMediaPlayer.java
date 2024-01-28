package dummydata;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.ImageView;

import dummydata.models.SatsangModel;
import dummydata.models.ShabadModel;

import java.io.IOException;
import java.util.List;

public class CustomMediaPlayer extends MediaPlayer {
    private  CustomMediaPlayer instance;

    private MediaPlayer mediaPlayer;
    private ImageView shabadPlayButton;
    private int currentAudioPosition;

    public CustomMediaPlayer(ImageView shabadPlayButton) {
        this.mediaPlayer = new MediaPlayer();
        this.shabadPlayButton = shabadPlayButton;
    }


    public void initSong(Context context, Uri myUri, int currentAudioPosition) {
        try {
            setbackAudio(context, myUri, currentAudioPosition);
            shabadPlayButton.callOnClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setbackAudio(Context context, Uri myUri, int currentAudioPosition) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            } else {
                this.mediaPlayer.reset();
            }
            mediaPlayer.setDataSource(context, myUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            this.currentAudioPosition = currentAudioPosition;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public int getCurrentAudioPosition() {
        return currentAudioPosition;
    }

    public void playNextAudio(Context context, List<ShabadModel> shabadModels) {
        int nextId = getCurrentAudioPosition() + 1;
        int upcomingAudioPos = nextId == shabadModels.size() ? 0 : nextId;
        initSong(context, Uri.parse(shabadModels.get(upcomingAudioPos).getShabadUrl()), upcomingAudioPos);
    }

    public void playPreviousAudio(Context context, List<ShabadModel> shabadModels) {
        int prevId = getCurrentAudioPosition() - 1;
        int upcomingAudioPos = prevId < 0 ? 0 : prevId;
        initSong(context, Uri.parse(shabadModels.get(upcomingAudioPos).getShabadUrl()), upcomingAudioPos);
    }

    public void playNextSatsangAudio(Context context, List<SatsangModel> satsangModels) {
        int nextId = getCurrentAudioPosition() + 1;
        int upcomingAudioPos = nextId == satsangModels.size() ? 0 : nextId;
        initSong(context, Uri.parse(satsangModels.get(upcomingAudioPos).getSatsangUrl()), upcomingAudioPos);
    }

    public void playPreviousSatsangAudio(Context context, List<SatsangModel> satsangModels) {
        int prevId = getCurrentAudioPosition() - 1;
        int upcomingAudioPos = prevId < 0 ? 0 : prevId;
        initSong(context, Uri.parse(satsangModels.get(upcomingAudioPos).getSatsangUrl()), upcomingAudioPos);
    }

    public void clear() {
        this.shabadPlayButton = null;
        this.currentAudioPosition = 0;
    }
}
