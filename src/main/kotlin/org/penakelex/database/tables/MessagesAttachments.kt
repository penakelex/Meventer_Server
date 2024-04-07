package org.penakelex.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object MessagesAttachments : Table("messages_attachments") {
    val message_id = long("message_id")
        .references(Messages.id, onDelete = ReferenceOption.CASCADE)
    val attachment = text("attachment")
}