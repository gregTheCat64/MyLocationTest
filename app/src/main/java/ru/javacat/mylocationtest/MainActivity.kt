package ru.javacat.mylocationtest

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ru.javacat.mylocationtest.databinding.ActivityMainBinding
import java.util.function.Consumer

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private val typeOfLocation = ACCESS_COARSE_LOCATION

    private lateinit var locationManager: LocationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        checkPermission()

        mainBinding.button.setOnClickListener {
            getLocation()
        }
    }


    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                typeOfLocation
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionListener()
            pLauncher.launch(typeOfLocation)
        } else getLocation()
    }

    private fun isLocationEnabled(): Boolean {
        //val lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun permissionListener() {

        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it == true) {
                getLocation()
            } else
                Toast.makeText(this, getString(R.string.permission_alarm), Toast.LENGTH_SHORT)
                    .show()
        }
    }


    private fun getLocation() {
        if (!isLocationEnabled()) {
            Toast.makeText(this, "Location disabled", Toast.LENGTH_SHORT).show()
            return
        } else {
            if (
                ActivityCompat.checkSelfPermission(
                    this,
                    typeOfLocation
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return

            } else {
                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
                //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0f, this)
//            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                when (Build.VERSION.SDK_INT) {
                    in 1..29 -> {
                        locationManager.requestSingleUpdate(
                            LocationManager.NETWORK_PROVIDER,
                            this,
                            null
                        )
                        Toast.makeText(this, "getting from networkProvider with request", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        Toast.makeText(this, "getting from networkProvider with getCurrent", Toast.LENGTH_SHORT)
                        locationManager.getCurrentLocation(
                            LocationManager.NETWORK_PROVIDER,
                            null,
                            application.mainExecutor
                        ) {
                            Toast.makeText(this, "getting from networkProvider with getCurrent, ${it.toString()}", Toast.LENGTH_SHORT).show()
                            mainBinding.textView.setText(it.longitude.toString() + ", " + it.latitude.toString())

                        }
                    }
                }
            }
        }
    }


    override fun onLocationChanged(location: Location) {
        mainBinding.textView.setText(location.longitude.toString() + ", " + location.latitude.toString())
    }

}



