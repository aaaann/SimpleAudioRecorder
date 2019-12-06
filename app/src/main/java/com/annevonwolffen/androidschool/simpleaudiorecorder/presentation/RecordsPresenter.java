package com.annevonwolffen.androidschool.simpleaudiorecorder.presentation;

import android.util.Log;

import androidx.annotation.NonNull;

import com.annevonwolffen.androidschool.simpleaudiorecorder.data.model.RecordModel;
import com.annevonwolffen.androidschool.simpleaudiorecorder.data.repository.RecordsProvider;
import com.annevonwolffen.androidschool.simpleaudiorecorder.util.ResourceWrapper;
import com.annevonwolffen.androidschool.simpleaudiorecorder.view.IMainView;
import com.annevonwolffen.androidschool.simpleaudiorecorder.view.IRecordRowView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecordsPresenter {
    private static final String TAG = "RecordsPresenter";

    private final WeakReference<IMainView> mMainActivityWeakReference;
    private final RecordsProvider mRecordsProvider;
    private final ResourceWrapper mResourceWrapper;
    private List<RecordModel> mRecords = new ArrayList<>();


    public RecordsPresenter(@NonNull IMainView mainView, @NonNull RecordsProvider provider, @NonNull ResourceWrapper resourceWrapper) {
        mMainActivityWeakReference = new WeakReference<>(mainView);
        mRecordsProvider = provider;
        mResourceWrapper = resourceWrapper;
    }


    public void loadData() {
        mRecordsProvider.loadRecords(new RecordsProvider.OnLoadingFinishListener() {
            @Override
            public void onFinish(List<RecordModel> recordModels) {
                mRecords = recordModels;
                mMainActivityWeakReference.get().showData();
            }
        });
    }

    public void onPlayStateChanged(boolean state, String filename) {
        for (RecordModel model : mRecords) {
            if (model.getName().equals(filename)) {
                model.setIsPlaying(state);
            }
        }
        mMainActivityWeakReference.get().showData();
    }


    /**
     * Метод для отвязки прикрепленной View.
     */
    public void detachView() {
        mMainActivityWeakReference.clear();
    }

    public void onBindRecordRowViewAtPosition(int position, IRecordRowView rowView) {
        RecordModel model = mRecords.get(position);
        rowView.setRecordName(model.getName());
        rowView.setRecordDuration(model.getDuration());
        rowView.setRecordButtonIcon(model.isPlaying() ? mResourceWrapper.getPauseIcon() : mResourceWrapper.getPlayIcon());
        rowView.setClickListener(position);
    }

    public int getRecordRowsCount() {
        return mRecords.size();
    }


    public void onRecordItemClicked(int position) {
        Log.d(TAG, "onRecordItemClicked() called with: position = [" + position + "]");
        RecordModel model = mRecords.get(position);
        if (model.isClicked()) {
            model.setIsPlaying(!model.isPlaying());
            mMainActivityWeakReference.get().pauseOrContinuePlay();
        } else {
            for (RecordModel record : mRecords) {
                record.setIsClicked(false);
                record.setIsPlaying(false);
            }
            model.setIsPlaying(true);
            model.setIsClicked(true);
            mMainActivityWeakReference.get().startPlay(model.getName());
        }

        mMainActivityWeakReference.get().showData();
    }

}