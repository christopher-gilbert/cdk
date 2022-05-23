package com.myorg

import software.amazon.awscdk.App
import software.amazon.awscdk.Environment
import software.amazon.awscdk.StackProps

    fun main() {

        val app = App()
        HelloCdkStack(
            app, "HelloCdkStackKotlin", StackProps.builder()
                .env(
                    Environment.builder()
                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                        .region(System.getenv("CDK_DEFAULT_REGION"))
                        .build()
                )
                .build()
        )
        app.synth()
    }

