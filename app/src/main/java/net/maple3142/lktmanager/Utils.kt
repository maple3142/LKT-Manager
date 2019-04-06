package net.maple3142.lktmanager

import java.io.BufferedReader
import java.io.InputStreamReader

class SuException(msg: String) : Exception(msg)

fun sudo(cmd: String): String {
    val proc = Runtime.getRuntime().exec("su")
    val ins = proc.inputStream
    val outs = proc.outputStream
    outs.write(cmd.toByteArray())
    outs.flush()
    outs.close()
    proc.waitFor()
    val reader = BufferedReader(InputStreamReader(ins))
    val result = reader.lineSequence().joinToString()
    if (proc.exitValue() != 0) {
        throw SuException("No permission!")
    }
    return result
}

fun hasRoot(): Boolean {
    return try {
        sudo("echo Hello World")
        true
    } catch (e: SuException) {
        false
    }
}

val profilereg = """PROFILE : (\w+)""".toRegex()
fun getProfile(): String? {
    val content = sudo("cat /data/LKT.prop")
    val result = profilereg.find(content)
    return result?.groupValues?.get(1)
}

val busyboxreg = """BUSYBOX : ([a-z0-9.\-]*)""".toRegex()
fun getBusyBoxVersion(): String? {
    val content = sudo("cat /data/LKT.prop")
    val result = busyboxreg.find(content)
    return result?.groupValues?.get(1)
}

val lktreg = """LKT™ (v\d\.\d)""".toRegex()
fun getLKTVersion(): String? {
    val content = sudo("cat /data/LKT.prop")
    val result = lktreg.find(content)
    return result?.groupValues?.get(1)
}
