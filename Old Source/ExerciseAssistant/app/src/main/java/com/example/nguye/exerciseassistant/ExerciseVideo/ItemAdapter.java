package com.example.nguye.exerciseassistant.ExerciseVideo;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.nguye.exerciseassistant.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ulfbert on 11/28/2016.
 */

public class ItemAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<ExerciseItem> listData;

    public ItemAdapter(Context context, ArrayList<ExerciseItem> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ExerciseItem item = listData.get(position);
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.title.setText(item.title);
        viewHolder.detail.setText(item.totalViewFormat + " - " + item.duration+ " - " + item.dateCreated);
        Picasso.with(context)
                .load(Uri.parse(item.thumbnailUrl))
                .into(viewHolder.thumbnail);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView detail;
        ImageView thumbnail;
        public ViewHolder(View itemView) {
            super(itemView);
            this.detail = (TextView)itemView.findViewById(R.id.textDetail);
            this.title = (TextView)itemView.findViewById(R.id.textTitle);
            this.thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
        }
    }
}
