package giselle.jei_mekanism_multiblocks.common.util;

import mekanism.api.math.MathUtils;
import mekanism.generators.common.config.MekanismGeneratorsConfig;

import mekanism.generators.common.content.turbine.TurbineValidator;

/** Uses the same configured units, throughput and buffer rules as Generators Version Locked. */
public final class TurbineGenerationMath {
    private TurbineGenerationMath() {}
    private static final java.lang.invoke.MethodHandle BALANCE_MULTIPLIER = findMultiplier("multiplier");
    private static final java.lang.invoke.MethodHandle GENERATION_MULTIPLIER = findMultiplier("generationMultiplier");

    private static java.lang.invoke.MethodHandle findMultiplier(String name) {
        try {
            return java.lang.invoke.MethodHandles.publicLookup().findStatic(
                    Class.forName("mekanism.generators.common.config.VersionLockedGeneratorsBalance"), name,
                    java.lang.invoke.MethodType.methodType(int.class));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException absent) {
            return null;
        }
    }

    public static int throughputMultiplier() {
        if (BALANCE_MULTIPLIER == null) return 1;
        try {
            return (int) BALANCE_MULTIPLIER.invokeExact();
        } catch (Throwable ex) {
            throw new IllegalStateException("Unable to read turbine throughput configuration", ex);
        }
    }

    public static int generationMultiplier() {
        if (GENERATION_MULTIPLIER == null) return 1;
        try {
            return (int) GENERATION_MULTIPLIER.invokeExact();
        } catch (Throwable ex) {
            throw new IllegalStateException("Unable to read turbine generation configuration", ex);
        }
    }

    public static double unclampedFlow(int lowerVolume, int dispersers) {
        return (double) lowerVolume * dispersers * MekanismGeneratorsConfig.generators.turbineDisperserChemicalFlow.getOrDefault()
                * throughputMultiplier();
    }

    public static double maxFlow(int lowerVolume, int dispersers, int vents) {
        return Math.min(Integer.MAX_VALUE, Math.min(unclampedFlow(lowerVolume, dispersers),
                vents * MekanismGeneratorsConfig.generators.turbineVentChemicalFlow.getOrDefault() * throughputMultiplier()));
    }

    public static int maxProduction(int lowerVolume, int dispersers, int blades, int coils, int vents) {
        double energyMultiplier = (double) MekanismGeneratorsConfig.generators.turbineJoulesPerSteam.getOrDefault() / TurbineValidator.MAX_BLADES
                * Math.min(blades, coils * MekanismGeneratorsConfig.generators.turbineBladesPerCoil.getOrDefault()) * generationMultiplier();
        return MathUtils.clampToInt(energyMultiplier * maxFlow(lowerVolume, dispersers, vents)
                / MekanismGeneratorsConfig.generators.turbineSteamDivisor.getOrDefault());
    }

    public static long steamCapacity(int lowerVolume) {
        return MathUtils.multiplyClamped(MathUtils.multiplyClamped((long) lowerVolume,
                MekanismGeneratorsConfig.generators.turbineChemicalPerTank.getOrDefault()), throughputMultiplier());
    }

    public static long energyCapacity(int volume, long maximumProduction) {
        return Math.max(MathUtils.multiplyClamped((long) volume, MekanismGeneratorsConfig.generators.turbineEnergyCapacityPerVolume.getOrDefault()),
                MathUtils.multiplyClamped(maximumProduction, 4));
    }
}
