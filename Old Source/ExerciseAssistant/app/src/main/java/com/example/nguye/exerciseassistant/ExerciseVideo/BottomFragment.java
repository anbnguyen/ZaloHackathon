package com.example.nguye.exerciseassistant.ExerciseVideo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.nguye.exerciseassistant.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ulfbert on 11/28/2016.
 */

public class BottomFragment extends Fragment {
    ArrayList<ExerciseItem> dataList;
    ItemAdapter adapter;
    @InjectView(R.id.textTitle)TextView txtTitle;
    @InjectView(R.id.textDetail)TextView txtDetail;
    @InjectView(R.id.recyclerRelated)RecyclerView recyclerView;
    String name;
    String detail;
    String videoID;

    public void setClass(int aClass) {
        Class = aClass;
    }

    int Class;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataList = new ArrayList<>();
        adapter = new ItemAdapter(getContext(), dataList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_fragment, container, false);
        ButterKnife.inject(this, view);
        txtTitle.setText(name);
        txtDetail.setText(detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                ExerciseItem tmp = dataList.get(position);
                if(Class == 1) {
                    ((ExerciseVideoActivity) getActivity()).Load(tmp.id);
                }
                else if(Class == 2)
                {
                    ((Recent_Video)getActivity()).Load(tmp.id);
                }
                change(tmp.id, tmp.title, tmp.totalViewFormat + " - " + tmp.duration);
            }
        });
        new LoadRelatedVideo(getContext(), dataList).execute("https://www.youtube.com/watch?v=" + videoID);
        return view;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void change(String ID, String name, String length){
        txtTitle.setText(name);
        txtDetail.setText(length);
        this.name = name;
        this.detail = length;
        this.videoID = ID;
        dataList.clear();
        adapter.notifyDataSetChanged();
        new LoadRelatedVideo(getContext(), dataList).execute("https://www.youtube.com/watch?v=" + videoID);
    }
    public void setLength(String detail) {
        this.detail = detail;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }
    public class LoadRelatedVideo extends LoadDataTask{

        public LoadRelatedVideo(Context context, ArrayList<ExerciseItem> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        protected void onPostExecute(String s) {
            Document doc = Jsoup.parse(s);
            Elements e = doc.getElementsByClass("content-wrapper");
            for(int i = 0; i < e.size(); i++){
                ExerciseItem tmp = new ExerciseItem();
                tmp.title = e.get(i).childNode(1).attr("title");
                String ID  = e.get(i).childNode(1).attr("href");
                tmp.id = ID.substring(9, ID.length());
                tmp.thumbnailUrl = "http://img.youtube.com/vi/" + tmp.id + "/sddefault.jpg";
                String time = e.get(i).childNode(1).childNode(3).childNode(0).toString();
                tmp.duration = time.substring(15, time.length());
                tmp.totalViewFormat = e.get(i).childNode(1).childNode(7).childNode(0).toString();
                tmp.dateCreated = "";
                dataList.add(tmp);
            }
            adapter.notifyDataSetChanged();
        }
    }
}
