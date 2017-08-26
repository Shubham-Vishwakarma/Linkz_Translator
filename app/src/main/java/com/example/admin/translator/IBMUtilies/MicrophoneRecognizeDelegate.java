package com.example.admin.translator.IBMUtilies;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;

/**
 * Created by Admin on 04/08/2017.
 */

public class MicrophoneRecognizeDelegate implements RecognizeCallback {

    private static final String TAG = "MicrophoneRecognizeDelegate";
    private TextView resultTextView;
    private final Activity activity;

    public MicrophoneRecognizeDelegate(Activity activity, TextView resultTextView)
    {
        this.activity = activity;
        this.resultTextView = resultTextView;
    }

    @Override
    public void onTranscription(SpeechResults speechResults) {
        Log.e(TAG,"Results = " + speechResults);
        try{
            if(speechResults.getResults()!=null && !speechResults.getResults().isEmpty())
            {
                String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                //double confidence = speechResults.getResults().get(0).getAlternatives().get(0).getConfidence();
                setResultTextView(text);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected() {
        Log.e(TAG,"Connected");
    }

    @Override
    public void onError(Exception e) {
        Log.e(TAG,"Error = " + e);
    }

    @Override
    public void onDisconnected() {
        Log.e(TAG,"Disconnected");
    }

    @Override
    public void onInactivityTimeout(RuntimeException runtimeException) {
        Log.e(TAG,"TimeOut = " + runtimeException);
    }

    @Override
    public void onListening() {
        Log.e(TAG,"Listening");
    }

    @Override
    public void onTranscriptionComplete() {
        Log.e(TAG,"Transcription Completed");
    }

    private void setResultTextView(final String text)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultTextView.setText(text);
            }
        });
    }
}