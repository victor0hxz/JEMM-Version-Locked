package giselle.jei_mekanism_multiblocks;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.input.KeyEvent;
import giselle.jei_mekanism_multiblocks.client.jei.JeiPlugin;
import giselle.jei_mekanism_multiblocks.client.jei.MultiblockWidget;
import giselle.jei_mekanism_multiblocks.client.jei.category.FusionReactorCategory;
import giselle.jei_mekanism_multiblocks.client.jei.category.FissionReactorCategory;
import giselle.jei_mekanism_multiblocks.client.jei.category.NaquadahReactorCategory;
import giselle.jei_mekanism_multiblocks.client.gui.CheckBoxWidget;
@EventBusSubscriber(modid="jei_mekanism_multiblocks",value=Dist.CLIENT)
public final class ClientReactorSmoke {
 static boolean done;
 static void check(boolean x,String message){if(!x)throw new AssertionError(message);System.out.println("JMM_CLIENT_PASS "+message);}
 static void checkbox(MultiblockWidget owner, String fieldName) throws Exception {
  var field=owner.getClass().getDeclaredField(fieldName);field.setAccessible(true);
  var checkbox=(CheckBoxWidget)field.get(owner);
  checkbox.setX(0);checkbox.setY(0);checkbox.setWidth(80);checkbox.setHeight(10);
  boolean before=checkbox.isSelected();
  check(checkbox.mouseClicked(new MouseButtonEvent(5,5,new MouseButtonInfo(0,0)),false),"reactor checkbox accepts real click: "+fieldName);
  check(checkbox.isSelected()!=before,"reactor checkbox toggles: "+fieldName);
  checkbox.setFocused(true);check(checkbox.keyPressed(new KeyEvent(32,0,0))&&checkbox.isSelected()==before,"reactor checkbox Space toggles: "+fieldName);
 }
 @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
  if(!Boolean.getBoolean("jmm.clientSmoke")||done)return;
  var mc=Minecraft.getInstance();var plugin=JeiPlugin.instance();
  if(mc.level==null||plugin==null||plugin.getRuntime()==null)return;
  done=true;
  try{
   check(plugin.getCategories().stream().anyMatch(c->c instanceof FusionReactorCategory),"JEI registered Fusion category");
   check(plugin.getCategories().stream().anyMatch(c->c instanceof NaquadahReactorCategory),"JEI registered optional Extras Naquadah category");
   var fusion=new FusionReactorCategory.FusionReactorCategoryWidget();fusion.initialize();checkbox(fusion,"useReactorGlassCheckBox");checkbox(fusion,"waterCooledCheckBox");
   var fission=new FissionReactorCategory.FissionReactorCategoryWidget();fission.initialize();checkbox(fission,"useReactorGlassCheckBox");
   var naquadah=new NaquadahReactorCategory.NaquadahWidget();naquadah.initialize();
   for(var owner:new MultiblockWidget[]{fusion,naquadah}){
    var field=MultiblockWidget.class.getDeclaredField("resultsList");field.setAccessible(true);
    var results=(giselle.jei_mekanism_multiblocks.client.gui.ContainerWidget)field.get(owner);
        for(String guide:new String[]{"fuel","coolant","start"}) {
     String label=net.minecraft.network.chat.Component.translatable("text.jei_mekanism_multiblocks.guide."+guide).getString();
     check(results.getChildren().stream().filter(w->w instanceof giselle.jei_mekanism_multiblocks.client.jei.ResultWidget).map(w->(giselle.jei_mekanism_multiblocks.client.jei.ResultWidget)w).anyMatch(w->w.getTextLabel().getMessage().getString().equals(label)),"translated tutorial row present: "+owner.getClass().getSimpleName()+" "+guide);
    }
   }
   RuntimeTurbineParity.verify();
   System.out.println("JMM_CLIENT_FUNCTIONAL_COMPLETE");
  }catch(Throwable ex){System.out.println("JMM_CLIENT_FUNCTIONAL_FAILED");ex.printStackTrace();}finally{mc.stop();}
 }
}

