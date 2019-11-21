package com.annevonwolffen.androidschool.simpleaudiorecorder.view;

import android.graphics.drawable.Drawable;

public interface IRecordRowView {
  void setRecordName(String name);
  void setRecordDuration(String stringDuration);
  void setRecordButtonIcon(Drawable icon);
  void setClickListener(int position);
}