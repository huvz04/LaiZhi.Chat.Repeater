package org.longchuanclub.mirai.plugin.Service

import entity.LZException
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import okhttp3.Request
import org.longchuanclub.mirai.plugin.PluginMain
import org.longchuanclub.mirai.plugin.entity.GroupDetail
import org.longchuanclub.mirai.plugin.entity.ImageFile
import org.longchuanclub.mirai.plugin.util.HttpClient
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

class ImageService {

    val sessionFactory = PluginMain.factory

    /**
     * 获取群聊信息
     */
    fun selectGroupDetail(id:Int):GroupDetail{
        sessionFactory.openSession().use { session ->
            session.beginTransaction()

            val result = session.get(GroupDetail::class.java,id)
            return result;
        }
    }
    /**
     * 更新qq群图库信息
     */
    fun updateGroupDetail(qq:Long){
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            // 查询图片列表
            val query = session.createQuery("FROM ImageFile WHERE qq = :id")
            query.setParameter("id", qq)
            val entity = query.resultList

            val entity2 = session.get(GroupDetail::class.java,qq)
            entity2.total = entity.size
            val result = session.persist(entity2)
            return result;
        }
    }
    /**
     * 来只图片
     */
    suspend fun getImage(qq:Long,name:String):ExternalResource{
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            // 查询图片列表
            val query = session.createQuery(
                "FROM ImageFile WHERE qq = :qq and about = :name"
            )
            query.setParameter("qq", qq)
            query.setParameter("name", name)
            val imageFiles = query.list() as List<ImageFile>

            // 从列表中随机选择一个数据
            val randomIndex = Random().nextInt(imageFiles.size)
            val randomImageFile = imageFiles[randomIndex]
            val ParentfilePath = "LaiZhi\\${qq}\\${name}\\${randomImageFile.md5}.${randomImageFile.type}}"
            val file = PluginMain.resolveDataFile(ParentfilePath)//获取ParentfilePath
            val res = file.toExternalResource().toAutoCloseable()
            return res;
        }

    }
    /**
     * 保存图片
     */
    suspend fun saveImage(qq:Long,name:String,image: Image){
        val url = image.queryUrl();
        val request = Request.Builder()
            .url(url)
            .build()
            val reponse =  HttpClient.okHttpClient.newCall(request).execute()
            val ParentfilePath = "LaiZhi\\${qq}\\${name}"
             val imageByte = reponse.body!!.bytes()
            val fileParent  = PluginMain.resolveDataFile(ParentfilePath)
            if (!fileParent.exists()) fileParent.mkdirs()
        val md5 =
            if(image.md5.isEmpty()){
                getMD5(imageByte)
            }else{
                image.md5
            }

        val contentType = reponse.header("Content-Type")
        val fileType :String?;
        when (contentType) {
            "image/jpeg" -> fileType = "jpg"
            "image/png" -> fileType = "png"
            "image/gif" -> fileType = "gif"
            else -> fileType = "jpg"
        }
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            // 查询图片列表
            val query = session.createQuery("FROM ImageFile WHERE md5 = :id")
            query.setParameter("id", md5)
            val entity = query.resultList
            if(entity.size>0) throw LZException("已存在同md5的图片");
            val en = ImageFile()
            en.qq = qq.toString()
            en.md5 = md5.toString()



            en.type = contentType.toString()

            session.persist(en)
        }
        val filePath = ParentfilePath +  "\\${md5}.${fileType}"
        val file  = PluginMain.resolveDataFile(filePath)
        file.writeBytes(imageByte)
        updateGroupDetail(qq)
    }
    /**
     * 删除图片
     */
    suspend fun removeImage(qq:Long,image: Image){
        val request = Request.Builder()
            .url(image.queryUrl())
            .build()
        val reponse =  HttpClient.okHttpClient.newCall(request).execute()
        val imageByte = reponse.body!!.bytes()
        val md5 =
            if(image.md5.isEmpty()){
                getMD5(imageByte)
            }else{
                image.md5
            }
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            // 先查询出要删除的数据
            val query = session.createQuery(
                "FROM ImageFile WHERE qq = :qq and md5 = :md5"
            )
            query.setParameter("qq", qq)
            query.setParameter("md5", md5)
            val imageFile = query.uniqueResult() as ImageFile?

            // 如果查询到了数据,则删除它
            imageFile?.let {
                session.remove(it)
            }
        }
    }


    /**
     * 计算MD5
     */
    private fun getMD5(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("MD5")
        md.update(bytes)
        val digest = md.digest()
        return BigInteger(1, digest).toString(16).padStart(32, '0')
    }



}