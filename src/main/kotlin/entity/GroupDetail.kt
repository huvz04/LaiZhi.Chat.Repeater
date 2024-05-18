package org.longchuanclub.mirai.plugin.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name="group_detail")
data class GroupDetail(
    @Id
    @GeneratedValue
    val id:String ="114514",
    val avatar:ByteArray = ByteArray(0),
    val name:String="群聊",
    var total:Int=0,
    val galleryNumber:Int=0,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GroupDetail

        if (id != other.id) return false
        if (!avatar.contentEquals(other.avatar)) return false
        if (name != other.name) return false
        if (total != other.total) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + avatar.contentHashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + total
        return result
    }
}
