package surreal.californication.api.capability.impl;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import ic2.core.block.comp.Energy;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ICEnergyContainer implements IEnergyContainer, ICapabilityProvider {
    public Energy energy;

    public void setEnergy(Energy energy) {
        this.energy = energy;
    }

    @Override
    public long acceptEnergyFromNetwork(EnumFacing side, long voltage, long amperage) {
        long canAccept = getEnergyCapacity() - getEnergyStored();
        if (voltage > 0L && (side == null || inputsEnergy(side))) {
            if (voltage > getInputVoltage()) {
                //metaTileEntity.doExplosion(GTUtility.getExplosionPower(voltage));
                return Math.min(amperage, getInputAmperage());
            }
            if (canAccept >= voltage) {
                long amperesAccepted = Math.min(canAccept / voltage, Math.min(amperage, getInputAmperage()));
                if (amperesAccepted > 0) {
                    setEnergyStored(getEnergyStored() + voltage * amperesAccepted);
                    return amperesAccepted;
                }
            }
        }
        return 0;
    }

    public void setEnergyStored(long energyStored) {
        energy.forceAddEnergy(energy.getEnergy());
        energy.addEnergy(energyStored);
        if (!energy.getParent().getWorld().isRemote) {
            energy.getParent().markDirty();
        }
    }

    @Override
    public boolean inputsEnergy(EnumFacing enumFacing) {
        return energy != null && !outputsEnergy(enumFacing) && getInputVoltage() > 0 && energy.getSinkDirs().contains(enumFacing);
    }

    @Override
    public boolean outputsEnergy(EnumFacing side) {
        return energy != null && getOutputVoltage() > 0 && energy.getSourceDirs().contains(side);
    }

    @Override
    public long changeEnergy(long l) {
        if (energy != null) {
            long oldEnergy = getEnergyStored();
            long newEnergy = (getEnergyCapacity() - oldEnergy < l) ? getEnergyCapacity() : (oldEnergy + l);
            if (newEnergy < 0) newEnergy = 0;

            return newEnergy - oldEnergy;
        }

        return 0;
    }

    @Override
    public long getEnergyStored() {
        return energy != null ? (long) Math.ceil(energy.getEnergy()) : 0;
    }

    @Override
    public long getEnergyCapacity() {
        return energy != null ? (long) Math.ceil(energy.getCapacity()) : 0;
    }

    @Override
    public long getOutputAmperage() {
        return energy != null && energy.getSourceDirs().size() > 0 ? 1L : 0;
    }

    @Override
    public long getOutputVoltage() {
        return energy != null ? GTValues.V[energy.getSourceTier()] : 0;
    }

    @Override
    public long getInputAmperage() {
        return energy != null && energy.getSinkDirs().size() > 0 ? 1L : 0;
    }

    @Override
    public long getInputVoltage() {
        return energy != null ? GTValues.V[energy.getSinkTier()] : 0;
    }

    @Override
    public boolean isOneProbeHidden() {
        return false;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER ? (T) this : null;
    }
}
