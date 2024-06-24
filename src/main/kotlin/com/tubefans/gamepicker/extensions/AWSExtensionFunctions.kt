package com.tubefans.gamepicker.extensions

import software.amazon.awssdk.services.ec2.model.InstanceStatus

val STATUS_TEMPLATE_STRING: String =
    """
    ```
    Name: %s
    ID: %s
    Summary: %s
    State: %s
    ```
    """.trimIndent()

fun InstanceStatus.toDisplayString(serverName: String): String =
    String.format(
        STATUS_TEMPLATE_STRING,
        serverName,
        this.instanceId(),
        this.instanceStatus(),
        this.instanceState(),
    )
