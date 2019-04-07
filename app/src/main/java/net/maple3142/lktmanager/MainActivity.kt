package net.maple3142.lktmanager

import android.app.ActivityManager
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTaskDescription(ActivityManager.TaskDescription(getString(R.string.app_name)))
        title = title.toString() + " v" + BuildConfig.VERSION_NAME

        if (!hasRoot()) {
            showMessage(getString(R.string.root_required), exit = true)
            return
        }
        findViewById<TextView>(R.id.busybox_ver).text = getString(R.string.busybox_ver, getBusyBoxVersion())
        findViewById<TextView>(R.id.lkt_ver).text = getString(R.string.lkt_ver, getLKTVersion())

        val initialProfileName = getProfile()
        if (initialProfileName == null) {
            showMessage(getString(R.string.lkt_not_installed), exit = true)
            return
        }
        val manager = ProfileManager(initialProfileName)
        manager.initializeWithActivity(this)
        updateCurrentProfileName(manager.currentProfile?.btn?.text.toString())
        for (profile in manager.profiles) {
            profile.btn?.setOnClickListener {
                val dialog = ProgressDialog.show(this, "",
                        getString(R.string.changing_profile, profile.btn?.text), true)
                dialog.show()
                manager.useProfile(profile) { success ->
                    this.runOnUiThread {
                        if (!success) {
                            showMessage(getString(R.string.failed_to_change_profile))
                        }
                        dialog.hide()
                        manager.profiles.forEach { it.btn?.isEnabled = true }
                        manager.currentProfile?.btn?.isEnabled = false
                        updateCurrentProfileName(manager.currentProfile?.btn?.text.toString())
                    }
                }
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
                        .setTitle(getString(R.string.about))
                        .setMessage(Html.fromHtml(getString(R.string.description), Html.FROM_HTML_MODE_LEGACY))
                        .setPositiveButton(R.string.ok, { _, _ ->

                        })
                        .setNeutralButton(R.string.github, { _, _ ->
                            openUri("https://github.com/maple3142/LKT-Manager")
                        })
                        .show()
                dialog.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
            }
        }
        return true
    }

    private fun updateCurrentProfileName(profileName: String?) {
        findViewById<TextView>(R.id.profile).text = getString(R.string.current_profile, profileName)
    }

    private fun showMessage(message: String, exit: Boolean = false) {
        val builder = AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, { _, _ ->
                    if (exit) {
                        System.exit(0)
                    }
                })
        builder.show()
    }

    private fun openUri(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }
}
