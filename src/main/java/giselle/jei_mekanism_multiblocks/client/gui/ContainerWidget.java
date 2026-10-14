package giselle.jei_mekanism_multiblocks.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public class ContainerWidget extends AbstractWidget
{
	private final List<AbstractWidget> children;
	private final List<AbstractWidget> unmodifiableChildren;
	private final List<AbstractWidget> functionWidgets;
	private final List<AbstractWidget> unmodifiableFunctionWidgets;

	private AbstractWidget focused;
    private AbstractWidget keyboardFocused;

	public ContainerWidget(int pX, int pY, int pWidth, int pHeight)
	{
		super(pX, pY, pWidth, pHeight, Component.empty());

		this.children = new ArrayList<>();
		this.unmodifiableChildren = Collections.unmodifiableList(this.children);
		this.functionWidgets = new ArrayList<>();
		this.unmodifiableFunctionWidgets = Collections.unmodifiableList(this.functionWidgets);
	}

	public AbstractWidget getChildUnderMouse(double pMouseX, double pMouseY)
	{
		for (AbstractWidget widget : this.getChildren())
		{
			double childMouseX = this.toChildX(pMouseX);
			double childMouseY = this.toChildY(pMouseY);

			if (widget.isMouseOver(childMouseX, childMouseY))
			{
				return widget;
			}

		}

		return null;
	}

	public List<AbstractWidget> getChildren()
	{
		return this.unmodifiableChildren;
	}

	public <WIDGET extends AbstractWidget> WIDGET addChild(WIDGET widget)
	{
		this.children.add(widget);
		this.onChildAdded(widget);
		return widget;
	}

	public boolean removeChild(AbstractWidget widget)
	{
		if (this.children.remove(widget))
		{
			this.onChildRemoved(widget);
			return true;
		}
		else
		{
			return false;
		}

	}

	protected void onChildAdded(AbstractWidget widget)
	{

	}

	protected void onChildRemoved(AbstractWidget widget)
	{
        if (this.keyboardFocused == widget) { this.keyboardFocused.setFocused(false); this.keyboardFocused = null; }
		if (this.getFocused() == widget)
		{
			this.focused = null;
		}

	}

	public void clearChildren()
	{
		new ArrayList<>(this.getChildren()).forEach(this::removeChild);
	}

	public List<AbstractWidget> getFunctionWidgets()
	{
		return this.unmodifiableFunctionWidgets;
	}

	public <WIDGET extends AbstractWidget> WIDGET addFunctionWidget(WIDGET widget)
	{
		this.functionWidgets.add(widget);
		this.onFunctionWidgetAdded(widget);
		return widget;
	}

	public boolean removeFunctionWidget(AbstractWidget widget)
	{
		if (this.functionWidgets.remove(widget))
		{
			this.onFunctionWidgetRemoved(widget);
			return true;
		}
		else
		{
			return false;
		}

	}

	protected void onFunctionWidgetAdded(AbstractWidget widget)
	{

	}

	protected void onFunctionWidgetRemoved(AbstractWidget widget)
	{
        if (this.keyboardFocused == widget) { this.keyboardFocused.setFocused(false); this.keyboardFocused = null; }
		if (this.getFocused() == widget)
		{
			this.focused = null;
		}

	}

	public void clearFunctionWidgets()
	{
		new ArrayList<>(this.getFunctionWidgets()).forEach(this::removeFunctionWidget);
	}

	public List<List<AbstractWidget>> getFunctionableWidgets()
	{
		return Arrays.asList(this.getChildren(), this.getFunctionWidgets());
	}

	public AbstractWidget getFocused()
	{
		return this.focused;
	}

	public Rect2i getBounds()
	{
		return new Rect2i(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}

	@Override
	public void setWidth(int value)
	{
		int prev = this.getWidth();
		super.setWidth(value);
		int next = this.getWidth();

		if (prev != next)
		{
			this.onWidthChanged();
		}

	}

	protected void onWidthChanged()
	{
		this.onSizeChanged();
	}

	@Override
	public void setHeight(int value)
	{
		int prev = this.getHeight();
		super.setHeight(value);
		int next = this.getHeight();

		if (prev != next)
		{
			this.onHeightChanged();
		}

	}

	protected void onHeightChanged()
	{
		this.onSizeChanged();

	}

	protected void onSizeChanged()
	{

	}

	protected void transformClient(Matrix3x2fStack pose)
	{
		pose.translate(this.getX(), this.getY());
	}

	protected double toChildX(double x)
	{
		return x - this.getX();
	}

	protected double toChildY(double y)
	{
		return y - this.getY();
	}

	@Override
	public void extractWidgetRenderState(GuiGraphicsExtractor pGuiGraphicsExtractor, int pMouseX, int pMouseY, float pPartialTicks)
	{
		Matrix3x2fStack pose = pGuiGraphicsExtractor.pose();
		pose.pushMatrix();
		this.transformClient(pose);
		int childMouseX = (int) this.toChildX(pMouseX);
		int childMouseY = (int) this.toChildY(pMouseY);

		for (List<AbstractWidget> widgets : this.getFunctionableWidgets())
		{
			for (AbstractWidget widget : widgets)
			{
				this.onRenderWidget(widgets, widget, pGuiGraphicsExtractor, childMouseX, childMouseY, pPartialTicks);
			}

		}

		pose.popMatrix();
	}

	protected void onRenderWidget(List<AbstractWidget> widgets, AbstractWidget widget, GuiGraphicsExtractor pGuiGraphicsExtractor, int childMouseX, int childMouseY, float pPartialTicks)
	{
		widget.extractRenderState(pGuiGraphicsExtractor, childMouseX, childMouseY, pPartialTicks);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
	{
		double pMouseX=event.x(), pMouseY=event.y(); int pButton=event.button();
		if (this.active && this.visible)
		{
			double childMouseX = this.toChildX(pMouseX);
			double childMouseY = this.toChildY(pMouseY);

			for (List<AbstractWidget> widgets : this.getFunctionableWidgets())
			{
				for (AbstractWidget widget : widgets)
				{
					if (widget.mouseClicked(new MouseButtonEvent(childMouseX, childMouseY, event.buttonInfo()), doubleClick))
					{
                        if (this.keyboardFocused != null && this.keyboardFocused != widget) this.keyboardFocused.setFocused(false);
                        this.keyboardFocused = widget;
                        widget.setFocused(true);
						this.focused = widget;
						return true;
					}

				}

			}

		}

		return super.mouseClicked(event, doubleClick);
	}

    @Override
    public boolean keyPressed(KeyEvent event) {
        return this.active && this.visible && this.keyboardFocused != null
                && this.keyboardFocused.keyPressed(event);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused && this.keyboardFocused != null) {
            this.keyboardFocused.setFocused(false);
            this.keyboardFocused = null;
        }
    }
	@Override
	public boolean mouseReleased(MouseButtonEvent event)
	{
		double pMouseX=event.x(), pMouseY=event.y(); int pButton=event.button();
		AbstractWidget focused = this.getFocused();
		this.focused = null;

		if (focused != null && this.active && this.visible)
		{
			double childMouseX = this.toChildX(pMouseX);
			double childMouseY = this.toChildY(pMouseY);
			return focused.mouseReleased(new MouseButtonEvent(childMouseX, childMouseY, event.buttonInfo()));
		}

		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double pDragX, double pDragY)
	{
		double pMouseX=event.x(), pMouseY=event.y(); int pButton=event.button();
		AbstractWidget focused = this.getFocused();

		if (focused != null && this.active && this.visible)
		{
			double childMouseX = this.toChildX(pMouseX);
			double childMouseY = this.toChildY(pMouseY);
			return focused.mouseDragged(new MouseButtonEvent(childMouseX, childMouseY, event.buttonInfo()), pDragX, pDragY);
		}

		return super.mouseDragged(event,pDragX,pDragY);
	}

	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY)
	{
		if (this.active && this.visible)
		{
			double childMouseX = this.toChildX(pMouseX);
			double childMouseY = this.toChildY(pMouseY);

			for (List<AbstractWidget> widgets : this.getFunctionableWidgets())
			{
				for (AbstractWidget widget : widgets)
				{
					if (widget.mouseScrolled(childMouseX, childMouseY, pScrollX, pScrollY))
					{
						return true;
					}

				}

			}

		}

		return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
	}

	@Override
	public void playDownSound(SoundManager pHandler)
	{

	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
	{

	}

}

