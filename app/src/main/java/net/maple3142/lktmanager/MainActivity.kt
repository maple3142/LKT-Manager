package net.maple3142.lktmanager

import android.app.ActivityManager
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.topjohnwu.superuser.Shell

class MainActivity : AppCompatActivity() {

    val ids = arrayListOf(R.id.battery, R.id.balanced, R.id.performance, R.id.turbo)
    var btns: List<Button>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        StrictMode::class.java.getMethod("disableDeathOnFileUriExposure").invoke(null) // being able to start intent with file://

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTaskDescription(ActivityManager.TaskDescription(getString(R.string.app_name)))
        btns = ids.map { findViewById<Button>(it) }
        findViewById<TextView>(R.id.manager_ver)!!.text = getString(R.string.manager_ver, BuildConfig.VERSION_NAME)

        if (!hasRoot()) {
            showMessage(getString(R.string.root_required), exit = true)
            return
        }
        val status = getLKTStatus()
        if (status == null) {
            showMessage(getString(R.string.log_file_not_found))
        }
        updateStatus(status)
        for (id in ids) {
            val btn = findViewById<Button>(id)
            btn.setOnClickListener {
                val dialog = ProgressDialog.show(this, "",
                        getString(R.string.changing_profile, btn.text), true)
                dialog.show()
                val mode = when (id) {
                    R.id.battery -> 1
                    R.id.balanced -> 2
                    R.id.performance -> 3
                    R.id.turbo -> 4
                    else -> 0
                }
                assert(mode != 0)
                Thread {
                    val code = Shell.su("lkt ${mode}").exec().code
                    this.runOnUiThread {
                        if (code != 0) {
                            showMessage(getString(R.string.failed_to_change_profile))
                        }
                        dialog.hide()
                        updateStatus(getLKTStatus())
                    }
                }.start()
            }
        }

        findViewById<Button>(R.id.xda).setOnClickListener {
            openUri("https://forum.xda-developers.com/apps/magisk/xz-lxt-1-0-insane-battery-life-12h-sot-t3700688")
        }
        findViewById<Button>(R.id.telegram).setOnClickListener {
            openUri("https://t.me/LKT_XDA")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> {
                val dialog = AlertDialog.Builder(this)
                        .setTitle(R.string.about)
                        .setMessage(Html.fromHtml(getString(R.string.description), Html.FROM_HTML_MODE_LEGACY))
                        .setPositiveButton(R.string.ok) { _, _ ->

                        }
                        .setNeutralButton(R.string.contact_dev) { _, _ ->
                            openUri("https://t.me/maple3142")
                        }
                        .show()
                dialog.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
            }
            R.id.openlog -> {
                val intent = Intent(Intent.ACTION_EDIT)
                intent.setDataAndType(Uri.parse("file:///data/LKT.prop"), "text/plain")
                startActivity(intent)
            }
        }
        return true
    }

    private fun updateStatus(status: LKTStatus?) {
        findViewById<TextView>(R.id.busybox_ver).text = getString(R.string.busybox_ver, status?.busyboxVersion)
        findViewById<TextView>(R.id.lkt_ver).text = getString(R.string.lkt_ver, status?.LKTVersion)
        val currentId = when (status?.profileName) {
            "Battery" -> 1
            "Balanced" -> 2
            "Performance" -> 3
            "Turbo" -> 4
            else -> 0
        }
        for (btn in btns!!) {
            btn.isEnabled = true
        }
        if (currentId != 0) {
            btns!![currentId - 1].isEnabled = false
        }
    }

    private fun showMessage(message: String, exit: Boolean = false) {
        val builder = AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.ok) { _, _ ->
                    if (exit) {
                        System.exit(0)
                    }
                }
        builder.show()
    }

    private fun openUri(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }
}
