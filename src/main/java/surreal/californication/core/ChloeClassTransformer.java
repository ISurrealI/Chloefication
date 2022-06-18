package surreal.californication.core;

import com.google.common.collect.Maps;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.Loader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import scala.tools.asm.Type;
import surreal.californication.ChloeValues;

import java.util.Map;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public class ChloeClassTransformer implements IClassTransformer {
    private static final String HOOKS = Type.getInternalName(ChloeHooks.class);
    private static final Map<String, Consumer<ClassNode>> MAP = Maps.newHashMap();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (MAP.containsKey(transformedName)) {
            ChloeLoadingPlugin.LOGGER.info("Manipulating " + transformedName);
            return transform(basicClass, MAP.get(transformedName));
        }
        return basicClass;
    }

    private byte[] transform(byte[] classBeingTransformed, Consumer<ClassNode> consumer) {
        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(classBeingTransformed);
            classReader.accept(classNode, 0);

            consumer.accept(classNode);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classBeingTransformed;
    }

    static {
        if (Loader.isModLoaded(ChloeValues.IC2)) {
            MAP.put("ic2.core.block.invslot.InvSlotCharge", cls -> {
                for (MethodNode method : cls.methods) {
                    if (method.name.equals("accepts")) {
                        AbstractInsnNode node = null;
                        for (AbstractInsnNode n : method.instructions.toArray()) {
                            if (n.getOpcode() == DCMPL && n.getNext().getOpcode() == IFLE) {
                                node = n.getNext();
                                break;
                            }
                        }

                        if (node != null) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 1));
                            list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "hasCapabilityItem", "(Lnet/minecraft/item/ItemStack;)Z", false));
                            method.instructions.insertBefore(node, list);
                        }
                    }

                    if (method.name.equals("charge")) {
                        AbstractInsnNode node = null;
                        for (AbstractInsnNode n : method.instructions.toArray()) {
                            if (n.getOpcode() == GETSTATIC) {
                                node = n;
                                break;
                            }
                        }

                        if (node != null) {
                            for (int i = 0; i < 8; i++) {
                                node = node.getNext();
                                method.instructions.remove(node.getPrevious());
                            }

                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 3));
                            list.add(new VarInsnNode(DLOAD, 1));
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, "ic2/core/block/invslot/InvSlotCharge", "tier", "I"));
                            list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "tryChargeItem", "(Lnet/minecraft/item/ItemStack;DI)D", false));
                            method.instructions.insertBefore(node, list);
                        }

                        break;
                    }
                }
            });
            MAP.put("ic2.core.block.invslot.InvSlotDischarge", cls -> {
                for (MethodNode method : cls.methods) {
                    if (method.name.equals("accepts")) {
                        AbstractInsnNode node = null;
                        for (AbstractInsnNode n : method.instructions.toArray()) {
                            if (n.getOpcode() == GETSTATIC && n.getNext().getOpcode() == ALOAD) {
                                node = n.getPrevious();
                                break;
                            }
                        }

                        if (node != null) {
                            for (int i = 0; i < 31; i++) {
                                node = node.getNext();
                                method.instructions.remove(node.getPrevious());
                            }

                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 1));
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, "ic2/core/block/invslot/InvSlotDischarge", "tier", "I"));
                            list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "accepts", "(Lnet/minecraft/item/ItemStack;I)Z", false));
                            method.instructions.insertBefore(node, list);
                        }
                    }
                    if (method.name.equals("discharge")) {
                        AbstractInsnNode node = null;
                        for (AbstractInsnNode n : method.instructions.toArray()) {
                            if (n.getOpcode() == GETSTATIC) {
                                node = n;
                                break;
                            }
                        }

                        if (node != null) {
                            for (int i = 0; i < 9; i++) {
                                node = node.getNext();
                                method.instructions.remove(node.getPrevious());
                            }

                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 4));
                            list.add(new VarInsnNode(DLOAD, 1));
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, "ic2/core/block/invslot/InvSlotDischarge", "tier", "I"));
                            list.add(new VarInsnNode(ILOAD, 3));
                            list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "tryDischargeItem", "(Lnet/minecraft/item/ItemStack;DIZ)D", false));
                            method.instructions.insertBefore(node, list);
                        }

                        break;
                    }
                }
            });
        }
    }
}
