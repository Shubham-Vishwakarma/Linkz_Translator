package com.example.admin.translator.IBMUtilies;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.translator.R;
import com.ibm.watson.developer_cloud.language_translator.v2.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v2.model.Language;

/**
 * Created by Admin on 04/08/2017.
 */

public class TranslationTask extends AsyncTask<String,Void,String> {

    private static final String TAG = "TranslationTask";
    private Activity activity;
    private LanguageTranslator translationService;
    private Language sourceLanguage, targetLanguage;
    private TextView resultTextView;
    private ProgressDialog progressDialog;

    public TranslationTask(Activity activity, LanguageTranslator translationService, Language sourceLanguage, Language targetLanguage, TextView resultTextView)
    {
        this.activity = activity;
        this.translationService = translationService;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.resultTextView = resultTextView;
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
            String translatedText = translationService.translate(strings[0], sourceLanguage, targetLanguage).execute().getFirstTranslation();
            Log.e(TAG, "Translated Text = " + translatedText);
            showTranslation(translatedText);
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"Error occured = " + e);
            Toast.makeText(activity, R.string.error,Toast.LENGTH_SHORT).show();
        }
        return "Did Translate";
    }

    private void showTranslation(final String translatedString)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultTextView.setText(translatedString);
            }
        });
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
