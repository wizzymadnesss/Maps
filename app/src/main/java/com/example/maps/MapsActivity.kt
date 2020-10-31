package com.example.maps

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback , GoogleMap.OnMarkerClickListener, AdapterView.OnItemSelectedListener{

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var lastLocation:Location

    private lateinit var mylatLng: LatLng

    private lateinit var polyline: Polyline

    var haveUbication: Boolean = false
    var haveLines: Boolean = false

    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE=1
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        //Spinner
        val spinner=findViewById<Spinner>(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.spinner,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener=this
        spinner.setSelection(0)


        //My Location
        fusedLocationClient=LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))


        mMap.setOnMarkerClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled=true

        setUpMap()

    }

    override fun onMarkerClick(p0: Marker?): Boolean = false


    private fun setUpMap(){
        if(ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        mMap.isMyLocationEnabled=true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) {location ->

            if(location != null){

                lastLocation=location
                val currentLatLong = LatLng(location.latitude,location.longitude)

                val markerOption = MarkerOptions().position(currentLatLong)
                mMap.addMarker(markerOption)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong,13f))
                mylatLng=currentLatLong
                haveUbication=true
            }

        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        when(position){
            0 -> {
                mMap.mapType=GoogleMap.MAP_TYPE_NORMAL
                newOption(R.drawable.world,LatLng(35.680513, 139.769051),"Japón")
            }
            1 -> {
                mMap.mapType=GoogleMap.MAP_TYPE_SATELLITE
                newOption(R.drawable.satellite,LatLng(52.516934, 13.403190),"Alemania")
            }
            2 -> {
                mMap.mapType=GoogleMap.MAP_TYPE_HYBRID
                newOption(R.drawable.mountains,LatLng(41.902609, 12.494847),"Italia")
            }
            3 -> {
                mMap.mapType=GoogleMap.MAP_TYPE_TERRAIN
                newOption(R.drawable.plains, LatLng(48.843489, 2.355331),"Francia")
            }
        }
        Toast.makeText(this,"Change",Toast.LENGTH_SHORT).show()
    }

    private fun newOption(image:Int,latLng: LatLng,name:String){

        if(haveLines){
            polyline.remove()
        }

        //Marker
        mMap.addMarker(MarkerOptions()
            .icon(BitmapDescriptorFactory.fromResource((image)))
            .anchor(0f,1f)
            .position(latLng)
            .title(name)
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13f))

        //Longitud
        if(haveUbication){
            val polylineOptions = PolylineOptions()
                .add(latLng)
                .add(mylatLng)

            polyline=mMap.addPolyline(polylineOptions)
            haveLines=true
        }

    }
}