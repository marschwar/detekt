package io.github.detekt.report.sarif

import io.github.detekt.sarif4k.ArtifactLocation
import io.github.detekt.sarif4k.Level
import io.github.detekt.sarif4k.Message
import io.github.detekt.sarif4k.PhysicalLocation
import io.github.detekt.sarif4k.Region
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Severity
import kotlin.io.path.invariantSeparatorsPathString

internal fun toResults(detektion: Detektion): List<io.github.detekt.sarif4k.Result> =
    detektion.issues.map { it.toResult() }

internal fun Severity.toResultLevel() = when (this) {
    Severity.Error -> Level.Error
    Severity.Warning -> Level.Warning
    Severity.Info -> Level.Note
}

private fun Issue.toResult(): io.github.detekt.sarif4k.Result {
    return io.github.detekt.sarif4k.Result(
        ruleID = "detekt.${ruleInfo.ruleSetId}.${ruleInfo.id}",
        level = severity.toResultLevel(),
        locations = (listOf(location) + references.map { it.location }).map { it.toLocation() }.distinct(),
        message = Message(text = message)
    )
}

private fun Location.toLocation(): io.github.detekt.sarif4k.Location {
    return io.github.detekt.sarif4k.Location(
        physicalLocation = PhysicalLocation(
            region = Region(
                startLine = source.line.toLong(),
                startColumn = source.column.toLong(),
                endLine = endSource.line.toLong(),
                endColumn = endSource.column.toLong(),
            ),
            artifactLocation = if (filePath.relativePath != null) {
                ArtifactLocation(
                    uri = filePath.relativePath?.invariantSeparatorsPathString,
                    uriBaseID = SRCROOT
                )
            } else {
                ArtifactLocation(uri = filePath.absolutePath.toUri().toString())
            }
        )
    )
}
