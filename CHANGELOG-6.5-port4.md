# Just Enough Mekanism Multiblocks Version Locked — 6.5-port4

- Calculate turbine power from Generators' configured joules-per-steam and steam divisor, rather than the unrelated core boiler maximum-energy-per-steam value.
- Include the optional Version Locked generation and throughput multipliers. Default Generators 1.2 predictions now match the real maximum turbine: 55.2 MFE/t at 11,040,000 mB/t, with 258,944,000 mB steam storage and a 220.8 MFE energy buffer.
- Use blade count for required coil calculation; keep steam capacity, maximum flow and energy-buffer predictions consistent with Generators.
- Detect the extra balance configuration optionally, avoiding a missing-class error with older Generators builds.
- Retain the Mekanism Extras reactor guide and existing multiblock calculators. Include the original MIT license in the JAR.

Validation: client tests with Generators 1.2 confirm production, flow, steam capacity and energy capacity match the tested server turbine. Existing connected glass and SPS geometry checks pass.

Client-side JEI addon for Minecraft 26.1.2 / NeoForge. Use Generators Version Locked 1.2 for the new balance profile and Mekanism core 2.7 or newer. Replace the old JMM JAR on every client. Original Giselle credits and MIT license preserved.
