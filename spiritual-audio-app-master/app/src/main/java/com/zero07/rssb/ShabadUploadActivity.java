package dummydata;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import dummydata.databinding.ActivityShabadUploadBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import dummydata.models.ShabadModel;

import java.util.ArrayList;
import java.util.List;

public class ShabadUploadActivity extends AppCompatActivity {

    List<ShabadModel> shabadModels;
    FirebaseFirestore database;
    ActivityShabadUploadBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shabad_upload);
        database = FirebaseFirestore.getInstance();
        binding = ActivityShabadUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.uploadShabadButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadShabadData();
            }
        });
    }

    protected void createShabadData() {
        shabadModels = new ArrayList<>();
    }

    protected void uploadShabadData() {
        createShabadData();
        WriteBatch batch = database.batch();
        for (ShabadModel model : shabadModels) {
            DocumentReference docRef = database.collection("Shabad").document(model.getShabadId());
            batch.set(docRef, model);
        }
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ShabadUploadActivity.this, "Shabad uploaded successsfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShabadUploadActivity.this, "Error while uploading Shabad", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}