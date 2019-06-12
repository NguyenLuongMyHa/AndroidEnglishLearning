package com.myha.toeicwords.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.myha.toeicwords.R;
import com.myha.toeicwords.WordMeaningActivity;
import com.myha.toeicwords.beans.History;

import java.util.ArrayList;


public class RecyclerViewAdapterHistory extends RecyclerView.Adapter<RecyclerViewAdapterHistory.HistoryViewHolder>
{

    private ArrayList<History> histories;
    private Context context;

    public RecyclerViewAdapterHistory(Context context, ArrayList<History> histories)
    {
        this.histories = histories;
        this.context = context;
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder
    {
        TextView enWord;
        TextView viDef;


        public HistoryViewHolder(View v)
        {
            super(v);
            enWord = (TextView) v.findViewById(R.id.en_word);
            viDef = (TextView) v.findViewById(R.id.vi_def);

            //Bat su kien nhan vao mot tu trong list tu dien
            v.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int position = getAdapterPosition();
                    String text = histories.get(position).get_en_word();
                    //Chuan bi tu en_word de Intent nho Bundle chuyen tu do qua WordMeaningActivity
                    Intent intent = new Intent(context, WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word", text);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }
    }


    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_layout, parent, false);
        return new HistoryViewHolder(view);
    }


    @Override
    public void onBindViewHolder(HistoryViewHolder holder, final int position)
    {
        holder.enWord.setText(histories.get(position).get_en_word());
        holder.viDef.setText(histories.get(position).get_def());

    }

    @Override
    public int getItemCount()
    {
        return histories.size();
    }
}
