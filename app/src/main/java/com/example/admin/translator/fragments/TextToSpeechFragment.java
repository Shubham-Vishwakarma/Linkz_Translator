package com.example.admin.translator.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.admin.translator.IBMUtilies.IBMInitialization;
import com.example.admin.translator.IBMUtilies.SynthesisTask;
import com.example.admin.translator.R;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextToSpeechFragment extends Fragment {

    private static final String TAG = "TextToSpeechFragment";
    private TextToSpeech textService;
    private TextInputEditText textInput;
    private LinearLayout translateButton;
    private Spinner voicesSpinner;
    private Voice selectedVoice;

    public TextToSpeechFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_to_speech, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.text_to_speech);

        textService = IBMInitialization.initTextToSpeechService(getContext());
        textInput = view.findViewById(R.id.textInput);
        translateButton = view.findViewById(R.id.translateButton);
        voicesSpinner = view.findViewById(R.id.voicesSpinner);
        setupSpinner();

        voicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0 : selectedVoice = Voice.EN_ALLISON;
                        break;
                    case 1 : selectedVoice = Voice.EN_LISA;
                        break;
                    case 2 : selectedVoice = Voice.EN_MICHAEL;
                        break;
                    case 3 : selectedVoice = Voice.FR_RENEE;
                        break;
                    case 4 : selectedVoice = Voice.GB_KATE;
                        break;
                    case 5 : selectedVoice = Voice.ES_SOFIA;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedVoice = Voice.EN_ALLISON;
            }
        });

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SynthesisTask(getActivity(),textService, selectedVoice).execute(textInput.getText().toString());
            }
        });
    }

    private void setupSpinner(){
        String voices[] = getActivity().getResources().getStringArray(R.array.voices);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,voices);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        voicesSpinner.setAdapter(adapter);
        voicesSpinner.setPadding(8,8,8,8);
    }
}
