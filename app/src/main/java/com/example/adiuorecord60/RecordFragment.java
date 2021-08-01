package com.example.adiuorecord60;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordFragment extends Fragment implements View.OnClickListener {
    private Chronometer chronometer;
    private MaterialButton recordBtn;
    private ImageButton listBtn;
    private Boolean isRecording = false;
    private ValueAnimator valueAnimator;
    private MediaRecorder recorder;
    private String path;

    public RecordFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_record, container, false);

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        chronometer = rootView.findViewById(R.id.my_chronometer);
        recordBtn = rootView.findViewById(R.id.record_btn);
        listBtn = rootView.findViewById(R.id.voice_list_btn);

        recordBtn.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.recordbtn_anim));

        path = getActivity().getExternalFilesDir("/").getAbsolutePath();

        recordBtn.setOnClickListener(this);
        listBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == recordBtn.getId()) {
            if (!isRecording) {
                startRecording();
            } else {
                stopRecording();
            }

        }
        if (v.getId() == listBtn.getId()) {
            getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    //اولی ورود از فرگمت اول دومی خروج از فرگمنت اول سومی برگشت از فرگمت دوم چهارمی از بین رفتنشم با اسلاید اوت
                    .replace(R.id.my_container, new VoiceListFragment(), String.valueOf(R.string.voicelist_fragment_tag))
                    .addToBackStack(getTag()).commit();
        }
    }

    private void startRecording() {
        recordBtn.getAnimation().cancel();
        chronometer.setBase(SystemClock.elapsedRealtime());
        recordBtn.setText("Stop");

        recordingBuild();
        startRecordingAnimator();

    }

    private void recordingBuild() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.US);
        Date date = new Date();

        String name = "Recording  " + simpleDateFormat.format(date) + " .3gp";
        recorder.setOutputFile(path + "/" + name);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


        try {
            recorder.prepare();
            recorder.start();
            chronometer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startRecordingAnimator() {
        valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), ContextCompat.getColor(getActivity(), R.color.purple_500), ContextCompat.getColor(getActivity(), R.color.purple_700)
                , ContextCompat.getColor(getActivity(), R.color.accent));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                recordBtn.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();

        isRecording = true;
    }


    private void stopRecording() {
        valueAnimator.cancel();

        recordBtn.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.recordbtn_anim));

        stopAndSave();

        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        recordBtn.setText("Record");
        isRecording = false;
    }

    private void stopAndSave() {
        recorder.stop();
        recorder.release();//to save file
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null && isRecording) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }
}
