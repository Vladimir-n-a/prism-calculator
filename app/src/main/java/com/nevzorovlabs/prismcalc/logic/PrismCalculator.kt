package com.nevzorovlabs.prismcalc.logic

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

data class EyeInput(
    val up: Double = 0.0,
    val down: Double = 0.0,
    val outVal: Double = 0.0,
    val inVal: Double = 0.0
)

data class EyeResult(
    val power: Double,
    val angleTabo: Double,
    val baseLabel: String,
    val h: Double,
    val v: Double
)

data class EqualDistribution(
    val rightPower: Double,
    val rightAngle: Double,
    val leftPower: Double,
    val leftAngle: Double
)

data class PrismResult(
    val right: EyeResult,
    val left: EyeResult,
    val distribution: EqualDistribution,
    val anyInput: Boolean
)

object PrismCalculator {

    private const val EPSILON = 0.01

    fun calculateEye(eye: EyeInput, isRightEye: Boolean): EyeResult {
        val h = eye.outVal - eye.inVal   // + = Out (toward temple, BO)
        val v = eye.up - eye.down        // + = Up (BU)
        val p = sqrt(h * h + v * v)

        // TABO frame (see protractor):
        //   Right eye (OD): Out -> 180°, In -> 0°/360°
        //   Left eye  (OS): Out -> 0°/360°, In -> 180°
        //   Both eyes:      Up  -> 90°,   Down -> 270°
        val x = if (isRightEye) -h else h
        val angleRad = atan2(v, x)
        val angleTabo = ((angleRad * 180.0 / PI) + 360.0) % 360.0

        return EyeResult(
            power = p,
            angleTabo = angleTabo,
            baseLabel = buildBaseLabel(h, v, p),
            h = h,
            v = v
        )
    }

    private fun buildBaseLabel(h: Double, v: Double, p: Double): String {
        if (p < EPSILON) return "No prism"

        val parts = mutableListOf<String>()
        when {
            h > EPSILON -> parts += "Out (BO)"
            h < -EPSILON -> parts += "In (BI)"
        }
        when {
            v > EPSILON -> parts += "Up (BU)"
            v < -EPSILON -> parts += "Down (BD)"
        }
        return parts.joinToString(" + ")
    }

    fun calculate(right: EyeInput, left: EyeInput): PrismResult {
        val rightResult = calculateEye(right, isRightEye = true)
        val leftResult = calculateEye(left, isRightEye = false)

        // Per-eye vectors expressed in the common TABO frame.
        val rx = -rightResult.h
        val ry = rightResult.v
        val lx = leftResult.h
        val ly = leftResult.v

        // Relative prism between the eyes (R - L); each eye carries half of it,
        // in opposite TABO directions. This matches the reference calculator.
        val dx = rx - lx
        val dy = ry - ly
        val total = sqrt(dx * dx + dy * dy)
        val half = total / 2.0

        val rightAngle = ((atan2(dy, dx) * 180.0 / PI) + 360.0) % 360.0
        val leftAngle = (rightAngle + 180.0) % 360.0

        val distribution = EqualDistribution(
            rightPower = half,
            rightAngle = rightAngle,
            leftPower = half,
            leftAngle = leftAngle
        )

        val anyInput = rightResult.power >= EPSILON || leftResult.power >= EPSILON

        return PrismResult(
            right = rightResult,
            left = leftResult,
            distribution = distribution,
            anyInput = anyInput
        )
    }
}
