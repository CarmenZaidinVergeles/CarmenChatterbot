package com.example.carmen.carmenchatterbot;

import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {


    private TextView textView;
    private TextView textView2;
    private ScrollView scrollView;
    private boolean ok;
    private TextToSpeech tts;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.scrollView = (ScrollView) findViewById(R.id.scrollView);
        this.textView = (TextView) findViewById(R.id.textView);
        this.textView2 = (TextView) findViewById(R.id.tv2);
        lanzarTTS();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //Programar boton hablar que lanza el intent
    public void hablar(View v) {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora");
        i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
        startActivityForResult(i, 1);
    }


    public void lanzarTTS() {
        Intent intent = new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
                tts.setLanguage(Locale.getDefault());

            } else {
                Intent intent = new Intent();
                intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(intent);
            }
        }
        if (requestCode == 1) { //Si hablo yo
            ArrayList<String> textos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            textView.setText(textos.get(0));//Recoge lo q hemos mandado por voz
            Tarea t = new Tarea();
            String s = textView.getText().toString();//Recoge lo escrito en el textview que le hemos mandado por voz
            t.execute(s); //Se ejecuta el chatterbot y contesta
            textView2.setText("");
        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //se puede reproducir
            ok = true;
        } else {
            //no se puede reproducir
            ok = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.carmen.carmenchatterbot/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.carmen.carmenchatterbot/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
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
            if (ok) {
                textView2.append(out + "\n");
                tts.setLanguage(new Locale("es", "ES"));
                tts.setPitch((float) 1.0);
                tts.speak(textView2.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            } else {

            }

        }
    }
}


