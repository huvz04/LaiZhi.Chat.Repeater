package org.longchuanclub.mirai.plugin.entity

data class GroupDetail(
    val id:String,
    val avatar:ByteArray,
    val name:String,
    val total:Int,
    val galleryNumber:Int,
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
