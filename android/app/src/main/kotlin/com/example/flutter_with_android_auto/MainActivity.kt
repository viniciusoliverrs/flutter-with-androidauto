package com.example.flutter_with_android_auto
import android.content.Context
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        AndroidAutoChannel.initialize(flutterEngine,context)
    }
}


object AndroidAutoChannel {
        private val CHANNEL_NAME = "androidAuto"
        private val EVENT_NAME = "androidAutoStatus"
        private var methodChannel: MethodChannel? = null
        private var carPropertyManagerChannel: EventChannel? = null
        fun setCounter(counter: Int) {
            val data = mapOf("counter" to counter)
            methodChannel?.invokeMethod("setCounter", data)
        }
        fun initialize(flutterEngine: FlutterEngine, context: Context) {
            methodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_NAME)
            methodChannel?.setMethodCallHandler { call, result ->
                when (call.method) {
                    "setCounter" -> {
                        val counter = call.arguments as Map<String, Any>
                        MainScreen.screen?.updateCounter(counter["counter"] as Int)
                        result.success(null)
                    }
                }
            }
            carPropertyManagerChannel = EventChannel(flutterEngine.dartExecutor.binaryMessenger, EVENT_NAME)
            carPropertyManagerChannel?.setStreamHandler(SimplesAndroidAutoConnectionEvent(context))
        }
}

class SimplesAndroidAutoConnectionEvent(private val context: Context) : EventChannel.StreamHandler {
    companion object {
        private var androidAutoEventSink:EventChannel.EventSink? = null
        fun onCarConnectionChange(status:String) {
            androidAutoEventSink?.success(status)
        }
    }
    override fun onListen(args: Any?, events: EventChannel.EventSink) {
        androidAutoEventSink = events
    }


    override fun onCancel(arguments: Any?) {
        androidAutoEventSink = null
    }
}

