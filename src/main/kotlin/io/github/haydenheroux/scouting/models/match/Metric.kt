package io.github.haydenheroux.scouting.models.match

import org.jetbrains.exposed.dao.id.IntIdTable

object MetricTable : IntIdTable() {
    val participantId = reference("participantId", ParticipantTable)
    val key = varchar("key", 255)
    val value = varchar("value", 255)
}
