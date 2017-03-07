package com.example.javi.chistesaleatorios;

import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

public class ChistesActivity extends AppCompatActivity {

    private final String[] listaChistes = {"chiste1.mp3", "chiste2.mp3", "chiste3.mp3", "chiste4.mp3",
                                            "chiste5.mp3", "chiste6.mp3", "chiste7.mp3", "chiste8.mp3",
                                            "chiste9.mp3", "chiste10.mp3"};

    private boolean aleatorioActivado;
    private boolean bucleActivado;
    private Button btnModoAleatorio, btnBucle, btnSiguienteChiste, btnChisteFav;

    private MediaPlayer mediaPlayer;
    private AssetManager manager;
    private int contadorChiste = 0;
    private int contadorBucle = 0;
    private int chisteFavorito = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chistes);

        aleatorioActivado = false;
        bucleActivado = false;
        mediaPlayer = new MediaPlayer();
        manager = getAssets();

        btnModoAleatorio = (Button) findViewById(R.id.btn_chistes_alea);
        btnBucle = (Button) findViewById(R.id.btn_chistes_bucle);
        btnSiguienteChiste = (Button) findViewById(R.id.btn_siguiente);
        btnChisteFav = (Button) findViewById(R.id.btn_chiste_fav);

    }

    public void reproducirSiguienteChiste(View v){
        // si se esta reproduciendo algo primero lo para
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        // si el modo aleatorio esta desactivado
        if(!aleatorioActivado){
            loadDescriptor(listaChistes[contadorChiste]);
            Log.d("***Chiste: ", contadorChiste+"");
            mediaPlayer.start();
            //actualiza el contador, reinicia si es mayor que 9
            if(contadorChiste < listaChistes.length - 1){
                contadorChiste++;
            }else{
                contadorChiste = 0;
            }
        // si el modo aleatorio esta activado
        }else{
            Random random = new Random();
            contadorChiste = random.nextInt(listaChistes.length);
            loadDescriptor(listaChistes[contadorChiste]);
            Log.d("***Chiste: ", contadorChiste+"");
            mediaPlayer.start();
        }
    }

    public void toggleAleatorio(View v){
        if(!aleatorioActivado){
            aleatorioActivado = true;
            Toast.makeText(this, "Modo aleatorio activado", Toast.LENGTH_SHORT).show();
            // cambia el texto del boton dependiendo de si aleatorio esta activado o no
            btnModoAleatorio.setText(R.string.chistes_btn_chisteorden);
        }else{
            aleatorioActivado = false;
            Toast.makeText(this, "Modo aleatorio desactivado", Toast.LENGTH_SHORT).show();
            // pasa al siguiente chiste de la lista despues del ultimo reproducido
            contadorChiste++;
            btnModoAleatorio.setText(R.string.chistes_btn_chistealea);
        }
    }

    public void reproducirFavorito(View v){
        if(chisteFavorito != -1){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            loadDescriptor(listaChistes[chisteFavorito]);
            mediaPlayer.start();
        }else{
            Toast.makeText(this, "No tienes ningun chiste guardado", Toast.LENGTH_SHORT).show();
        }

    }

    public void toggleBucle(View v){
        if(!bucleActivado){
            bucleActivado = true;
            btnBucle.setText(getString(R.string.chistes_btn_uno));
            btnSiguienteChiste.setVisibility(View.INVISIBLE);
            btnChisteFav.setVisibility(View.INVISIBLE);

            reproduccionBucle(listaChistes[0]);
        }else{
            bucleActivado = false;
            btnBucle.setText(getString(R.string.chistes_btn_chistesbucle));
            btnSiguienteChiste.setVisibility(View.VISIBLE);
            btnChisteFav.setVisibility(View.VISIBLE);
        }
    }


    private void reproduccionBucle(String chiste) {
        AssetFileDescriptor descriptor;
        aleatorioActivado = false;

        if(bucleActivado){
            try{
                mediaPlayer.release();
                mediaPlayer = new MediaPlayer();
                descriptor = manager.openFd(listaChistes[contadorBucle]);
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.release();
                        mediaPlayer = new MediaPlayer();
                        if(contadorBucle < listaChistes.length - 1){
                            contadorBucle++;
                        }else{
                            contadorBucle = 0;
                        }
                        reproduccionBucle(listaChistes[contadorBucle]);
                    }
                });

            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void loadDescriptor(String chiste) {
        try{
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(contadorChiste != chisteFavorito){
                        mostrarDialog();
                    }
                    mediaPlayer.release();
                    mediaPlayer = new MediaPlayer();
                }
            });

            AssetFileDescriptor descriptor = manager.openFd(chiste);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            mediaPlayer.prepare();

        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private void mostrarDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("¿Guardar como favorito?")
                .setMessage("¿Quieres convertir este chiste en tu favorito?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chisteFavorito = contadorChiste;
                        Log.d("***ChisteFav: ", chisteFavorito+"");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }
}
