package me.cameronwhyte.mods.lavasmelting;

import com.sun.management.OperatingSystemMXBean;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod("lavasmelting")
public class LavaSmelting {

    private static final Logger LOGGER = LogManager.getLogger();
    private OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    private Random random = new Random();

    public LavaSmelting() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Lava Smelting is setup!");
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        LOGGER.info("Lava Smelting client is setup!");
    }

    @SubscribeEvent
    public void burnableItem(ItemTossEvent event) {
        ItemEntity tossed = event.getEntityItem();
        Optional<FurnaceRecipe> canBeSmelted = tossed.world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(tossed.getItem()), tossed.world);
        if(!canBeSmelted.isPresent()) return;
        if(osBean.getProcessCpuLoad() > .90) {
            surrenderItem();
        }
        World world = tossed.world;
        ItemEntity smelted = new ItemEntity(world, tossed.getPosX(), tossed.getPosY(), tossed.getPosZ(), canBeSmelted.get().getRecipeOutput());
        smelted.getItem().setCount(tossed.getItem().getCount());
        AtomicBoolean hasBeenBurned = new AtomicBoolean(false);
        Thread cancelBurning = new Thread(() -> {
           while(true) {
               if(!hasBeenBurned.get()) {
                   if(tossed.isInLava() || tossed.isBurning()) {
                       smelted.setPosition(tossed.getPosX(), tossed.getPosY(), tossed.getPosZ());
                       world.addEntity(smelted);
                       hasBeenBurned.set(true);
                   }
               }
               if(hasBeenBurned.get()) {
                   if(smelted.isInLava() || smelted.isBurning()) {
                       smelted.extinguish();
                       smelted.setInvulnerable(true);
                       smelted.setMotion(random.nextGaussian() / 8, .15, random.nextGaussian() / 8);
                   }
                   smelted.setInvulnerable(false);
               }
           }
        });
        cancelBurning.setName(smelted.getUniqueID().toString()+"_lavasmelting");
        cancelBurning.start();
    }

    @SubscribeEvent
    public void pickupBurnedItem(PlayerEvent.ItemPickupEvent event) {
        Map<Thread, StackTraceElement[]> allThreads = Thread.getAllStackTraces();
        allThreads.forEach((x,e) -> {
            if(x.getName().equals(event.getOriginalEntity().getUniqueID().toString()+"_lavasmelting")) {
                x.stop();
            }
        });
    }

    @SubscribeEvent
    public void burnedItemDespawn(ItemExpireEvent event) {
        Map<Thread, StackTraceElement[]> allThreads = Thread.getAllStackTraces();
        allThreads.forEach((x,e) -> {
            if(x.getName().equals(event.getEntityItem().getUniqueID().toString()+"_lavasmelting")) {
                x.stop();
            }
        });
    }

    private void surrenderItem() {
        Map<Thread, StackTraceElement[]> allThreads = Thread.getAllStackTraces();
        allThreads.forEach((x,e) -> {
            if(x.getName().contains("_lavasmelting")) {
                x.stop();
            }
        });
    }
}
