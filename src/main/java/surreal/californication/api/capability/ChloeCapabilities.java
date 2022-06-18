package surreal.californication.api.capability;

import gregtech.api.capability.GregtechCapabilities;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.IElectricItem;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Energy;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import surreal.californication.ChloeValues;
import surreal.californication.api.capability.impl.ICElectricItem;
import surreal.californication.api.capability.impl.ICEnergyContainer;

public class ChloeCapabilities {
    private static final ResourceLocation CAPABILITY_IC_ELECTRIC_ITEM = new ResourceLocation(ChloeValues.MODID, "ic_electric_item_capability");
    private static final ResourceLocation CAPABILITY_IC_ENERGY_CONTAINER = new ResourceLocation(ChloeValues.MODID, "ic_energy_container");

    @SubscribeEvent
    public void attachItemCapability(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();

        if (stack.getItem() instanceof IElectricItem) {
            IElectricItem elItem = (IElectricItem) stack.getItem();
            event.addCapability(CAPABILITY_IC_ELECTRIC_ITEM, new ICElectricItem(stack, elItem));
        }
    }

    @SubscribeEvent
    public void  attachTileCapability(AttachCapabilitiesEvent<TileEntity> event) {
        TileEntity tile = event.getObject();

        if (tile instanceof TileEntityBlock) {
            event.addCapability(CAPABILITY_IC_ENERGY_CONTAINER, new ICEnergyContainer());
        }
    }

    @SubscribeEvent
    public void energyLoad(EnergyTileLoadEvent event) {
        IEnergyTile energyTile = event.tile;
        World world = event.getWorld();
        BlockPos pos = EnergyNet.instance.getPos(energyTile);

        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof TileEntityBlock) {
            TileEntityBlock tile = (TileEntityBlock) tileEntity;
            if (tile.hasComponent(Energy.class) && tile.hasCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, null)) {
                ICEnergyContainer container = (ICEnergyContainer) tile.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, null);
                if (container != null) container.setEnergy(tile.getComponent(Energy.class));
            }
        }
    }
}
