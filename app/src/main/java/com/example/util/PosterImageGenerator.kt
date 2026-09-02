package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.data.NovelRepository
import com.example.data.model.PosterTemplate
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

object PosterImageGenerator {

    private fun getPersianBoldTypeface(context: Context): Typeface {
        return runCatching { ResourcesCompat.getFont(context, R.font.font_vazirmatn_bold) }.getOrNull() ?: Typeface.DEFAULT_BOLD
    }

    private fun getPersianRegularTypeface(context: Context): Typeface {
        return runCatching { ResourcesCompat.getFont(context, R.font.font_vazirmatn_regular) }.getOrNull() ?: Typeface.SANS_SERIF
    }

    fun generateAndSharePoster(
        context: Context,
        template: PosterTemplate,
        quoteText: String,
        chapterTitle: String
    ): Boolean {
        return try {
            val width = 1080
            val boldTf = getPersianBoldTypeface(context)
            val regTf = getPersianRegularTypeface(context)
            val bitmap = when (template) {
                PosterTemplate.TICKET -> createTicketPoster(width, quoteText, chapterTitle, regTf, boldTf)
                PosterTemplate.CYBER_GLASS -> createCyberPoster(width, quoteText, chapterTitle, regTf, boldTf)
                PosterTemplate.IMPERIAL_GOLD -> createImperialPoster(width, quoteText, chapterTitle, regTf, boldTf)
                PosterTemplate.DARK_EDITORIAL -> createEditorialPoster(width, quoteText, chapterTitle, regTf, boldTf)
            }

            // Save to cache directory
            val imagesFolder = File(context.cacheDir, "images")
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "quote_poster_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            // Get content Uri
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(
                    Intent.EXTRA_TEXT,
                    """
                    📖 رمان «راز الماس»
                    📜 برشی از: $chapterTitle
                    ✍️ نویسنده: ${NovelRepository.NOVEL_AUTHOR}

                    📢 کانال رسمی: ${NovelRepository.NOVEL_CHANNEL}
                    📱 اپلیکیشن کتاب‌خوان: روایت ما
                    """.trimIndent()
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "اشتراک‌گذاری کارت رمان راز الماس")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun createTicketPoster(w: Int, quote: String, chapter: String, regTf: Typeface, boldTf: Typeface): Bitmap {
        val cardMargin = 60f
        val textWidth = (w - (cardMargin * 2) - 120f).toInt()

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF111827.toInt()
            textSize = 48f
            typeface = regTf
        }

        val staticLayout = StaticLayout.Builder.obtain(
            "«$quote»",
            0,
            "«$quote»".length,
            textPaint,
            textWidth
        )
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(24f, 1.25f)
            .build()

        val topHeaderHeight = 240f
        val textHeight = staticLayout.height.toFloat()
        val footerHeight = 280f
        val calculatedHeight = cardMargin + topHeaderHeight + textHeight + footerHeight + cardMargin
        val h = max(1100f, calculatedHeight).toInt()

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Outer canvas background
        val bgPaint = Paint().apply { color = 0xFF0B0F19.toInt() }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        // Ticket card
        val cardRect = RectF(cardMargin, cardMargin, w - cardMargin, h - cardMargin)
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFFFFFFF.toInt() }
        canvas.drawRoundRect(cardRect, 40f, 40f, cardPaint)

        // Top tag
        val tagPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF121820.toInt() }
        val tagRect = RectF(cardMargin + 50f, cardMargin + 50f, cardMargin + 430f, cardMargin + 130f)
        canvas.drawRoundRect(tagRect, 20f, 20f, tagPaint)

        val tagTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
            textSize = 34f
            typeface = boldTf
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("برشی از رمان راز الماس", tagRect.centerX(), tagRect.centerY() + 12f, tagTextPaint)

        val chPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF4B5563.toInt()
            textSize = 36f
            typeface = boldTf
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(chapter, w - cardMargin - 50f, cardMargin + 100f, chPaint)

        // Top divider
        val linePaint = Paint().apply {
            color = 0xFFE5E7EB.toInt()
            strokeWidth = 4f
        }
        canvas.drawLine(cardMargin + 50f, cardMargin + 170f, w - cardMargin - 50f, cardMargin + 170f, linePaint)

        // Draw Quote Text
        canvas.save()
        canvas.translate(cardMargin + 60f, cardMargin + 220f)
        staticLayout.draw(canvas)
        canvas.restore()

        // Footer Section - Positioned safely right after the text
        val footerStartY = cardMargin + 220f + textHeight + 50f
        canvas.drawLine(cardMargin + 50f, footerStartY, w - cardMargin - 50f, footerStartY, linePaint)

        // Barcode lines
        val barcodeY = footerStartY + 30f
        val barcodeW = w - (cardMargin * 2) - 100f
        val step = barcodeW / 36f
        val barcodePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF121820.toInt() }
        for (i in 0 until 36) {
            val isThick = (i % 3 == 0)
            barcodePaint.strokeWidth = if (isThick) 6f else 3f
            barcodePaint.alpha = if (i % 5 == 0) 90 else 220
            val x = cardMargin + 50f + (i * step)
            canvas.drawLine(x, barcodeY, x, barcodeY + 36f, barcodePaint)
        }

        // Author and Channel Info separated vertically to prevent overlap
        val authorPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF1F2937.toInt()
            textSize = 36f
            typeface = boldTf
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("✍️ نویسنده: ${NovelRepository.NOVEL_AUTHOR}", w - cardMargin - 50f, barcodeY + 95f, authorPaint)

        val channelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF4B5563.toInt()
            textSize = 32f
            typeface = regTf
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("📢 کانال رسمی: ${NovelRepository.NOVEL_CHANNEL}", w - cardMargin - 50f, barcodeY + 150f, channelPaint)

        return bitmap
    }

    private fun createCyberPoster(w: Int, quote: String, chapter: String, regTf: Typeface, boldTf: Typeface): Bitmap {
        val cardMargin = 60f
        val textWidth = (w - (cardMargin * 2) - 120f).toInt()

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFF1F5F9.toInt()
            textSize = 48f
            typeface = regTf
        }

        val staticLayout = StaticLayout.Builder.obtain(
            "«$quote»",
            0,
            "«$quote»".length,
            textPaint,
            textWidth
        )
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(24f, 1.25f)
            .build()

        val topHeaderHeight = 220f
        val textHeight = staticLayout.height.toFloat()
        val footerHeight = 250f
        val calculatedHeight = cardMargin + topHeaderHeight + textHeight + footerHeight + cardMargin
        val h = max(1100f, calculatedHeight).toInt()

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgShader = LinearGradient(0f, 0f, w.toFloat(), h.toFloat(), 0xFF141E30.toInt(), 0xFF0A0F19.toInt(), Shader.TileMode.CLAMP)
        val bgPaint = Paint().apply { shader = bgShader }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        val cardRect = RectF(cardMargin, cardMargin, w - cardMargin, h - cardMargin)
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF00E5FF.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        canvas.drawRoundRect(cardRect, 40f, 40f, borderPaint)

        // Top emblem
        val headerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF00E5FF.toInt()
            textSize = 38f
            typeface = boldTf
        }
        canvas.drawText("💎 راز الماس", cardMargin + 60f, cardMargin + 110f, headerPaint)

        val chPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF38BDF8.toInt()
            textSize = 36f
            typeface = boldTf
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(chapter, w - cardMargin - 60f, cardMargin + 110f, chPaint)

        // Text
        canvas.save()
        canvas.translate(cardMargin + 60f, cardMargin + 200f)
        staticLayout.draw(canvas)
        canvas.restore()

        // Footer
        val footerStartY = cardMargin + 200f + textHeight + 40f
        val divPaint = Paint().apply {
            color = 0x3300E5FF.toInt()
            strokeWidth = 3f
        }
        canvas.drawLine(cardMargin + 60f, footerStartY, w - cardMargin - 60f, footerStartY, divPaint)

        val authorPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFE0E7FF.toInt()
            textSize = 36f
            typeface = boldTf
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("✍️ اثر: ${NovelRepository.NOVEL_AUTHOR}", w - cardMargin - 60f, footerStartY + 65f, authorPaint)

        val channelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF7C92B3.toInt()
            textSize = 32f
            typeface = regTf
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("📢 کانال: ${NovelRepository.NOVEL_CHANNEL}", w - cardMargin - 60f, footerStartY + 125f, channelPaint)

        return bitmap
    }

    private fun createImperialPoster(w: Int, quote: String, chapter: String, regTf: Typeface, boldTf: Typeface): Bitmap {
        val cardMargin = 50f
        val textWidth = (w - (cardMargin * 2) - 140f).toInt()

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF2A1F14.toInt()
            textSize = 48f
            typeface = regTf
        }

        val staticLayout = StaticLayout.Builder.obtain(
            "«$quote»",
            0,
            "«$quote»".length,
            textPaint,
            textWidth
        )
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(24f, 1.25f)
            .build()

        val topHeaderHeight = 240f
        val textHeight = staticLayout.height.toFloat()
        val footerHeight = 250f
        val calculatedHeight = cardMargin + topHeaderHeight + textHeight + footerHeight + cardMargin
        val h = max(1100f, calculatedHeight).toInt()

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint().apply { color = 0xFFFBF5E6.toInt() }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF9E7D4A.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 14f
        }
        canvas.drawRect(cardMargin, cardMargin, w - cardMargin, h - cardMargin, borderPaint)

        val innerBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFD4B483.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawRect(cardMargin + 20f, cardMargin + 20f, w - cardMargin - 20f, h - cardMargin - 20f, innerBorder)

        val topPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF6E4018.toInt()
            textSize = 36f
            typeface = boldTf
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("📜 برشی از روایت کهن راز الماس", cardMargin + 60f, cardMargin + 110f, topPaint)

        val chPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF965D2C.toInt()
            textSize = 34f
            typeface = boldTf
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(chapter, w - cardMargin - 60f, cardMargin + 110f, chPaint)

        val divPaint = Paint().apply {
            color = 0xFFD4B483.toInt()
            strokeWidth = 3f
        }
        canvas.drawLine(cardMargin + 60f, cardMargin + 160f, w - cardMargin - 60f, cardMargin + 160f, divPaint)

        // Text
        canvas.save()
        canvas.translate(cardMargin + 70f, cardMargin + 210f)
        staticLayout.draw(canvas)
        canvas.restore()

        val footerStartY = cardMargin + 210f + textHeight + 40f
        canvas.drawLine(cardMargin + 60f, footerStartY, w - cardMargin - 60f, footerStartY, divPaint)

        val authorPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF5E4A36.toInt()
            textSize = 36f
            typeface = boldTf
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("✍️ به قلم: ${NovelRepository.NOVEL_AUTHOR}", w - cardMargin - 70f, footerStartY + 65f, authorPaint)

        val channelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF8B6338.toInt()
            textSize = 32f
            typeface = regTf
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("📢 کانال: ${NovelRepository.NOVEL_CHANNEL}", w - cardMargin - 70f, footerStartY + 125f, channelPaint)

        return bitmap
    }

    private fun createEditorialPoster(w: Int, quote: String, chapter: String, regTf: Typeface, boldTf: Typeface): Bitmap {
        val cardMargin = 60f
        val textWidth = (w - (cardMargin * 2) - 180f).toInt()

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFE6EDF3.toInt()
            textSize = 46f
            typeface = regTf
        }

        val staticLayout = StaticLayout.Builder.obtain(
            "«$quote»",
            0,
            "«$quote»".length,
            textPaint,
            textWidth
        )
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(24f, 1.25f)
            .build()

        val topHeaderHeight = 220f
        val textHeight = staticLayout.height.toFloat()
        val footerHeight = 250f
        val calculatedHeight = cardMargin + topHeaderHeight + textHeight + footerHeight + cardMargin
        val h = max(1100f, calculatedHeight).toInt()

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint().apply { color = 0xFF0D1117.toInt() }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF30363D.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawRoundRect(RectF(cardMargin, cardMargin, w - cardMargin, h - cardMargin), 32f, 32f, borderPaint)

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFE6EDF3.toInt()
            textSize = 38f
            typeface = boldTf
        }
        canvas.drawText("راز الماس", cardMargin + 60f, cardMargin + 110f, titlePaint)

        val chPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF5288C1.toInt()
            textSize = 36f
            typeface = boldTf
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(chapter, w - cardMargin - 60f, cardMargin + 110f, chPaint)

        // Accent line on right
        val accentLinePaint = Paint().apply {
            color = 0xFF2EA6FF.toInt()
            strokeWidth = 10f
        }
        canvas.drawLine(cardMargin + 60f, cardMargin + 200f, cardMargin + 60f, cardMargin + 200f + textHeight, accentLinePaint)

        // Text
        canvas.save()
        canvas.translate(cardMargin + 100f, cardMargin + 200f)
        staticLayout.draw(canvas)
        canvas.restore()

        val footerStartY = cardMargin + 200f + textHeight + 40f
        val divPaint = Paint().apply {
            color = 0x1AFFFFFF.toInt()
            strokeWidth = 3f
        }
        canvas.drawLine(cardMargin + 60f, footerStartY, w - cardMargin - 60f, footerStartY, divPaint)

        val authorPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF8B949E.toInt()
            textSize = 34f
            typeface = boldTf
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("✍️ نویسنده: ${NovelRepository.NOVEL_AUTHOR}", w - cardMargin - 60f, footerStartY + 65f, authorPaint)

        val channelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF8B949E.toInt()
            textSize = 32f
            typeface = regTf
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("📢 کانال: ${NovelRepository.NOVEL_CHANNEL}", w - cardMargin - 60f, footerStartY + 125f, channelPaint)

        return bitmap
    }
}

