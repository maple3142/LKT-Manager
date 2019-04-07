package net.maple3142.lktmanager

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews


class WidgetProvider : AppWidgetProvider() {

    val SET_LKT_PROFILE = "net.maple3142.lktmanager.SET_LKT_PROFILE"
    val LKT_PROFILE = "LKT_PROFILE"

    override fun onUpdate(ctx: Context, manager: AppWidgetManager, appwidgetids: IntArray) {
        for (widgetid in appwidgetids) {
            val views = RemoteViews(ctx.packageName, R.layout.widget_layout)
            val intent = Intent().setAction(SET_LKT_PROFILE)
            intent.putExtra(LKT_PROFILE, 1)
            val pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.widget_battery, pendingIntent)
            manager.updateAppWidget(widgetid, views)
        }
    }

    override fun onReceive(ctx: Context, intent: Intent) {
        val manager = AppWidgetManager.getInstance(ctx)
        Log.d("lktmamanger", intent.action)
        when (intent.action) {
            (SET_LKT_PROFILE) -> {
                Log.d("lktmamanger", "hello")
            }
        }
        super.onReceive(ctx, intent)
    }
}
