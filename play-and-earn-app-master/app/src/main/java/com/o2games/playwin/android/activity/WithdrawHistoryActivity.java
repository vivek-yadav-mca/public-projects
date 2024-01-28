package dummydata.android.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ironsource.mediationsdk.IronSource;
import dummydata.android.Constants;
import dummydata.android.R;
import dummydata.android.adapter.WithdrawHistoryAdapter;
import dummydata.android.databinding.ActivityWithdrawHistoryBinding;
import dummydata.android.model.WithdrawRequestData;
import dummydata.android.userData.UserContext;

import java.util.ArrayList;
import java.util.List;

public class WithdrawHistoryActivity extends AppCompat {

    ActivityWithdrawHistoryBinding binding;

    DatabaseReference databaseRef;
    List<WithdrawRequestData> withdrawRequestData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_history);
        binding = ActivityWithdrawHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String userId = UserContext.getLoggedInUser().getId();

        databaseRef = FirebaseDatabase.getInstance().getReference();
        WithdrawHistoryAdapter adapter = new WithdrawHistoryAdapter(this, withdrawRequestData);

        databaseRef
                .child(Constants.FOR_USER_WITHDRAW)
                .child(userId)
                .orderByChild("date")
//                .limitToLast(100)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        withdrawRequestData.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            WithdrawRequestData model = data.getValue(WithdrawRequestData.class);
                            withdrawRequestData.add(model);
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        binding.rvWithdrawHistory.setLayoutManager(new GridLayoutManager(this, 1));
        binding.rvWithdrawHistory.setAdapter(adapter);



    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}