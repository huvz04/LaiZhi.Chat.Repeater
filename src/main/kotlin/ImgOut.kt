package org.longchuanclub.mirai.plugin

import org.longchuanclub.mirai.plugin.entity.GroupDetail
import org.longchuanclub.mirai.plugin.entity.ImageFile
import util.skia.ImageDrawerComposer
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.MessageDigest

fun main() {
//        val filepath = File("C:\\Users\\dache\\Pictures\\re")
//        val images = filepath.listFiles()
//         val simgs =  mutableListOf<ImageData>()
//    images?.forEach {
//        run {
//            val o1 = ImageData(it.name, null, 0);
//            val img1 = File(filepath.absolutePath + "\\${it.name}").listFiles{ file -> file.extension == "jpg" || file.extension == "png" || file.extension == "gif"}
//            if (img1 != null && img1.isNotEmpty()) {
//                o1.Img  = img1[0]
//                o1.size = img1.size
//                simgs.add(o1)
//            }
//        }
//    }


    val filePath = "C:\\re\\va.jpg"
    val s = File(filePath)
    val groupDetail = GroupDetail(
        1114514.toString(),
        s.readBytes(),
        "JHU Waikato「原神」交流栈",
        10,
        14
        );
    val newMap:HashMap<String,List<ImageFile>> = hashMapOf()
    val v1 = ImageFile();
    v1.type="jpg";v1.md5="cjj1";v1.url="C:\\re\\114514\\test\\cjj1.jpg"
    v1.about="cjj";
    var v2 = ImageFile();
    v2.type="jpg";v2.md5="zms";v2.url="C:\\re\\114514\\真没睡\\zms.jpg"
    v2.about="真没睡";
    newMap.put("cjj",arrayListOf(v1))
    newMap.put("真没睡",arrayListOf(v2,v1,v1))
    newMap.put("真没睡2",arrayListOf(v2,v1,v1))
    newMap.put("真没睡3",arrayListOf(v2,v1,v1))
    newMap.put("真没睡4",arrayListOf(v2,v1,v1))
    newMap.put("真没睡6",arrayListOf(v2,v1,v1))
    newMap.put("真没睡5",arrayListOf(v2,v1,v1))
    newMap.put("真没睡7",arrayListOf(v2,v1,v1))
    newMap.put("真没睡8",arrayListOf(v2,v1,v1))
    newMap.put("真没睡9",arrayListOf(v2,v1,v1))
    newMap.put("真没睡12",arrayListOf(v2,v1,v1))
    newMap.put("真没睡33",arrayListOf(v2,v1,v1))
    newMap.put("真没睡21",arrayListOf(v2,v1,v1))
    newMap.put("真没睡21",arrayListOf(v2,v1,v1))
    val composer = ImageDrawerComposer(
        1430, (newMap.size/6+1)*(185+40)+200,
        "titleText", newMap, 6,
        groupDetail,
        40f,
        100,
        185f
    )
    val outputFile = composer.draw()
    // 指定本地目录路径
    val localDirectoryPath = "C:\\re"


    // 生成本地文件路径
    val localFilePath = "$localDirectoryPath/outimg.png"

     //将 ExternalResource 对象保存到本地文件
    var fiile = outputFile.inputStream().use { inputStream ->
        File(localFilePath).outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    val file = File(localFilePath)
    val md5 = getMD5(file.readBytes())
    println("MD5 of $file is: $md5")
    }
fun getMD5(bytes: ByteArray): String {
    val md = MessageDigest.getInstance("MD5")
    md.update(bytes)
    val digest = md.digest()
    return BigInteger(1, digest).toString(16).padStart(32, '0')
}