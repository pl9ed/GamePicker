package com.tubefans.gamepicker.exceptions

/**
 * Exception thrown when resource state does not match desired state
 */
class AwsTransitionException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause) {
    companion object {
        const val AWS_TRANSITION_TEMPLATE = "AWS resource %s still not %s. Current status: %s"
    }

    constructor(resourceName: String, desiredState: String, currentState: String) : this(
        String.format(
            AWS_TRANSITION_TEMPLATE,
            resourceName,
            desiredState,
            currentState,
        ),
        null,
    )
}
