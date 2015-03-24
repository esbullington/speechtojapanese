package com.ericbullington.speechtojapanese;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioRecorder implements Runnable {

    private static String TAG = "AudioRecorder";
    private boolean isRecording;
    private int audioBufferSize;
    private AudioRecord mRecorder;
    private String fileName;

    public AudioRecorder(int audioBufferSize, String fileName, AudioRecord mRecorder) {
        this.audioBufferSize = audioBufferSize;
        this.fileName = fileName;
        this.mRecorder = mRecorder;
    }

    public void setRecordingStatus(boolean _isRecording) {
        this.isRecording = _isRecording;
    }

    public void run() {
        // do whatever you want with data
        byte data[] = new byte[audioBufferSize];
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(fileName);
        } catch (FileNotFoundException ex) {
            Log.i(TAG, "Error writing audio to file: ", ex);
        }

        int read = 0;

        if(null != out){
            // Keep recording until user presses "stop" button
            while(isRecording){
                read = mRecorder.read(data, 0, audioBufferSize);

                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                    try {
                        out.write(data);
                    } catch (IOException ex) {
                        Log.i(TAG, "Error writing audio to file: ", ex);
                    }
                }
            }

            try {
                out.close();
            } catch (IOException ex) {
                Log.i(TAG, "Error writing audio to file: ", ex);
            }
        }
    }


}