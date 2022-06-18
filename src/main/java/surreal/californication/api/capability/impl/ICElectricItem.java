package surreal.californication.api.capability.impl;

import gregtech.api.capability.GregtechCapabilities;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class ICElectricItem implements gregtech.api.capability.IElectricItem, ICapabilityProvider {
    private final ItemStack stack;
    private final IElectricItem item;

    public ICElectricItem(ItemStack stack, IElectricItem item) {
        this.stack = stack;
        this.item = item;
    }


    @Override
    public boolean canProvideChargeExternally() {
        return item.canProvideEnergy(stack);
    }

    @Override
    public boolean chargeable() {
        return true;
    }

    @Override
    public void addChargeListener(BiConsumer<ItemStack, Long> biConsumer) { }

    @Override
    public long charge(long l, int i, boolean b, boolean b1) {
        return (long) Math.ceil(ElectricItem.manager.charge(stack, l, i, b, b1));
    }

    @Override
    public long discharge(long l, int i, boolean b, boolean b1, boolean b2) {
        return (long) Math.ceil(ElectricItem.manager.discharge(stack, l, i, b, b1, b2));
    }

    @Override
    public long getTransferLimit() {
        return (long) Math.floor(item.getTransferLimit(stack));
    }

    @Override
    public long getMaxCharge() {
        return (long) Math.floor(item.getMaxCharge(stack));
    }

    @Override
    public long getCharge() {
        return (long) Math.floor(ElectricItem.manager.getCharge(stack));
    }

    @Override
    public int getTier() {
        return item.getTier(stack);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM ? (T) this : null;
    }
}
