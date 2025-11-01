# WeatherSDK

SDK Android để lấy thông tin dự báo thời tiết từ [Open-Meteo API](https://open-meteo.com/).

## Tính năng

- 🌤️ Lấy dự báo thời tiết theo tọa độ GPS
- 📊 Hỗ trợ nhiều thông số thời tiết (nhiệt độ, lượng mưa, v.v.)
- 🔄 Sử dụng Kotlin Coroutines cho async operations
- 🏗️ Áp dụng mô hình **MVVM/Clean Architecture**
- ✅ Type-safe error handling với `Result` wrapper
- 🚀 Dễ dàng tích hợp vào dự án Android của bạn

## Yêu cầu

- Android minSdk: 24 (Android 7.0)
- Kotlin
- AndroidX

## Cài đặt

### Yêu cầu trước

Trước khi sử dụng, bạn cần publish SDK lên GitHub và JitPack. Xem hướng dẫn chi tiết trong [PUBLISH.md](PUBLISH.md).

### Gradle (Kotlin DSL)

Thêm vào `build.gradle.kts` (Project level):

```kotlin
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Thêm dependency vào `build.gradle.kts` (App level):

```kotlin
dependencies {
    implementation("com.github.nhatanhdeveloper:WeatherSDK:1.0.0")
}
```

### Gradle (Groovy)

Thêm vào `build.gradle` (Project level):

```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Thêm dependency vào `build.gradle` (App level):

```groovy
dependencies {
    implementation 'com.github.nhatanhdeveloper:WeatherSDK:1.0.0'
}
```

**Lưu ý:** 
- Thay `nhatanhdeveloper` bằng tên GitHub của bạn
- Thay `1.0.0` bằng tag version bạn đã publish (ví dụ: `v1.0.0` hoặc `1.0.0`)
- Có thể sử dụng `-SNAPSHOT` cho version đang phát triển: `1.0.0-SNAPSHOT`

## Kiến trúc

SDK được xây dựng theo mô hình **MVVM/Clean Architecture** với các layer:

- **Data Layer**: `WeatherRepository` - Xử lý API calls và data sources
- **Domain Layer**: `WeatherUseCase` - Business logic và validation
- **Presentation Layer**: `WeatherSDK` - Entry point cho người dùng SDK

## Sử dụng

### Cơ bản với Result Pattern (Khuyến nghị)

```kotlin
import com.lusa.weathersdk.WeatherSDK
import com.lusa.weathersdk.data.Result
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Khởi tạo SDK
        val weatherSDK = WeatherSDK()
        
        // Lấy thông tin thời tiết với Result pattern
        lifecycleScope.launch {
            when (val result = weatherSDK.getWeatherForecast(
                latitude = 10.762622,  // Vĩ độ (ví dụ: Ho Chi Minh City)
                longitude = 106.660172 // Kinh độ
            )) {
                is Result.Success -> {
                    val response = result.data
                    
                    // Xử lý dữ liệu
                    println("Location: ${response.latitude}, ${response.longitude}")
                    println("Timezone: ${response.timezone}")
                    
                    // Lấy nhiệt độ hiện tại
                    if (response.hourly.temperature2m.isNotEmpty()) {
                        val currentTemp = response.hourly.temperature2m[0]
                        val unit = response.hourlyUnits.temperature2m
                        println("Temperature: $currentTemp $unit")
                    }
                }
                is Result.Error -> {
                    // Xử lý lỗi
                    println("Error: ${result.exception.message}")
                    result.exception.printStackTrace()
                }
            }
        }
    }
}
```

### Lấy thông tin thời tiết với các thông số tùy chỉnh

```kotlin
val weatherSDK = WeatherSDK()

lifecycleScope.launch {
    when (val result = weatherSDK.getWeatherForecast(
        latitude = 10.762622,
        longitude = 106.660172,
        hourly = "temperature_2m,precipitation,relativehumidity_2m"
    )) {
        is Result.Success -> {
            // Xử lý response
            val response = result.data
        }
        is Result.Error -> {
            // Xử lý lỗi
        }
    }
}
```

### Sử dụng trong ViewModel (MVVM Pattern)

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lusa.weathersdk.WeatherSDK
import com.lusa.weathersdk.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    
    private val weatherSDK = WeatherSDK()
    
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState
    
    fun loadWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            
            when (val result = weatherSDK.getCurrentWeather(latitude, longitude)) {
                is Result.Success -> {
                    _weatherState.value = WeatherState.Success(result.data)
                }
                is Result.Error -> {
                    _weatherState.value = WeatherState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }
}

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val data: com.lusa.weathersdk.model.WeatherResponse) : WeatherState()
    data class Error(val message: String) : WeatherState()
}
```

### Backward Compatibility (Throw Exception)

Nếu bạn muốn sử dụng cách cũ (throw exception), có thể dùng method `getWeatherForecastOrThrow()`:

```kotlin
lifecycleScope.launch {
    try {
        val response = weatherSDK.getWeatherForecastOrThrow(
            latitude = 10.762622,
            longitude = 106.660172
        )
        // Xử lý response
    } catch (e: Exception) {
        // Xử lý lỗi
    }
}
```

## Cấu trúc dữ liệu

### WeatherResponse

```kotlin
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val generationTimeMs: Double,
    val utcOffsetSeconds: Int,
    val timezone: String,
    val timezoneAbbreviation: String,
    val elevation: Double,
    val hourlyUnits: HourlyUnits,
    val hourly: HourlyData
)
```

### HourlyData

```kotlin
data class HourlyData(
    val time: List<String>,
    val temperature2m: List<Double>,
    val precipitation: List<Double>
)
```

## Ví dụ đầy đủ với MVVM

### Activity/Fragment

```kotlin
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var textView: TextView
    private lateinit var viewModel: WeatherViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        textView = findViewById(R.id.textView)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        
        observeWeatherState()
        viewModel.loadWeather(10.762622, 106.660172)
    }
    
    private fun observeWeatherState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weatherState.collect { state ->
                    when (state) {
                        is WeatherState.Loading -> {
                            textView.text = "Đang tải..."
                        }
                        is WeatherState.Success -> {
                            val response = state.data
                            if (response.hourly.temperature2m.isNotEmpty()) {
                                val currentTemp = response.hourly.temperature2m[0]
                                val unit = response.hourlyUnits.temperature2m
                                textView.text = "Nhiệt độ: $currentTemp $unit"
                            }
                        }
                        is WeatherState.Error -> {
                            textView.text = "Lỗi: ${state.message}"
                        }
                    }
                }
            }
        }
    }
}
```

### Hoặc sử dụng trực tiếp (không cần ViewModel)

```kotlin
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.lusa.weathersdk.WeatherSDK
import com.lusa.weathersdk.data.Result

class MainActivity : AppCompatActivity() {
    
    private lateinit var textView: TextView
    private val weatherSDK = WeatherSDK()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        textView = findViewById(R.id.textView)
        loadWeather()
    }
    
    private fun loadWeather() {
        lifecycleScope.launch {
            when (val result = weatherSDK.getWeatherForecast(
                latitude = 10.762622,
                longitude = 106.660172
            )) {
                is Result.Success -> {
                    val response = result.data
                    if (response.hourly.temperature2m.isNotEmpty()) {
                        val currentTemp = response.hourly.temperature2m[0]
                        val unit = response.hourlyUnits.temperature2m
                        textView.text = "Nhiệt độ: $currentTemp $unit"
                    }
                }
                is Result.Error -> {
                    textView.text = "Lỗi: ${result.exception.message}"
                }
            }
        }
    }
}
```

## Permissions

SDK yêu cầu quyền INTERNET để gọi API. Thêm vào `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## ProGuard

Nếu bạn sử dụng ProGuard, các rules đã được tự động thêm vào. Không cần cấu hình thêm.

## Dependencies

SDK sử dụng các thư viện sau (đã được include tự động):

- Retrofit 2.9.0
- OkHttp 4.12.0
- Gson (qua Retrofit converter)
- Kotlin Coroutines 1.7.3

## API Reference

### WeatherSDK

#### `getWeatherForecast(latitude: Double, longitude: Double, hourly: String = "temperature_2m,precipitation"): Result<WeatherResponse>`

Lấy dự báo thời tiết cho một vị trí cụ thể. Trả về `Result` wrapper để xử lý lỗi type-safe.

**Parameters:**
- `latitude`: Vĩ độ (từ -90 đến 90)
- `longitude`: Kinh độ (từ -180 đến 180)
- `hourly`: Các thông số hourly cần lấy (mặc định: "temperature_2m,precipitation")

**Returns:** `Result<WeatherResponse>` - `Result.Success` nếu thành công, `Result.Error` nếu có lỗi

**Validation:** Tự động validate latitude và longitude trong UseCase

#### `getCurrentWeather(latitude: Double, longitude: Double): Result<WeatherResponse>`

Tương tự `getWeatherForecast()` với thông số mặc định.

#### `getWeatherForecastOrThrow(latitude: Double, longitude: Double, hourly: String = "temperature_2m,precipitation"): WeatherResponse`

Lấy dự báo thời tiết và throw exception nếu có lỗi (backward compatibility).

**Throws:** `Exception` nếu có lỗi xảy ra

### Result

Sealed class để wrap kết quả:

```kotlin
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
}
```

### WeatherRepository

Interface cho data layer:

```kotlin
interface IWeatherRepository {
    suspend fun getWeatherForecast(...): Result<WeatherResponse>
    suspend fun getCurrentWeather(...): Result<WeatherResponse>
}
```

### WeatherUseCase

Domain layer chứa business logic và validation.

## License

[Thêm license của bạn ở đây]

## Tác giả

[Thêm thông tin của bạn ở đây]

## Đóng góp

Mọi đóng góp đều được chào đón! Vui lòng tạo issue hoặc pull request.

## Liên kết

- [Open-Meteo API Documentation](https://open-meteo.com/en/docs)

