package uz.itmaker.mapapp.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.itmaker.mapapp.models.Address

@Database(entities = [Address::class], version = 1, exportSchema = false)
abstract class AddressDatabase : RoomDatabase() {
    abstract fun addressDao(): AddressDao
}