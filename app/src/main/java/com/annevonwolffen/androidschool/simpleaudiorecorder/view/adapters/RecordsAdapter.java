package com.annevonwolffen.androidschool.simpleaudiorecorder.view.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.annevonwolffen.androidschool.simpleaudiorecorder.R;
import com.annevonwolffen.androidschool.simpleaudiorecorder.data.repository.RecordsProvider;
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
                    .inflate(R.layout.record_item, parent, false), mPresenter);
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
    private final RecordsPresenter mPresenter;
    private TextView mRecordName;
    private TextView mRecordDuration;
    private Button mRecordButton;

    public RecordViewHolder(@NonNull View itemView, RecordsPresenter presenter) {
      super(itemView);

      mPresenter = presenter;
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

    @Override
    public void setRecordButtonIcon(Drawable icon) {
      mRecordButton.setBackground(icon);
    }

    @Override
    public void setClickListener(final int position) {
      mRecordButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mPresenter.onRecordItemClicked(position);
        }
      });
    }


  }

}