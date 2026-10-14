package giselle.jei_mekanism_multiblocks;
import giselle.jei_mekanism_multiblocks.common.util.TurbineGenerationMath;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
/** Invoke verify() only from a bootstrapped NeoForge test client/server. Does not alter loaded configuration. */
public final class RuntimeTurbineParity {
 public static final class Turbine extends TurbineMultiblockData {
  int dispersers;
  public Turbine(){super(null);}
  @Override public int getDispersers(){return dispersers;}
 }
 public static void verify() throws Exception {
  var field=sun.misc.Unsafe.class.getDeclaredField("theUnsafe");field.setAccessible(true);
  var unsafe=(sun.misc.Unsafe)field.get(null);
  var actual=(Turbine)unsafe.allocateInstance(Turbine.class);
  int count=0;
  for(int[] geometry:new int[][]{{27,8,4,1,4},{1989,224,28,7,500},{Integer.MAX_VALUE,224,28,7,Integer.MAX_VALUE},{100,8,28,0,10}}){
   actual.lowerVolume=geometry[0];actual.dispersers=geometry[1];actual.blades=geometry[2];actual.coils=geometry[3];actual.vents=geometry[4];
   int jmm=TurbineGenerationMath.maxProduction(geometry[0],geometry[1],geometry[2],geometry[3],geometry[4]);
   if(actual.getMaxProduction()!=jmm)throw new AssertionError("Turbine production differs: server="+actual.getMaxProduction()+", JMM="+jmm);
   if(actual.getSteamCapacity()!=TurbineGenerationMath.steamCapacity(geometry[0]))throw new AssertionError("Turbine steam capacity differs");
   if(actual.getMaxFlowRate()!=(int)TurbineGenerationMath.maxFlow(geometry[0],geometry[1],geometry[4]))throw new AssertionError("Turbine steam flow differs");
   count++;
  }
  System.out.println("JMM_RUNTIME_TURBINE_PARITY_PASS cases="+count+" throughput="+TurbineGenerationMath.throughputMultiplier()+" generation="+TurbineGenerationMath.generationMultiplier());
 }
}
