package io.github.detekt.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths

internal class ConfigMigrationsSpec {
    @Test
    fun read() {
        val userConfigUrl = checkNotNull(ConfigMigrationsSpec::class.java.getResource("/empty.yml"))
        val defaultConfigUrl = checkNotNull(ConfigMigrationsSpec::class.java.getResource("/default-detekt-config.yml"))
        val targetFile = File("/tmp/config.yml")

        migrate(userConfigUrl, defaultConfigUrl, targetFile.toURI().toURL())

        assertThat(targetFile).hasSameTextualContentAs(Paths.get(defaultConfigUrl.toURI()).toFile())
    }
}
