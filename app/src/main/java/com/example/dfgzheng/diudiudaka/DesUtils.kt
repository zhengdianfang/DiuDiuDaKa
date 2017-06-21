package com.example.dfgzheng.diudiudaka

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESedeKeySpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created by dfgzheng on 19/06/2017.
 */
class DesUtils {
    companion object{
        val ir = byteArrayOf(105, 115, 91, 115, 105, 100, 105, 119)
        val key = byteArrayOf(97, 110, 98, 97, 110, 103, 106, 105, 116, 117, 97, 110, 64, 108, 120, 49, 48, 36, 35, 51, 54, 53, 35, 36)

        @Throws(Exception::class)
        fun encode3DES(str: String, bArr: ByteArray, bArr2: ByteArray): String {
            val generateSecret = SecretKeyFactory.getInstance("desede").generateSecret(DESedeKeySpec(bArr2))
            val instance = Cipher.getInstance("desede/CBC/PKCS5Padding")
            instance.init(1, generateSecret, IvParameterSpec(bArr))
            return String(Base64.encode(instance.doFinal(str.toByteArray(charset("utf-8"))), Base64.DEFAULT))
        }

        @Throws(Exception::class)
        fun decode3DES(str: String, bArr: ByteArray, bArr2: ByteArray): String {
            val generateSecret = SecretKeyFactory.getInstance("desede").generateSecret(DESedeKeySpec(bArr2))
            val instance = Cipher.getInstance("desede/CBC/PKCS5Padding")
            instance.init(2, generateSecret, IvParameterSpec(bArr))
            return String(instance.doFinal(Base64.decode(str, Base64.DEFAULT)))
        }

        @Throws(Exception::class)
        fun encodeDES(str: String, bArr: ByteArray, str2: String): String {
            val ivParameterSpec = IvParameterSpec(bArr)
            val secretKeySpec = SecretKeySpec(str2.toByteArray(), "DES")
            val instance = Cipher.getInstance("DES/CBC/PKCS5Padding")
            instance.init(1, secretKeySpec, ivParameterSpec)
            return String(Base64.encode(instance.doFinal(str.toByteArray()), Base64.DEFAULT))
        }

        @Throws(Exception::class)
        fun decodeDES(str: String, bArr: ByteArray, str2: String): String {
            val decode = Base64.decode(str, Base64.DEFAULT)
            val ivParameterSpec = IvParameterSpec(bArr)
            val secretKeySpec = SecretKeySpec(str2.toByteArray(), "DES")
            val instance = Cipher.getInstance("DES/CBC/PKCS5Padding")
            instance.init(2, secretKeySpec, ivParameterSpec)
            return String(instance.doFinal(decode))
        }
    }


}