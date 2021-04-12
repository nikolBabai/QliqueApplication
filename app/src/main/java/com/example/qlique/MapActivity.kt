package com.example.qlique

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import java.io.IOException
import java.util.*

private lateinit var fusedLocationClient: FusedLocationProviderClient
class MapActivity : AppCompatActivity() , OnMapReadyCallback,
GoogleApiClient.OnConnectionFailedListener{
    private val FINE_LOCATION: String = Manifest.permission.ACCESS_FINE_LOCATION
    private val COURSE_LOCATION: String = Manifest.permission.ACCESS_COARSE_LOCATION
    private val LOCATION_PERMISSION_REQUEST_CODE = 1234
    private var mLocationPermissionsGranted = false
    private var mMap: GoogleMap? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val DEFAULT_ZOOM = 15f
    private var mSearchText: AutoCompleteTextView? = null
    private var mGps: ImageView? = null
    private var mPlaceAutoCompleteAdapter: PlaceAutocompleteAdapter? = null
    private var mGoogleApiClient: GoogleApiClient?=null
    private val LAT_LNG_BOUNDS = LatLngBounds(
        LatLng(-40.0 , -168.0),
        LatLng(71.0, 136.0)
    )
    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "onMapReady: map is ready")
        mMap = googleMap
        if (mLocationPermissionsGranted) {
            getDeviceLocation()

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap!!.isMyLocationEnabled = true;
            mMap!!.uiSettings.isMyLocationButtonEnabled = false;

            init()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mSearchText = findViewById(R.id.input_search);
        mGps = findViewById(R.id.ic_gps)
        getLocationPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                mFusedLocationProviderClient?.lastLocation?.addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        moveCamera(
                            LatLng(
                                location.latitude
                                ,location.longitude
                            ),
                            DEFAULT_ZOOM, "My Location"
                        )
                    }
                }

            }else{
               getLocationPermission()
            }
        }
    }

    private fun getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions")
        val permissions = arrayOf<String>(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    COURSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mLocationPermissionsGranted = true
                initMap()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: called.")
        mLocationPermissionsGranted = false
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    var i = 0
                    while (i < grantResults.size) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false
                            Log.d(TAG, "onRequestPermissionsResult: permission failed")
                            return
                        }
                        i++
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted")
                    mLocationPermissionsGranted = true
                    //initialize our map
                    initMap()
                }
            }
        }
    }

    private fun getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location")
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            if (mLocationPermissionsGranted) {
                    val location: Task<*> =
                        mFusedLocationProviderClient!!.lastLocation
                    location.addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "onComplete: found location!")
                                val currentLocation: Location? = task.result as Location?
                                if (currentLocation != null) {
                                    fusedLocationClient.lastLocation
                                        .addOnSuccessListener { location : Location? ->
                                            if (location != null) {
                                                moveCamera(
                                                    LatLng(
                                                        location.latitude
                                                        ,location.longitude
                                                    ),
                                                    DEFAULT_ZOOM, "My Location"
                                                )
                                            }
                                        }
                                    /*
                                    moveCamera(
                                        LatLng(
                                            currentLocation.latitude,
                                            currentLocation.longitude
                                        ),
                                        DEFAULT_ZOOM, "My Location"
                                    )
                                    */
                                }
                            } else {
                                Log.d(TAG, "onComplete: current location is null")
                                Toast.makeText(
                                    this@MapActivity,
                                    "unable to get current location",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.message)
        }
    }

    private fun moveCamera(latLng: LatLng, zoom: Float, title: String) {
        Log.d(
            TAG,
            "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude
        )
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        if (title != "My Location") {
            val options: MarkerOptions = MarkerOptions()
                .position(latLng)
                .title(title)
            mMap!!.addMarker(options)
        }

        hideSoftKeyboard()
    }
    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
    private fun initMap() {
        Log.d(TAG, "initMap: initializing map")
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addApi(Places.GEO_DATA_API)
            .addApi(Places.PLACE_DETECTION_API)
            .enableAutoManage(this, this)
            .build()

        val mapFragment : SupportMapFragment=
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MapActivity)
    }

    private fun init() {
        Log.d(TAG, "init: initializing")
        mPlaceAutoCompleteAdapter = PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUNDS,null)
        mSearchText?.setAdapter(mPlaceAutoCompleteAdapter)
        mSearchText!!.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                || keyEvent.action == KeyEvent.ACTION_DOWN
                || keyEvent.action == KeyEvent.KEYCODE_ENTER
            ) {
                //execute our method for searching
                geoLocate()
            }
            false
        }
        mGps!!.setOnClickListener {
            Log.d(TAG, "onClick: clicked gps icon")
            getDeviceLocation()
        }
    }
    private fun geoLocate() {
        Log.d(TAG, "geoLocate: geolocating")
        val searchString = mSearchText!!.text.toString()
        val geocoder = Geocoder(this@MapActivity)
        var list: List<Address> = ArrayList()
        try {
            list = geocoder.getFromLocationName(searchString, 1)
        } catch (e: IOException) {
            Log.e(TAG, "geoLocate: IOException: " + e.message)
        }
        if (list.isNotEmpty()) {
            val address: Address = list[0]
            Log.d(TAG, "geoLocate: found a location: $address")
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(
                LatLng(
                    address.latitude,
                    address.longitude
                ), DEFAULT_ZOOM,
                address.getAddressLine(0)
            )
        }
    }


}