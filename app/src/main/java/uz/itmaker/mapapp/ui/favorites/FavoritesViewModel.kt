package uz.itmaker.mapapp.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import uz.itmaker.mapapp.models.Address
import uz.itmaker.mapapp.persistence.AddressDatabase

class FavoritesViewModel : ViewModel(), KoinComponent {

    private val addressDatabase: AddressDatabase by inject()

    val favoritesList: LiveData<List<Address>>

    init {
        favoritesList = addressDatabase.addressDao().getAddressList()
    }

    fun insert(address: Address) = viewModelScope.launch {
        addressDatabase.addressDao().insert(address)
    }

    fun delete(address: Address) = viewModelScope.launch {
        addressDatabase.addressDao().delete(address)
    }


}