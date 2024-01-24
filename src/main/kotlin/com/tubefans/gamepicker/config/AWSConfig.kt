package com.tubefans.gamepicker.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.ec2.Ec2Client

@Configuration
class AWSConfig {

    @Bean
    fun ec2Client(): Ec2Client = Ec2Client.builder()
        .build()

}
