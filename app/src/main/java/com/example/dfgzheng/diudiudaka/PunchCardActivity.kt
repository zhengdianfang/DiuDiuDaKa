package com.example.dfgzheng.diudiudaka

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import org.json.JSONObject
import kotlin.properties.Delegates
import android.os.Build.VERSION
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.squareup.okhttp.*
import org.json.JSONArray
import java.io.IOException
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*




/**
 * Created by dfgzheng on 20/06/2017.
 */
class PunchCardActivity : AppCompatActivity(){

    var userInfo: JSONObject? = null
    var okhttpClient: OkHttpClient by Delegates.notNull<OkHttpClient>()


    private var recyclerView: RecyclerView by Delegates.notNull<RecyclerView>()

    private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder> by Delegates.notNull<RecyclerView.Adapter<RecyclerView.ViewHolder>>()
    private var officeList: JSONArray = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_punch_card)

        recyclerView = findViewById(R.id.officeListView) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter =  object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun getItemCount(): Int {
                val length = officeList.length()
                return length
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
                val office = officeList.getJSONObject(position)
                val textView = holder?.itemView?.findViewById(R.id.nameView) as TextView
                val button = holder?.itemView?.findViewById(R.id.punchCardButton) as Button
                textView.text = office.getString("officeName")
                if (office.getString("officeId") == "2"){
                   button.setTextColor(Color.BLUE)
                }
                button.setOnClickListener {
                    punchCard(office.getDouble("longitude"), office.getDouble("latitude"), office.getInt("officeId"))
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
                return object: RecyclerView.ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.office_list_item, parent,false)){}
            }

        }
        recyclerView.adapter = adapter

        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val userInfoString = defaultSharedPreferences.getString("userinfo", "")
        if (!TextUtils.isEmpty(userInfoString)) {
            userInfo = JSONObject(userInfoString)
        }
        okhttpClient = OkHttpClient()
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), 0)
        }else{
            getOfficeList()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 0) {
            getOfficeList()
        }
    }

    fun getOfficeList() {

        val jsonObject = JSONObject()
        jsonObject.put("mobile_type", "A")
        jsonObject.put("machine_id", (application as AppApplication).getPhoneID())
        jsonObject.put("IP", (application as AppApplication).getMobileIp())
        jsonObject.put("version", (application as AppApplication).getVersionName())
        jsonObject.put("token", userInfo?.getString("token"))
        jsonObject.put("username", userInfo?.getString("username"))
        jsonObject.put("timestamp", getTime())
        jsonObject.put("accountType", userInfo?.optInt("accountType"))
        val jsonObject1 = JSONObject()

        val decimalFormat = DecimalFormat("####.000000")
        val format = decimalFormat.format(116.4668480424)
        val format2 = decimalFormat.format(39.9144156336)

        jsonObject1.put("longitude", format)
        jsonObject1.put("latitude", format2)
        jsonObject1.put("ab_number", userInfo?.getString("abNumber"))

        val aa = DesUtils.encode3DES(jsonObject1.toString(), DesUtils.ir, DesUtils.key).replace("\n","")
        val params = URLEncoder.encode(aa, "UTF-8")

        val request = Request.Builder().url("http://work.bangcommunity.com/work/nvt/punch/getOfficeList.do")
                .method("POST", RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "id=" + params))
                .header("header", DesUtils.encode3DES(jsonObject.toString(), DesUtils.ir, DesUtils.key).replace("\n",""))
                .header("username", userInfo?.getString("username"))
                .header("token", userInfo?.getString("token"))
                .build()
        Log.d("PunchCardActivity", jsonObject1.toString())
        Log.d("PunchCardActivity", jsonObject.toString())
        Log.d("PunchCardActivity",  DesUtils.encode3DES(jsonObject.toString(), DesUtils.ir, DesUtils.key))
        Log.d("PunchCardActivity",  DesUtils.encode3DES(jsonObject1.toString(), DesUtils.ir, DesUtils.key))
        Log.d("PunchCardActivity",  params)

        okhttpClient.newCall(request).enqueue(object : Callback{
            override fun onFailure(request: Request?, e: IOException?) {

            }

            override fun onResponse(response: Response?) {
                val data = response?.body()?.string()
                Log.d("PunchCardActivity", data)
                val jsonObject2 = JSONObject(data)
                val jsonObject3 = jsonObject2.getJSONObject("RESULT_DATA")
                val jsonArray = jsonObject3.getJSONArray("officeList")
                runOnUiThread({
                    officeList = jsonArray
                    adapter.notifyDataSetChanged()
                })

            }

        })
    }

    fun punchCard(longitude: Double, latitude: Double, officeId: Int){
        val jsonObject = JSONObject()
        val jsonObject1 = JSONObject()

        try {
            jsonObject.put("mobile_type", "A")
            jsonObject.put("machine_id", (application as AppApplication).getPhoneID())
            jsonObject.put("IP", (application as AppApplication).getMobileIp())
            jsonObject.put("version", userInfo?.getString("newversion"))
            jsonObject.put("token", userInfo?.getString("token"))
            jsonObject.put("username", userInfo?.getString("username"))
            jsonObject.put("timestamp", getTime())
            jsonObject.put("accountType", userInfo?.optInt("accountType"))
            Log.d("PunchCardActivity", userInfo?.getString("newversion"))
            jsonObject1.put("longitude", longitude)
            jsonObject1.put("latitude", latitude)
            jsonObject1.put("ab_number", userInfo?.getString("abNumber"))
            jsonObject1.put("ab_name", userInfo?.getString("abNumber"))
            jsonObject1.put("officeId", officeId)
            jsonObject1.put("module", "W")
            val aa = DesUtils.encode3DES(jsonObject1.toString(), DesUtils.ir, DesUtils.key).replace("\n","")
            val params = URLEncoder.encode(aa, "UTF-8")
            val headers = DesUtils.encode3DES(jsonObject.toString(), DesUtils.ir, DesUtils.key).replace("\n","")
            val request = Request.Builder().url("http://work.bangcommunity.com/work/nvt/punch/punchCard.do")
                    .method("POST", RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "id=" + params))
                    .header("header", headers)
                    .header("username", userInfo?.getString("username"))
                    .header("token", userInfo?.getString("token"))
                    .build()

            Log.d("PunchCardActivity", jsonObject.toString())
            Log.d("PunchCardActivity", jsonObject1.toString())
            Log.d("PunchCardActivity", params)
            Log.d("PunchCardActivity", headers)
            okhttpClient.newCall(request).enqueue(object : Callback{
                override fun onFailure(request: Request?, e: IOException?) {

                }

                override fun onResponse(response: Response?) {
                    val data = response?.body()?.string()
                    Log.d("PunchCardActivity", data)
                    val jsonObject2 = JSONObject(data)
                    val string = jsonObject2.getJSONObject("RESULT_DATA").getString("errorMsg")
                    runOnUiThread({
                        Toast.makeText(recyclerView.context, string, Toast.LENGTH_SHORT).show()
                    })

                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getTime() : String{
        val simpleDateFormat = SimpleDateFormat("yyyyMMddHHmmssSSS")
        return simpleDateFormat.format(Date())
    }
}