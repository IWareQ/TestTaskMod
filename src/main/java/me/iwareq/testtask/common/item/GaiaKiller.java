package me.iwareq.testtask.common.item;

import me.iwareq.testtask.TestTaskMod;
import me.iwareq.testtask.common.config.GaiaKillerConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.entity.EntityDoppleganger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author IWareQ
 */
public class GaiaKiller extends ItemSword {
    public static final DamageSource DAMAGE_SOURCE = new DamageSource("gaiaKiller").setDamageBypassesArmor().setDamageIsAbsolute();

    private static final GaiaKillerConfig CONFIG = GaiaKillerConfig.getInstance();

    public GaiaKiller() {
        super(ToolMaterial.EMERALD);
        this.setMaxDamage(-1);
        this.setCreativeTab(TestTaskMod.CREATIVE_TAB);
        this.setHasSubtypes(true);
        this.setUnlocalizedName("gaiaKiller");
        this.setTextureName("stick");
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (target instanceof EntityDoppleganger && attacker instanceof EntityPlayer) {
            EntityPlayer attackerPlayer = (EntityPlayer) attacker;

            if (getRemainingCooldownSeconds(stack) > 0) {
                attackerPlayer.addChatMessage(new ChatComponentText("§cДо перезарядки еще " + getRemainingCooldownSeconds(stack) + " сек."));
                return super.hitEntity(stack, target, attacker);
            }

            ItemNBTHelper.setLong(stack, "cooldown", System.currentTimeMillis());
            ItemNBTHelper.setInt(stack, "kills", ItemNBTHelper.getInt(stack, "kills", 0) + 1);

            EntityDoppleganger doppleganger = (EntityDoppleganger) target;
            doppleganger.setDead();
            doppleganger.onDeath(DAMAGE_SOURCE);

            GaiaKillerConfig.GaiaKillerEntry config = getConfig(stack);
            if (ThreadLocalRandom.current().nextDouble() < config.getDoubleDropChance()) {
                // в идеале заюзать Mixins, но так тоже пойдет
                try {
                    Method dropFewItems = EntityLivingBase.class.getDeclaredMethod("dropFewItems", boolean.class, int.class);
                    dropFewItems.setAccessible(true);
                    dropFewItems.invoke(target, true, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (ThreadLocalRandom.current().nextDouble() < config.getExtraDropChance()) {
                List<GaiaKillerConfig.GaiaKillerEntry.ExtraDropEntry> extraDrops = config.getExtraDrops();
                for (GaiaKillerConfig.GaiaKillerEntry.ExtraDropEntry extraDrop : extraDrops) {
                    if (ThreadLocalRandom.current().nextDouble() < extraDrop.getChance()) {
                        target.entityDropItem(extraDrop.toItemStack(), 1f);
                    }
                }
            }

            if (getNeededKillsForNextLevel(stack) <= 0) {
                GaiaKillerConfig.GaiaKillerEntry nextConfig = getConfig(stack.getItemDamage() + 1);
                if (nextConfig != null) {
                    stack.setItemDamage(stack.getItemDamage() + 1);
                }
            }
        }

        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advance) {
        GaiaKillerConfig.GaiaKillerEntry config = getConfig(stack);
        list.add("§7Уровень меча: §a" + (stack.getItemDamage() + 1) + "/" + CONFIG.getLevels().size());
        int neededKillsForNextLevel = getNeededKillsForNextLevel(stack);
        if (neededKillsForNextLevel > 0) {
            list.add("§7Нужно §a" + neededKillsForNextLevel + " §7убийств для следующего уровня");
        }

        list.add("§7Шанс х2 дропа с Гайи: §b" + (int) (config.getDoubleDropChance() * 100) + "%");
        list.add("§7Шанс на дополнительный дроп: §b" + (int) (config.getExtraDropChance() * 100) + "%");

        int remaining = getRemainingCooldownSeconds(stack);
        if (remaining > 0) {
            list.add("§cПерезарядка: §e" + remaining + " сек.");
        } else {
            list.add("§aГотов к использованию");
        }
    }

    protected int getRemainingCooldownSeconds(ItemStack stack) {
        long cooldown = ItemNBTHelper.getLong(stack, "cooldown", 0L);
        int remaining = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - cooldown);

        int killCooldown = getConfig(stack).getKillCooldown();
        return remaining > killCooldown ? 0 : killCooldown - remaining;
    }

    protected int getNeededKillsForNextLevel(ItemStack stack) {
        GaiaKillerConfig.GaiaKillerEntry config = getConfig(stack.getItemDamage() + 1);
        if (config == null) {
            return 0;
        }

        int kills = ItemNBTHelper.getInt(stack, "kills", 0);
        return config.getKillsForLevel() - kills;
    }

    protected GaiaKillerConfig.GaiaKillerEntry getConfig(ItemStack stack) {
        return getConfig(stack.getItemDamage());
    }

    protected GaiaKillerConfig.GaiaKillerEntry getConfig(int level) {
        return level < CONFIG.getLevels().size() ? CONFIG.getLevels().get(level) : null;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List result) {
        for (int i = 0; i < CONFIG.getLevels().size(); ++i) {
            ItemStack itemStack = new ItemStack(this, 1, i);
            result.add(itemStack);
        }
    }
}
