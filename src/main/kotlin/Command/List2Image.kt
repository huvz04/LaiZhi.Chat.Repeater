package org.longchuanclub.mirai.plugin.Command


import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import okhttp3.Request
import org.longchuanclub.mirai.plugin.PluginMain
import org.longchuanclub.mirai.plugin.Service.ImageService
import org.longchuanclub.mirai.plugin.entity.GroupDetail
import org.longchuanclub.mirai.plugin.entity.ImageFile
import org.longchuanclub.mirai.plugin.util.HttpClient
import util.skia.ImageDrawerComposer

object List2Image :SimpleCommand(PluginMain,"获取图库",description = "获取全部的图库列表") {

    @Handler
    suspend fun handlerlist(sender: CommandSender){
//        val files = File(PluginMain.dataFolder.absolutePath+"\\LaiZhi\\${sender.subject?.id}")
//        val images = files.listFiles()
//        val simgs =  mutableListOf<ImageData>()
//        images?.forEach {
//            run {
//                var o1 = ImageData(it.name, null, 0);
//                var img1 = File(files.absolutePath + "\\${it.name}").listFiles{file -> file.extension == "jpg" || file.extension == "png" || file.extension == "gif"|| file.extension == "jpeg"}
//                if (img1 != null) {
//                    if(img1.isNotEmpty()) {
//                        o1.Img  = img1.get(0)
//                        o1.size = img1.size
//                    }
//                }
//                if(o1.Img!=null && o1.size>0)simgs.add(o1)
//            }
//        }

        val request = sender.subject?.avatarUrl?.let {
            Request.Builder()
                .url(it)
                .build()
        }
        val reponse = request?.let { HttpClient.okHttpClient.newCall(it).execute() }
        val imageByte = reponse?.body!!.bytes()
        val list : List<ImageFile> = ImageService.selectImageDetail(sender.subject?.id?:114514L)
        val groupDetail = GroupDetail(
            sender.subject?.id.toString(),
            imageByte,
            sender.getGroupOrNull()?.name?:"群聊",
            list.size,
            sender.getGroupOrNull()?.members?.size?:0
        );
        val newMap:HashMap<String,ArrayList<ImageFile>> = hashMapOf()
        for(i in list){
            var list2 : ArrayList<ImageFile> = arrayListOf()

            try{
                if(newMap[i.about] !=null) {
                    list2 = newMap[i.about]!!
                }
                    list2.add(i)
                    newMap[i.about] = list2
            }catch (_:Exception){
                PluginMain.logger.error("读取图片错误:i.url+\\${i.md5}.${i.type}....自动移除脏数据")
                ImageService.deleteImage(i.id);
            }



        }
        val composer = ImageDrawerComposer(
            1430, (newMap.size/6+1)*(185+40)+200,
            "titleText", newMap, 6,
            groupDetail,
            40f,
            100,
            185f
        )
            try {
                val res = composer.draw();
                sender.subject?.let {
                    res.uploadAsImage(it)
                    sender.subject!!.sendImage(res)
                }
            }catch (e:Exception)
            {
                sender.subject?.sendMessage("图片发送失败呜呜\n异常:${e.message}")
            }





    }




}