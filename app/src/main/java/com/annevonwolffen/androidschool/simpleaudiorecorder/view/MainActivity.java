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

public class MainActivity extends AppCompatActivity implements IMainView{

    private static final int READ_EXT_STORAGE_REQ_CODE = 1;
    private static final int WRITE_EXT_STORAGE_REQ_CODE = 2;
    public static final String EXTRA_FILENAME = "filename";

    private RecordsAdapter mAdapter;
    private RecordsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        providePresenter();
        initRecyclerView();

        FloatingActionButton startRecordButton = findViewById(R.id.fab_start_record);
        startRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // запросить разрешение на доступ к external storage
                // если есть или принято, то создать файл и начать запись (стартовать сервис)
                //prepareForStartRecord();
            }
        });
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

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXT_STORAGE_REQ_CODE :{
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
                    //startRecord();
                    Toast.makeText(getApplicationContext(), "You grant write external storage permission.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void providePresenter() {
        RecordsProvider recordsProvider = new RecordsProvider(this);
        mPresenter = new RecordsPresenter(this, recordsProvider);
    }

//    private void prepareForStartRecord() {
//        // check permission for write in external storage
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            // start foreground service
//            startRecord();
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXT_STORAGE_REQ_CODE);
//        }
//    }


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
        startService(createFileForRecord());
    }

    private String createFileForRecord() {
        return Environment.getExternalStorageDirectory() + "/record.mp4/";
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
