package com.yauhenl.poe.configuration

import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.InetAddress


@Configuration
open class ElasticsearchConfiguration {

    @Bean
    open fun getClient(): Client {
        return PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300))
    }
}