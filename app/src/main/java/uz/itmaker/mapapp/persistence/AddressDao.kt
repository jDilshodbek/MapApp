package uz.itmaker.mapapp.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import uz.itmaker.mapapp.models.Address


@Dao
interface AddressDao {

    @Query("SELECT * from address")
    fun getAddressList(): LiveData<List<Address>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(address: Address)

    @Delete
    suspend fun delete(address: Address)
}