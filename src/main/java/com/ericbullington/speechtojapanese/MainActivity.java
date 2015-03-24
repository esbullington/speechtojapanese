package com.ericbullington.speechtojapanese;


import android.app.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

// Approach to recording to audio sampling inspired by article "Audio Recording in .wav format
// http://www.edumobile.org/android/android-development/audio-recording-in-wav-format-in-android-programming/
// Actual WAV file headers taken from AOSP project and licensed under Apache
@SuppressWarnings("ResultOfMethodCallIgnored")
public class MainActivity extends Activity {
    
    private static final String TAG="MainActivity";

    public final static String EXTRA_MESSAGE = "com.ericbullington.speechtojapanese.MESSAGE";
    
    private static final String FILE_EXTENSION = ".wav";
    private static final String FILE_DIRECTORY = "speechtojapanese";
    private static final String FILE_NAME = "temp.audio";
    private static final int BPP = 16;
    private static final int SAMPLE_RATE = 44100;
    private static final int WAV_FORMAT_PCM = AudioFormat.ENCODING_PCM_16BIT;

    private Context mContext = null;
    private AudioRecord mRecorder = null;
    private int audioBufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Context
        mContext = getApplicationContext();
        
        // UI
        setContentView(R.layout.main);

        // Set audio buffer size for given sample rate
        audioBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        
        // Set click listener
        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColoredButton btn = (ColoredButton) findViewById(R.id.btnStart);
                if (isRecording) {
                    v.clearAnimation();
                    btn.setColorGreen();
                    Log.i(TAG, "Recording stopped.");
                    stopRecording();
                } else {
                    btn.setColorRed();
                    Animation mAnimation = new AlphaAnimation(1, 0);
                    mAnimation.setDuration(600);
                    mAnimation.setInterpolator(new StepInterpolator(0.5f));
                    mAnimation.setRepeatCount(Animation.INFINITE);
                    mAnimation.setRepeatMode(Animation.REVERSE);
                    btn.startAnimation(mAnimation);
                    Log.i(TAG, "Now recording...");
                    startRecording();
                }
            }
        });
    }

    private String getFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,FILE_DIRECTORY);

        if(!file.exists()){
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + FILE_EXTENSION);
    }

    private String getTempFilename(){

        String filepath = Environment.getExternalStorageDirectory().getPath();

        File fileDir = new File(filepath,FILE_DIRECTORY);

        if(!fileDir.exists()){
            fileDir.mkdirs();
        }

        File file = new File(filepath,FILE_NAME);

        if(file.exists())
            file.delete();

        return (fileDir.getAbsolutePath() + "/" + FILE_NAME);
    }

    private void startRecording(){
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, WAV_FORMAT_PCM, audioBufferSize);

        if(mRecorder.getState() == 1)
            mRecorder.startRecording();

        synchronized (this) {
            isRecording = true;
        }

        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeTempAudio();
            }
        });

        recordingThread.start();
    }

    private void writeTempAudio(){

        byte data[] = new byte[audioBufferSize];
        String filename = getTempFilename();
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(filename);
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

    private void stopRecording(){
        if(null != mRecorder){

            synchronized (this) {
                isRecording = false;
            }

            if(mRecorder.getState() == 1)
                mRecorder.stop();

            mRecorder.release();

            mRecorder = null;
            recordingThread = null;
        }

        postWaveFile(getTempFilename(), getFilename());
        new File(getTempFilename()).delete();
    }


    private void postWaveFile(String inFilename,String outFilename){

        FileInputStream in = null;
        FileOutputStream out = null;
        int channelNumber = 2;
        int audioLength = 0;
        int dataLength = 36;

        byte[] data = new byte[audioBufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            audioLength = (int) in.getChannel().size();
            dataLength = audioLength + 36;

            byte[] header = makeWavHeader(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, channelNumber, dataLength);

            out.write(header, 0, 44);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();

            new PostSample(this).execute(outFilename);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // makeWaveHeader From AOSP
    // https://github.com/android/platform_frameworks_base/blob/master/core/java/android/speech/tts/FileSynthesisCallback.java
    // Apache License, Version 2.0
    private byte[] makeWavHeader(int SAMPLE_RATEInHz, int audioFormat, int channelCount,
        int dataLength) throws IOException {

        final int WAV_HEADER_LENGTH = 44;

        int sampleSizeInBytes = (audioFormat == AudioFormat.ENCODING_PCM_8BIT ? 1 : 2);
        int byteRate = SAMPLE_RATEInHz * sampleSizeInBytes * channelCount;
        short blockAlign = (short) (sampleSizeInBytes * channelCount);
        short bitsPerSample = (short) (sampleSizeInBytes * 8);

//        byte[] header = new byte[44];

        byte[] headerBuf = new byte[WAV_HEADER_LENGTH];
        ByteBuffer header = ByteBuffer.wrap(headerBuf);
        header.order(ByteOrder.LITTLE_ENDIAN);

        header.put(new byte[]{ 'R', 'I', 'F', 'F' });
        header.putInt(dataLength + WAV_HEADER_LENGTH - 8);  // RIFF chunk size
        header.put(new byte[]{ 'W', 'A', 'V', 'E' });
        header.put(new byte[]{ 'f', 'm', 't', ' ' });
        header.putInt(16);  // size of fmt chunk
        header.putShort((short) WAV_FORMAT_PCM);
        header.putShort((short) channelCount);
        header.putInt(SAMPLE_RATEInHz);
        header.putInt(byteRate);
        header.putShort(blockAlign);
        header.putShort(bitsPerSample);
        header.put(new byte[]{ 'd', 'a', 't', 'a' });
        header.putInt(dataLength);
        return headerBuf;
    }

}
