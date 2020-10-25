package uz.itmaker.mapapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.kelin.translucentbar.library.TranslucentBarManager
import kotlinx.android.synthetic.main.bottom_nav_layout.*
import uz.itmaker.mapapp.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )

        val translucentBarManager = TranslucentBarManager(this)
        translucentBarManager.transparent(this)

        NavigationUI.setupWithNavController(bottomNav, navController)

        val radius = resources.getDimension(R.dimen.radius_small)
        val bottomNavigationViewBackground = bottomNav.background as MaterialShapeDrawable
        bottomNavigationViewBackground.shapeAppearanceModel =
            bottomNavigationViewBackground.shapeAppearanceModel.toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, radius)
                .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                .build()


        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination?.id) {
                R.id.favoritesFragment -> translucentBarManager.translucent(this)
                R.id.mapFragment -> translucentBarManager.transparent(this)
            }
        }

    }
}