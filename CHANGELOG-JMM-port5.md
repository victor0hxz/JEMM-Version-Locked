# Just Enough Mekanism Multiblocks 6.5-port5 (ATM11 0.9.0 / Minecraft 26.1.2)

- Restores checkbox mouse handling after the 26.1.2 input API migration. Reactor glass and water cooling now toggle through the real widget click callback.
- Adds Space, Enter and keypad Enter support to focused checkboxes. Nested widget containers retain keyboard focus after releasing the mouse and clear it when a child is removed or loses focus.
- Expands the existing Fusion and Naquadah reactor JEI Results pages with fuel supply, input/output cooling, ignition and operating assumptions in English and Brazilian Portuguese. Naquadah includes an even injection-rate selector (2-98) and identifies Polonium Containing Steam correctly. No new mod is required.
- Caps Fusion passive generation and steam estimates to the same integer transfer range used by the actual reactor tick. Estimates assume sustained supply, sufficient cooling and free output capacity.
- Builds against Mekanism Version Locked 2.8 and Generators Version Locked 1.3. Industrial turbine formulas continue to read runtime throughput and generation settings; no balance values were changed.

Validation: clean Gradle build; headless real Minecraft AbstractWidget mouse/key regression and nested focus regression; tutorial coverage contract. An isolated real client and integrated server with JEI 29.37, Core 2.8, Generators 1.3 and Extras fix6 passed Fusion/Naquadah category registration, reactor glass/water click and Space toggles, translated tutorial rows, and four production turbine parity cases (small, large, saturated and zero coils) using runtime throughput 1 and generation 100. Full updated-modpack dedicated-server integration is tracked by the parent task.


