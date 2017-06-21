package com.anbang.bbchat.helper

import com.example.dfgzheng.diudiudaka.DesUtils
import kotlin.properties.Delegates

/**
 * Created by dfgzheng on 18/06/2017.
 */

object Des3Helper {
    private var sr: ByteArray by Delegates.notNull<ByteArray>()
    private var ir: ByteArray by Delegates.notNull<ByteArray>()

    private external fun getIR(): ByteArray

    private val iu: ByteArray = byteArrayOf()
        external get

    private external fun getSR(): ByteArray

    private val su: ByteArray = byteArrayOf()
        external get

    init {
        try {
            System.loadLibrary("s")
        } catch (th: Throwable) {
        }

        sr = getSR()
        ir = getIR()
    }

    fun encode3DES(str: String): String {
        return DesUtils.encode3DES(str, ir, sr)
    }

//    fun decode3DES(str: String): String {
//        return DesUtils.decode3DES(str, ir, sr)
//    }
}
