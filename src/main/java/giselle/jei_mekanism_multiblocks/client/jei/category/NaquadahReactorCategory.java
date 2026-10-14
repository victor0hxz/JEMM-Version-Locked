package giselle.jei_mekanism_multiblocks.client.jei.category;

import java.util.function.Consumer;
import giselle.jei_mekanism_multiblocks.client.gui.IntSliderWithButtons;
import giselle.jei_mekanism_multiblocks.client.jei.MultiblockCategory;
import giselle.jei_mekanism_multiblocks.client.jei.MultiblockWidget;
import giselle.jei_mekanism_multiblocks.client.jei.ResultWidget;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/** Optional Extras integration using registry IDs so JMM also runs without Extras. */
public class NaquadahReactorCategory extends MultiblockCategory<NaquadahReactorCategory.NaquadahWidget> {
    public static ItemStack block(String name, int count) {
        return new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("mekanism_extras", name)), count);
    }

    public NaquadahReactorCategory(IGuiHelper helper) {
        super(helper, Identifier.fromNamespaceAndPath("mekanism_extras", "naquadah_reactor"), NaquadahWidget.class,
            Component.translatable("text.jei_mekanism_multiblocks.naquadah.title"), block("naquadah_reactor_controller", 1));
    }
    @Override public int getHeight() { return 215; }

    @Override
    protected void getRecipeCatalystItemStacks(Consumer<ItemStack> consumer) {
        super.getRecipeCatalystItemStacks(consumer);
        for (String name : new String[]{"naquadah_reactor_controller", "naquadah_reactor_casing", "naquadah_reactor_port",
                "naquadah_reactor_logic_adapter", "lead_coated_laser_focus_matrix"}) consumer.accept(block(name, 1));
    }

    public static class NaquadahWidget extends MultiblockWidget {
        private IntSliderWithButtons ports;
        private IntSliderWithButtons adapters;
        private IntSliderWithButtons layer;
        private IntSliderWithButtons injection;

        public NaquadahWidget() {
            addPreview();
            addChild(new giselle.jei_mekanism_multiblocks.client.gui.LabelWidget(90, 130, 90, 10, Component.literal("9 x 9 x 9")));
            addChild(new giselle.jei_mekanism_multiblocks.client.gui.LabelWidget(90, 145, 90, 10, Component.literal("C: Casing")));
            addChild(new giselle.jei_mekanism_multiblocks.client.gui.LabelWidget(90, 160, 90, 10, Component.literal("R: Controller")));
            addChild(new giselle.jei_mekanism_multiblocks.client.gui.LabelWidget(90, 175, 90, 10, Component.literal("P: Port  L: Laser")));
            addChild(new giselle.jei_mekanism_multiblocks.client.gui.LabelWidget(90, 195, 90, 10, Component.translatable("text.jei_mekanism_multiblocks.naquadah.example")));
        }

        @Override
        protected void collectOtherConfigs(Consumer<AbstractWidget> consumer) {
            consumer.accept(ports = new IntSliderWithButtons(0, 0, 0, 0, "text.jei_mekanism_multiblocks.specs.ports", 2, 2, 220));
            consumer.accept(adapters = new IntSliderWithButtons(0, 0, 0, 0, "text.jei_mekanism_multiblocks.specs.logic_adapters", 0, 0, 218));
            ports.getSlider().addValueChangeHanlder(value -> {
                adapters.getSlider().setMaxValue(220 - value);
                adapters.getSlider().setValue(Math.min(adapters.getSlider().getValue(), 220 - value));
                markNeedUpdate();
            });
            adapters.getSlider().addValueChangeHanlder(value -> markNeedUpdate());
            consumer.accept(injection = new IntSliderWithButtons(0, 0, 0, 0, "text.jei_mekanism_multiblocks.specs.injection_rate", new giselle.jei_mekanism_multiblocks.client.gui.Mod2IntSliderWidget(0, 0, 0, 0, Component.empty(), 2, 2, 98, 1)));
            injection.getSlider().addValueChangeHanlder(value -> markNeedUpdate());
            consumer.accept(layer = new IntSliderWithButtons(0, 0, 0, 0, "text.jei_mekanism_multiblocks.naquadah.layer", 1, 1, 9));
        }

        private void addPreview() {
            addChild(new AbstractWidget(0, 130, 81, 81, Component.empty()) {
                @Override public void extractWidgetRenderState(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
                    var font = net.minecraft.client.Minecraft.getInstance().font;
                    for (int x = 0; x < 9; x++) for (int z = 0; z < 9; z++) {
                        char cell = NaquadahLayout.block(x, layer.getSlider().getValue() - 1, z);
                        int color = switch (cell) { case 'R' -> 0xff40b840; case 'L' -> 0xffd08020; case 'P' -> 0xff4080d0; case 'C' -> 0xff777777; default -> 0xffdddddd; };
                        int px = getX() + x * 9, py = getY() + z * 9;
                        graphics.fill(px, py, px + 8, py + 8, color);
                        if (cell != '.') graphics.text(font, String.valueOf(cell), px + 2, py, 0xffffffff, false);
                    }
                }
                @Override protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {}
            });
        }

        @Override
        protected void collectCost(ICostConsumer consumer) {
            // Union of the six faces of the validator's 9x9 octagonal grid: 330 blocks.
            int portCount = ports.getSlider().getValue();
            int adapterCount = adapters.getSlider().getValue();
            consumer.accept(block("naquadah_reactor_casing", 330 - 2 - portCount - adapterCount));
            consumer.accept(block("naquadah_reactor_controller", 1));
            consumer.accept(block("lead_coated_laser_focus_matrix", 1));
            consumer.accept(block("naquadah_reactor_port", portCount));
            if (adapterCount > 0) consumer.accept(block("naquadah_reactor_logic_adapter", adapterCount));
        }

        @Override
        protected void collectResult(Consumer<AbstractWidget> consumer) {
            int rate = injection.getSlider().getValue();
            ResultWidget fuelGuide = new ResultWidget(Component.translatable("text.jei_mekanism_multiblocks.guide.fuel"), giselle.jei_mekanism_multiblocks.common.util.VolumeTextHelper.formatMBt(rate));
            fuelGuide.setJeiTooltip(Component.translatable("text.jei_mekanism_multiblocks.naquadah.guide.fuel", rate / 2));
            consumer.accept(fuelGuide);
            ResultWidget coolantGuide = new ResultWidget(Component.translatable("text.jei_mekanism_multiblocks.guide.coolant"), Component.translatable("text.jei_mekanism_multiblocks.naquadah.guide.output"));
            coolantGuide.setJeiTooltip(Component.translatable("text.jei_mekanism_multiblocks.naquadah.guide.coolant"));
            consumer.accept(coolantGuide);
            ResultWidget startGuide = new ResultWidget(Component.translatable("text.jei_mekanism_multiblocks.guide.start"), Component.translatable("text.jei_mekanism_multiblocks.guide.hohlraum_laser"));
            startGuide.setJeiTooltip(Component.translatable("text.jei_mekanism_multiblocks.naquadah.guide.start"));
            consumer.accept(startGuide);
            consumer.accept(new ResultWidget(Component.translatable("text.jei_mekanism_multiblocks.naquadah.size"), Component.literal("9 x 9 x 9")));
            consumer.accept(new ResultWidget(Component.translatable("text.jei_mekanism_multiblocks.naquadah.controller"), Component.translatable("text.jei_mekanism_multiblocks.naquadah.top_center")));
            consumer.accept(new ResultWidget(Component.translatable("text.jei_mekanism_multiblocks.naquadah.shape"), Component.translatable("text.jei_mekanism_multiblocks.naquadah.octagonal")));
            consumer.accept(new ResultWidget(Component.translatable("text.jei_mekanism_multiblocks.naquadah.interior"), Component.translatable("text.jei_mekanism_multiblocks.naquadah.empty")));
            consumer.accept(new ResultWidget(Component.translatable("text.jei_mekanism_multiblocks.naquadah.ports"), Component.translatable("text.jei_mekanism_multiblocks.naquadah.configure")));
        }

        @Override public int getDimensionWidthMin() { return 9; }
        @Override public int getDimensionWidthMax() { return 9; }
        @Override public int getDimensionLengthMin() { return 9; }
        @Override public int getDimensionLengthMax() { return 9; }
        @Override public int getDimensionHeightMin() { return 9; }
        @Override public int getDimensionHeightMax() { return 9; }
    }
}

