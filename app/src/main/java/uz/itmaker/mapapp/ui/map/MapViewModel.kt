package uz.itmaker.mapapp.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import uz.itmaker.mapapp.models.FeatureMember
import uz.itmaker.mapapp.net.ApiService

class MapViewModel : ViewModel(), KoinComponent {

    private val API_KEY = "3b321ad7-7008-4e08-8ed8-85d5e1504fd1"

    val results = MutableLiveData<List<FeatureMember>>()

    private val apiService: ApiService by inject()

    private val cd = CompositeDisposable()
    fun searchMap(text: String) {

        if (!text.isNullOrBlank()) {
            apiService.getSearchList(API_KEY, text, "json", 15)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    results.postValue(it.response.geoObjectCollection.featureMember)
                }, {

                }).let { cd.add(it) }
        } else {
            results.postValue(listOf())
        }


    }


    fun clearSearch() {
        results.postValue(listOf())
    }

    override fun onCleared() {
        super.onCleared()
        cd.clear()
    }
}