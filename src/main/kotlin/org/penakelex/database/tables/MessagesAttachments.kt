package org.penakelex.database.tables

import org.jetbrains.exposed.sql.Table

object MessagesAttachments : Table("message_attachments") {
    val message_id = long("message_id").references(Messages.id)
    val attachment = text("attachment")
}