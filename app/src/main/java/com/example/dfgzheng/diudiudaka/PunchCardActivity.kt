package com.example.dfgzheng.diudiudaka

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import org.json.JSONObject
import kotlin.properties.Delegates
import android.os.Build.VERSION
import android.util.Log
import com.squareup.okhttp.*
import java.io.IOException
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*




/**
 * Created by dfgzheng on 20/06/2017.
 */
class PunchCardActivity : AppCompatActivity(){

    val latitudeTarget  = 39.915
    val longitudeTarget = 116.472
    val officeId = 2

    var userInfo: JSONObject? = null
    var okhttpClient: OkHttpClient by Delegates.notNull<OkHttpClient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_punch_card)

        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val userInfoString = defaultSharedPreferences.getString("userinfo", "")
        if (!TextUtils.isEmpty(userInfoString)) {
            userInfo = JSONObject(userInfoString)
        }
        okhttpClient = OkHttpClient()
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), 0)
        }else{
          //  getOfficeList()
        }

        findViewById(R.id.punchCard).setOnClickListener {
            punchCard()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 0) {
            //getOfficeList()
        }
    }

    fun getOfficeList() {

        val jsonObject = JSONObject()
        jsonObject.put("mobile_type", "A")
        jsonObject.put("machine_id", (application as AppApplication).getPhoneID())
        jsonObject.put("IP", (application as AppApplication).getMobileIp())
       // jsonObject.put("IP", "10.0.8.1")
        jsonObject.put("version", (application as AppApplication).getVersionName())
        jsonObject.put("token", userInfo?.getString("token"))
//        jsonObject.put("token", "4be87dc0b6814cf0a79328bb8e4c93ac")
        jsonObject.put("username", userInfo?.getString("username"))
        jsonObject.put("timestamp", getTime())
//        jsonObject.put("timestamp", "20170621211254707")
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

            }

        })
    }

    fun punchCard() {
        val jsonObject = JSONObject()
        val jsonObject1 = JSONObject()
        try {
            jsonObject.put("mobile_type", "A")
            jsonObject.put("machine_id", (application as AppApplication).getPhoneID())
            jsonObject.put("IP", (application as AppApplication).getMobileIp())
            jsonObject.put("version", (application as AppApplication).getVersionName())
            jsonObject.put("token", userInfo?.getString("token"))
            jsonObject.put("username", userInfo?.getString("username"))
            jsonObject.put("timestamp", getTime())
            jsonObject.put("accountType", userInfo?.optInt("accountType"))

            jsonObject1.put("longitude", longitudeTarget)
            jsonObject1.put("latitude", latitudeTarget)
            jsonObject1.put("ab_number", userInfo?.getString("abNumber"))
            jsonObject1.put("ab_name", userInfo?.getString("abNumber"))
            jsonObject1.put("officeId", officeId)
            jsonObject1.put("module", "W")
            val aa = DesUtils.encode3DES(jsonObject1.toString(), DesUtils.ir, DesUtils.key).replace("\n","")
            val params = URLEncoder.encode(aa, "UTF-8")

            val request = Request.Builder().url("http://work.bangcommunity.com/work/nvt/punch/punchCard.do")
                    .method("POST", RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "id=" + params))
                    .header("header", DesUtils.encode3DES(jsonObject.toString(), DesUtils.ir, DesUtils.key).replace("\n",""))
                    .header("username", userInfo?.getString("username"))
                    .header("token", userInfo?.getString("token"))
                    .build()


            okhttpClient.newCall(request).enqueue(object : Callback{
                override fun onFailure(request: Request?, e: IOException?) {

                }

                override fun onResponse(response: Response?) {
                    val data = response?.body()?.string()
                    Log.d("PunchCardActivity", data)

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