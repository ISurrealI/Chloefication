package surreal.californication.core;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
public class ChloeHooks {
    public static boolean hasCapabilityItem(ItemStack stack) {
        return stack.hasCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
    }

    public static double tryChargeItem(ItemStack stack, double charge, int tier) {
        if (hasCapabilityItem(stack)) {
            IElectricItem item = stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
            return item.charge((long) charge, tier, false, false);
        } else if (ElectricItem.manager.charge(stack, charge, tier, true, true) > 0D) return ElectricItem.manager.charge(stack, charge, tier, false, false);

        return 0D;
    }

    public static double tryDischargeItem(ItemStack stack, double charge, int tier, boolean ignoreLimit) {
        if (hasCapabilityItem(stack)) {
            IElectricItem item = stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
            return item.discharge((long) charge, tier, ignoreLimit, false, false);
        } else {
            return ElectricItem.manager.discharge(stack, charge, tier, ignoreLimit, true, false);
        }
    }

    public static boolean accepts(ItemStack stack, int tier) {
        return Info.itemInfo.getEnergyValue(stack) > 0.0D || ElectricItem.manager.discharge(stack, 1.0D / 0.0, tier, true, true, true) > 0.0D || hasCapabilityItem(stack);
    }
}
