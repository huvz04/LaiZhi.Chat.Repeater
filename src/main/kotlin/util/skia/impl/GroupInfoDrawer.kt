package org.longchuanclub.mirai.plugin.util.skia.impl

import org.jetbrains.skia.*
import org.longchuanclub.mirai.plugin.entity.GroupDetail
import org.longchuanclub.mirai.plugin.util.skia.ImageDrawer

class GroupInfoDrawer
    (
    private val outputWidth: Int,
    private val groupDetail: GroupDetail,
    private val infoHeight:Int,
    private val lt: Float
        )
    : ImageDrawer {
    override fun draw(canvas: Canvas) {
        // 绘制群头像

        val surfaceBitmap = Surface.makeRasterN32Premul(infoHeight, infoHeight)



        val avatarRadius = infoHeight / 2f - 10f
        val avatarX = lt + 10f
        val avatarY = infoHeight / 2f - 5f
        val avatarImage = Image.makeFromEncoded(groupDetail.avatar)


        val canvas2 = surfaceBitmap.canvas
        val rectPath = Path().apply {
            addCircle(avatarX, avatarY, avatarRadius,PathDirection.CLOCKWISE)
        }
        canvas2.clipPath(rectPath)
        canvas2.drawImageRect(
            avatarImage,
            Rect.makeXYWH(0f, 0f, infoHeight.toFloat(), infoHeight.toFloat())
            ,Paint())
        val imageavatar = surfaceBitmap.makeImageSnapshot()
        canvas.drawImage(imageavatar,avatarX,avatarY)


        // 绘制群名称和群号
        val nameX = avatarX + avatarRadius + 20f + lt
        val nameY = avatarY - 10f + lt
        val nametextFont = Font(Typeface.makeFromName("微软雅黑",FontStyle.BOLD), 24f)
        val textPaint = Paint().apply {
            color = Color.BLACK
        }
        canvas.drawString(groupDetail.name, nameX, nameY, nametextFont,textPaint)
        val infoPaint = Paint().apply {
            color = Color.makeRGB(139,139,156)
        }
        val idtextFont = Font(Typeface.makeFromName("微软雅黑",FontStyle.NORMAL), 16f)


        val detailtFont = Font(Typeface.makeFromName("微软雅黑",FontStyle.NORMAL), 20f)
        canvas.drawString(" ${groupDetail.id}", nameX, nameY + 30f,idtextFont, infoPaint)
        val infoText = "总图片数: ${groupDetail.total} "
        // 绘制其他信息
        val infoX = outputWidth  - idtextFont.measureText(infoText).width - 200f
        val infoY = nameY
        canvas.drawImage(getInfo(detailtFont,infoText),infoX,infoY)


    }


    fun getInfo(font:Font,info:String):Image{
        val surfaceBitmap2 = Surface.makeRasterN32Premul(infoHeight, infoHeight)

        val canvas3 = surfaceBitmap2.canvas
        val rect = RRect.makeXYWH(0f,0f,font.measureText(info).width+10f,font.measureText(info).height+10f,20f)
        val textPaint3 = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
        }
        val webpaint = Paint().apply {
            color = Color.makeRGB(20,20,20)
            strokeWidth = 2f // 设置画笔宽度为 3 像素
            strokeJoin = PaintStrokeJoin.ROUND
        }
        val paint = Paint().apply {
            color = Color.makeRGB(20,20,20)
            strokeWidth = 2f // 设置画笔宽度为 3 像素
            strokeJoin = PaintStrokeJoin.ROUND
            mode = PaintMode.STROKE
            imageFilter = ImageFilter.makeBlur(1f,1f,FilterTileMode.REPEAT)
        }
        canvas3.drawRRect(rect,paint)
//        canvas3.clipPath(rectdetailPath)
        canvas3.drawPath(
            Path().apply {
                addRRect(rect)
            }
            ,paint)
        canvas3.drawString(info, font.measureText(info).width, font.measureText(info).height, font, textPaint3)


       return surfaceBitmap2.makeImageSnapshot()

    }
}