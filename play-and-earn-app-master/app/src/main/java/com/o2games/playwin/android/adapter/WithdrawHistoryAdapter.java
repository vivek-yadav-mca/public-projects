package dummydata.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dummydata.android.R;
import dummydata.android.model.WithdrawRequestData;

import java.util.List;

public class WithdrawHistoryAdapter extends RecyclerView.Adapter<WithdrawHistoryAdapter.WithdrawHistoryViewHolder> {

    Context context;
    List<WithdrawRequestData> withdrawRequestData;

    public WithdrawHistoryAdapter(Context context, List<WithdrawRequestData> withdrawRequestData) {
        this.context = context;
        this.withdrawRequestData = withdrawRequestData;
    }

    @NonNull
    @Override
    public WithdrawHistoryAdapter.WithdrawHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_rv_withdraw_history, null);
        return new WithdrawHistoryAdapter.WithdrawHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WithdrawHistoryAdapter.WithdrawHistoryViewHolder holder, int position) {
        WithdrawRequestData model = withdrawRequestData.get(position);

        holder.withdraw_date.setText(model.getDate());
        holder.withdraw_time.setText(model.getTime());
        holder.payment_mode.setText(model.getPaymentMode());
        holder.withdraw_amount.setText(model.getWithdrawAmount());
        holder.transaction_ID.setText(model.getTransactionId());

    }

    @Override
    public int getItemCount() {
        return withdrawRequestData.size();
    }

    public class WithdrawHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView withdraw_date;
        TextView withdraw_time;
        TextView payment_mode;
        TextView withdraw_amount;
        TextView transaction_ID;

        public WithdrawHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            withdraw_date = itemView.findViewById(R.id.history_withdraw_date);
            withdraw_time = itemView.findViewById(R.id.history_withdraw_time);
            payment_mode = itemView.findViewById(R.id.history_payment_mode);
            withdraw_amount = itemView.findViewById(R.id.history_withdraw_amount);
            transaction_ID = itemView.findViewById(R.id.history_withdraw_transactionID);

        }
    }

}