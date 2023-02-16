package com.jumpcutfindo.microchip.helper;

public class StatUtils {
    /**
     * Calculates the maximum speed of a mob in meters per second based on the parameters provided.
     * @param baseSpeed The base movement speed of the mob.
     * @param speedEffectLevel The level of the Speed effect applied to the mob.
     * @return The maximum possible movement speed of the mob.
     */
    public static float calculateMaxSpeed(float baseSpeed, int speedEffectLevel) {
        return (baseSpeed * 4.317f) * (speedEffectLevel * 0.2f);
    }

    /**
     * Calculates the maximum jump height in meters of a mob based on the parameters provided.
     * @param jumpEffectLevel The level of the Jump Boost effect applied to the mob.
     * @return The maximum possible jump height of the mob.
     */
    public static float calculateMaxJumpHeight(float jumpEffectLevel) {
        return jumpEffectLevel == 0 ? 1.2522f : 0.0308354f * (float) Math.pow(jumpEffectLevel, 2) + 0.744631f * jumpEffectLevel + 1.836131f;
    }

    /**
     * Calculates the maximum jump height in meters of a mob based on the jump strength.
     * @param jumpStrength The jump strength of the mob (if applicable, only horses have this value).
     * @return The maximum possible jump height of the mob.
     */
    public static float calculateMaxJumpHeightWithJumpStrength(float jumpStrength) {
        return (float) Math.pow(jumpStrength, 1.7) * 5.293f;
    }
}
