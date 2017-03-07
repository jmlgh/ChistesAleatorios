package com.example.javi.chistesaleatorios;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class BienvenidaActivity extends AppCompatActivity {

    private static final String TAG = BienvenidaActivity.class.getSimpleName();

    private Button btnEntrar, btnSalir;
    private SoundPool soundPool;
    AudioManager audioManager;
    private AssetManager assetManager;
    private int soundID_Salir, soundID_Entrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        btnEntrar = (Button) findViewById(R.id.btn_entrar);
        btnSalir = (Button) findViewById(R.id.btn_salir);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        assetManager = getAssets();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // si la version es superior o igual a lollipop inicializa soundpool con el builder
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            // configura los atributos de audio
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();
            // configura el soundpool con los atributos creados anteriormente
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
            // configura el file descriptor
            try{
                AssetFileDescriptor descriptorSalir = assetManager.openFd("game over.mp3");
                AssetFileDescriptor descriptorEntrar = assetManager.openFd("aplausos.mp3");
                soundID_Salir = soundPool.load(descriptorSalir, 1);
                soundID_Entrar = soundPool.load(descriptorEntrar, 1);

            }catch(IOException e){
                Log.e(TAG, "Error al recuperar el archivo de audio");
            }
            // si la version es inferior inicializa soundpool con metodos obsoletos
        }else{
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
            try{
                AssetFileDescriptor descriptorSalir = assetManager.openFd("game over.mp3");
                AssetFileDescriptor descriptorEntrar = assetManager.openFd("aplausos.mp3");
                soundID_Salir = soundPool.load(descriptorSalir, 1);
                soundID_Entrar = soundPool.load(descriptorEntrar, 1);
            }catch(IOException e){
                Log.e(TAG, "Error al recuperar el archivo de audio");
            }
        }

        // asigna un listener a soundPool para que espere a que el sonido cargue
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(final SoundPool soundPool, int sampleId, int status) {
                // asigna los listeners a los botones
                btnEntrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        soundPool.play(soundID_Entrar, 1, 1, 0, 0, 1);
                        Intent i = new Intent(BienvenidaActivity.this, ChistesActivity.class);
                        startActivity(i);
                        soundPool.stop(soundID_Entrar);
                    }
                });

                btnSalir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        soundPool.play(soundID_Salir, 1, 1, 0, 0, 1);
                        finish();

                    }
                });
            }
        });
    }
}
