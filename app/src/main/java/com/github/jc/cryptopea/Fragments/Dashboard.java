package com.github.jc.cryptopea.Fragments;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jc.cryptopea.Adapters.RewardTransactionsAdapter;
import com.github.jc.cryptopea.Models.RewardTransactionItem;
import com.github.jc.cryptopea.R;
import com.github.jc.cryptopea.Utils.Constants;
import com.github.jc.cryptopea.Utils.CustomDividerItemDecoration;
import com.github.jc.cryptopea.Utils.RecyclerViewItemTouchListener;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class Dashboard extends Fragment implements RecyclerViewItemTouchListener.ItemClickListener {

    private View view;
    private RecyclerView rvTransactions;

    private RewardTransactionsAdapter rewardTransactionsAdapter;
    private List<RewardTransactionItem> rewardTransactionItemList = new ArrayList<>();

    public Dashboard() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        rvTransactions = view.findViewById(R.id.rvTransactions);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvTransactions.setLayoutManager(mLayoutManager);
        rvTransactions.setItemAnimator(new DefaultItemAnimator());
        rvTransactions.addItemDecoration(new CustomDividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL, 8));
        rewardTransactionsAdapter = new RewardTransactionsAdapter(getActivity(), rewardTransactionItemList);
        rvTransactions.setAdapter(rewardTransactionsAdapter);
        rvTransactions.addOnItemTouchListener(new RecyclerViewItemTouchListener.RecyclerItemTouchListener(getActivity(), rvTransactions, this));
        initializeDummyRewardItems();
        return view;
    }

    public void initializeDummyRewardItems() {
        RewardTransactionItem r1 = new RewardTransactionItem("id-00001", "You received ether from External Account", "Payment successful.", "05:25 PM", "03/21/2018", "+ 0.0000002346");
        rewardTransactionItemList.add(r1);
        r1 = new RewardTransactionItem("id-00002", "You received ether from External Account", "Payment successful.", "06:28 PM", "03/21/2018", "+ 0.0000002087");
        rewardTransactionItemList.add(r1);
        r1 = new RewardTransactionItem("id-00003", "You received ether from External Account", "Payment successful.", "06:31 PM", "03/21/2018", "+ 0.0000002458");
        rewardTransactionItemList.add(r1);
        r1 = new RewardTransactionItem("id-00004", "You sent ether to External Account", "Payment successful.", "06:41 PM", "03/21/2018", "+ 0.0000002136");
        rewardTransactionItemList.add(r1);
        r1 = new RewardTransactionItem("id-00005", "You received ether from External Account", "Payment successful.", "06:45 PM", "03/21/2018", "+ 0.0000003456");
        rewardTransactionItemList.add(r1);
        r1 = new RewardTransactionItem("id-00006", "You received ether from External Account", "Payment successful.", "07:27 PM", "03/21/2018", "+ 0.0000001236");
        rewardTransactionItemList.add(r1);
        r1 = new RewardTransactionItem("id-00007", "You received ether from External Account", "Payment successful.", "07:42 PM", "03/21/2018", "+ 0.0000004236");
        rewardTransactionItemList.add(r1);
        r1 = new RewardTransactionItem("id-00008", "You received ether from External Account", "Payment successful.", "08:25 PM", "03/21/2018", "+ 0.0000002231");
        rewardTransactionItemList.add(r1);
        r1 = new RewardTransactionItem("id-00009", "You received ether from External Account", "Payment successful.", "09:25 PM", "03/21/2018", "+ 0.0000004421");
        rewardTransactionItemList.add(r1);
        r1 = new RewardTransactionItem("id-00010", "You received ether from External Account", "Payment successful.", "11:25 PM", "03/21/2018", "+ 0.0000003792");
        rewardTransactionItemList.add(r1);
        rewardTransactionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view, int position) {
        Constants.showSnackMessage(getActivity(), "Position: " + position + " --- " + ((TextView) view.findViewById(R.id.tvValue)).getText().toString(), false);
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}