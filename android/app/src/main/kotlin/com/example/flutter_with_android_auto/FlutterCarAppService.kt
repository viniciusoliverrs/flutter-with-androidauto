package com.example.flutter_with_android_auto
import android.content.Intent
import androidx.car.app.*
import androidx.car.app.model.Action
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.car.app.validation.HostValidator
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner



class FlutterCarAppService  : CarAppService() {

    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        return AndroidAutoSession()
    }
}

class AndroidAutoSession : Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        lifecycle.addObserver(CarAppLifecycleObserver())
        return MainScreen(carContext)
    }
}
class CarAppLifecycleObserver : DefaultLifecycleObserver {
    override fun onResume(owner: LifecycleOwner) {
        SimplesAndroidAutoConnectionEvent.onCarConnectionChange(owner.lifecycle.currentState.name)
    }

    override fun onPause(owner: LifecycleOwner) {
        SimplesAndroidAutoConnectionEvent.onCarConnectionChange(owner.lifecycle.currentState.name)
    }
}


class MainScreen(carContext: CarContext) : Screen(carContext) {
    companion object {
         lateinit var screen:MainScreen
         var counter: Int = 0
    }
    lateinit var template: Template
    override fun onGetTemplate(): Template {
        screen = this
        screen.template = MessageTemplate.Builder("Counter: ${counter}")
                .addAction(
                        Action.Builder()
                                .setTitle("Add")
                                .setOnClickListener {
                                    counter += 1
                                    AndroidAutoChannel.setCounter(counter)
                                }
                                .build())
                .setHeaderAction(Action.APP_ICON)
                .build()
        return screen.template
    }

   fun updateCounter(value:Int) {
       counter = value
       invalidate()
    }
}