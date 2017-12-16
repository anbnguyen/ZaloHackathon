package com.example.nguye.exerciseassistant.ExerciseVideo;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by KIRA on 28/4/2017.
 */

public class LoadDataRecent extends AsyncTask<ArrayList<String>,Context,ExerciseItem> {
    Context context;
    LoadListener listener;
    ArrayList<ExerciseItem> list;
    LoadDataRecent(Context context, ArrayList<ExerciseItem> dataList){
        this.context = context;
        listener = (Recent_Video)context;
        list = dataList;
    }

    @Override
    protected void onPostExecute(ExerciseItem exerciseItem) {
        listener.doneLoading();
    }

    @Override
    protected ExerciseItem doInBackground(ArrayList<String>... params) {
        for(int i = 0; i < params[0].size();++i) {
            ExerciseItem tmp = new ExerciseItem();
            tmp.id = params[0].get(i);
            try {
                String vidID = "https://www.youtube.com/watch?v=" + tmp.id;
                if (vidID != null) {
                    URL embededURL = new URL("http://www.youtube.com/oembed?url=" +
                            vidID + "&format=json"
                    );
                    tmp.title = new JSONObject(IOUtils.toString(embededURL)).getString("title");
                    tmp.thumbnailUrl = "http://img.youtube.com/vi/" + tmp.id + "/sddefault.jpg";
                    tmp.duration = "";
                    tmp.totalViewFormat = "";
                    tmp.dateCreated = "";
                    list.add(tmp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
