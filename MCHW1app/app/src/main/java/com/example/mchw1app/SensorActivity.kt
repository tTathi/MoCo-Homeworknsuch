package com.example.mchw1app

import android.hardware.SensorEventListener
import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.content.Context

class SensorActivity(context: Context) : Activity(), SensorEventListener {


    val context: Context = context
    private lateinit var sensorManager: SensorManager
    private var mGravity: Sensor? = null
    private var gravX: Float = 0f
    private var gravY: Float = 0f
    private var gravZ: Float = 0f


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.main)

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        //val lux = event.values[0]
        Log.d("Sensors", event.values[0].toString())
        gravX = event.values[0]
        gravY = event.values[1]
        gravZ = event.values[2]
    }

    public override fun onResume() {
        super.onResume()
        Log.d("Sensors", "Mic check")
        mGravity?.also { grav ->
            sensorManager.registerListener(this, grav, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    public override fun onPause() {
        super.onPause()
        Log.d("Sensors", "Mic check")
        sensorManager.unregisterListener(this)
    }

    public fun resumeSensor() {
        Log.d("Sensors", "Mic check 2.0")
        sensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL)
    }

    public fun stopSensor() {
        sensorManager.unregisterListener(this)
    }

    fun init() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    }

    /*
    public fun getValue(): Float[]
    {
        return arrayOf(gravX, gravY, gravZ)
    }
    */

}