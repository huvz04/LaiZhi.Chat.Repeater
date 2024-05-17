package org.longchuanclub.mirai.plugin.entity

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import org.longchuanclub.mirai.plugin.config.LzConfig.provideDelegate

object GarData : AutoSavePluginData("GarData") {

    @ValueDescription("图片数据库")
    val gardata:Map<String,List<String>> by value()
}