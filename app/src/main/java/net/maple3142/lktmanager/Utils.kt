package net.maple3142.lktmanager

import com.topjohnwu.superuser.Shell

class SuException(msg: String) : Exception(msg)

//fun sudo(cmd: String): String {
//    val proc = Runtime.getRuntime().exec("su")
//    val ins = proc.inputStream
//    val outs = proc.outputStream
//    outs.write(cmd.toByteArray())
//    outs.flush()
//    outs.close()
//    proc.waitFor()
//    val result = ins.bufferedReader().use { it.readText() }
//    if (proc.exitValue() != 0) {
//        throw SuException("No permission!")
//    }
//    return result
//}

fun hasRoot(): Boolean {
    val code = Shell.su("echo Hello World").exec().code
    return code == 0
}

data class LKTStatus(val profileName: String?, val busyboxVersion: String?, val LKTVersion: String?)

val profilereg = """PROFILE : (\w+)""".toRegex()
val busyboxreg = """BUSYBOX : ([a-z0-9.\-]*)""".toRegex()
val lktreg = """LKT™ (v\d\.\d)""".toRegex()
fun getLKTStatus(): LKTStatus? {
    return try {
        val content = Shell.su("cat /data/LKT.prop").exec().out.joinToString()
        val profile = profilereg.find(content)?.groupValues?.get(1)
        val busybox = busyboxreg.find(content)?.groupValues?.get(1)
        val lkt = lktreg.find(content)?.groupValues?.get(1)
        LKTStatus(profile, busybox, lkt)
    } catch (e: SuException) {
        null
    }
}
