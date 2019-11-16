package com.annevonwolffen.androidschool.simpleaudiorecorder.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.annevonwolffen.androidschool.simpleaudiorecorder.R;
import com.annevonwolffen.androidschool.simpleaudiorecorder.presentation.RecordsPresenter;
import com.annevonwolffen.androidschool.simpleaudiorecorder.view.IRecordRowView;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.RecordViewHolder> {

    private final RecordsPresenter mPresenter;

    public RecordsAdapter(RecordsPresenter presenter) {
        mPresenter = presenter;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.record_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        mPresenter.onBindRecordRowViewAtPosition(position, holder);
    }

    @Override
    public int getItemCount() {
        return mPresenter.getRecordRowsCount();
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder
            implements IRecordRowView {
        private TextView mRecordName;
        private TextView mRecordDuration;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);

            mRecordName = itemView.findViewById(R.id.tv_record_name);
            mRecordDuration = itemView.findViewById(R.id.tv_record_duration);
        }


        @Override
        public void setRecordName(String name) {
            mRecordName.setText(name);
        }

        @Override
        public void setRecordDuration(String stringDuration) {
            mRecordDuration.setText(stringDuration);
        }
    }

}
