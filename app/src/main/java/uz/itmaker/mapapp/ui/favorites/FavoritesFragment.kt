package uz.itmaker.mapapp.ui.favorites

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_favorites.*
import org.koin.android.ext.android.inject
import uz.itmaker.mapapp.R


/**
 * A simple [Fragment] subclass.
 * Use the [FavoritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val favAdapter = FavAdapter()

    private val favoritesViewModel: FavoritesViewModel by inject()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRv()
        observedData()
    }

    private fun setUpRv() {
        rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        rvFavorites.adapter = favAdapter
        val itemDecorator =
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.rv_divder)!!)
        rvFavorites.addItemDecoration(itemDecorator)

    }

    private fun observedData() {
        favoritesViewModel.favoritesList.observe(viewLifecycleOwner, Observer {
            favAdapter.setItems(it)
        })


        favAdapter.OnDeleteClick = { address ->
            favoritesViewModel.delete(address)
            Snackbar.make(
                requireView(),
                getString(R.string.removed_from_fav),
                Snackbar.LENGTH_SHORT
            ).show()
        }

    }


}