package com.muhammedkursatgokgun.mymaps
/*
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.maps.CameraUpdate

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.muhammedkursatgokgun.mymaps.databinding.ActivityMapsBinding
import com.muhammedkursatgokgun.mymaps.model.Place
import com.muhammedkursatgokgun.mymaps.roomdb.PlaceDao
import com.muhammedkursatgokgun.mymaps.roomdb.PlaceDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMapLongClickListener{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var db: PlaceDatabase
    private lateinit var placeDao: PlaceDao

    private var basla : Boolean = false
    private var selectedLocationLa : Double? = null
    private var selectedLocationLo : Double? = null
    private val mDisposable = CompositeDisposable()
    var placeFromMain : Place? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        registerLauncher()
        basla = true

        db= Room.databaseBuilder(applicationContext,PlaceDatabase::class.java,"Places")
            //.allowMainThreadQueries()
            .build()
        placeDao= db.placeDao()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        val intent = intent
        val info = intent.getStringExtra("info")

        if(info == "new"){
            binding.saveButton.visibility = View.VISIBLE
            binding.deleteButton.visibility = View.GONE

            locationManager= this.getSystemService(LOCATION_SERVICE) as LocationManager

            locationListener= object : LocationListener {
                override fun onLocationChanged(p0: Location) {
                    if(basla){
                        val userLocation= LatLng(p0.latitude,p0.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,17f))
                        basla = false
                    }

                    println(p0.toString())
                }
            }

            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(binding.root,"Permission Needed for Location",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                        //request permission
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }else{
                    //request permission
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,10f,locationListener)

            }
            mMap.isMyLocationEnabled = true


            //36.911694, 30.726285 sevdiğimin ilk konumu
            // Add a marker in Sydney and move the camera
            /*val sevdigim= LatLng(36.911694,30.726285)
            mMap.addMarker(MarkerOptions().position(sevdigim).title("sevdiğim"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sevdigim,17f))*/

            //36.9283175,30.7309632 sevdiğimin yeni konumu
            /*val evimiz= LatLng(36.9283175,30.7309632)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(evimiz,17f))
            mMap.addMarker(MarkerOptions().position(evimiz).title("evimiz"))*/

            //36.939543, 30.806853 babamın evi
            /*val babaminEvi= LatLng(36.939543,30.806853)
            mMap.addMarker(MarkerOptions().position(babaminEvi).title("Babamın Evi"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(babaminEvi,17f))*/

            //36.9543449,30.8107331 işyeri
            /*val isyeri= LatLng(36.9543449,30.8107331)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(isyeri,19f))
            mMap.addMarker(MarkerOptions().position(isyeri).title("İşyeri"))*/
        }else{
            mMap.clear()
            placeFromMain = intent.getSerializableExtra("place") as? Place

            placeFromMain?.let {
                val ltltn = LatLng(it.latitute,it.longitute)

                mMap.addMarker(MarkerOptions().position(ltltn).title(it.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltltn,17f))

                binding.editTextPlace.setText(it.name)
                binding.saveButton.visibility= View.GONE
                binding.deleteButton.visibility= View.VISIBLE
            }

        }


    }

    private fun registerLauncher(){


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {result ->
            if(result){
                //permission granted
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,10f,locationListener)
                    mMap.isMyLocationEnabled = true
                }
            }else{
                //permission denied
                Toast.makeText(this@MapsActivity,"Need Permission",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))
        selectedLocationLa = p0.latitude
        selectedLocationLo = p0.longitude
    }

    fun save(view: View){
        var place= Place(binding.editTextPlace.text.toString(),selectedLocationLa!!,selectedLocationLo!!)
        mDisposable.add(
            placeDao.insert(place)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse) // bu işlem bittikten sonra napcaz
        )
        Toast.makeText(this,"Adaaaaam",Toast.LENGTH_LONG).show()
    }

    private fun handleResponse(){
        val intent= Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }


    fun delete(view: View?){
        placeFromMain?.let {
            mDisposable.add(placeDao.delete(it)
                    .observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mDisposable.clear()
    }
}*/
import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.muhammedkursatgokgun.mymaps.model.Place
import com.muhammedkursatgokgun.mymaps.R
import com.muhammedkursatgokgun.mymaps.databinding.ActivityMapsBinding
import com.muhammedkursatgokgun.mymaps.roomdb.PlaceDao
import com.muhammedkursatgokgun.mymaps.roomdb.PlaceDatabase
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private val mDisposable = CompositeDisposable()
    var selectedLatitude: Double? = null
    var selectedLongitude: Double? = null
    var placeFromMain: Place? = null
    private lateinit var db: PlaceDatabase
    private lateinit var placeDao: PlaceDao
    private lateinit var sharedPreferences : SharedPreferences
    var trackBoolean : Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerLauncher()

        selectedLatitude = 0.0
        selectedLongitude = 0.0

        binding.saveButton.isEnabled = false

        sharedPreferences =
            getSharedPreferences("com.muhammedkursatgokgun.mymaps", MODE_PRIVATE)
        trackBoolean = false


        db = Room.databaseBuilder(
            applicationContext,
            PlaceDatabase::class.java, "Places"
        ) //.allowMainThreadQueries()
            .build()

        placeDao = db.placeDao()

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        val intent = intent
        val info = intent.getStringExtra("info")

        if (info == "new") {
            binding.saveButton.visibility = View.VISIBLE
            binding.deleteButton.visibility = View.GONE
            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    trackBoolean = sharedPreferences.getBoolean("trackBoolean", false)
                    if (!trackBoolean!!) {
                        val userLocation = LatLng(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                        sharedPreferences.edit().putBoolean("trackBoolean", true).apply()
                    }
                }

            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //request permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(binding.root, "Permission needed for location", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
                } else {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastLocation != null) {
                    val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
                }
            }
        } else {
            //Sqlite data && intent data
            mMap.clear()
            placeFromMain = intent.getSerializableExtra("place") as? Place
            placeFromMain?.let {
                val latLng = LatLng(it.latitute, it.longitute)

                mMap.addMarker(MarkerOptions().position(latLng).title(it.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                binding.editTextPlace.setText(it.name)
                binding.saveButton.visibility = View.GONE
                binding.deleteButton.visibility = View.VISIBLE
            /*else{
            mMap.clear()
            placeFromMain = intent.getSerializableExtra("place") as? Place

            placeFromMain?.let {
                val ltltn = LatLng(it.latitute,it.longitute)

                mMap.addMarker(MarkerOptions().position(ltltn).title(it.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltltn,17f))

                binding.editTextPlace.setText(it.name)
                binding.saveButton.visibility= View.GONE
                binding.deleteButton.visibility= View.VISIBLE
            }*/
            }

        }
    }

    override fun onMapLongClick(latLng: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(latLng))
        selectedLatitude = latLng.latitude
        selectedLongitude = latLng.longitude
        binding.saveButton.isEnabled = true
    }

    private fun handleResponse() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun save(view: View?) {

        //placeDao.insert(place).subscribeOn(Schedulers.io()).subscribe();
        val place = Place(
            binding.editTextPlace.text.toString(),
            selectedLatitude!!, selectedLongitude!!
        )
        mDisposable.add(placeDao.insert(place)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse))
    }

    fun delete(view: View?) {
        placeFromMain?.let {
            mDisposable.add(placeDao.delete(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse))

        }
    }

    private fun registerLauncher() {
        permissionLauncher = registerForActivityResult(RequestPermission()) { result ->
            if (result) {
                //permission granted
                if (ContextCompat.checkSelfPermission(this@MapsActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (lastLocation != null) {
                        val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
                    }
                }
            } else {
                //permission denied
                Toast.makeText(this@MapsActivity, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable.clear()
    }

}