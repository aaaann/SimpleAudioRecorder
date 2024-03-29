package com.annevonwolffen.androidschool.simpleaudiorecorder.view;

public interface IMainView {
  /**
   * show recorded audio-files from external storage
   */
  void showData();

  void startPlay(String fileName);

  void pauseOrContinuePlay();

}