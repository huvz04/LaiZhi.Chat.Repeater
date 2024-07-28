package org.longchuanclub.mirai.plugin.Command

import entity.LZException
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.SimpleListenerHost
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.info
import okhttp3.Request
import org.longchuanclub.mirai.plugin.PluginMain
import org.longchuanclub.mirai.plugin.Service.ImageService
import org.longchuanclub.mirai.plugin.config.LzConfig
import org.longchuanclub.mirai.plugin.util.HttpClient
import org.longchuanclub.mirai.plugin.util.SendTask
import java.io.File
import java.time.Instant
import kotlin.coroutines.CoroutineContext

object ImageEvent : SimpleListenerHost(){

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        PluginMain.logger.error("未知错误${exception}")
    }
    var isKey:Boolean = false;
    @EventHandler
    suspend fun GroupMessageEvent.onMessage(): ListeningStatus { // 可以抛出任何异常, 将在 handleException 处理
        val msg = message.content;
        val strname : String?
        //TODO 从消息记录器获取
//        else if(msg.startsWith("#remove"))
//        {
//            if( sender.id== LzConfig.adminQQid){
//                val filename = msg.drop(6).trim()
//                ImageService.removeImage(sender.group.id,filename)
//                SendTask.sendMessage(group,"清理成功")
//                return ListeningStatus.LISTENING
//
//            }
//            else{
//                SendTask.sendMessage(group,At(sender)+"你没权限执行")
//                return ListeningStatus.LISTENING
//            }
//        }
        //TODO 后续增加分人分群控制
//        if(msg == "开关关键字"){
//            if(!LzConfig.enablelist.contains(group.id.toString())){
////                LzConfig.enablelist = mutableListOf(group.id.toString()).plus(LzConfig.enablelist)
//                SendTask.sendMessage(group,"哼哼，接下来你只能使用\"来只\"获取啦")
//            }else {
//                SendTask.sendMessage(group,"已经打开关键字检索辣")
//            }
//        }

        /**
         * 根据关键字匹配
         */
        if(msg.startsWith("添加")){
            strname = msg.drop("添加".length).trim()
            Lzsave(strname,sender)
            return ListeningStatus.LISTENING
        }
        else if(msg == "#group dump"){
            ImageService.updateGrouplist(group.id)
            SendTask.sendMessage(group,"更新成功")
            return ListeningStatus.LISTENING
        }
        else if(msg == "随机来只")
        {
            getRamImg()
        }
        else{
            if(isKey){
                val filenamelist = countFile(group)
                for(eqstr  in filenamelist){
                    if(msg.contains(eqstr)) {
                        getImg(eqstr,-1)
                        return ListeningStatus.LISTENING
                    }
                }
            }
            else{
                if(msg.startsWith("来只")){
                    var getnum = -1;
                    val strlist = msg.split(" ");
                    if(strlist.isNotEmpty()){
                        if(strlist[0].length>2)
                        {
                            strname = strlist[0].drop(2)
                            if(strlist.size==2){
                                try {
                                    getnum = strlist[1].toInt();
                                }
                                catch (e:Exception){
                                    PluginMain.logger.error("转换错误，请确认参数是否为int/Long类型")
                                }
                            }
                        }
                        else{
                            strname = strlist[1].trim()
                            if(strlist.size==3){
                                try {
                                    getnum = strlist[2].toInt();
                                }
                                catch (e:Exception){
                                    PluginMain.logger.error("转换错误，请确认参数是否为int/Long类型")
                                }
                            }
                        }
                    }
                    else{
                        strname = msg.drop("来只".length).trim()
                    }
                    getImg(strname,getnum)
                }

            }

        }

        return ListeningStatus.LISTENING
    }
    private  fun countFile(group: Group): List<String> {
        val filenamelist = mutableListOf<String>();
        val fileURl =  PluginMain.dataFolder.absolutePath+"/LaiZhi/${group.id}";
        val filelist= File(fileURl).listFiles();
        for(f in filelist!!){
            filenamelist.add(f.name)
        }
        return filenamelist.sortedBy{ it.length }.reversed();
    }

    /**
     * 获取图片
     */
    private suspend fun GroupMessageEvent.getImg(arg: String?, arg1 : Int) {
        if (arg != null) {
            val res = ImageService.getImage(sender.group.id,arg)
            //如果不为空，就上传

            this.subject.let {
                val img = res.uploadAsImage(it)
                res.closed
                if(res.isAutoClose) PluginMain.logger.info { "已经关闭了流" }
                SendTask.sendMessage(group, img);
            }
            res.closed
        }

    }
    private suspend fun GroupMessageEvent.getRamImg() {
            val res = ImageService.getRandomImage(sender.group.id)
           this.subject.let {
                val img = res.uploadAsImage(it)
                res.closed
                if(res.isAutoClose) PluginMain.logger.info { "已经关闭了流" }
                SendTask.sendMessage(group, img);
            }
            res.closed

    }
    /**
     * 清理图库
     */
    private suspend fun GroupMessageEvent.clear(filename: String){
        if(  filename  in LzConfig.ProtectImageList)
            this.group.sendMessage(At(sender) +"这是受保护的图库，你无法删除噢(请联系管理员)")
        else {
            var file = File(PluginMain.dataFolderPath.toString()+"/LaiZhi/${this.group.id}/$filename")
            try {
                file.deleteRecursively()
            } catch (e: Exception) {
                this.group.sendMessage("泪目,未知错误")
                e.printStackTrace()
            }
        }

    }/**
     * 保存图库
     */
    private suspend fun GroupMessageEvent.Lzsave(arg: String?, sender1: Member)
    {
        SendTask.sendMessage(group, At(sender1) +"请在300s内发送一张图片")
        var ti1m1 = Instant.now().epochSecond
        globalEventChannel().subscribe<GroupMessageEvent>{
            if((Instant.now().epochSecond-ti1m1)>=300){
                SendTask.sendMessage(group, At(sender1) +"已超时，请重新发送图片")
                return@subscribe ListeningStatus.STOPPED
            }
            if(this.sender.id == sender1.id){
                var chain = this.message;
                val image: Image? = chain.findIsInstance<Image>()

                if(image!=null) {
                    try{
                        val url2 = image.queryUrl()
                        val request = Request.Builder()
                            .url(url2)
                            .build()
                        val response = HttpClient.okHttpClient.newCall(request).execute()
                        val contentType = response.header("Content-Type")
                        val fileType = when (contentType) {
                            "image/jpeg" -> "jpg"
                            "image/png" -> "png"
                            "image/gif" -> "gif"
                            else -> "jpg"
                        }

                        val imageByte = response.body!!.bytes()
                        arg?.let { it1 -> ImageService.saveImage(this.subject.id,it1,imageByte,fileType) }
                        SendTask.sendMessage(group, chain+ PlainText("保存成功噢"));
                    }catch (e: LZException){
                        SendTask.sendMessage(group,"该图库已存在相同图片哦")
                    }
//                    catch (e:Exception)
//                    {
//                        PluginMain.logger.info(e)
//                        SendTask.sendMessage(group,At(sender1)+"保存失败，请尝试使用电脑NTqq发送")
//                    }

                    return@subscribe ListeningStatus.STOPPED
                }


            }
            return@subscribe ListeningStatus.LISTENING

        }
    }


}