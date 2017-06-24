package com.example.dfgzheng.diudiudaka

import android.app.Application
import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager
import java.util.*
import android.text.TextUtils
import java.net.Inet4Address
import java.net.NetworkInterface


/**
 * Created by dfgzheng on 20/06/2017.
 */
class AppApplication : Application(){

    var phoneIp = ""

    fun getPhoneID(): String {
//        val telephonyManager = baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        val str = telephonyManager.deviceId
        //return UUID((Settings.Secure.getString(contentResolver, "android_id")).hashCode().toLong(),("80914103211118510720").hashCode().toLong() or (str.hashCode().toLong() shl 32)).toString()
        return "ffffffff-8ddb-fa06-6dc3-386d0033c587"

     //   (telephonyManager.simSerialNumber).hashCode().toLong() or (str.hashCode().toLong() shl 32)
    }


    fun getMobileIp(): String {
        if (!TextUtils.isEmpty(phoneIp)) {
            return phoneIp
        }
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val inetAddresses = (networkInterfaces.nextElement() as NetworkInterface).inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        val str = inetAddress.getHostAddress().toString()
                        phoneIp = str
                        return str
                    }
                }
            }
        } catch (th: Throwable) {
            th.printStackTrace()
        }

        return phoneIp
    }

    fun getVersionName () : String{
        return packageManager.getPackageInfo(packageName, 0).versionName
    }
}