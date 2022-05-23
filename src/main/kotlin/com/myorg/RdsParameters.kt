package com.myorg

data class RdsParameters(
    val applicationName: String,
    val environment: String,
    val storage: Int? = 20,
    val instanceClass: String? = "db.t2.micro",
    val pgVersion: String? = "12.9"
)
