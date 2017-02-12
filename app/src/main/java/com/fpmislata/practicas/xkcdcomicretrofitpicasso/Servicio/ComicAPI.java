package com.fpmislata.practicas.xkcdcomicretrofitpicasso.Servicio;

import com.fpmislata.practicas.xkcdcomicretrofitpicasso.Modelos.ComicModelo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Pau on 1/2/17.
 */

public interface ComicAPI {

    String Url_Comics = "https://xkcd.com/";

    //String urlJson = "https://xkcd.com/"+numComic+"/info.0.json";
    //String urlJsonInicial = "https://xkcd.com/info.0.json";

    @GET("{numero}/info.0.json")
    Call<ComicModelo> getComicModelo(@Path("numero") String numero);

    @GET("/info.0.json")
    Call<ComicModelo> getComicModeloInicial();

}
