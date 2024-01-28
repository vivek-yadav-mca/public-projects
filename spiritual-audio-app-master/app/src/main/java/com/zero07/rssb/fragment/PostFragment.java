package dummydata.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import dummydata.Constants;
import dummydata.R;
import dummydata.activity.AddPostActivity;
import dummydata.adapters.SocialPostAdapter;
import dummydata.databinding.FragmentPostBinding;
import dummydata.models.SocialPostModel;
import dummydata.userModels.UserContext;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PostFragment extends Fragment {

    private static final String TAG = PostFragment.class.getName();

    FragmentPostBinding binding;
    Animation floatingScale;
    FirebaseDatabase database;

    private AlertDialog dialog;

    public PostFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);
        binding = FragmentPostBinding.inflate(inflater, container, false);

        database = FirebaseDatabase.getInstance();

        ArrayList<SocialPostModel> socialPostsList = new ArrayList<>();
        SocialPostAdapter adapter = new SocialPostAdapter(getContext(), socialPostsList);

        database.getReference()
                .child(Constants.USER_POST_DATA)
                .child(UserContext.getLoggedInUser().getId())

        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                socialPostsList.clear();
                SocialPostModel getModelData = snapshot.getValue(SocialPostModel.class);

                socialPostsList.add(getModelData);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), "Unable to retrieve data", Toast.LENGTH_LONG).show();
            }
        });


        binding.socialPostRv.setLayoutManager(new GridLayoutManager(getContext(), 1));
        binding.socialPostRv.setAdapter(adapter);

        floatingScale = AnimationUtils.loadAnimation(getContext(), R.anim.anim_floating_button_scale);

        binding.addPostFloatButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.viewBgFloatingButn.setVisibility(View.VISIBLE);
                binding.viewBgFloatingButn.startAnimation(floatingScale);

                floatingScale.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
//                        binding.viewBgFloatingButn.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(getContext(), AddPostActivity.class);
                        Toast.makeText(getContext(), "Please Wait...", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

            }
        });


        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_post, container, false);
    }


    private void addPostEventListener(DatabaseReference mPostReference) {
    }




}
