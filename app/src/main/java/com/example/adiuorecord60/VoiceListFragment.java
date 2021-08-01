package com.example.adiuorecord60;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class VoiceListFragment extends Fragment implements View.OnClickListener, RecyclerAdapter.RecyclerViewOnItemClick, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private BottomSheetBehavior behavior;
    private View bottomSheet;
    private ImageButton play_pause;
    private SeekBar seekBar;
    private TextView voiceName;
    private String path;
    private File directory;
    private File currentFile;
    private File[] files;
    private MediaPlayer player;
    private Handler handler = new Handler();
    private Timer timer;
    private TextView timerText;
    private TextView timeNow;



    public VoiceListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_voice_list, container, false);
        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        directory = new File(path);
        files = directory.listFiles();

        adapter = new RecyclerAdapter(files);

        bottomSheet = rootView.findViewById(R.id.my_bottomSheet);
        play_pause = rootView.findViewById(R.id.bottomSheet_btn);
        seekBar = rootView.findViewById(R.id.bottomSheet_seekBar);
        voiceName = rootView.findViewById(R.id.bottomSheet_text);
        recyclerView = rootView.findViewById(R.id.voice_list);
        timerText = rootView.findViewById(R.id.TimerText_BottomSheep);
        timeNow = rootView.findViewById(R.id.timeNow);

        behavior = BottomSheetBehavior.from(bottomSheet);


        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (player.isPlaying()) {
            player.pause();
            play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);

        } else {
            player.start();
            play_pause.setImageResource(R.drawable.ic_baseline_pause_24);


        }
    }

    @Override
    public void onItemClickListener(File file, int position) {
        currentFile = file;
        play_pause.setOnClickListener(this);
        if (player != null && player.isPlaying()) {
            player.stop();
        }
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        try {

            player.setDataSource(file.getAbsolutePath());
            player.prepare();
            player.start();
            play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            voiceName.setText(file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekBar.setMax(player.getDuration());//تایم رو برمیگردونه
        seekBar.setOnSeekBarChangeListener(this);

        int h=player.getDuration()%3600000;
        String H=String.valueOf(player.getDuration()/3600000);
        String M=String.valueOf(h/60000);
        int s=h%60000;
        String S = String.valueOf(s/1000);


        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new MyTimerTask(), 0, 500);



        timerText.setText(" / "+ H+":"+M+":"+S);


    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.stop();
        mp.reset();
        play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        try {
            mp.setDataSource(currentFile.getAbsolutePath());
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            player.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    seekBar.setProgress(player.getCurrentPosition());

                    int h=player.getCurrentPosition()%3600000;
                    String H=String.valueOf(player.getCurrentPosition()/3600000);
                    String M=String.valueOf(h/60000);
                    int s=h%60000;
                    String S = String.valueOf(s/1000);
                    timeNow.setText( H+":"+M+":"+S);
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null && timer == null) {
            player.stop();
            player.reset();
            player = null;
            timer.purge();
            timer.cancel();
        }
    }
}
