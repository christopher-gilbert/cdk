package com.myorg

import software.amazon.awscdk.services.rds.CfnDBInstance
import software.amazon.awscdk.services.secretsmanager.CfnSecretTargetAttachment
import software.amazon.awscdk.services.secretsmanager.Secret
import software.amazon.awscdk.services.secretsmanager.SecretStringGenerator
import software.constructs.Construct

class PostgresConstruct(
    scope: Construct,
    id: String,
    private val parameters: RdsParameters
) : Construct(scope, id) {


    init {
        val masterSecret = createCredentials("masterCredentials", "${parameters.applicationName}_admin")
        val userSecret = createCredentials("userCredentials", "${parameters.applicationName}_user")

        val dbInstance = CfnDBInstance.Builder.create(this, "${idPrefix()}-db")
            .allocatedStorage(parameters.storage.toString())
            .multiAz(false)
            .dbInstanceClass(parameters.instanceClass)
            .dbName("database")
            .engine("postgres")
            .engineVersion(parameters.pgVersion)
            .masterUsername(masterSecret.secretValueFromJson("username").unsafeUnwrap())
            .masterUserPassword(masterSecret.secretValueFromJson("password").unsafeUnwrap())
            .build()

        CfnSecretTargetAttachment.Builder.create(this, "masterSecretTargetAttachment")
            .secretId(masterSecret.secretArn)
            .targetId(dbInstance.ref)
            .targetType("AWS::RDS::DBInstance")
            .build()
        CfnSecretTargetAttachment.Builder.create(this, "userSecretTargetAttachment")
            .secretId(userSecret.secretArn)
            .targetId(dbInstance.ref)
            .targetType("AWS::RDS::DBInstance")
            .build()
    }


    private fun createCredentials(type: String, username: String) = Secret.Builder
        .create(this, "${idPrefix()}/database-${type}")
        .description("Database owner credentials")
        .generateSecretString(
            SecretStringGenerator.builder()
                .secretStringTemplate("""{"username": "$username"}""")
                .generateStringKey("password")
                .passwordLength(32)
                .excludeCharacters("@/\\'\" ")
                .build()
        )
        .build()

    private fun idPrefix() = "${parameters.applicationName}-${parameters.environment}"
}