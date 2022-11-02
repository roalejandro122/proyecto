package com.fabianpalacios.weather


import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import com.fabianpalacios.weather.services.model.WeatherService
import android.os.Bundle
import com.fabianpalacios.weather.R
import com.fabianpalacios.weather.services.model.WeatherService.OnDataResponse
import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.fabianpalacios.weather.services.model.Root
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder

enum class ProviderType{
    BASIC
}

class MainActivity : AppCompatActivity() {
/*
     fun onCreate(savedInstanceState: Bundle?){
        //Splash
        Thread.sleep(2000) //HACK:
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Anakytics Event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message","Integracion de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

    }
*/

    private var txtCountryISOCode: EditText? = null
    private var txtCityName: EditText? = null
    private var lblCurrent: TextView? = null
    private var lblMin: TextView? = null
    private var lblMax: TextView? = null
    private var service: WeatherService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        service = WeatherService("a9cf0f7a3cc84a884d84d4df48f057c2")

        //setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?: "",provider ?: "")

    }

    fun setup(email: String,provider: String){
        title = "inicio"
        emailTextView.text = email
        providerTextView.text = provider

        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

    }

    fun initViews() {
        txtCountryISOCode = findViewById(R.id.txtCountryISOCode)
        txtCityName = findViewById(R.id.txtCityName)
        lblCurrent = findViewById(R.id.lblCurrent)
        lblMin = findViewById(R.id.lblMin)
        lblMax = findViewById(R.id.lblMax)
    }

    fun btnGetInfoOnClick(view: View?) {
        val alert = AlertDialog.Builder(this)
        val text = StringBuilder()
        if (txtCountryISOCode!!.text.toString().isEmpty() || txtCityName!!.text.toString()
                .isEmpty()
        ) {
            text.append(getString(R.string.Fields_cannot_be_empty))
            alert.setMessage(text)
            alert.setPositiveButton("close", null)
            alert.show()
        } else {
            getWeatherInfo(txtCityName!!.text.toString(), txtCountryISOCode!!.text.toString())
        }
    }

    fun getWeatherInfo(cityName: String?, countryISOCode: String?) {
        service!!.requestWeatherData(
            cityName!!,
            countryISOCode!!,
            object : OnDataResponse {
                override fun onChange(isNetworkError: Boolean, statusCode: Int, root: Root?) {
                    if (!isNetworkError) {
                        if (statusCode == 200) {
                            if (root != null) {
                                showWeatherInfo(root)
                            }
                        } else {
                            Log.d("Weather", "Service error")
                        }
                    } else {
                        Log.d("Weather", "Network error")
                    }
                }
            })
    }

    @SuppressLint("SetTextI18n")
    fun showWeatherInfo(root: Root) {
        val temp = root.main!!.temp.toString()
        val tempMin = root.main!!.tempMin.toString()
        val tempMax = root.main!!.tempMax.toString()
        lblCurrent!!.text = getString(R.string.current) + " " + temp
        lblMin!!.text = getString(R.string.minimum) + " " + tempMin
        lblMax!!.text = getString(R.string.maximum) + " " + tempMax
    }
}