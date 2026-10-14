package giselle.jei_mekanism_multiblocks;
import giselle.jei_mekanism_multiblocks.client.gui.CheckBoxWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
public final class WidgetRegression {
 public static void main(String[] args) throws Exception {
  // Only the audio service needs a client singleton for AbstractWidget.mouseClicked.
  // All hit-testing and dispatch use the real Minecraft implementation.
  var unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe"); unsafeField.setAccessible(true);
  var unsafe = (sun.misc.Unsafe) unsafeField.get(null);
  var instance = net.minecraft.client.Minecraft.class.getDeclaredField("instance"); instance.setAccessible(true);
  instance.set(null, unsafe.allocateInstance(net.minecraft.client.Minecraft.class));
  var widget = new CheckBoxWidget(0,0,80,10,Component.empty(),false) { @Override public void playDownSound(net.minecraft.client.sounds.SoundManager manager) {} };
  int[] changes={0}; widget.addSelectedChangedHandler(value -> changes[0]++);
  check(widget.mouseClicked(new MouseButtonEvent(5,5,new MouseButtonInfo(0,0)),false),"inside mouse click handled");
  check(widget.isSelected() && changes[0]==1,"mouse click must toggle and notify once");
  widget.setFocused(true);
  check(widget.keyPressed(new KeyEvent(32,0,0)) && !widget.isSelected(),"space toggles");
  check(widget.keyPressed(new KeyEvent(257,0,0)) && widget.isSelected(),"enter toggles");
  widget.active=false; check(!widget.keyPressed(new KeyEvent(32,0,0)) && widget.isSelected(),"disabled keyboard ignored");
  widget.active=true; widget.setFocused(false);
  check(!widget.keyPressed(new KeyEvent(32,0,0)) && widget.isSelected(),"unfocused keyboard ignored");
    check(!widget.mouseClicked(new MouseButtonEvent(90,5,new MouseButtonInfo(0,0)),false),"outside click ignored");
  check(!widget.mouseClicked(new MouseButtonEvent(5,5,new MouseButtonInfo(1,0)),false),"right click ignored");
  var container = new giselle.jei_mekanism_multiblocks.client.gui.ContainerWidget(0,0,100,100);
  container.addChild(widget);
  check(container.mouseClicked(new MouseButtonEvent(5,5,new MouseButtonInfo(0,0)),false),"container click routed");
  container.mouseReleased(new MouseButtonEvent(5,5,new MouseButtonInfo(0,0)));
  boolean before = widget.isSelected();
  check(container.keyPressed(new KeyEvent(32,0,0)) && widget.isSelected()!=before,"keyboard focus persists after release");
    check(container.keyPressed(new KeyEvent(335,0,0)),"keypad Enter toggles");
  widget.visible=false; before=widget.isSelected();
  check(!container.keyPressed(new KeyEvent(32,0,0)) && widget.isSelected()==before,"hidden keyboard ignored");
  check(!widget.mouseClicked(new MouseButtonEvent(5,5,new MouseButtonInfo(0,0)),false),"hidden mouse ignored");
  widget.visible=true; widget.active=false;
  check(!widget.mouseClicked(new MouseButtonEvent(5,5,new MouseButtonInfo(0,0)),false),"disabled mouse ignored");
  widget.active=true; container.removeChild(widget);
  check(!container.keyPressed(new KeyEvent(32,0,0)),"removed child loses keyboard focus");
  System.out.println("Widget regression passed");
 }
 private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
}


