package com.annevonwolffen.androidschool.simpleaudiorecorder.data.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.annevonwolffen.androidschool.simpleaudiorecorder.data.model.RecordModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecordsProvider {

    private final Context mContext; //todo: remove ??? (not in use)


    public RecordsProvider(Context context) {
        mContext = context;
    }

    public void loadRecords(@NonNull OnLoadingFinishListener onLoadingFinishListener) {
        LoadingRecordsAsyncTask loadingRecordsAsyncTask = new LoadingRecordsAsyncTask(onLoadingFinishListener);
        loadingRecordsAsyncTask.execute();
    }



    private class LoadingRecordsAsyncTask extends AsyncTask<Void, Void, List<RecordModel>> {

        private final OnLoadingFinishListener mOnLoadingFinishListener;

        LoadingRecordsAsyncTask(@NonNull OnLoadingFinishListener onLoadingFinishListener) {
            mOnLoadingFinishListener = onLoadingFinishListener;
        }

        @Override
        protected void onPostExecute(List<RecordModel> recordModels) {
            super.onPostExecute(recordModels);

            mOnLoadingFinishListener.onFinish(recordModels);
        }

        @Override
        protected List<RecordModel> doInBackground(Void... voids) {
            return getData();
        }

        private List<RecordModel> getData() {
            // todo read dir where records from this app are being created
            File dir = new File(Environment.getExternalStorageDirectory() + "/SimpleAudioRecorder");
            // todo loop over all files in dir and creating record models on them (how to get duration ???)
            List<RecordModel> records = new ArrayList<>();
            if (dir.exists()) {
                for (String s : dir.list()) {
                    records.add(new RecordModel(s, s)); //todo: find out how to get other params from files - i need duration
                }
            }
            return records;
        }
    }


    public interface OnLoadingFinishListener {
        void onFinish(List<RecordModel> recordModels);
    }

}
