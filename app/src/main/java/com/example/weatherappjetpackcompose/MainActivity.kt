package com.example.weatherappjetpackcompose


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherappjetpackcompose.data.WeatherModel
import com.example.weatherappjetpackcompose.screens.DialogSearch
import com.example.weatherappjetpackcompose.screens.MainCard
import com.example.weatherappjetpackcompose.screens.TabLayout
import com.example.weatherappjetpackcompose.ui.theme.WeatherAppJetpackComposeTheme
import org.json.JSONObject

////https://www.weatherapi.com/
const val API_KEY = "1587944526bb42c4a40104029230308"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            WeatherAppJetpackComposeTheme {

                val daysList = remember {
                    mutableStateOf(listOf<WeatherModel>())
                }
                //показывает состояние
                val dialogState = remember {
                    mutableStateOf(false)
                }

                val currentDay = remember {
                    mutableStateOf(WeatherModel(
                        "",
                        "",
                        //заполнить ,чтобы не было ошибки
                        "0.0",
                        "",
                        "",
                        "0.0",
                        "0.0",
                        ""
                    )
                    )
                }

                //показывает диалог
                if (dialogState.value) {
                    DialogSearch(dialogState, onSubmit = {
                        //передаем написанный текст -город
                       getData(it, this, daysList, currentDay)
                    })
                }

               getData("London", this, daysList, currentDay)

                //  фон
                Image(
                    painter = painterResource(id = R.drawable.weather_bg),
                    contentDescription = "im1",
                    //какртинка занимает весь экран
                    modifier = Modifier
                        .fillMaxSize()
                        //прозрачность
                        .alpha(0.5f),
                    //растягивает на весь экран
                    contentScale = ContentScale.FillBounds
                )




                Column {
                    MainCard(currentDay, onClickSync = {
                        getData("London", this@MainActivity, daysList, currentDay)
                    }, onClickSearch = {
                        dialogState.value = true
                    }
                    )
                    TabLayout(daysList, currentDay)
                }

            }
        }
    }
}

private fun getData(city: String, context: Context,
                    daysList: MutableState<List<WeatherModel>>,
                    currentDay: MutableState<WeatherModel>){
    val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
            "&q=$city" +
            "&days=" +
            "3" +
            "&aqi=no&alerts=no"
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        {
                response ->
            val list = getWeatherByDays(response)
            currentDay.value = list[0]
            daysList.value = list
        },
        {
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}

private fun getWeatherByDays(response: String): List<WeatherModel>{
    if (response.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

    for (i in 0 until days.length()){
        val item = days[i] as JSONObject
        list.add(
            WeatherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition")
                    .getString("text"),
                item.getJSONObject("day").getJSONObject("condition")
                    .getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()

            )
        )
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c"),
    )
    return list
}



//import org.json.JSONObject
//
////https://www.weatherapi.com/
//const val API_KEY = "1587944526bb42c4a40104029230308"
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            WeatherAppJetpackComposeTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
//                    Greeting("Moscow",this)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun Greeting(name: String,context: Context) {
//   //состояние
//    val state = remember {
//        mutableStateOf("Unknown")
//    }
//
//    //колонна на весь экран
//    Column(modifier = Modifier.fillMaxSize()) {
//
//        //коробка половину
//        Box(modifier = Modifier.fillMaxHeight(0.5f)
//            //занять всю ширину
//            .fillMaxWidth()
////            //цвет коробки
////            .background(color = Color.Blue)
//           ,
//            //размещаю по центру
//            contentAlignment = Alignment.Center
//
//        ){
//            //передаем состояние state
//            Text(text = "Temp $name=${state.value}")
//        }
//
//        //коробка займет то что осталось
//        Box(modifier = Modifier.fillMaxHeight()
//            //занять всю ширину
//            .fillMaxWidth()
////            //цвет коробки
////            .background(color = Color.Green)
//            ,
//                //размещаю внизу по центру
//                contentAlignment = Alignment.BottomCenter
//        ) {
//           //кнопка с текстом
//            Button(onClick = {
//                getData(name, context , state)
//
//            }, //тступ кнопки
//                modifier = Modifier.padding(5.dp)
//               // по всей ширине
//                .fillMaxWidth()
//            ) { Text(text = "Refresh") }
//
//
//        }
//    }
//}
//fun getData(name: String, context: Context, mState: MutableState<String>){
//    val url = "https://api.weatherapi.com/v1/current.json" +
//            "?key=$API_KEY&" +
//            "q=$name" +
//            "&aqi=no"
//    val queue = Volley.newRequestQueue(context)
//    val stringRequest = StringRequest(
//        Request.Method.GET,
//        url,
//        {
//                response->
//            val obj = JSONObject(response)
//            val temp = obj.getJSONObject("current")
//            mState.value = temp.getString("temp_c")
//            Log.d("MyLog","Response: ${temp.getString("temp_c")}")
//        },
//        {
//            Log.d("MyLog","Volley error: $it")
//        }
//    )
//    queue.add(stringRequest)
//}