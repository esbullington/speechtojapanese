package com.ericbullington.speechtojapanese;


import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// Approach to recording to audio sampling inspired by article "Audio Recording in .wav format" at:
// http://www.edumobile.org/android/android-development/audio-recording-in-wav-format-in-android-programming/
// Actual WAV file header write method taken directly from AOSP project and licensed under Apache
@SuppressWarnings("ResultOfMethodCallIgnored")
public class AudioRecorder {

    // Class constants
    private static final String TAG="AudioRecorder";

    // Audio file config
    private static final String FILE_EXTENSION = ".wav";
    private static final String FILE_DIRECTORY = "speechtojapanese";
    private static final String FILE_NAME = "temp.audio";

    // Audio constants
    private static final int ENCODING_PCM_16BIT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int CHANNEL_IN_MONO = AudioFormat.CHANNEL_IN_MONO;
    private static final int SAMPLE_RATEInHz = 44100;

    private Context mContext = null;
    private AudioRecord mRecorder = null;
    private int mAudioBufferSize = 0;
    private Thread mRecordingThread = null;
    private boolean mIsRecording = false;
    
    public AudioRecorder(Context mContext) {

        this.mContext = mContext;

        // Set audio buffer size for given sample rate
        mAudioBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATEInHz, CHANNEL_IN_MONO,
                ENCODING_PCM_16BIT);

    }

    public File getFileDirectory() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        return new File(filepath, FILE_DIRECTORY);
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

    public void start(){
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATEInHz,
                CHANNEL_IN_MONO, ENCODING_PCM_16BIT, mAudioBufferSize);

        if(mRecorder.getState() == 1)
            mRecorder.startRecording();

        synchronized (this) {
            mIsRecording = true;
        }

        mRecordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeTempAudio();
            }
        });

        mRecordingThread.start();
    }

    private void writeTempAudio(){

        // Buffer size set in constructor by AudioRecord
        byte data[] = new byte[mAudioBufferSize];
        String filename = getTempFilename();
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(filename);
        } catch (FileNotFoundException ex) {
            Log.e(TAG, "Error writing audio to file: ", ex);
        }

        int read = 0;

        if(null != out){
            // Keep recording until user presses "stop" button
            while(mIsRecording){
                read = mRecorder.read(data, 0, mAudioBufferSize);

                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                    try {
                        out.write(data);
                    } catch (IOException ex) {
                        Log.e(TAG, "Error writing audio to file: ", ex);
                    }
                }
            }

            try {
                out.close();
            } catch (IOException ex) {
                Log.e(TAG, "Error writing audio to file: ", ex);
            }
        }
    }

    public void stop(){
        if(null != mRecorder){

            synchronized (this) {
                mIsRecording = false;
            }

            if(mRecorder.getState() == 1)
                mRecorder.stop();

            mRecorder.release();

            mRecorder = null;
            mRecordingThread = null;
        }

        postWaveFile(getTempFilename(), getFilename());
        new File(getTempFilename()).delete();
    }


    private void postWaveFile(String inFilename,String outFilename){

        FileInputStream in = null;
        FileOutputStream out = null;
        int channelCount = 2;
        int audioLength = 0;
        // Will always be at least 44 bytes long since that's how long the header is
        int dataLength = 44;

        byte[] data = new byte[mAudioBufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            audioLength = (int) in.getChannel().size();
            dataLength = audioLength - 44;

            byte[] header = makeWavHeader(SAMPLE_RATEInHz, CHANNEL_IN_MONO, channelCount, dataLength);

            out.write(header, 0, 44);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();

            new PostSample(mContext).execute(outFilename);

        } catch (IOException ex) {
            Log.e(TAG, "Error writing audio: ", ex);
        }
    }

    // makeWaveHeader From AOSP
    // https://github.com/android/platform_frameworks_base/blob/master/core/java/android/speech/tts/FileSynthesisCallback.java
    // Apache License, Version 2.0
    private byte[] makeWavHeader(int SAMPLE_RATEInHzInHz, int audioFormat, int channelCount,
                                 int dataLength) throws IOException {

        final int WAV_HEADER_LENGTH = 44;

        int sampleSizeInBytes = (audioFormat == AudioFormat.ENCODING_PCM_8BIT ? 1 : 2);
        int byteRate = SAMPLE_RATEInHzInHz * sampleSizeInBytes * channelCount;
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
        header.putShort((short) ENCODING_PCM_16BIT);
        header.putShort((short) channelCount);
        header.putInt(SAMPLE_RATEInHzInHz);
        header.putInt(byteRate);
        header.putShort(blockAlign);
        header.putShort(bitsPerSample);
        header.put(new byte[]{ 'd', 'a', 't', 'a' });
        header.putInt(dataLength);
        return headerBuf;
    }
    
}