package uz.itmaker.mapapp.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.itmaker.mapapp.R
import uz.itmaker.mapapp.models.Address

class FavAdapter : RecyclerView.Adapter<FavAdapter.ViewHolder>() {
    private var favItems = listOf<Address>()

    var OnDeleteClick: ((Address) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val favText = itemView.findViewById<TextView>(R.id.favText)
        val favIcon = itemView.findViewById<ImageView>(R.id.favImage)

        init {
            favIcon.setOnClickListener {
                OnDeleteClick?.invoke(favItems[adapterPosition])

            }

        }


        fun bindData(address: Address) {
            favText.text = address.formatted

        }

    }

    fun setItems(items: List<Address>) {
        val diffCallback = FavCallBack(favItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)
        favItems = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.fav_item_, parent, false)
    )

    override fun getItemCount() = favItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(favItems[position])

    }
}