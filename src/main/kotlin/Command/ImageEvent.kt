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
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.info
import org.longchuanclub.mirai.plugin.PluginMain
import org.longchuanclub.mirai.plugin.Service.ImageService
import org.longchuanclub.mirai.plugin.config.LzConfig
import org.longchuanclub.mirai.plugin.util.ImageUtils
import org.longchuanclub.mirai.plugin.util.SendTask
import java.io.File
import java.time.Instant

object ImageEvent : SimpleListenerHost(){

//    override fun handleException(context: CoroutineContext, exception: Throwable) {
//        // 处理 onMessage 中未捕获的异常
//        PluginMain.logger.error("未知错误")
//    }
    var isKey:Boolean = false;
    @EventHandler
    suspend fun GroupMessageEvent.onMessage(): ListeningStatus { // 可以抛出任何异常, 将在 handleException 处理
        val msg = message.content;
        val strname : String?
        if(LzConfig.Graphicslist.contains(msg)){
            val files  = countFile(group)
            var cnt = 0;
            SendTask.sendMessage(
                group,
                buildMessageChain {
                    At(sender.id)
                    +"检索到的图库如下:"
                    for(s in files) {
                        cnt++;
                        +PlainText("[$s], ")
                    }
                    }

                )


        }
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
        else if(msg == "开关关键字"){
            if(isKey){
                isKey =false;
                SendTask.sendMessage(group,"哼哼，接下来你只能使用\"来只\"获取啦")
            }else {
                isKey =true;
                SendTask.sendMessage(group,"已经打开关键字检索辣")
            }
        }
        /**
         *  匹配添加指令
         */


        /**
         * 根据关键字匹配
         */
        else if(msg.startsWith("添加")){
            strname = msg.drop("添加".length).trim()
            Lzsave(strname,sender)

            return ListeningStatus.LISTENING
        }
        else{
            if(isKey){
                var filenamelist = countFile(group)
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
                            PluginMain.logger.info("开始分割数组")
                            strname = strlist[0].drop(2)
                            PluginMain.logger.info("arg:1${strname}")
                            if(strlist.size==2){
                                try {
                                    getnum = strlist[1].toInt();
                                }
                                catch (e:Exception){
                                    PluginMain.logger.error("转换错误，请确认参数是否为int/Long类型")
                                }
                            }
                        }
                        //"来只 夏"
                        else{
                            //直接提取
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

        return ListeningStatus.LISTENING // 表示继续监听事件
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

            if (res != null) {
                this.subject.let {
                    val img = res.uploadAsImage(it)
                    res.closed
                    if(res.isAutoClose) PluginMain.logger.info { "已经关闭了流" }
                    SendTask.sendMessage(group, img);

                }
            } else {
                SendTask.sendMessage(group, "目录下找不到图片噢")
            }

        }
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

    }

    /**
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

                        arg?.let { it1 -> ImageService.saveImage(this.subject.id,it1,image) }
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