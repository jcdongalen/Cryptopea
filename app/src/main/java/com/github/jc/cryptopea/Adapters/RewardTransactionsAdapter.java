package com.github.jc.cryptopea.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jc.cryptopea.Models.RewardTransactionItem;
import com.github.jc.cryptopea.R;

import java.util.List;

/**
 * Created by Owner on 3/21/2018.
 */

public class RewardTransactionsAdapter extends RecyclerView.Adapter<RewardTransactionsAdapter.ViewHolder> {

    private Context mContext;
    private List<RewardTransactionItem> rewardTransactionItemList;

    public RewardTransactionsAdapter(Context context, List<RewardTransactionItem> rewardTransactionItemList){
        this.mContext = context;
        this.rewardTransactionItemList = rewardTransactionItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward_transaction, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RewardTransactionItem item = rewardTransactionItemList.get(position);
        holder.imgLogo.setImageResource(R.drawable.ic_eth);
        holder.tvRemarks.setText(item.getRemarks());
        holder.tvStatus.setText(item.getStatus());
        holder.tvDate.setText(item.getDate());
        holder.tvTime.setText(item.getTime());
        holder.tvValue.setText(item.getValue());

        if(item.getValue().contains("-"))
            holder.tvValue.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
        else
            holder.tvValue.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
    }

    @Override
    public int getItemCount() {
        return rewardTransactionItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgLogo;
        public TextView tvRemarks, tvStatus, tvValue, tvTime, tvDate;
        public ViewHolder(View itemView) {
            super(itemView);
            imgLogo = itemView.findViewById(R.id.imgLogo);
            tvRemarks = itemView.findViewById(R.id.tvRemarks);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvValue = itemView.findViewById(R.id.tvValue);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }


}
