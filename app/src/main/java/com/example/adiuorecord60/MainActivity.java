package com.example.adiuorecord60;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    FrameLayout container;
    private static final int PERMISSION_REQ_COD = 100;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Bundle savedInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        savedInstance = savedInstanceState;

        checkForPermissions();


    }

    private void checkForPermissions() {
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            setFragment();
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQ_COD);

        }
    }


    private void setFragment() {
        if (savedInstance == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.my_container, new RecordFragment()
                    , String.valueOf(R.string.record_fragment_tag)).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQ_COD) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setFragment();
            } else {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQ_COD);
            }
        }
    }
}