package uz.test.mapapp.net

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import uz.test.mapapp.models.Yandex

interface ApiService {

    @GET("/1.x/")
    fun getSearchList(
        @Query("apikey") apiKey: String,
        @Query("geocode") search: String,
        @Query("format") format: String,
        @Query("results") results: Int
    ): Single<Yandex>
}