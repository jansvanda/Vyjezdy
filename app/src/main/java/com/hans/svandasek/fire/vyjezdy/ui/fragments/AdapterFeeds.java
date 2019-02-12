package com.hans.svandasek.fire.vyjezdy.ui.fragments;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hans.svandasek.fire.vyjezdy.R;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

public class AdapterFeeds extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<DataFeed> data= Collections.emptyList();
    DataFeed current;
    int currentPos=0;

    // create constructor to innitilize context and data sent from MainActivity
    public AdapterFeeds(Context context, List<DataFeed> data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.source_item, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView dName;
        //TextView dUrl;
        TextView dId;
        CardView mCardSource;


        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            dName = itemView.findViewById(R.id.text_view_source);
            dId = itemView.findViewById(R.id.text_view_id);
            mCardSource = itemView.findViewById(R.id.card_view_source);
            //dUrl = itemView.findViewById(R.id.text_view_url);

            this.dName = itemView.findViewById(R.id.text_view_source);


            // on item click
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
        DataFeed current = data.get(position);
        myHolder.dName.setText(current.district_name);
        //myHolder.dUrl.setText(current.district_url);
        myHolder.dId.setText(current.d_id);
    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }




}