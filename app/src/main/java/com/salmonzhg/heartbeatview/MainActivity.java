package com.salmonzhg.heartbeatview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.salmonzhg.heartbeatview.views.DigitalGroupView;
import com.salmonzhg.heartbeatview.views.HeartbeatView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private HeartbeatView mHeartbeatView;
    private RecyclerView mHeartbeatRecycler;
    private List<HeartbeatEntity> mData = new ArrayList<>();
    private HeartbeatAdapter mAdapter;
    private DigitalGroupView mDigiResult;
    private TextView mTextUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHeartbeatView = (HeartbeatView) findViewById(R.id.heartbeat);
        mDigiResult = (DigitalGroupView) findViewById(R.id.digi_heartbeat_result);
        mTextUnit = (TextView) findViewById(R.id.text_unit);

        mHeartbeatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeartbeatView.startAnim();

                hideResult();
            }
        });
        mHeartbeatView.setHeartBeatAnimListener(new HeartbeatView.HeartBeatAnimImpl() {
            @Override
            public void onAnimFinished() {
                int randomNum = (int) (50 + Math.random() * 50);
                HeartbeatEntity e = new HeartbeatEntity();
                e.date = "2016-06-27";
                e.datum = String.valueOf(randomNum);
                mData.add(0, e);
                mAdapter.notifyItemInserted(0);
                mHeartbeatRecycler.scrollToPosition(0);

                showResult();
                mDigiResult.setDigits(randomNum);
            }
        });

        genData();
        mHeartbeatRecycler = (RecyclerView) findViewById(R.id.recycler_heartbeat);
        mHeartbeatRecycler.setLayoutManager(new LinearLayoutManager(this));
        mHeartbeatRecycler.setItemAnimator(new DefaultItemAnimator());
        mHeartbeatRecycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mAdapter = new HeartbeatAdapter();
        mHeartbeatRecycler.setAdapter(mAdapter);
    }

    private void genData() {
        for (int i = 5; i > 0; i--) {
            HeartbeatEntity e = new HeartbeatEntity();
            e.date = "2016-0" + i + "-27";
            e.datum = String.valueOf((int) (50 + Math.random() * 50));
            mData.add(e);
        }
    }

    class HeartbeatAdapter extends RecyclerView.Adapter<HeartbeatAdapter.HeartbeatHolder> {
        @Override
        public HeartbeatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            HeartbeatHolder holder = new HeartbeatHolder(LayoutInflater.from(MainActivity.this).
                    inflate(R.layout.item_heartbeat, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(HeartbeatHolder holder, int position) {
            holder.textDate.setText(mData.get(position).date);
            holder.textDatum.setText(mData.get(position).datum);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class HeartbeatHolder extends RecyclerView.ViewHolder {
            TextView textDate;
            TextView textDatum;

            public HeartbeatHolder(View itemView) {
                super(itemView);
                textDate = (TextView) itemView.findViewById(R.id.date);
                textDatum = (TextView) itemView.findViewById(R.id.datum);
            }
        }
    }

    private void hideResult() {
        AlphaAnimation mHiddenAction = new AlphaAnimation(1f, 0f);
        mHiddenAction.setDuration(400);

        mTextUnit.setAnimation(mHiddenAction);
        mDigiResult.setAnimation(mHiddenAction);

        mTextUnit.setVisibility(View.GONE);
        mDigiResult.setVisibility(View.GONE);
    }

    private void showResult() {
        mTextUnit.setVisibility(View.VISIBLE);
        mDigiResult.setVisibility(View.VISIBLE);
    }

    class HeartbeatEntity {
        String date;
        String datum;
    }
}
