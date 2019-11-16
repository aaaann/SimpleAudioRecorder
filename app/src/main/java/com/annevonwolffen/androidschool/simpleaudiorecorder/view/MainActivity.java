package com.annevonwolffen.androidschool.simpleaudiorecorder.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.annevonwolffen.androidschool.simpleaudiorecorder.R;
import com.annevonwolffen.androidschool.simpleaudiorecorder.data.repository.RecordsProvider;
import com.annevonwolffen.androidschool.simpleaudiorecorder.presentation.RecordsPresenter;
import com.annevonwolffen.androidschool.simpleaudiorecorder.view.adapters.RecordsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class MainActivity extends AppCompatActivity implements IMainView {

    private static final int READ_EXT_STORAGE_REQ_CODE = 1;
    private static final int WRITE_EXT_STORAGE_REQ_CODE = 2;
    private static final int RECORD_AUDIO_REQ_CODE = 3;
    public static final String EXTRA_FILENAME = "filename";

    private RecordsAdapter mAdapter;
    private RecordsPresenter mPresenter;

    private FloatingActionButton startRecordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        providePresenter();
        initRecyclerView();

        startRecordButton = findViewById(R.id.fab_start_record);
//        startRecordButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // запросить разрешение на доступ к external storage
//                // если есть или принято, то создать файл и начать запись (стартовать сервис)
//                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    if (isExternalStorageReadable()) {
//                        startRecord();
//                    }
//                } else {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXT_STORAGE_REQ_CODE);
//                }
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (isExternalStorageReadable()) {
                mPresenter.loadData();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXT_STORAGE_REQ_CODE);
        }


        startRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // запросить разрешение на доступ к external storage
                // если есть или принято, то создать файл и начать запись (стартовать сервис)
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (isExternalStorageReadable()) {
                        startRecord();
                    }
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXT_STORAGE_REQ_CODE);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXT_STORAGE_REQ_CODE: {
                int grantResultsLength = grantResults.length;
                if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isExternalStorageReadable()) {
                        mPresenter.loadData();
                    }
                    Toast.makeText(getApplicationContext(), "You grant read external storage permission.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You denied read external storage permission.", Toast.LENGTH_LONG).show();
                }
            }
            case WRITE_EXT_STORAGE_REQ_CODE: {
                int grantResultsLength = grantResults.length;
                if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //start record, dh start service
                    startRecord();
                    Toast.makeText(getApplicationContext(), "You grant write external storage permission.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
                }
            }

            case RECORD_AUDIO_REQ_CODE: {
                int grantResultsLength = grantResults.length;
                if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //start record, dh start service
                    startService(createFileForRecord());
                    Toast.makeText(getApplicationContext(), "You grant record audio permission.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You denied record audio permission.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void providePresenter() {
        RecordsProvider recordsProvider = new RecordsProvider(this);
        mPresenter = new RecordsPresenter(this, recordsProvider);
    }


    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new
                LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mAdapter = new RecordsAdapter(mPresenter);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void startRecord() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            if (isExternalStorageReadable()) {
                startService(createFileForRecord());
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_REQ_CODE);
        }
    }

    private String createFileForRecord() {
        File folder = new File(Environment.getExternalStorageDirectory() + "/SimpleAudioRecorder");
        File file = new File(folder + "/record_" + System.currentTimeMillis() + ".mp4");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return file.getAbsolutePath();//Environment.getExternalStorageDirectory() + "/SimpleAudioRecorder/record_" + System.currentTimeMillis();
    }

    private void startService(String filename) {
        Intent intent = new Intent(MainActivity.this, RecordAudioService.class);
        intent.putExtra(EXTRA_FILENAME, filename);
        startService(intent);
    }

    @Override
    public void showData() {
        mAdapter.notifyDataSetChanged();
    }
}
