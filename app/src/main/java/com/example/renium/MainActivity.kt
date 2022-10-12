package com.example.renium


import android.app.ProgressDialog
import android.graphics.Color

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.renium.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

import com.google.android.material.datepicker.MaterialDatePicker
import io.github.farshidroohi.ChartEntity
import io.github.farshidroohi.LineChart

import org.json.JSONObject
import java.math.BigDecimal
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    lateinit var btnStartDate: Button
    lateinit var btnEndDate: Button
    lateinit var btnSubmit: Button
    private var startDate: String? = null
    private var endDate: String? = null
    var volleyRequestQueue: RequestQueue? = null
    var dialog: ProgressDialog? = null
    var relative_velocity: String = ""
    private lateinit var binding: ActivityMainBinding
    var asteroidName = ""
    var asteroidId = ""
    var asteroidNameAndId: String = ""
    var asteroidList = ArrayList<String>()
    var asteroidOnDatesList = kotlin.collections.ArrayList<AsteroidOnDates>()
    val entries: ArrayList<BarEntry> = ArrayList()
    val labels = ArrayList<String>()

    val barArrayList = kotlin.collections.ArrayList<BarEntry>()
    val labelsNames = kotlin.collections.ArrayList<String>()


    val TAG = "Handy Opinion Tutorials"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        btnStartDate = findViewById(R.id.btnStartDate)
        btnEndDate = findViewById(R.id.btnEndDate)
        btnSubmit = findViewById(R.id.btnSubmit)





        btnStartDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager, "DatePicker")
            datePicker.addOnPositiveButtonClickListener {
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
                val date = dateFormatter.format(Date(it))
                btnStartDate.text = date
                startDate = date

            }
            datePicker.addOnNegativeButtonClickListener {
                Toast.makeText(this, "${datePicker.headerText} is cancelled", Toast.LENGTH_LONG)
                    .show()
            }
            datePicker.addOnCancelListener {
                Toast.makeText(this, "Date Picker Cancelled", Toast.LENGTH_LONG).show()
            }
        }
        btnEndDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager, "DatePicker")
            datePicker.addOnPositiveButtonClickListener {
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
                val date = dateFormatter.format(Date(it))
                btnEndDate.text = date
                endDate = date

            }
            datePicker.addOnNegativeButtonClickListener {
                Toast.makeText(this, "${datePicker.headerText} is cancelled", Toast.LENGTH_LONG)
                    .show()
            }
            datePicker.addOnCancelListener {
                Toast.makeText(this, "Date Picker Cancelled", Toast.LENGTH_LONG).show()
            }
        }
        btnSubmit.setOnClickListener {
            Log.d(
                "najishResponse",
                "https://api.nasa.gov/neo/rest/v1/feed?start_date=$startDate&end_date=$endDate&api_key=DEMO_KEY"
            )
            SendSignUpDataToServer()
        }


    }

    fun SendSignUpDataToServer() {
        volleyRequestQueue = Volley.newRequestQueue(this)
        dialog = ProgressDialog.show(this, "", "Please wait...", true);


        val strReq: StringRequest = @RequiresApi(Build.VERSION_CODES.N)
        object : StringRequest(
            Method.GET,
            "https://api.nasa.gov/neo/rest/v1/feed?start_date=$startDate&end_date=$endDate&api_key=DEMO_KEY",
            com.android.volley.Response.Listener { response ->
                Log.e(TAG, "response: " + response)
                dialog?.dismiss()

                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)
                    val elementCount = responseObj.getInt("element_count")
                    val near_earth_objects = responseObj.getJSONObject("near_earth_objects")


                    val listOfDates = getDates(startDate!!, endDate!!)
                    val sortedDistance = ArrayList<BigDecimal>()
                    val asteroidLengthOnDate = ArrayList<Int>()

                    listOfDates?.forEach {
                        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
                        val date = dateFormatter.format(it)
                        val asteroidOnDate = near_earth_objects.getJSONArray(date)
                        asteroidLengthOnDate.add(asteroidOnDate.length())

                        asteroidOnDatesList.add(
                            AsteroidOnDates(
                                date.toString(),
                                asteroidOnDate.length()
                            )
                        )



                        Log.d(
                            "asteroidOnDate",
                            "Date ".plus(date.plus(" ").plus(asteroidOnDate.toString()))
                        )
                        val length = asteroidOnDate.length()
                        var size: Double = 0.0

                        for (i in 0 until length) {
                            val data = asteroidOnDate.getJSONObject(i)
                            size += data.getJSONObject("estimated_diameter")
                                .getJSONObject("kilometers").getDouble("estimated_diameter_max")

                            val close_approach_data = data.getJSONArray("close_approach_data")
                            asteroidName = data.getString("name")
                            asteroidId = data.getString("id")
                            val approachDataLength = close_approach_data.length()
                            val listOfAsteroidDistanceFromEarth = ArrayList<String>()

                            for (i in 0 until approachDataLength) {
                                val distanceData = close_approach_data.getJSONObject(i)
                                val close_approach_date =
                                    distanceData.getString("close_approach_date")
                                val distanceInKilometers =
                                    distanceData.getJSONObject("miss_distance")
                                        .getString("kilometers")


                                relative_velocity = distanceData.getJSONObject("relative_velocity")
                                    .getString("kilometers_per_hour")


                                //2. closest asteroid
                                Log.d(
                                    "close_approach_datee",
                                    close_approach_date.toString().plus(" distance")
                                        .plus(distanceInKilometers).plus(asteroidName)
                                        .plus(asteroidId)
                                )
                                val asteroidWithDates =
                                    close_approach_date.toString().plus(" distance")
                                        .plus(distanceInKilometers)
                                asteroidNameAndId =
                                    asteroidWithDates.plus(asteroidName).plus(asteroidId)
                                Log.d("asteroidNameAndId", asteroidNameAndId)
                                asteroidList.add(asteroidNameAndId)


                                val sortedDistance =
                                    asteroidWithDates.substring(asteroidWithDates.lastIndexOf("e") + 1)
                                Log.d("asteroidDistance", sortedDistance)
                                listOfAsteroidDistanceFromEarth.add(sortedDistance)


                                //3 estimated diameter
                                val averageSizeInKm = size / length
                                Log.d("estimated_diameterr", averageSizeInKm.toString())
                                binding.tvAverageSizeOfAsteroids.text = averageSizeInKm.toString()


                            }
                            val list: ArrayList<Double> = ArrayList()
                            list.add(relative_velocity.toDouble())
                            Collections.sort(list, Collections.reverseOrder())

                            //1 fastest asteroid

                            val fastestAsteroid = list[0]
                            Log.d(
                                "speedInKM",
                                fastestAsteroid.toString().plus(asteroidId).plus(asteroidName)
                            )
                            binding.tvFastestAsteroidId.text = asteroidId
                            binding.tvFastestAsteroidName.text = asteroidName
                            binding.tvFastestAsteroidSpeed.text = fastestAsteroid.toString()



                            for (d in listOfAsteroidDistanceFromEarth) {
                                val converted = d.toBigDecimal()
                                sortedDistance.add(converted)


                            }


                        }


                    }
                    Log.d("najishAsteroid", asteroidOnDatesList.toString())

                    for (i in 0 until asteroidOnDatesList.size) {
                        entries.add(BarEntry(asteroidOnDatesList[i].size.toFloat(), i))
                        labels.add(asteroidOnDatesList[i].month)
                    }
                    val bardataset = BarDataSet(entries, "Dates")
                    val data = BarData(labels, bardataset)
                    binding.barChart.data = data
                    binding.barChart.animateY(5000)






                    Collections.sort(sortedDistance, Comparator.reverseOrder());
                    Log.d("sortedDistance", sortedDistance.toString())
                    val closestDistance = sortedDistance.last()
                    asteroidList.forEach {
                        if (it.contains(closestDistance.toString())) {
                            val closestData = it
                            Log.d("closestData", closestData)

                            var distance = closestData
                            distance = distance.substring(distance.indexOf("e") + 1)
                            distance = distance.substring(0, distance.indexOf("("))

                            var name = closestData
                            name = name.substring(name.indexOf("(") + 1)
                            name = name.substring(0, name.indexOf(")"))

                            var id = closestData
                            id = id.substring(id.lastIndexOf(")") + 1)

                            binding.tvClosestAsteroidName.text = name
                            binding.tvClosestAsteroidId.text = id
                            binding.tvClosestAsteroidDistance.text = distance


                        }
                    }


                } catch (e: Exception) { // caught while parsing the response
                    Log.e(TAG, "problem occurred")
                    e.printStackTrace()
                }
            },
            com.android.volley.Response.ErrorListener { volleyError -> // error occurred
                Log.e(TAG, "problem occurred, volley error: " + volleyError.printStackTrace())
                dialog?.dismiss()
            }) {


            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                val headers: MutableMap<String, String> = HashMap()
                // Add your Header paramters here
                return headers
            }
        }
        // Adding request to request queue
        volleyRequestQueue?.add(strReq)
    }


    private fun getDates(dateString1: String, dateString2: String): List<Date>? {
        val dates = ArrayList<Date>()
        val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = df1.parse(dateString1)
            date2 = df1.parse(dateString2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        while (!cal1.after(cal2)) {
            dates.add(cal1.time)
            cal1.add(Calendar.DATE, 1)
        }
        return dates
    }


    companion object {

        private val barSet = listOf(
            "JAN" to 4F,
            "FEB" to 7F,
            "MAR" to 2F,
            "MAY" to 2.3F,
            "APR" to 5F,
            "JUN" to 4F
        )

    }
}








