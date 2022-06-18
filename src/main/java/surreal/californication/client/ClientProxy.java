package surreal.californication.client;

import net.minecraft.item.Item;
import surreal.californication.common.CommonProxy;

public class ClientProxy extends CommonProxy {
    @Override
    public void registerItemRenders(Item item, int meta) {
        super.registerItemRenders(item, meta);
    }
}
