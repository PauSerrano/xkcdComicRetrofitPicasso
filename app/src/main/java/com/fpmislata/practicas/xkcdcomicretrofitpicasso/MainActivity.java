package com.fpmislata.practicas.xkcdcomicretrofitpicasso;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fpmislata.practicas.xkcdcomicretrofitpicasso.Modelos.ComicModelo;
import com.fpmislata.practicas.xkcdcomicretrofitpicasso.Servicio.ComicAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Random;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity {

    private static int contador=0;

    ProgressBar barra;
    ImageView imagen;
    TextView titulo;

    String urlImagen;
    ComicAPI comicAPI;
    OkHttpClient cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bloqueamos la pantalla para facilitar la vista de las imagenes
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        barra = (ProgressBar) findViewById(R.id.progressBar);
        imagen = (ImageView) findViewById(R.id.imageView);
        titulo = (TextView) findViewById(R.id.textView);

        // TODO: Creamos un objeto de OKHTTPCLIENT  para Retrofit y Picasso
        // de esta maneta usaremos menos memoria y la misma cola de peticiones en red
        cliente = new OkHttpClient.Builder().build();
        //Cuando utilicemos Retrofit o Picasso les pasaremos como parametro este cliente

        // Debemos conseguir la url de la imagen, utilizamos Retrofit
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ComicAPI.Url_Comics)
                .addConverterFactory(GsonConverterFactory.create(gson))
                // Le pasamos el ***** CLIENTE ******
                .client(cliente)
                .build();
        comicAPI = retrofit.create(ComicAPI.class);

        Call<ComicModelo> callComic = comicAPI.getComicModeloInicial();

        barra.setIndeterminate(true);
        callComic.enqueue(callBackComic);

    }

    Callback<ComicModelo> callBackComic = new Callback<ComicModelo>() {
        @Override
        public void onResponse(Call<ComicModelo> call, Response<ComicModelo> response) {
            ComicModelo comicModelo = response.body();

            if (comicModelo!=null){

                urlImagen = comicModelo.getImg();
                //Utilizamos picaso para descargar la imagen
                cargarImagenConPicasso(urlImagen);

            } else {
                Toast.makeText(MainActivity.this, "Error, codigo: "+response.code(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ComicModelo> call, Throwable t) {

            Toast.makeText(MainActivity.this, "Error, mensaje: "+t.getMessage(), Toast.LENGTH_LONG).show();
        }
    };


    public void cargarOtroComic(View view) {

        Random random = new Random();

        int numComic = random.nextInt(1000);
        String numeroString = String.valueOf(numComic);

        barra.setIndeterminate(true);
        barra.setVisibility(View.VISIBLE);
        Call<ComicModelo> callComic = comicAPI.getComicModelo(numeroString);
        callComic.enqueue(callBackComic);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("urlImagenGuardada", urlImagen);
    }

   /* @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String urlImagenSave = savedInstanceState.getString("urlImagenGuardada");
        cargarImagenConPicasso(urlImagenSave);
    }*/


    // TODO: Aqui tenemos PICASSO +++++++++++++++++++++++++++++++++++++++++
    public void cargarImagenConPicasso (String url){

        // Le pasamos el ***** CLIENTE ******
        Picasso picassoCliente = new Picasso.Builder(MainActivity.this)
                .downloader(new OkHttp3Downloader(cliente)).build();

        picassoCliente.load(urlImagen)
                .placeholder(R.drawable.cargando)
                .error(R.drawable.error)
                //.fit()
                .resize(600,200)
                //.centerCrop()
                .centerInside()
                .into(imagen, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //barra.setIndeterminate(false);
                        //barra.setProgress(100);
                        barra.setVisibility(View.GONE);
                        titulo.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(MainActivity.this, "Error en la carga de la imagen en el imageView",  Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /* @Override
    protected void onResume() {

        Picasso.with(MainActivity.this).load(urlImagen)
                .placeholder(R.drawable.cargando)
                .error(R.drawable.error)
                .into(imagen);

        super.onResume();


    }*/
}


