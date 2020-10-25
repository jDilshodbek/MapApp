package uz.test.mapapp.di

import androidx.room.Room
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import uz.test.mapapp.BuildConfig

import uz.test.mapapp.net.ApiService
import uz.test.mapapp.persistence.AddressDatabase
import uz.test.mapapp.ui.favorites.FavoritesViewModel
import uz.test.mapapp.ui.map.MapViewModel

val appModule = module {


    viewModel { MapViewModel() }
    viewModel { FavoritesViewModel() }

    // Room : Database
    single {
        Room.databaseBuilder(
            androidContext()
            , AddressDatabase::class.java,
            "favorite_address_database"
        )
            .build()
    }

    // Retrofit : service
    single {
        val BASE_URL = "https://geocode-maps.yandex.ru"
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) level =
                HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .method(original.method, original.body)
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        retrofit.create(ApiService::class.java)
    }


}