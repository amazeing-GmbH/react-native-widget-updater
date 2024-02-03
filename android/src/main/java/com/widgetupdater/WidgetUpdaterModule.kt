package com.widgetupdater

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import com.facebook.react.bridge.*

class WidgetUpdaterModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {
  private val awt = AppWidgetManager.getInstance(this.reactApplicationContext)

  override fun getName(): String {
    return NAME
  }

  @ReactMethod
  fun updateAppWidgets(widgetClasses: ReadableArray, promise: Promise) {
    if (widgetClasses.size() == 0) {
      promise.reject("ENOARG", "No widget classes passed to getAppWidgetIds")
      return
    }

    Log.d(this.name, "Updating app widgets")

    try {
      val updatableWidgets = getAppWidgetIdsInternal(widgetClasses)

      updatableWidgets.forEach { entry ->
        val className: String = entry.key
        val values: IntArray = entry.value
        if (values.isEmpty()) {
          return@forEach
        }

        // send intent for updating to widget
        val provider = getWidgetProvider(className)
        Log.d(this.name, "Sending ACTION_APPWIDGET_UPDATE intent to $provider")
        val updateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        updateIntent.component = provider
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, values)
        this.reactApplicationContext.sendBroadcast(updateIntent)
      }
    } catch (exc: Exception) {
      promise.reject(exc)
    }

    promise.resolve(null)
  }

  @ReactMethod
  fun getAppWidgetIds(widgetClasses: ReadableArray, promise: Promise) {
    try {
      val appWidgetIds: Map<String, IntArray> = getAppWidgetIdsInternal(widgetClasses)
      promise.resolve(Arguments.makeNativeMap(appWidgetIds))
    } catch (exc: Exception) {
      promise.reject(exc)
    }
  }

  @Throws(IllegalArgumentException::class, ClassNotFoundException::class)
  internal fun getAppWidgetIdsInternal(widgetClasses: ReadableArray): Map<String, IntArray> {
    if (widgetClasses.size() == 0) {
      throw java.lang.IllegalArgumentException("No widget classes passed to getAppWidgetIds")
    }

    val appWidgetIds = mutableMapOf<String, IntArray>()

    for (i in 0 until widgetClasses.size()) {
      val widgetClass: String? = widgetClasses.getString(i)
      if (widgetClass.isNullOrBlank()) {
        continue
      }
      Log.d(this.name, "Getting widget ids for $widgetClass")

      val widgetIds = awt.getAppWidgetIds(getWidgetProvider(widgetClass))
      if (widgetIds.isNotEmpty()) {
        appWidgetIds[widgetClass] = widgetIds
      }
    }

    Log.d(this.name, "Returning widget ids: $appWidgetIds")
    return appWidgetIds
  }

  @Throws(ClassNotFoundException::class)
  internal fun getWidgetProvider(className: String): ComponentName {
    val provider = ComponentName(this.reactApplicationContext, "${this.reactApplicationContext.packageName}${className}")

    // Test if the class exists
    Class.forName(provider.className)
    return provider
  }

  companion object {
    const val NAME = "WidgetUpdater"
  }
}
