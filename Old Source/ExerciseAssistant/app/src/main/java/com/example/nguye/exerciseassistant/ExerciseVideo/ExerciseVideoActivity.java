package com.example.nguye.exerciseassistant.ExerciseVideo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.nguye.exerciseassistant.Login_Register.ConnectionClass;
import com.example.nguye.exerciseassistant.R;
import com.example.nguye.exerciseassistant.GlobalVariables;
import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

public class ExerciseVideoActivity extends FragmentActivity implements LoadListener, ChangeTopFragmentVideo{
    ProgressDialog progressDialog;
    String BLENDERFIT = "https://www.youtube.com/user/FitnessBlender/videos";
    String BEFIT = "https://www.youtube.com/user/BeFit/videos";
    RecyclerView recyclerView;
    DraggablePanel draggablePanel;
    ArrayList<ExerciseItem> dataList;
    ItemAdapter adapter;
    BottomFragment bottomFragment;
    static int views = 0;
    boolean load2Times = false;
    int current = -1;


    YouTubePlayerSupportFragment youtubeFragment;
    YouTubePlayer youtubePlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initComponent();
        new LoadDataTask(this, dataList).execute(BLENDERFIT);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading video");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void initComponent() {
        draggablePanel = (DraggablePanel)findViewById(R.id.draggablePanel);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();
        adapter = new ItemAdapter(this, dataList);
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener(){
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                ExerciseItem tmp = dataList.get(position);
                if(current == position && draggablePanel.isMinimized())
                    draggablePanel.maximize();
                else if(current == -1)
                {
                    initializeYoutubeFragment(tmp.id);

                    draggablePanel.setFragmentManager(getSupportFragmentManager());
                    draggablePanel.setTopFragment(youtubeFragment);
                    bottomFragment = new BottomFragment();
                    bottomFragment.setVideoID(tmp.id);
                    bottomFragment.setName(tmp.title);
                    bottomFragment.setClass(1);
                    bottomFragment.setLength(tmp.totalViewFormat + " - " + tmp.duration);
                    draggablePanel.setBottomFragment(bottomFragment);
                    draggablePanel.initializeView();
                    hookDraggablePanelListeners();
                    current = position;
                }
                else{
                    bottomFragment.change(tmp.id, tmp.title, tmp.totalViewFormat + " - " + tmp.duration);
                    youtubePlayer.loadVideo(tmp.id);
                    draggablePanel.maximize();
                }
            }
        });
    }
    @Override
    public void doneLoading() {
        if(load2Times){
            Collections.sort(dataList);
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();

        }
        else {
            new LoadDataTask(this, dataList).execute(BEFIT);
            load2Times = true;
        }

    }
    private void initializeYoutubeFragment(final String videoID) {
        youtubeFragment = new YouTubePlayerSupportFragment();
        youtubeFragment.initialize(getResources().getString(R.string.YTube_Key), new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    youtubePlayer = player;
                    //  views = LoadViewfromServer();
                    //Post videoID lên server
                    PostVideoURLtoServer(videoID);
                    youtubePlayer.loadVideo(videoID);

                    Log.d("VideoID:",videoID);
                    youtubePlayer.setShowFullscreenButton(true);
                }
            }



            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult error) {
            }
        });
    }

    private void PostVideoURLtoServer(final String videoID) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Login....", "Logging in, please wait...", true);
        final AsyncTask asyncLogin = new AsyncTask()
        {
            Boolean isSuccess = false;
            String result =  " ";
            Context context;
            ConnectionClass connectionClass = new ConnectionClass();
            Connection con = connectionClass.connectionClass();
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Object o)
            {
                super.onPostExecute(o);
                progressDialog.dismiss();

                //Close connection
                try
                {
                    con.close();
                }
                catch (Exception e)
                {
                    Log.e("ERROR: ", e.getMessage());
                }

                Toast.makeText(ExerciseVideoActivity.this, result, Toast.LENGTH_LONG).show();

            }

            @Override
            protected Object doInBackground(Object[] params)
            {
                if(videoID.trim().equals(""))
                    result = "Enter VideoID";
                else
                {
                    try
                    {
                        if (con == null)
                        {
                            result = "Check Your Internet Access!";
                        }
                        else
                        {
                            // Change below query according to your own database.
                            String query =
                                    "IF NOT EXISTS(SELECT '" + GlobalVariables.username + "' FROM ViewInfo WHERE username = '"+GlobalVariables.username+"' AND videoID = '" + videoID + "')" +
                                            "INSERT INTO ViewInfo\n" +
                                            "(username,videoID,viewCount)\n" +
                                            "VALUES\n" +
                                            "(" + "'" + GlobalVariables.username + "','" + videoID + "','1')\n" +
                                            "ELSE UPDATE ViewInfo\n" +
                                            "SET viewCount = viewCount + 1\n" +
                                            "WHERE videoID = '" +videoID+"'\n";
                            ;
                            Statement stmt = con.createStatement();
                            int rs = stmt.executeUpdate(query);
                            Log.d("DEBUGrs: ", String.valueOf(rs));
                            if(rs > 0)
                            {
                                result = "Insert Successful";
                                isSuccess=true;
                                con.close();
                            }
                            else
                            {
                                result = "Cant insert Video";
                                isSuccess = false;
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        isSuccess = false;
                        result = ex.getMessage();
                    }
                }
                return result;
            }
        };

        asyncLogin.execute();
    }

    private int LoadViewfromServer() {
        int v = 0;
        //load
        return v;
    }

    private void hookDraggablePanelListeners() {
        draggablePanel.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {
                youtubePlayer.play();
            }

            @Override
            public void onMinimized() {
            }

            @Override
            public void onClosedToLeft() {
                youtubePlayer.release();
            }

            @Override
            public void onClosedToRight() {
                youtubePlayer.release();
            }
        });
    }

    @Override
    public void Load(String ID) {
        youtubePlayer.loadVideo(ID);
        PostVideoURLtoServer(ID);
    }

    @Override
    public void onBackPressed() {
        if(draggablePanel.isActivated()){
            if(draggablePanel.isMaximized())
                draggablePanel.minimize();
        }
        else
            super.onBackPressed();
    }
}
