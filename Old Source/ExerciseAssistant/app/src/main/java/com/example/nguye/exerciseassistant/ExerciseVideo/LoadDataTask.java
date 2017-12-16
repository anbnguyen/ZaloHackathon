package com.example.nguye.exerciseassistant.ExerciseVideo;

import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Ulfbert on 11/28/2016.
 */

public class LoadDataTask extends AsyncTask<Object, Integer, String> {
    Context context;
    LoadListener listener;
    ArrayList<ExerciseItem> list;

    public LoadDataTask(Context context, ArrayList<ExerciseItem> list) {
        this.context = context;
        this.listener = (ExerciseVideoActivity)context;
        this.list = list;
    }

    public LoadDataTask() {
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        Document doc = Jsoup.parse(s);
        Elements titlespans = doc.getElementsByClass("yt-lockup-content");
        Elements viewdates = doc.getElementsByClass("yt-lockup-meta");
        for(int i =0; i < 20; i++){
            ExerciseItem tmp = new ExerciseItem();
            Element titlespan =  titlespans.get(i);
            String href = titlespan.getElementsByClass("yt-lockup-title").get(0).childNode(0).attr("href");
            tmp.id = href.substring(9, href.length());
            tmp.thumbnailUrl = "http://img.youtube.com/vi/" + tmp.id + "/sddefault.jpg";
            tmp.title = titlespan.getElementsByClass("yt-lockup-title").get(0).childNode(0).attr("title");
            String hold = titlespan.getElementsByClass("yt-lockup-title").get(0).childNode(1).childNode(0).toString();
            tmp.duration = hold.substring(15, hold.length() - 1);
            Element viewdate = viewdates.get(i);
            hold = viewdate.childNode(1).childNode(0).toString();
            tmp.totalViewFormat = hold.substring(4, hold.length() - 5);
            hold = viewdate.childNode(1).childNode(1).toString();
            tmp.dateCreated = hold.substring(4, hold.length() - 5);
            list.add(tmp);
        }
        listener.doneLoading();
    }

    @Override
    protected String doInBackground(Object... params) {
        /*
        HttpGet httpGet = new HttpGet(params[0].toString());
        HttpEntity httpEntity = null;
        HttpClient httpClient = new DefaultHttpClient();

        try {
            httpEntity = httpClient.execute(httpGet).getEntity();
        } catch (ClientProtocolException e)
        {
            e.printStackTrace();
            return "";
        } catch (IOException e)
        {
            e.printStackTrace();
            return "";
        }

        if (httpEntity != null)
        {
            try {
                InputStream inputStream = httpEntity.getContent();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null)
                {

                    stringBuilder.append(line);
                }
                inputStream.close();
                Log.i("data", stringBuilder.toString());
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }

        return "";*/
        try {
            URL url = new URL((String) params[0]);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
            conn.connect();
            InputStream is = conn.getInputStream();
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            return buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
