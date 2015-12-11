package models

import java.sql.Time

/**
 * Created by rumata on 11.12.15.
 */
case class Comment(id: Option[Long] = None,
                   postId: Long,
                   author: String,
                   content: String,
                   createdAt: Time)
