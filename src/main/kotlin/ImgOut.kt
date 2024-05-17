package org.longchuanclub.mirai.plugin

import org.longchuanclub.mirai.plugin.entity.GroupDetail
import util.skia.ImageDrawerComposer
import java.io.File

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
    val composer = ImageDrawerComposer(
        900, 1100,
        "titleText", arrayListOf(), 4,
        groupDetail,

        40f,
        100
    )
    val outputFile = composer.draw()
    // 指定本地目录路径
    val localDirectoryPath = "C:\\re"


    // 生成本地文件路径
    val localFilePath = "$localDirectoryPath/outimg.png"

     //将 ExternalResource 对象保存到本地文件
    outputFile.inputStream().use { inputStream ->
        File(localFilePath).outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    }
