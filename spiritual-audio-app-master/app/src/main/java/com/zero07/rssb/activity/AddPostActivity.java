package dummydata.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import dummydata.Constants;
import dummydata.R;
import dummydata.models.SocialPostModel;
import dummydata.userModels.UserContext;
import dummydata.databinding.ActivityAddPostBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddPostActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 1;

    ActivityAddPostBinding binding;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri userSelectedImage;

    Timestamp firbaseTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

/*** Progress Dialog ***/
        AlertDialog.Builder progressDialog = new AlertDialog.Builder(AddPostActivity.this);
        View rootView1 = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.layout_dialog_progress,
                (ConstraintLayout) findViewById(R.id.dialog_progress_constraint));
        progressDialog.setView(rootView1);
        progressDialog.setCancelable(false);

        AlertDialog dialog = progressDialog.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    /** show dialog below ***/
/*** Progress Dialog ***/

//        EEE, d MMM yyyy HH:mm    dd-MM-yyyy HH:mm:ss
        SimpleDateFormat format= new SimpleDateFormat("EEE, d MMM yyyy - HH:mm:ss", Locale.getDefault());  //for more format check at the end
        String currentDate = format.format(new Date());

        binding.dialogCurrentTime.setText(currentDate);

        binding.dialogUserName.setText(UserContext.getLoggedInUser().getUserName());

        RequestOptions loadImage = new RequestOptions()
                .centerCrop()
                .circleCrop()  //to crop image in circle view
                .placeholder(R.drawable.user_profile)
                .error(R.drawable.user_profile);

        Glide.with(this)
                .load(UserContext.getLoggedInUser().getUserPhotoUrl())
                .apply(loadImage)
                .into(binding.dialogUserPhoto);

        binding.addPostUserSelectedPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                Intent galleryintent = new Intent();
                galleryintent.setAction(Intent.ACTION_PICK);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent, IMAGE_REQUEST_CODE);
            }
        });

        binding.addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String postText = binding.addPostEditText.getText().toString();

                if (postText.isEmpty()) {
                    binding.addPostEditText.setError("Please type something");
                    return;
                }

                if (userSelectedImage != null) {
                    dialog.show();

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                            .child(Constants.USER_POST_IMAGES_DATA)
                            .child(UserContext.getLoggedInUser().getId())
                            .child(currentDate);

                    storageReference
                            .putFile(userSelectedImage)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        storageReference
                                                .getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String userImageUrl = uri.toString();

                                                        String post_user_photo = UserContext.getLoggedInUser().getUserPhotoUrl();
                                                        String post_username = UserContext.getLoggedInUser().getUserName();
                                                        String post_date = currentDate;
                                                        String post_text = postText;
                                                        String post_image = userImageUrl;

                                                        SocialPostModel model = new SocialPostModel(post_user_photo,
                                                                post_username, post_date, post_text, post_image);

                                                        database.getReference()
                                                                .child(Constants.USER_POST_DATA)
                                                                .child(UserContext.getLoggedInUser().getId())
                                                                .child(currentDate)
                                                                .setValue(model)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Intent intent = new Intent(AddPostActivity.this, SocialPostActivity.class);
                                                                        startActivity(intent);

                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                }
                            });
                }

            }
        });


    }

    private void showProgressDialog() {
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageData = data.getData();

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                if (imageData != null) {
                        binding.addPostUserSelectedPhoto.setImageURI(imageData);
                        userSelectedImage = imageData;
                }
            }
        } else {
            Toast.makeText(this, "Failed to load image from gallery", Toast.LENGTH_LONG).show();
        }
    }


}

/*** Format
                yyyy-MM-dd 1969-12-31
                yyyy-MM-dd 1970-01-01
          yyyy-MM-dd HH:mm 1969-12-31 16:00
          yyyy-MM-dd HH:mm 1970-01-01 00:00
         yyyy-MM-dd HH:mmZ 1969-12-31 16:00-0800
         yyyy-MM-dd HH:mmZ 1970-01-01 00:00+0000
  yyyy-MM-dd HH:mm:ss.SSSZ 1969-12-31 16:00:00.000-0800
  yyyy-MM-dd HH:mm:ss.SSSZ 1970-01-01 00:00:00.000+0000
yyyy-MM-dd'T'HH:mm:ss.SSSZ 1969-12-31T16:00:00.000-0800
yyyy-MM-dd'T'HH:mm:ss.SSSZ 1970-01-01T00:00:00.000+0000

 Format ***/