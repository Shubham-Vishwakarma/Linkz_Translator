package com.example.admin.translator.IBMUtilies;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.admin.translator.R;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

/**
 * Created by Admin on 04/08/2017.
 */

public class SynthesisTask extends AsyncTask<String,Void,String> {

    private static final String TAG = "SynthesisTask";
    private StreamPlayer player = new StreamPlayer();
    private Activity activity;
    private TextToSpeech textService;
    private Voice voice;
    private ProgressDialog progressDialog;

    public SynthesisTask(Activity activity,TextToSpeech textService,Voice voice)
    {
        this.activity = activity;
        this.textService = textService;
        this.voice = voice;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e(TAG,"On Start");
        showProgressDialog();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            player.playStream(textService.synthesize(strings[0], voice).execute());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "Did Synthesize";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.e(TAG,"On Stop");
        hideProgressDialog();
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(activity.getString(R.string.wait_for_moment));
        progressDialog.show();
    }

    private void hideProgressDialog(){
        progressDialog.hide();
    }
}
