package lktm.maple3142.net.lktmanager

import android.app.Activity
import android.content.DialogInterface
import android.os.Process
import android.support.v7.app.AlertDialog
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

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

val reg = """PROFILE : (\w+)""".toRegex()
fun getProfile(): String? {
    val content = sudo("cat /data/LKT.prop")
    val result = reg.find(content)
    return result?.groupValues?.get(1)
}

fun showMessage(activity: Activity, message: String, exit: Boolean=false) {
    AlertDialog.Builder(activity)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { _, _ ->
                if(exit){
                    System.exit(0)
                }
            }).create().show()
}
