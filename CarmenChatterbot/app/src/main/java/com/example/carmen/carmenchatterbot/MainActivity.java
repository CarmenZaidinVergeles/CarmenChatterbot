package com.example.carmen.carmenchatterbot;

import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carmen.carmenchatterbot.bot.ChatterBot;
import com.example.carmen.carmenchatterbot.bot.ChatterBotFactory;
import com.example.carmen.carmenchatterbot.bot.ChatterBotSession;
import com.example.carmen.carmenchatterbot.bot.ChatterBotType;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private android.widget.EditText editText;
    private android.widget.Button button;
    private android.widget.TextView textView;
    private android.widget.ScrollView scrollView;
    private boolean ok;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.scrollView = (ScrollView) findViewById(R.id.scrollView);
        this.textView = (TextView) findViewById(R.id.textView);
        this.button = (Button) findViewById(R.id.button);
        this.editText = (EditText) findViewById(R.id.editText);

        lanzarTTS();

    }
    //Programar boton hablar que lanza el intent
    public void hablar(View v){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Habla ahora");
        i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
        startActivityForResult(i, 1);
    }



    public void init(){
        textView.setText("");

    }

    public void lanzarTTS(){
        Intent intent= new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, 0);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== 0) {
            if(resultCode== TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts= new TextToSpeech(this, this);
                tts.setLanguage(Locale.getDefault());
            } else{
                Intent intent= new Intent();
                intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(intent);
            }
        }
        if(requestCode== 1) { //Si hablo yo
            ArrayList<String> textos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            textView.setText(textos.get(0));
//            for(String s: textos){
//                textView.append(s + "\n");
//            }
        }
    }






    public void enviar(View v){
        Tarea t= new Tarea();
        String s = editText.getText().toString();
        t.execute(s);
        textView.append("tu> " + s + "\n");
        editText.setText("");
        if(ok) {
            tts.setLanguage(new Locale("es", "ES"));
            tts.setPitch((float) 50.0);
            tts.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            Toast.makeText(this, "Se puede", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se puede", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            //se puede reproducir
            ok = true;
        } else{
            //no se puede reproducir
            ok = false;
        }

    }

    public class Tarea extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            ChatterBotFactory factory = new ChatterBotFactory();

            ChatterBot bot1 = null;
            try {
                bot1 = factory.create(ChatterBotType.CLEVERBOT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ChatterBotSession bot1session = bot1.createSession();

//            ChatterBot bot2 = null;
//            try {
//                bot2 = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            ChatterBotSession bot2session = bot2.createSession();

            String s = params[0];


            try {
                return bot1session.think(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String out) {
            textView.append("bot> " + out + "\n");
        }
    }
}


