package com.android.dreamguard.data.local.datastore

object PredictionDataStore {
    var gender: String? = null
    var age: Int = 0
    var hoursOfSleep: Int = 0
    var occupation: Int = 0
    var activityLevel: Int = 0 // Scale: 1-100
    var stressLevel: Int = 0 // Scale: 1-10
    var weight: Int = 0 // In kilograms
    var height: Int = 0 // In centimeters
    var heartRate: Int = 0 // Beats per minute
    var systolic: Int = 0 // Systolic blood pressure
    var diastolic: Int = 0 // Diastolic blood pressure
    var dailySteps: Int = 0 // New property added
    var sleepQuality: Int = 0 // Skala kualitas tidur (1-10)

    fun reset() {
        gender = null
        age = 0
        hoursOfSleep = 0
        occupation = 0
        activityLevel = 0
        stressLevel = 0
        weight = 0
        height = 0
        heartRate = 0
        systolic = 0
        diastolic = 0
        dailySteps = 0
        sleepQuality = 0
    }
}
