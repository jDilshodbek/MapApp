package uz.test.mapapp

import androidx.multidex.MultiDexApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import uz.test.mapapp.di.appModule

class MapApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MapApp)
            modules(appModule)
        }
    }
}