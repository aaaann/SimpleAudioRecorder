package com.annevonwolffen.androidschool.simpleaudiorecorder.presentation;

import androidx.annotation.NonNull;

import com.annevonwolffen.androidschool.simpleaudiorecorder.data.model.RecordModel;
import com.annevonwolffen.androidschool.simpleaudiorecorder.data.repository.RecordsProvider;
import com.annevonwolffen.androidschool.simpleaudiorecorder.view.IMainView;
import com.annevonwolffen.androidschool.simpleaudiorecorder.view.IRecordRowView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecordsPresenter {

    private final WeakReference<IMainView> mMainActivityWeakReference;
    private final RecordsProvider mRecordsProvider;
    private List<RecordModel> mRecords = new ArrayList<>();

    public RecordsPresenter(@NonNull IMainView mainView, @NonNull RecordsProvider provider) {
        mMainActivityWeakReference = new WeakReference<>(mainView);
        mRecordsProvider = provider;
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
    }

    public int getRecordRowsCount() {
        return mRecords.size();
    }
}
