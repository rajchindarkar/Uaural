package com.dc.msu.uaural;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.jean.jcplayer.JcPlayerManagerListener;
import com.example.jean.jcplayer.general.JcStatus;
import com.example.jean.jcplayer.general.errors.OnInvalidPathListener;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnInvalidPathListener, JcPlayerManagerListener {
    JcPlayerView jcPlayerView;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();
    String TAG = "JC";
    RecyclerView recyclerView;
    private AudioAdapter audioAdapter;
    int flag = 0;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait..");
        initView();//init views
        addAudio();//add all mp3 from server to JC audio array list
        new AddLocalMP3().execute();//Add all local mp3 files in JcAudio list


    }

    private void addAudio() {
        jcAudios.add(JcAudio.createFromURL("Maroon 5 - Girls like you", "https://firebasestorage.googleapis.com/v0/b/u-aural.appspot.com/o/Maroon%205%20-%20Girls%20Like%20You%20ft.%20Cardi%20B.mp3?alt=media&token=9382aa32-1129-4236-87b7-dd218efafc63"));
        jcAudios.add(JcAudio.createFromURL("Adele - Rolling in the Deep (Official Music Video)", "https://firebasestorage.googleapis.com/v0/b/u-aural.appspot.com/o/Adele%20-%20Rolling%20in%20the%20Deep%20(Official%20Music%20Video).mp3?alt=media&token=a5f60b99-5df1-40a0-9b91-026546a97db7"));
        jcAudios.add(JcAudio.createFromURL("Bryan Adams - The Best of Me", "https://firebasestorage.googleapis.com/v0/b/u-aural.appspot.com/o/Bryan%20Adams%20-%20The%20Best%20of%20Me.mp3?alt=media&token=82cc5a73-90a5-4f91-a8cc-c3dfd6586791"));
        jcAudios.add(JcAudio.createFromURL("CAN'T STOP THE FEELING! -JT", "https://firebasestorage.googleapis.com/v0/b/u-aural.appspot.com/o/CAN'T%20STOP%20THE%20FEELING!%20-JT.mp3?alt=media&token=5c3258b1-39fb-4200-815c-2fb084d05f1a"));
        jcAudios.add(JcAudio.createFromURL("Eagles - Hotel California", "https://firebasestorage.googleapis.com/v0/b/u-aural.appspot.com/o/Eagles%20-%20Hotel%20California.mp3?alt=media&token=31c36e9e-baa2-4b86-bd54-1acef4026548"));
        jcAudios.add(JcAudio.createFromURL("Ellie Goulding - Love Me Like You Do (Official Video)", "https://firebasestorage.googleapis.com/v0/b/u-aural.appspot.com/o/Ellie%20Goulding%20-%20Love%20Me%20Like%20You%20Do%20(Official%20Video).mp3?alt=media&token=e145943c-79fb-4937-9f2a-ba02cac5a3fd"));
        jcAudios.add(JcAudio.createFromURL("Jonas Brothers - Sucker (Official Video)", "https://firebasestorage.googleapis.com/v0/b/u-aural.appspot.com/o/Jonas%20Brothers%20-%20Sucker%20(Official%20Video).mp3?alt=media&token=27e5bad9-716b-416a-b21f-98ac578b29f4"));
        jcAudios.add(JcAudio.createFromURL("Michael Jackson - Black or White (Official Video)", "https://firebasestorage.googleapis.com/v0/b/u-aural.appspot.com/o/Michael%20Jackson%20-%20Black%20or%20White%20(Official%20Video).mp3?alt=media&token=a6d291ac-fb84-41b0-be6e-eff9cd570844"));
        jcAudios.add(JcAudio.createFromURL("Set Fire To The Rain", "https://firebasestorage.googleapis.com/v0/b/u-aural.appspot.com/o/Set%20Fire%20To%20The%20Rain.mp3?alt=media&token=6d01dde2-1323-48e5-911d-532b1eb459cd"));
        jcAudios.add(JcAudio.createFromURL("The Greatest Showman Cast - This Is Me (Official Audio)", "https://firebasestorage.googleapis.com/v0/b/u-aural.appspot.com/o/The%20Greatest%20Showman%20Cast%20-%20This%20Is%20Me%20(Official%20Audio).mp3?alt=media&token=25e3d81e-7ead-4860-89ec-aa065f28508c"));
        jcAudios.add(JcAudio.createFromURL("The Police - Every Breath You Take", "https://firebasestorage.googleapis.com/v0/b/u-aural.appspot.com/o/The%20Police%20-%20Every%20Breath%20You%20Take.mp3?alt=media&token=263084b9-9c25-4baa-b219-8c58776817f3"));

    }

    @Override
    protected void onStop() {
        jcPlayerView.createNotification();
        super.onStop();
    }

    protected void adapterSetup() {
        audioAdapter = new AudioAdapter(jcPlayerView.getMyPlaylist());
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                //Item click in list
                //Play selected song inside handler(thread)
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        jcPlayerView.playAudio(jcPlayerView.getMyPlaylist().get(position));

                    }
                });
            }


        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(audioAdapter);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

    }

    private void initView() {
        jcPlayerView = (JcPlayerView) findViewById(R.id.jcplayer);
        recyclerView = findViewById(R.id.recyclerView);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jcPlayerView.kill();
    }

    @Override
    public void onPathError(JcAudio jcAudio) {
        Toast.makeText(this, jcAudio.getPath() + " with problems", Toast.LENGTH_LONG).show();
//        player.removeAudio(jcAudio);
//        player.next();
    }


    @Override
    public void onCompletedAudio() {
        Log.d("music", "complete");
    }

    @Override
    public void onPaused(JcStatus status) {
        Log.d("music", "paused");

        jcPlayerView.createNotification();
        jcPlayerView.onTimeChanged(status);
    }

    @Override
    public void onContinueAudio(JcStatus status) {
        Log.d("music", "continue");
    }

    @Override
    public void onPlaying(JcStatus status) {
        jcPlayerView.createNotification();
        Log.d("music", "playing");
    }

    @Override
    public void onTimeChanged(@NonNull JcStatus status) {
        updateProgress(status);
        Log.d("music", "timeChange");
    }

    @Override
    public void onJcpError(@NonNull Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void updateProgress(final JcStatus jcStatus) {
        //update progress by thread

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // calculate progress
                float progress = (float) (jcStatus.getDuration() - jcStatus.getCurrentPosition())
                        / (float) jcStatus.getDuration();
                progress = 1.0f - progress;
                audioAdapter.updateProgress(jcStatus.getJcAudio(), progress);
            }
        });
    }


    @Override
    public void onStopped(JcStatus status) {
        Log.d("music", "stop");
    }

    @Override
    public void onPreparedAudio(JcStatus jcStatus) {
        Log.d("music", "prepare");
    }

    public void getAllAudioFromDevice(final Context context) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
            Cursor c = context.getContentResolver().query(uri, projection, MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%Download%"}, null);
            Log.d("cursorCount", String.valueOf(c.getCount()));
            if (c != null) {
                while (c.moveToNext()) {

                    String path = c.getString(0);
                    String album = c.getString(1);
                    String artist = c.getString(2);

                    String name = path.substring(path.lastIndexOf("/") + 1);


                    Log.d("Name :" + name, " Album :" + album);
                    Log.d("Path :" + path, " Artist :" + artist);

                    jcAudios.add(JcAudio.createFromFilePath(name, path));
                }
                c.close();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            new AddLocalMP3().execute();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllAudioFromDevice(this);
        adapterSetup();

    }

    @Override
    public void onBackPressed() {
        showExitPopup();//Exit dialog
    }

    private void showExitPopup() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void searchMP3(File dir) {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            File[] files = dir.listFiles();

            for (File file : files) {
                if (file.isFile() && isMP3(file)) {
                    jcAudios.add(JcAudio.createFromFilePath(file.getName(), file.getAbsolutePath()));
                    Log.i("TXT", file.getName());
                } else if (file.isDirectory()) {
                    searchMP3(file.getAbsoluteFile());
                }
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }

    }

    private boolean isMP3(File file) {
        boolean is = false;
        if (file.getName().endsWith(".mp3")) {
            is = true;
            Log.e("", "mp3 files -->" + is);
        }
        return is;
    }
    public class AddLocalMP3 extends AsyncTask{
        @Override
        protected void onPreExecute() {

            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                searchMP3(new File(Environment.getExternalStorageDirectory().getPath()));//add all mp3 from local storage to JC audio array list

            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            progressDialog.dismiss();
            jcPlayerView.initPlaylist(jcAudios, MainActivity.this);
            jcPlayerView.createNotification();
            adapterSetup();//show all songs in list
            super.onPostExecute(o);
        }
    }
}