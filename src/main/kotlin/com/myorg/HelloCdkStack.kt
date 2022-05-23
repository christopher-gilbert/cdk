package com.myorg

import software.amazon.awscdk.services.s3.Bucket
import software.constructs.Construct
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps

class HelloCdkStack(
    scope: Construct,
    id: String,
    props: StackProps? = null
) : Stack(scope, id, props) {

    init {
        Bucket.Builder.create(this, "my-bucket").versioned(true).build()
        PostgresConstruct(this, "db", RdsParameters(applicationName = "cosmos", environment = "ci"))
    }

}
