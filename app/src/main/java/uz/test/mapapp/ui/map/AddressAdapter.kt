package uz.test.mapapp.ui.map

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xeoh.android.texthighlighter.TextHighlighter
import uz.test.mapapp.R
import uz.test.mapapp.models.FeatureMember

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {
    private var addressList = arrayListOf<FeatureMember>()
    private var searchText: String = ""
    var onAddressClick: ((Double, Double, String) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val addressText = itemView.findViewById<TextView>(R.id.addressText)
        val textHighLighter =
            TextHighlighter().setForegroundColor(Color.BLACK).addTarget(addressText).setBold(true)

        init {
            itemView.setOnClickListener {
                val point = addressList[adapterPosition].geoObject.point.pos.split(" ")
                onAddressClick?.invoke(
                    point[0].toDouble(),
                    point[1].toDouble(),
                    addressList[adapterPosition].geoObject.metaDataProperty.geocoderMetaData.address.formatted
                )

            }

        }

        fun bindData(featureMember: FeatureMember) {
            addressText.text =
                featureMember.geoObject.metaDataProperty.geocoderMetaData.address.formatted
            featureMember.geoObject.point.pos
            if (searchText.isNotBlank()) {
                textHighLighter.highlight(searchText, TextHighlighter.CASE_INSENSITIVE_MATCHER)
            }

        }

    }


    fun setItems(items: List<FeatureMember>) {
        addressList.clear()
        addressList.addAll(items)
        notifyDataSetChanged()
    }

    fun searchText(text: String) {
        this.searchText = text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.address_item, parent, false)
    )

    override fun getItemCount() = addressList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(addressList[position])

    }
}