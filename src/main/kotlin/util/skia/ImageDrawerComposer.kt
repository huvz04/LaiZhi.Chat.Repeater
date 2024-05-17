package util.skia

import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Surface
import org.longchuanclub.mirai.plugin.entity.GroupDetail
import org.longchuanclub.mirai.plugin.entity.ImageData
import org.longchuanclub.mirai.plugin.util.skia.impl.GroupInfoDrawer
import util.skia.impl.BackgroundDrawer
import util.skia.impl.ImagePreviewDrawer
import util.skia.impl.MaskDrawer
import java.io.File
import java.io.FileOutputStream

class ImageDrawerComposer(
    private val outputWidth: Int,
    private val outputHeight: Int,
    private val titleText: String,
    private val fileList: List<ImageData>,
    private val numImagesPerRow: Int,
    private val groupDetail: GroupDetail,
    private val lt: Float,
    private val infoHeight:Int
) {

    fun draw(): File {
        val surface = Surface.makeRasterN32Premul(outputWidth, outputHeight)
        val canvas = surface.canvas

        val drawers = listOf(
            BackgroundDrawer(outputWidth, outputHeight, titleText),
            ImagePreviewDrawer(fileList, outputWidth, outputHeight, numImagesPerRow, /* 其他参数 */),
            MaskDrawer(outputWidth, outputHeight,infoHeight,lt),
            GroupInfoDrawer(outputWidth,groupDetail,infoHeight,lt)
        )

        drawers.forEach { drawer ->
            drawer.draw(canvas)
        }

        val tempFile = File.createTempFile("image", ".png")
        val outputStream = FileOutputStream(tempFile)
        val image = surface.makeImageSnapshot()
        val encoded = image.encodeToData(EncodedImageFormat.PNG, 110)
        encoded?.use { data ->
            outputStream.write(data.bytes)
        }
        outputStream.close()
        surface.close()
        //val externalResource = tempFile.toExternalResource()
        //tempFile.delete()
        return tempFile
    }
}