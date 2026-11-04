/*
 * Discipline Planner - Daily Habit & Self-Mastery Platform
 * Copyright (C) 2026 Discipline Planner Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.UserProfileEntity

@Composable
fun AvatarCanvas(
  profile: UserProfileEntity,
  sizeDp: Dp = 160.dp,
  enableAnimation: Boolean = true,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "avatar_float")
  val floatOffset by if (enableAnimation) {
    infiniteTransition.animateFloat(
      initialValue = -6f,
      targetValue = 6f,
      animationSpec = infiniteRepeatable(
        animation = tween(2200, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      ),
      label = "avatar_y"
    )
  } else {
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
  }

  val skinColor = parseHexColor(profile.skinToneHex, Color(0xFFF5D0A9))
  val skinShadow = skinColor.copy(
    red = (skinColor.red * 0.75f).coerceIn(0f, 1f),
    green = (skinColor.green * 0.75f).coerceIn(0f, 1f),
    blue = (skinColor.blue * 0.75f).coerceIn(0f, 1f)
  )

  val auraColors = when (profile.auraTheme) {
    "spartan_fire" -> listOf(Color(0xFFEF4444), Color(0xFF7F1D1D))
    "cosmic_violet" -> listOf(Color(0xFF8B5CF6), Color(0xFF2E1065))
    "emerald" -> listOf(Color(0xFF10B981), Color(0xFF064E3B))
    "gold_dawn" -> listOf(Color(0xFFF59E0B), Color(0xFF78350F))
    else -> listOf(Color(0xFF2563EB), Color(0xFF0F172A))
  }

  Box(
    modifier = modifier.size(sizeDp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(
      modifier = Modifier
        .size(sizeDp)
        .offset(y = floatOffset.dp)
    ) {
      val w = size.width
      val h = size.height

      // 1. Draw Background Aura / Gradient Circle
      val auraBrush = Brush.radialGradient(
        colors = auraColors,
        center = Offset(w * 0.5f, h * 0.5f),
        radius = w * 0.5f
      )
      drawCircle(
        brush = auraBrush,
        radius = w * 0.48f,
        center = Offset(w * 0.5f, h * 0.5f)
      )

      // Outer glow ring
      drawCircle(
        color = auraColors.first().copy(alpha = 0.35f),
        radius = w * 0.485f,
        center = Offset(w * 0.5f, h * 0.5f),
        style = Stroke(width = 3.dp.toPx())
      )

      // 2. Draw Shoulders / Outfit
      drawOutfit(profile.outfit, w, h)

      // 3. Draw Neck
      drawRect(
        color = skinShadow,
        topLeft = Offset(w * 0.42f, h * 0.60f),
        size = Size(w * 0.16f, h * 0.12f)
      )

      // 4. Draw Head (3D rounded egg/rectangle)
      val headRect = Size(w * 0.36f, h * 0.38f)
      val headTopLeft = Offset(w * 0.32f, h * 0.28f)
      
      // Head base shadow
      drawRoundRect(
        color = skinShadow,
        topLeft = headTopLeft,
        size = headRect,
        cornerRadius = CornerRadius(w * 0.18f, h * 0.19f)
      )
      // Head gradient for 3D specular depth
      val headBrush = Brush.verticalGradient(
        colors = listOf(skinColor, skinShadow),
        startY = headTopLeft.y,
        endY = headTopLeft.y + headRect.height
      )
      drawRoundRect(
        brush = headBrush,
        topLeft = Offset(headTopLeft.x, headTopLeft.y - 2f),
        size = Size(headRect.width, headRect.height - 4f),
        cornerRadius = CornerRadius(w * 0.18f, h * 0.19f)
      )

      // 5. Draw Facial Hair (Beard / Mustache)
      drawFacialHair(profile.facialHair, w, h)

      // 6. Draw Eyes & Smile
      drawEyesAndFace(w, h)

      // 7. Draw Eyewear
      drawEyewear(profile.eyewear, w, h)

      // 8. Draw Hairstyle
      drawHair(profile.hairStyle, w, h)

      // 9. Draw Streak-Unlocked Headwear
      drawHeadwear(profile.equippedHeadwear, w, h)
    }
  }
}

private fun DrawScope.drawOutfit(outfit: String, w: Float, h: Float) {
  val path = Path().apply {
    moveTo(w * 0.15f, h * 0.95f)
    lineTo(w * 0.28f, h * 0.68f)
    lineTo(w * 0.72f, h * 0.68f)
    lineTo(w * 0.85f, h * 0.95f)
    close()
  }

  when (outfit) {
    "armor" -> {
      // Spartan Bronze / Steel Plate Armor
      val armorBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFD97706), Color(0xFF78350F), Color(0xFF451A03)),
        startY = h * 0.68f,
        endY = h * 0.95f
      )
      drawPath(path, armorBrush)
      // Bronze plate chest emblem (Spartan Lambda Λ)
      val lambda = Path().apply {
        moveTo(w * 0.5f, h * 0.73f)
        lineTo(w * 0.44f, h * 0.86f)
        moveTo(w * 0.5f, h * 0.73f)
        lineTo(w * 0.56f, h * 0.86f)
      }
      drawPath(lambda, Color(0xFFFDE68A), style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
    }
    "hoodie" -> {
      val hoodieBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
        startY = h * 0.68f,
        endY = h * 0.95f
      )
      drawPath(path, hoodieBrush)
      // Draw hoodie collar
      drawCircle(Color(0xFF334155), radius = w * 0.12f, center = Offset(w * 0.5f, h * 0.72f))
    }
    "blazer" -> {
      drawPath(path, Color(0xFF1E1B4B))
      // Shirt and tie
      val shirtPath = Path().apply {
        moveTo(w * 0.44f, h * 0.68f)
        lineTo(w * 0.56f, h * 0.68f)
        lineTo(w * 0.5f, h * 0.82f)
        close()
      }
      drawPath(shirtPath, Color.White)
      drawLine(
        Color(0xFFDC2626),
        start = Offset(w * 0.5f, h * 0.70f),
        end = Offset(w * 0.5f, h * 0.84f),
        strokeWidth = 3.dp.toPx()
      )
    }
    "martial_gi" -> {
      drawPath(path, Color(0xFFF8FAFC))
      // Black belt ribbon
      drawRect(
        Color(0xFF0F172A),
        topLeft = Offset(w * 0.32f, h * 0.86f),
        size = Size(w * 0.36f, h * 0.05f)
      )
    }
    else -> { // tee
      val teeBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
        startY = h * 0.68f,
        endY = h * 0.95f
      )
      drawPath(path, teeBrush)
    }
  }
}

private fun DrawScope.drawEyesAndFace(w: Float, h: Float) {
  // Eyebrows
  drawLine(
    Color(0xFF1E293B),
    start = Offset(w * 0.38f, h * 0.39f),
    end = Offset(w * 0.44f, h * 0.38f),
    strokeWidth = 2.5.dp.toPx(),
    cap = StrokeCap.Round
  )
  drawLine(
    Color(0xFF1E293B),
    start = Offset(w * 0.62f, h * 0.39f),
    end = Offset(w * 0.56f, h * 0.38f),
    strokeWidth = 2.5.dp.toPx(),
    cap = StrokeCap.Round
  )

  // Eyes (Duolingo-styled friendly bold dots with specular white catchlight)
  drawCircle(Color(0xFF0F172A), radius = w * 0.032f, center = Offset(w * 0.41f, h * 0.43f))
  drawCircle(Color.White, radius = w * 0.010f, center = Offset(w * 0.405f, h * 0.425f))

  drawCircle(Color(0xFF0F172A), radius = w * 0.032f, center = Offset(w * 0.59f, h * 0.43f))
  drawCircle(Color.White, radius = w * 0.010f, center = Offset(w * 0.585f, h * 0.425f))

  // Confident smile
  val smilePath = Path().apply {
    moveTo(w * 0.45f, h * 0.54f)
    quadraticTo(w * 0.5f, h * 0.58f, w * 0.55f, h * 0.54f)
  }
  drawPath(smilePath, Color(0xFF0F172A), style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))
}

private fun DrawScope.drawFacialHair(facialHair: String, w: Float, h: Float) {
  val hairColor = Color(0xFF1E293B)
  when (facialHair) {
    "stubble" -> {
      drawCircle(hairColor.copy(alpha = 0.25f), radius = w * 0.08f, center = Offset(w * 0.5f, h * 0.56f))
    }
    "full_beard" -> {
      val beardPath = Path().apply {
        moveTo(w * 0.33f, h * 0.48f)
        lineTo(w * 0.35f, h * 0.63f)
        quadraticTo(w * 0.5f, h * 0.70f, w * 0.65f, h * 0.63f)
        lineTo(w * 0.67f, h * 0.48f)
        quadraticTo(w * 0.5f, h * 0.54f, w * 0.33f, h * 0.48f)
        close()
      }
      drawPath(beardPath, hairColor)
    }
    "mustache" -> {
      val stache = Path().apply {
        moveTo(w * 0.42f, h * 0.51f)
        quadraticTo(w * 0.5f, h * 0.49f, w * 0.58f, h * 0.51f)
        quadraticTo(w * 0.5f, h * 0.54f, w * 0.42f, h * 0.51f)
        close()
      }
      drawPath(stache, hairColor)
    }
    "goatee" -> {
      val goatee = Path().apply {
        moveTo(w * 0.46f, h * 0.56f)
        lineTo(w * 0.54f, h * 0.56f)
        lineTo(w * 0.5f, h * 0.64f)
        close()
      }
      drawPath(goatee, hairColor)
    }
  }
}

private fun DrawScope.drawEyewear(eyewear: String, w: Float, h: Float) {
  when (eyewear) {
    "round_specs" -> {
      drawCircle(Color(0xFF334155), radius = w * 0.052f, center = Offset(w * 0.41f, h * 0.43f), style = Stroke(width = 2.dp.toPx()))
      drawCircle(Color(0xFF334155), radius = w * 0.052f, center = Offset(w * 0.59f, h * 0.43f), style = Stroke(width = 2.dp.toPx()))
      drawLine(Color(0xFF334155), Offset(w * 0.46f, h * 0.43f), Offset(w * 0.54f, h * 0.43f), strokeWidth = 2.dp.toPx())
    }
    "bold_frames" -> {
      drawRoundRect(Color(0xFF0F172A), topLeft = Offset(w * 0.35f, h * 0.39f), size = Size(w * 0.12f, h * 0.08f), cornerRadius = CornerRadius(4f, 4f), style = Stroke(3.dp.toPx()))
      drawRoundRect(Color(0xFF0F172A), topLeft = Offset(w * 0.53f, h * 0.39f), size = Size(w * 0.12f, h * 0.08f), cornerRadius = CornerRadius(4f, 4f), style = Stroke(3.dp.toPx()))
      drawLine(Color(0xFF0F172A), Offset(w * 0.47f, h * 0.43f), Offset(w * 0.53f, h * 0.43f), strokeWidth = 3.dp.toPx())
    }
    "aviator" -> {
      val lensBrush = Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
      drawRoundRect(lensBrush, topLeft = Offset(w * 0.35f, h * 0.38f), size = Size(w * 0.12f, h * 0.09f), cornerRadius = CornerRadius(10f, 10f))
      drawRoundRect(lensBrush, topLeft = Offset(w * 0.53f, h * 0.38f), size = Size(w * 0.12f, h * 0.09f), cornerRadius = CornerRadius(10f, 10f))
      drawLine(Color(0xFFD97706), Offset(w * 0.34f, h * 0.38f), Offset(w * 0.66f, h * 0.38f), strokeWidth = 2.dp.toPx())
    }
    "cyber_visor" -> {
      val visorBrush = Brush.horizontalGradient(listOf(Color(0xFF06B6D4), Color(0xFF3B82F6), Color(0xFF8B5CF6)))
      drawRoundRect(
        visorBrush,
        topLeft = Offset(w * 0.32f, h * 0.39f),
        size = Size(w * 0.36f, h * 0.07f),
        cornerRadius = CornerRadius(6f, 6f)
      )
    }
  }
}

private fun DrawScope.drawHair(hairStyle: String, w: Float, h: Float) {
  val hairColor = Color(0xFF0F172A)
  when (hairStyle) {
    "quiff" -> {
      val quiff = Path().apply {
        moveTo(w * 0.30f, h * 0.32f)
        quadraticTo(w * 0.32f, h * 0.18f, w * 0.52f, h * 0.18f)
        quadraticTo(w * 0.68f, h * 0.20f, w * 0.70f, h * 0.32f)
        lineTo(w * 0.65f, h * 0.30f)
        quadraticTo(w * 0.50f, h * 0.26f, w * 0.35f, h * 0.30f)
        close()
      }
      drawPath(quiff, hairColor)
    }
    "afro" -> {
      drawCircle(hairColor, radius = w * 0.24f, center = Offset(w * 0.5f, h * 0.28f))
    }
    "topknot" -> {
      drawCircle(hairColor, radius = w * 0.07f, center = Offset(w * 0.5f, h * 0.18f))
      drawRoundRect(
        hairColor,
        topLeft = Offset(w * 0.31f, h * 0.25f),
        size = Size(w * 0.38f, h * 0.08f),
        cornerRadius = CornerRadius(8f, 8f)
      )
    }
    "side_part" -> {
      val part = Path().apply {
        moveTo(w * 0.30f, h * 0.32f)
        quadraticTo(w * 0.40f, h * 0.22f, w * 0.68f, h * 0.24f)
        lineTo(w * 0.68f, h * 0.32f)
        quadraticTo(w * 0.50f, h * 0.28f, w * 0.30f, h * 0.32f)
        close()
      }
      drawPath(part, hairColor)
    }
    "fade" -> {
      drawRoundRect(
        hairColor,
        topLeft = Offset(w * 0.33f, h * 0.25f),
        size = Size(w * 0.34f, h * 0.06f),
        cornerRadius = CornerRadius(6f, 6f)
      )
    }
  }
}

private fun DrawScope.drawHeadwear(headwear: String, w: Float, h: Float) {
  when (headwear) {
    "laurel" -> {
      // Golden Victor Laurel Wreath (7+ days)
      val gold = Color(0xFFF59E0B)
      val green = Color(0xFF10B981)
      drawLine(gold, Offset(w * 0.28f, h * 0.28f), Offset(w * 0.72f, h * 0.28f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
      for (i in 0..4) {
        val x = w * (0.33f + i * 0.085f)
        drawCircle(green, radius = w * 0.022f, center = Offset(x, h * 0.25f))
        drawCircle(gold, radius = w * 0.012f, center = Offset(x, h * 0.27f))
      }
    }
    "knight_helm" -> {
      // Steel Knight Greathelm (14+ days)
      val steel = Color(0xFF94A3B8)
      val steelDark = Color(0xFF475569)
      val helm = Path().apply {
        moveTo(w * 0.30f, h * 0.32f)
        lineTo(w * 0.50f, h * 0.18f)
        lineTo(w * 0.70f, h * 0.32f)
        lineTo(w * 0.66f, h * 0.44f)
        lineTo(w * 0.34f, h * 0.44f)
        close()
      }
      drawPath(helm, steel)
      drawPath(helm, steelDark, style = Stroke(2.dp.toPx()))
      // Visor slits
      drawLine(Color(0xFF0F172A), Offset(w * 0.40f, h * 0.36f), Offset(w * 0.60f, h * 0.36f), strokeWidth = 3.dp.toPx())
    }
    "spartan_helmet" -> {
      // Spartan Bronze War Helmet (30+ days)
      val bronze = Color(0xFFD97706)
      val crestRed = Color(0xFFDC2626)
      // Red Crest on top
      val crest = Path().apply {
        moveTo(w * 0.36f, h * 0.22f)
        quadraticTo(w * 0.5f, h * 0.08f, w * 0.64f, h * 0.22f)
        close()
      }
      drawPath(crest, crestRed)

      // Bronze brow
      val helm = Path().apply {
        moveTo(w * 0.30f, h * 0.32f)
        lineTo(w * 0.50f, h * 0.20f)
        lineTo(w * 0.70f, h * 0.32f)
        lineTo(w * 0.66f, h * 0.40f)
        lineTo(w * 0.50f, h * 0.44f)
        lineTo(w * 0.34f, h * 0.40f)
        close()
      }
      drawPath(helm, bronze)
    }
    "gold_crown" -> {
      // Sovereign Gold Crown (60+ days)
      val gold = Color(0xFFFBBF24)
      val ruby = Color(0xFFEF4444)
      val crown = Path().apply {
        moveTo(w * 0.32f, h * 0.28f)
        lineTo(w * 0.32f, h * 0.18f)
        lineTo(w * 0.41f, h * 0.23f)
        lineTo(w * 0.50f, h * 0.15f)
        lineTo(w * 0.59f, h * 0.23f)
        lineTo(w * 0.68f, h * 0.18f)
        lineTo(w * 0.68f, h * 0.28f)
        close()
      }
      drawPath(crown, gold)
      // Gems
      drawCircle(ruby, radius = w * 0.016f, center = Offset(w * 0.50f, h * 0.24f))
      drawCircle(ruby, radius = w * 0.012f, center = Offset(w * 0.38f, h * 0.25f))
      drawCircle(ruby, radius = w * 0.012f, center = Offset(w * 0.62f, h * 0.25f))
    }
    "mystic_halo" -> {
      // Mystic Halo Aura (75+ days)
      val haloGlow = Color(0xFFFDE047)
      drawOval(
        haloGlow,
        topLeft = Offset(w * 0.30f, h * 0.12f),
        size = Size(w * 0.40f, h * 0.08f),
        style = Stroke(width = 4.dp.toPx())
      )
    }
    "emperor_crown" -> {
      // Imperial Supreme Crown (365+ days)
      val gold = Color(0xFFF59E0B)
      val diamond = Color(0xFF67E8F9)
      drawArc(
        color = gold,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(w * 0.34f, h * 0.14f),
        size = Size(w * 0.32f, h * 0.22f)
      )
      // Emperor Cross
      drawLine(Color(0xFFFEF08A), Offset(w * 0.5f, h * 0.10f), Offset(w * 0.5f, h * 0.15f), strokeWidth = 3.dp.toPx())
      drawLine(Color(0xFFFEF08A), Offset(w * 0.47f, h * 0.12f), Offset(w * 0.53f, h * 0.12f), strokeWidth = 3.dp.toPx())
      drawCircle(diamond, radius = w * 0.018f, center = Offset(w * 0.5f, h * 0.22f))
    }
  }
}

private fun parseHexColor(hex: String, defaultColor: Color): Color {
  return try {
    Color(android.graphics.Color.parseColor(hex))
  } catch (_: Exception) {
    defaultColor
  }
}
