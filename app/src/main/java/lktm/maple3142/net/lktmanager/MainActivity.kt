package lktm.maple3142.net.lktmanager

import android.app.ActivityManager
import android.app.ProgressDialog
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Button
import android.widget.TextView
import lktm.maple3142.net.lktmanager.R.id.battery
import java.lang.Exception
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTaskDescription(ActivityManager.TaskDescription(getString(R.string.app_name)))
        try {
            sudo("echo Hello World")
        } catch (e: SuException) {
            showMessage(this, getString(R.string.root_required), exit = true)
            return
        }
        updateCurrentProfile()
        val profileToIdMap = mapOf(1 to R.id.battery, 2 to R.id.balanced, 3 to R.id.performance, 4 to R.id.turbo)
        for ((profile, id) in profileToIdMap) {
            val btn = findViewById<Button>(id)
            btn.setOnClickListener {
                val dialog = ProgressDialog.show(this, "",
                        getString(R.string.changing_profile, btn.text), true)
                dialog.show()
                Thread {
                    try {
                        sudo("lkt $profile")
                    } catch (e: SuException) {
                        this.runOnUiThread {
                            showMessage(this, getString(R.string.failed_to_change_profile))
                        }
                    }
                    this.runOnUiThread {
                        dialog.hide()
                        updateCurrentProfile()
                    }
                }.start()
            }
        }
    }

    private fun updateCurrentProfile() {
        val profileView = findViewById<TextView>(R.id.profile)
        try {
            val profile = getProfile()
            profileView.text = getString(R.string.current_profile, profile)
        } catch (e: SuException) {
            showMessage(this, getString(R.string.lkt_not_installed), exit = true)
        }
    }
}
