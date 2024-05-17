package org.longchuanclub.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.MemberJoinRequestEvent
import net.mamoe.mirai.event.events.MemberLeaveEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.info
import org.longchuanclub.mirai.plugin.Command.getImgList
import org.longchuanclub.mirai.plugin.Service.myEvent
import org.longchuanclub.mirai.plugin.config.LzConfig
import org.longchuanclub.mirai.plugin.util.SendTask


object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "io.huvz.laizhi",
        name = "来只XX",
        version = "0.2.6"
    ) {
        author("Huvz")
        info(
            """
            个人自用
            来只&来点 功能 将群友话语做成可以出发的图
        """.trimIndent()
        )
        //dependsOn("xyz.cssxsh.mirai.plugin.mirai-hibernate-plugin", false)
        // author 和 info 可以删除.
    }
) {

    override fun onEnable() {

        LzConfig.reload()
        CommandManager.registerCommand(getImgList)
        globalEventChannel().registerListenerHost(myEvent)
//        if(LzConfig.openWelcome){
//            globalEventChannel().subscribeAlways<MemberLeaveEvent> {
//                SendTask.sendMessage(group,"(˚ ˃̣̣̥᷄⌓˂̣̣̥᷅ ) 泪目，有人离开了群。")
//            }
//            globalEventChannel().subscribeAlways<MemberJoinEvent> {
//                SendTask.sendMessage(group,At(member)+"欢迎进入本群哇！ ૮(˶ᵔ ᵕ ᵔ˶)ა")
//
//            }
//        }

//        globalEventChannel().subscribeAlways<MemberJoinRequestEvent> {
//            //自动同意加群申请 如果不在黑名单里
//          if(!LzConfig.Blacklist.contains(this.fromId)) accept()
//        }
        logger.info { "Plugin loaded" }
    }



}


