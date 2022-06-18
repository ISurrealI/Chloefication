package surreal.californication;

import gregtech.api.GTValues;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import surreal.californication.api.capability.ChloeCapabilities;
import surreal.californication.common.CommonProxy;

import static surreal.californication.ChloeValues.*;

@Mod(modid = MODID, name = NAME, version = VERSION, dependencies = "required-after:forge@[14.23.5.2847,);" + GTValues.MOD_VERSION_DEP)
public class Californication {
    @SidedProxy(serverSide = "surreal.californication.common.CommonProxy", clientSide = "surreal.californication.client.ClientProxy")
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PROXY.preInit(event);
    }

    public void init(FMLInitializationEvent event) {
        PROXY.init(event);
        if (Loader.isModLoaded(IC2)) MinecraftForge.EVENT_BUS.register(new ChloeCapabilities());
    }

    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit(event);
    }
}