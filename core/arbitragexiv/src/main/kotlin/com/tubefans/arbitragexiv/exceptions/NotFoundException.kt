package com.tubefans.arbitragexiv.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
    constructor() : super()
}
