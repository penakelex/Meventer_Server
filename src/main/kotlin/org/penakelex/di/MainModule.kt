package org.penakelex.di

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val mainModule = module {
    val config = HoconApplicationConfig(ConfigFactory.load())
    single {
        Database.connect(
            url = config.property("database.url").getString(),
            user = config.property("database.user").getString(),
            password = config.property("database.password").getString(),
            driver = config.property("database.driver").getString()
        )
    }
}