package com.lusa.weathersdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.lusa.weathersdk.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    
    private lateinit var tvLocation: TextView
    private lateinit var tvTimezone: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvPrecipitation: TextView
    private lateinit var tvHourlyData: TextView
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        loadWeatherData()
    }
    
    private fun initViews() {
        tvLocation = findViewById(R.id.tvLocation)
        tvTimezone = findViewById(R.id.tvTimezone)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvPrecipitation = findViewById(R.id.tvPrecipitation)
        tvHourlyData = findViewById(R.id.tvHourlyData)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun loadWeatherData() {
        progressBar.visibility = android.view.View.VISIBLE
        tvError.visibility = android.view.View.GONE
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.weatherApiService.getWeatherForecast(
                        latitude = 10.762622,
                        longitude = 106.660172,
                        hourly = "temperature_2m,precipitation"
                    )
                }
                
                displayWeatherData(response)
            } catch (e: Exception) {
                tvError.text = "Lỗi: ${e.message}"
                tvError.visibility = android.view.View.VISIBLE
                Toast.makeText(this@MainActivity, "Lỗi khi tải dữ liệu: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                progressBar.visibility = android.view.View.GONE
            }
        }
    }
    
    private fun displayWeatherData(response: com.lusa.weathersdk.model.WeatherResponse) {
        // Hiển thị thông tin vị trí
        tvLocation.text = "Vị trí: ${response.latitude}, ${response.longitude}"
        tvTimezone.text = "Múi giờ: ${response.timezone} (${response.timezoneAbbreviation})"
        
        // Lấy dữ liệu hiện tại (giờ đầu tiên)
        if (response.hourly.time.isNotEmpty() && 
            response.hourly.temperature2m.isNotEmpty() && 
            response.hourly.precipitation.isNotEmpty()) {
            
            val currentTime = response.hourly.time[0]
            val currentTemp = response.hourly.temperature2m[0]
            val currentPrecip = response.hourly.precipitation[0]
            
            tvTemperature.text = "Nhiệt độ: $currentTemp ${response.hourlyUnits.temperature2m}"
            tvPrecipitation.text = "Lượng mưa: $currentPrecip ${response.hourlyUnits.precipitation}"
            
            // Hiển thị dữ liệu 24 giờ đầu
            val hourlyText = StringBuilder()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val displayFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
            
            val hoursToShow = minOf(24, response.hourly.time.size)
            for (i in 0 until hoursToShow) {
                try {
                    val date = dateFormat.parse(response.hourly.time[i])
                    val displayTime = if (date != null) displayFormat.format(date) else response.hourly.time[i]
                    hourlyText.append("$displayTime - ")
                    hourlyText.append("Nhiệt độ: ${response.hourly.temperature2m[i]}${response.hourlyUnits.temperature2m}, ")
                    hourlyText.append("Mưa: ${response.hourly.precipitation[i]}${response.hourlyUnits.precipitation}\n")
                } catch (e: Exception) {
                    hourlyText.append("${response.hourly.time[i]} - ")
                    hourlyText.append("Nhiệt độ: ${response.hourly.temperature2m[i]}${response.hourlyUnits.temperature2m}, ")
                    hourlyText.append("Mưa: ${response.hourly.precipitation[i]}${response.hourlyUnits.precipitation}\n")
                }
            }
            
            tvHourlyData.text = hourlyText.toString()
        }
    }
}