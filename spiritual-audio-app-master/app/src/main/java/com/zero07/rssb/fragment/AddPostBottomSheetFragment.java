package dummydata.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import dummydata.R;
import dummydata.userModels.UserContext;

public class AddPostBottomSheetFragment extends BottomSheetDialogFragment {

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_add_post, container, false);

        ImageView dialogUserPhoto = rootView.findViewById(R.id.dialog_user_photo);
        TextView dialogUserName = rootView.findViewById(R.id.dialog_user_name);
        ImageView addPostSelectedPhoto = rootView.findViewById(R.id.add_post_user_selected_photo);

        Button postButton = rootView.findViewById(R.id.add_post_button);
        ImageView cancelButton = rootView.findViewById(R.id.close_button_post_dialog);

        dialogUserName.setText(UserContext.getLoggedInUser().getUserName());

        RequestOptions loadImage = new RequestOptions()
                .centerCrop()
                .circleCrop()  //to crop image in circle view
                .placeholder(R.drawable.user_profile)
                .error(R.drawable.user_profile);

        Glide.with(getActivity())
                .load(UserContext.getLoggedInUser().getUserPhotoUrl())
                .apply(loadImage)
                .into(dialogUserPhoto);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                alertDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                alertDialog.dismiss();
            }
        });

        addPostSelectedPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (data.getData() != null) {

            }
        }
    }


}
