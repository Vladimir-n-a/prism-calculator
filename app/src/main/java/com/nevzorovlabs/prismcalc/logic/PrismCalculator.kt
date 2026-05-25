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
        val h = eye.outVal - eye.inVal
        val v = eye.up - eye.down
        val p = sqrt(h * h + v * v)

        val angleRad = atan2(v, h)
        val angleTabo = ((angleRad * 180.0 / PI) + 360.0) % 360.0

        val baseLabel = buildBaseLabel(h, v, p)

        return EyeResult(
            power = p,
            angleTabo = angleTabo,
            baseLabel = baseLabel,
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

        val rightX = rightResult.h
        val rightY = rightResult.v
        val leftX = -leftResult.h
        val leftY = leftResult.v

        val totalX = rightX + leftX
        val totalY = rightY + leftY
        val totalP = sqrt(totalX * totalX + totalY * totalY)

        val halfP = totalP / 2.0
        val halfAngleAbs = atan2(totalY, totalX) * 180.0 / PI

        val rightHalfAngle = ((halfAngleAbs) + 360.0) % 360.0
        val leftHalfAngle = ((180.0 - halfAngleAbs) + 360.0) % 360.0

        val distribution = EqualDistribution(
            rightPower = halfP,
            rightAngle = rightHalfAngle,
            leftPower = halfP,
            leftAngle = leftHalfAngle
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
