package com.android.dreamguard.data.local.datastore

object PredictionDataStore {
    var gender: String? = null
    var age: Int = 0
    var hoursOfSleep: Int = 0
    var occupation: Int = 0
    var activityLevel: Int = 0
    var stressLevel: Int = 0
    var weight: Int = 0
    var height: Int = 0
    var heartRate: Int = 0
    var systolic: Int = 0
    var diastolic: Int = 0
    var dailySteps: Int = 0
    var sleepQuality: Int = 0

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
