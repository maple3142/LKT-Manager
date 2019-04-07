package net.maple3142.lktmanager

import android.app.Activity
import android.widget.Button

class Profile(val name: String, val mode: Int, val viewId: Int) {
    var btn: Button? = null
    fun initializeButton(activity: Activity) {
        btn = activity.findViewById<Button>(viewId)
    }
}

class ProfileManager(val initialProfileName: String) {
    val profiles = arrayListOf<Profile>(
            Profile("Battery", 1, R.id.battery),
            Profile("Balanced", 2, R.id.balanced),
            Profile("Performance", 3, R.id.performance),
            Profile("Turbo", 4, R.id.turbo)
    )
    var currentProfile = profiles.find { it.name == initialProfileName }

    fun useProfile(target: Profile, callback: (success: Boolean) -> Unit) {
        Thread {
            try {
                sudo("lkt ${target.mode}")
                currentProfile = target
                callback(true)
            } catch (e: SuException) {
                callback(false)
            }
        }.start()
    }

    fun getProfileByMode(mode: Int): Profile? {
        return profiles.find { it.mode == mode }
    }

    fun getProfileByName(name: String): Profile? {
        return profiles.find { it.name == name }
    }

    fun initializeWithActivity(activity: Activity) {
        profiles.forEach { it.initializeButton(activity) }
        currentProfile?.btn?.isEnabled = false
    }
}
