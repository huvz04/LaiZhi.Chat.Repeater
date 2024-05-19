package org.longchuanclub.mirai.plugin.Service

import entity.LZException
import entity.data.GroupDetails
import entity.data.ImageFiles
import net.mamoe.mirai.console.plugin.info
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import okhttp3.Request
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

import org.longchuanclub.mirai.plugin.PluginMain
import org.longchuanclub.mirai.plugin.entity.GroupDetail
import org.longchuanclub.mirai.plugin.entity.ImageFile
import org.longchuanclub.mirai.plugin.util.HttpClient
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

object ImageService {
    private val db = Database.connect("jdbc:postgresql://localhost:5432/postgres",
        user = "postgres", password = "postgres")
    /**
     * 获取群聊信息
     */
    fun selectGroupDetail(id: Int): GroupDetail {
        return transaction(db) {
            GroupDetails.selectAll().where { GroupDetails.id eq id.toString() }
                .map { GroupDetail(it[GroupDetails.id], it[GroupDetails.avatar], it[GroupDetails.name], it[GroupDetails.total], it[GroupDetails.galleryNumber]) }
                .firstOrNull() ?: throw IllegalArgumentException("Group with ID $id not found")
        }
    }

    /**
     * 获取群聊的图片列表
     */
    fun selectImageDetail(id: Long): List<ImageFile> {
        return transaction(db) {
            ImageFiles.select { ImageFiles.qq eq id.toString() }
                .map {
                    ImageFile(
                        it[ImageFiles.id] ?: 0, // 处理 id 为 null 的情况
                        it[ImageFiles.md5] ?: "", // 处理 md5 为 null 的情况
                        it[ImageFiles.qq] ?: "", // 处理 qq 为 null 的情况
                        it[ImageFiles.count] ?: 0, // 处理 count 为 null 的情况
                        it[ImageFiles.about] ?: "", // 处理 about 为 null 的情况
                        it[ImageFiles.type] ?: "", // 处理 type 为 null 的情况
                        it[ImageFiles.url] ?: "" // 处理 url 为 null 的情况
                    )
                }
        }
    }

    /**
     * 更新qq群图库信息
     */
    fun updateGroupDetail(qq: Long) {
        transaction(db) {
            val entity = selectImageDetail(qq)
            val entity2 = selectGroupDetail(qq.toInt())
            entity2.total = entity.size
            GroupDetails.update({ GroupDetails.id eq entity2.id }) {
                it[total] = entity2.total
            }
        }
    }

    /**
     * 获取图片
     */
    suspend fun getImage(qq: Long, name: String): ExternalResource {
        return transaction(db) {
            ImageFiles.select { (ImageFiles.qq eq qq.toString()) and (ImageFiles.about eq name) }
                .map {
                    ImageFile(
                        0,
                        it[ImageFiles.md5],
                        it[ImageFiles.qq],
                        it[ImageFiles.count],
                        it[ImageFiles.about],
                        it[ImageFiles.type],
                        it[ImageFiles.url]
                    )
                }
                .randomOrNull() ?: throw IllegalArgumentException("No image found for group $qq and name $name")
        }.let {
            val ParentfilePath = "LaiZhi/$qq/$name/${it.md5}.${it.type}}"
            val file = PluginMain.resolveDataFile(ParentfilePath)
            file.toExternalResource().toAutoCloseable()
        }
    }

    /**
     * 保存图片信息
     */



     suspend fun saveImage(q1: Long, name: String, image: Image) {
                val url2 = image.queryUrl()
                val request = Request.Builder()
                    .url(url2)
                    .build()
                val response = HttpClient.okHttpClient.newCall(request).execute()
                val ParentfilePath = "LaiZhi/$q1/$name"

                val imageByte = response.body!!.bytes()
                val fileParent = PluginMain.resolveDataFile(ParentfilePath)
                if (!fileParent.exists()) fileParent.mkdirs()

                val md5a = getMD5(imageByte)

                val contentType = response.header("Content-Type")
                val fileType = when (contentType) {
                    "image/jpeg" -> "jpg"
                    "image/png" -> "png"
                    "image/gif" -> "gif"
                    else -> "jpg"
                }
        val filePath = ParentfilePath +  "\\${md5a}.${fileType}"
        val file  = PluginMain.resolveDataFile(filePath)
        file.writeBytes(imageByte)
        transaction(db) {
            val entity = ImageFiles.selectAll()
                .where { ImageFiles.md5 eq md5a.toString() }
                .firstOrNull()
            if (entity != null) throw LZException("已存在同md5的图片")
            ImageFiles.insert {
                it[md5] = md5a.toString()
                it[qq] = q1.toString()
                it[count] = 0L
                it[about] = name
                it[type] = fileType
                it[url] = ParentfilePath
            }
        }
    }

    /**
     * 更新图片信息
     */
    fun updateImage(imageFile: ImageFile) {
        transaction(db) {
            ImageFiles.update(
                {
                    ImageFiles.id eq 8  }
            ) {
                it[md5] = imageFile.md5
                it[qq] = imageFile.qq
                it[count] = imageFile.count
                it[about] = imageFile.about
                it[type] = imageFile.type
                it[url] = imageFile.url
            }
        }
    }

    /**
     * 删除图片信息
     */
    fun deleteImage(id: Long) {
        transaction(db) {
            ImageFiles.deleteWhere { ImageFiles.id eq id }
        }
    }

    /**
     * 计算MD5
     */
    fun getMD5(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("MD5")
        md.update(bytes)
        val digest = md.digest()
        return BigInteger(1, digest).toString(16).padStart(32, '0')
    }

    fun close() {

    }
}