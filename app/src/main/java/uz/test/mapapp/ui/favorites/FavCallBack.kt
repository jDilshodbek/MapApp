package uz.test.mapapp.ui.favorites

import androidx.recyclerview.widget.DiffUtil
import uz.test.mapapp.models.Address

class FavCallBack(
    private val oldAddressList: List<Address>,
    private val newAddressList: List<Address>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldAddressList[oldItemPosition] == newAddressList[newItemPosition]
    }

    override fun getOldListSize(): Int {
        return oldAddressList.size
    }

    override fun getNewListSize(): Int {
        return newAddressList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldAddressList[oldItemPosition] == newAddressList[newItemPosition]
    }
}