package dummydata.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import dummydata.R;
import dummydata.databinding.ActivityPhotoBinding;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";
    private ActivityPhotoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        binding = ActivityPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}