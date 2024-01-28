package dummydata;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import dummydata.databinding.ActivityStoryUploadBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import dummydata.models.StoryModel;

import java.util.ArrayList;
import java.util.List;

public class StoryUploadActivity extends AppCompatActivity {

    List<StoryModel> storyModels;
    FirebaseFirestore database;
    ActivityStoryUploadBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_upload);
        database = FirebaseFirestore.getInstance();
        binding = ActivityStoryUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.uploadStoryButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadStoryData();
            }
        });
    }

    protected void createStoryData() {
        storyModels = new ArrayList<>();
        /**** Upload Here ****/
    }

    protected void uploadStoryData() {
        createStoryData();
        WriteBatch batch = database.batch();
        for (StoryModel model : storyModels) {
            DocumentReference docRef = database.collection(Constants.STORY_COLLECTION).document(model.getId());
            batch.set(docRef, model);
        }
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(StoryUploadActivity.this, "Story uploaded successsfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StoryUploadActivity.this, "Error while uploading Story", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}