package util.skia.impl

import org.jetbrains.skia.*
import org.longchuanclub.mirai.plugin.entity.ImageData
import org.longchuanclub.mirai.plugin.util.skia.ImageDrawer
import java.io.File

class ImagePreviewDrawer(
    private val fileList: List<ImageData>,
    private val outputWidth: Int,
    private val outputHeight: Int,
    private val numImagesPerRow: Int,
    private val infoHeight:Int,
    private val lt:Float,
    private val targetSize:Float
    // 其他参数
) : ImageDrawer {
    override fun draw(canvas: Canvas) {
        val font = Font(Typeface.makeFromName("MiSans",FontStyle.NORMAL), 20f)

        val detailpaint = Paint().apply {
            color = Color.makeARGB( 230,173,216,230)
        }

        val webpaint = Paint().apply {
            color = Color.makeARGB( 200,240,248,255)
        }
        val textPaint2 = Paint().apply {
//            color = Color.BLACK
            color = Color.makeRGB(13,13,13)
            isAntiAlias = true
        }
        val file = File("C:\\re\\114514\\test\\test.jpeg")
        val firstX = 20+lt;
        canvas.drawImage(
            drawImageDetail(webpaint,file)
            ,firstX,infoHeight.toFloat()+lt*2)
        val infoname  =getInfo(font,"CJJ",detailpaint,textPaint2)

        canvas.drawImage(
            infoname,
            (firstX+targetSize)/2 -10,infoHeight.toFloat()+targetSize+lt*2-23
        )
    }
    private fun drawImageDetail(
        backgroundPaint: Paint,
        originalImage:File,
    ): Image {
        val surfaceBitmap2 = Surface.makeRasterN32Premul(targetSize.toInt(), targetSize.toInt())

        val canvas3 = surfaceBitmap2.canvas
        val rect = RRect.makeXYWH(5f,5f,targetSize-5,
            targetSize-5,20f)


        val paint = Paint().apply {
            color = Color.makeRGB(20,20,20)
            strokeWidth = 1f
            strokeJoin = PaintStrokeJoin.ROUND
            mode = PaintMode.STROKE
            imageFilter = ImageFilter.makeBlur(1f,1f, FilterTileMode.MIRROR)
        }
        canvas3.drawRRect(rect,backgroundPaint)
//        canvas3.clipPath(rectdetailPath)
        canvas3.drawPath(
            Path().apply {
                addRRect(rect)
            }
            ,paint)
        // 缩放原始图像并绘制到新的图像对象上
        var image :Image = Image.makeFromEncoded(originalImage.readBytes())

        canvas3.drawImageRect(
            getIMage(originalImage.readBytes(),image.width,image.height),
            Rect.makeXYWH(15f, 15f, targetSize-30f, targetSize-30f)
        )


        return surfaceBitmap2.makeImageSnapshot()

    }
    private fun getIMage(image:ByteArray,width:Int,height:Int): Image {
        val surfaceBitmap = Surface.makeRasterN32Premul(infoHeight, infoHeight)
        val avatarImage = Image.makeFromEncoded(image)
        val canvas2 = surfaceBitmap.canvas
        // 计算图像的缩放比例
        val scaleX =  width / infoHeight
        val scaleY = height / infoHeight
        val scale = Integer.min(scaleX, scaleY)

        // 计算图像的偏移量
        val offsetX = (width - infoHeight * scale) / 2
        val offsetY = (height - infoHeight * scale) / 2
        val value = Integer.max(offsetX,offsetY)
        canvas2.drawImageRect(
            avatarImage,
            RRect.makeXYWH(0f, 0f, value.toFloat(), value.toFloat(),10f)
            ,Paint())
        return surfaceBitmap.makeImageSnapshot()
    }

    private fun getInfo(font:Font, info:String, backgroundPaint:Paint,textPaint: Paint):Image{
        val surfaceBitmap2 = Surface.makeRasterN32Premul(infoHeight+90, infoHeight)

        val canvas3 = surfaceBitmap2.canvas
        val rect = RRect.makeXYWH(5f,5f,font.measureText(info).width+40f,font.measureText(info).height+10f,10f)


        val paint = Paint().apply {
            color = Color.makeRGB(20,20,20)
            strokeWidth = 1f // 设置画笔宽度为 3 像素
            strokeJoin = PaintStrokeJoin.ROUND
            mode = PaintMode.STROKE
            imageFilter = ImageFilter.makeBlur(1f,1f,FilterTileMode.MIRROR)
        }
        canvas3.drawRRect(rect,backgroundPaint)
//        canvas3.clipPath(rectdetailPath)
        canvas3.drawPath(
            Path().apply {
                addRRect(rect)
            }
            ,paint)
        canvas3.drawString(info, 24f, 27f, font, textPaint)


        return surfaceBitmap2.makeImageSnapshot()

    }


}