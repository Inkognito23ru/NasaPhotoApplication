package ru.osipov.nasaphotoapplication.api;

import java.util.List;

import io.reactivex.Single;

import retrofit2.http.GET;
import retrofit2.http.Path;

import ru.osipov.nasaphotoapplication.api.model.DateDTO;
import ru.osipov.nasaphotoapplication.api.model.PhotoDTO;

public interface NasaApi {

    @GET("natural/all")
    Single<List<DateDTO>> getDatesWithPhoto();

    @GET("natural/date/{date}")
    Single<List<PhotoDTO>> getPhotosForDate(@Path("date") String date);
}
