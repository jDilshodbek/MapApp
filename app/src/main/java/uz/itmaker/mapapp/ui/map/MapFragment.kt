package uz.itmaker.mapapp.ui.map

import android.Manifest
import android.app.Dialog
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import kotlinx.android.synthetic.main.fragment_map.*
import org.koin.android.viewmodel.ext.android.viewModel
import uz.itmaker.mapapp.Address
import uz.itmaker.mapapp.R
import uz.itmaker.mapapp.ui.favorites.FavoritesViewModel
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(R.layout.fragment_map), Map.CameraCallback, UserLocationObjectListener,
    CameraListener, MapLoadedListener {
    val PERMISSION_ID = 42
    val REQUIRED_SDK_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val REQUEST_CODE_ASK_PERMISSIONS = 1
    val REQUEST_CHECK_SETTINGS = 1
    private lateinit var missingPermissions: ArrayList<String>
    private var isMapLoadedSusseccfully = false
    private lateinit var userLocationLayer: UserLocationLayer
    private val searchResult = mutableListOf<Address>()
    private val RESULT_NUMBER_LIMIT = 15
    private val addressAdapter by lazy { AddressAdapter() }
    private lateinit var rvAddress: RecyclerView
    private lateinit var emptyTextView: TextView

    private val mapViewModel: MapViewModel by viewModel()
    private val favoritesViewModel: FavoritesViewModel by viewModel()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("522fb9ba-acc3-4c2a-ad64-371448cace44")
        MapKitFactory.initialize(requireActivity())


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        searchBtn.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_baseline_search_24,
            0,
            R.drawable.ic_baseline_mic_none_24,
            0
        )

        checkPermission()
        val aligment = Alignment(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM)
        mapView.map.logo.setAlignment(aligment)

        mapView.map.addCameraListener(this)
        mapView.map.setMapLoadedListener(this)




        mapView.map.isRotateGesturesEnabled = true
        mapView.map.isZoomGesturesEnabled = true





        searchBtn.setOnClickListener {

            val dialogAddress =
                BottomSheetDialog(requireContext(), R.style.RounderBottomSheetDialogTheme)
            dialogAddress.setContentView(R.layout.fragment_addres_search)

            val bottomSheet: FrameLayout =
                dialogAddress.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!

            val behavior = BottomSheetBehavior.from(bottomSheet)

            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            val searchView = dialogAddress.findViewById<SearchView>(R.id.searchView)

            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        submitQuery(it)
                    }


                    return true
                }

            })


            searchView?.setOnCloseListener {
                submitQuery("")
                return@setOnCloseListener true
            }

            rvAddress = dialogAddress.findViewById(R.id.rvAddress)!!
            rvAddress.layoutManager = LinearLayoutManager(requireContext())
            rvAddress.adapter = addressAdapter
            rvAddress.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )

            emptyTextView = dialogAddress.findViewById(R.id.emptyTextView)!!



            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        mapViewModel.clearSearch()
                    }
                }

            })

            addressAdapter.onAddressClick = { lng, lat, text ->
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
                goPlace(lat, lng, text)
            }


            dialogAddress.show()


        }



        mapViewModel.results.observe(viewLifecycleOwner, Observer {
            addressAdapter.setItems(it)
            emptyTextView.isVisible = it.isEmpty()

        })

        meFab.setOnClickListener {
            findMe()
        }


    }


    private fun checkPermission() {

        val missingPermissions = arrayListOf<String>()

        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(context!!, permission)

            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }

        if (!missingPermissions.isEmpty()) {

            val permissions = missingPermissions.toTypedArray()

            requestPermissions(permissions, REQUEST_CODE_ASK_PERMISSIONS)

        } else {

            val grantResults = IntArray(REQUIRED_SDK_PERMISSIONS.size)

            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)

            onRequestPermissionsResult(
                REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                grantResults
            )

        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {

            for (index in permissions.indices) {

                if ((grantResults[index] != PackageManager.PERMISSION_GRANTED)) {


                    activity!!.finish()

                    return

                }
            }


            askTurnOnLocation()
        }
    }


    private fun askTurnOnLocation() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            numUpdates = 3
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        locationRequest?.let {

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(it)

            val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())


            task.addOnSuccessListener { locationSettingsResponse ->

                Log.i("sdwwdf", ":scwc")
                moveToHome()
            }


            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(
                            activity,
                            REQUEST_CHECK_SETTINGS
                        )

                        moveToHome()


                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.


                        Toast.makeText(context, "Ooops!", Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }


    }

    private fun moveToHome() {

        mapView.map.move(
            CameraPosition(mapView.map.cameraPosition.target, mapView.map.maxZoom, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1.toFloat()), this
        )
        Log.i("adskhbwevf", ":" + mapView.map.cameraPosition.target)
    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onObjectUpdated(userLocationView: UserLocationView, p1: ObjectEvent) {


    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationLayer.setAnchor(
            PointF(mapView.width() * 0.5.toFloat(), mapView.height().toFloat() * 0.5.toFloat()),
            PointF(mapView.width() * 0.5.toFloat(), mapView.height().toFloat() * 0.5.toFloat())
        )


        userLocationView.pin.setIcon(ImageProvider.fromResource(context, R.drawable.pin))
        userLocationView.arrow.setIcon(ImageProvider.fromResource(context, R.drawable.pin))
        Log.i("eqfefg24", "wdwd")


    }


    override fun onMapLoaded(p0: MapLoadStatistics) {
        isMapLoadedSusseccfully = true

        val mapkit = MapKitFactory.getInstance()


        mapView?.let {


            userLocationLayer = mapkit.createUserLocationLayer(mapView.mapWindow)
            userLocationLayer.isVisible = true
            userLocationLayer.isHeadingEnabled = true
            userLocationLayer.setObjectListener(this)

            Handler().postDelayed({
                if (isMapLoadedSusseccfully && userLocationLayer.isValid) {

                    mapView?.map?.move(
                        CameraPosition(
                            mapView.map.cameraPosition.target,
                            mapView.map.maxZoom,
                            0.0f,
                            0.0f
                        ),
                        Animation(Animation.Type.SMOOTH, 1.toFloat()), null
                    )
                }

            }, 5000)
        }

    }

    override fun onMoveFinished(p0: Boolean) {

    }


    private fun submitQuery(query: String) {
        mapViewModel.searchMap(query)
        addressAdapter.searchText(query)
    }

    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateSource,
        p3: Boolean
    ) {

    }

    private fun goPlace(lat: Double, lng: Double, text: String) {
        mapView.map.move(
            CameraPosition(Point(lat, lng), mapView.map.maxZoom, 10.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1.toFloat()), null
        )

        userLocationLayer.resetAnchor()
        mapView.map.mapObjects.clear()
        mapView.map.mapObjects.addPlacemark(
            Point(lat, lng),
            ImageProvider.fromResource(context, R.drawable.pin)
        )


        showAddressDetailSheet(lat, lng, text)

    }

    fun findMe() {
        if (isMapLoadedSusseccfully) {
            if (userLocationLayer.isValid && userLocationLayer?.cameraPosition() != null) {
                mapView?.getMap()?.move(
                    CameraPosition(
                        userLocationLayer.cameraPosition()!!.target,
                        mapView.getMap().getMaxZoom(), 10.0f, 0.0f
                    ),
                    Animation(Animation.Type.SMOOTH, 1f),
                    null
                )
            }
        }

    }

    private fun showAddressDetailSheet(lat: Double, lng: Double, text: String) {

        val parentLayout = requireView().findViewById<CoordinatorLayout>(R.id.parentLayout)
        val bottomSheet = parentLayout.findViewById<ConstraintLayout>(R.id.add_to_fab_layout)

        val behaviour = BottomSheetBehavior.from(bottomSheet)

        val addresstext = bottomSheet.findViewById<TextView>(R.id.addressText)
        val closeIcon = bottomSheet.findViewById<ImageView>(R.id.closeSheetIcon)
        val addToFavBtn = bottomSheet.findViewById<MaterialButton>(R.id.addToFavBtn)

        addresstext.text = text


        behaviour.state = BottomSheetBehavior.STATE_EXPANDED

        closeIcon.setOnClickListener {
            behaviour.state = BottomSheetBehavior.STATE_HIDDEN
        }



        addToFavBtn.setOnClickListener {
            // show save dialog

            saveToFaveDialog(lat, lng, text)
        }


    }


    private fun saveToFaveDialog(lat: Double, lng: Double, text: String) {

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.add_fav_dialog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val addressEditText = dialog.findViewById<EditText>(R.id.addressEditText)
        val editImage = dialog.findViewById<ImageView>(R.id.editImage)
        val cancelBtn = dialog.findViewById<MaterialButton>(R.id.cancelBtn)
        val saveBtn = dialog.findViewById<MaterialButton>(R.id.saveBtn)


        addressEditText.setText(text)

        editImage.setOnClickListener {
            addressEditText.isEnabled = true
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        saveBtn.setOnClickListener {
            // save to db
            val address =
                uz.itmaker.mapapp.models.Address(0, addressEditText.text.toString(), lat, lng)
            favoritesViewModel.insert(address)
            Snackbar.make(requireView(), getString(R.string.added_to_fav), Snackbar.LENGTH_SHORT)
                .show()
            dialog.dismiss()
        }

        dialog.show()


    }


}