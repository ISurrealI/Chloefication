package surreal.californication.debugh;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import surreal.californication.config.ChloeConfig;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ItemInHandCommand extends CommandBase {
    private static final String alias = "chloe";

    @Nonnull
    @Override
    public List<String> getAliases() {
        return Collections.singletonList(alias);
    }

    @Nonnull
    @Override
    public String getName() {
        return alias;
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
        World world = sender.getEntityWorld();
        EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(sender.getCommandSenderEntity().getName());

        if (player != null && (ChloeConfig.BASIC.debughMode || FMLLaunchHandler.isDeobfuscatedEnvironment())) {
            String name = asString(player.getHeldItemMainhand());
            player.sendMessage(new TextComponentString(name));
        }
    }

    private static String asString(ItemStack stack) {
        String name = Item.REGISTRY.getNameForObject(stack.getItem()).toString();
        int count = stack.getCount();
        int meta = stack.getMetadata();

        NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound().copy() : null;

        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(":").append(meta);
        builder.append("*").append(count);

        if (nbt != null) builder.append("#").append(nbt.toString());

        return builder.toString();
    }
}
