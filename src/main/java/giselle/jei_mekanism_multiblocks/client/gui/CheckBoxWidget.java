package giselle.jei_mekanism_multiblocks.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;

import giselle.jei_mekanism_multiblocks.client.GuiHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.KeyEvent;

public class CheckBoxWidget extends AbstractWidget
{
	private final List<Consumer<Boolean>> selectedChangedHandlers;
	private boolean selected;
	private boolean shadow;

	public CheckBoxWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, boolean pSelected)
	{
		super(pX, pY, pWidth, pHeight, pMessage);
		this.selectedChangedHandlers = new ArrayList<>();
		this.selected = pSelected;
		this.setFGColor(0x404040);
		this.shadow = false;
	}

	public void addSelectedChangedHandler(Consumer<Boolean> handler)
	{
		this.selectedChangedHandlers.add(handler);
	}

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        this.onPress();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.active && this.visible && this.isFocused()
                && (event.key() == 32 || event.key() == 257 || event.key() == 335)) {
            this.onPress();
            return true;
        }
        return false;
    }

	public void onPress()
	{
		this.setSelected(!this.isSelected());
	}

	public boolean isSelected()
	{
		return this.selected;
	}

	public void setSelected(boolean selected)
	{
		if (this.isSelected() != selected)
		{
			this.selected = selected;

			for (Consumer<Boolean> handler : this.selectedChangedHandlers)
			{
				handler.accept(selected);
			}

		}

	}

	@Override
	public void extractWidgetRenderState(GuiGraphicsExtractor pGuiGraphicsExtractor, int pMouseX, int pMouseY, float pPartialTicks)
	{



		int checkerLength = this.height;
		GuiHelper.blit(pGuiGraphicsExtractor, GuiHelper.WIDGETS_LOCATION, this.getX(), this.getY(), checkerLength, checkerLength, 0.0F + (this.isHoveredOrFocused() ? 10.0F : 0.0F), 16.0F + (this.selected ? 10.0F : 0.0F), 10, 10, 256, 256);

		int j = getFGColor();
		GuiHelper.drawScaledText(pGuiGraphicsExtractor, this.getMessage(), this.getX() + checkerLength + 1, this.getY(), this.width - checkerLength - 1, j | Mth.ceil(this.alpha * 255.0F) << 24, this.isShadow());
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
	{
		this.defaultButtonNarrationText(pNarrationElementOutput);

	}

	public boolean isShadow()
	{
		return this.shadow;
	}

	public void setShadow(boolean shadow)
	{
		this.shadow = shadow;
	}

}


