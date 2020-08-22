package me.cameronwhyte.mods.lavasmelting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("lavasmelting")
public class LavaSmelting {

    private static final Logger LOGGER = LogManager.getLogger();

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
    public void itemJoinWorld(EntityJoinWorldEvent event) {
        /*if(event.getEntity() instanceof ItemEntity && !(event.getEntity() instanceof NewItemEntity)) {
            NewItemEntity item = new NewItemEntity(event.getWorld(), event.getEntity().getPosX(), event.getEntity().getPosY(), event.getEntity().getPosZ(), ((ItemEntity) event.getEntity()).getItem());
            System.out.println("made item noW!");
            item.setMotion(event.getEntity().getMotion());
            item.setDefaultPickupDelay();
            event.getEntity().remove();
            event.getWorld().addEntity(item);
            System.out.println("Added item");
        }*/
        if(event.getEntity() instanceof ItemEntity) {
            if(!(event.getEntity() instanceof NewItemEntity)) {
                ItemEntity oldItem = (ItemEntity) event.getEntity();
                World world = event.getWorld();
                NewItemEntity newItemEntity = new NewItemEntity(world, oldItem.getPosX(), oldItem.getPosY(), oldItem.getPosZ(), oldItem.getItem());
                newItemEntity.setMotion(oldItem.getMotion());
                oldItem.remove();
                world.addEntity(newItemEntity);
            }
        }
    }

    /*@SubscribeEvent
    public void worldTick(TickEvent.PlayerTickEvent event) {
        World world = event.player.world;
        world.getPlayers().forEach(player -> {
            List<Entity> entities = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(player.getPosX()-15, player.getPosY()-15, player.getPosZ()-15, player.getPosX()+15, player.getPosY()+15, player.getPosZ()+15));
            entities.forEach(x -> {
                if(!((ItemEntity) x).getItem().getOrCreateTag().contains("used")) {
                    Optional<FurnaceRecipe> canBeSmelted = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(((ItemEntity) x).getItem()), world);
                    if(canBeSmelted.isPresent()) {
                        if(x.isInLava() || x.isBurning()) {
                            ItemEntity smelted = new ItemEntity(world, x.getPosX(), x.getPosY(), x.getPosZ(), canBeSmelted.get().getRecipeOutput());
                            smelted.getItem().setCount(((ItemEntity) x).getItem().getCount());
                            smelted.getItem().getOrCreateTag().putString("type", "lavasmelting");
                            ((ItemEntity) x).getItem().getOrCreateTag().putString("used", "lavasmelting");
                            smelted.setPosition(x.getPosX(), x.getPosY(), x.getPosZ());
                            world.addEntity(smelted);
                        }
                    }
                }
                if(((ItemEntity) x).getItem().getOrCreateTag().contains("type")) {
                    if(((ItemEntity) x).getItem().getOrCreateTag().get("type").equals("lavasmelting")) {
                        if(x.isInLava() || x.isBurning()) {
                            x.extinguish();
                            x.setInvulnerable(true);
                            x.setMotion(random.nextGaussian() / 8, .15, random.nextGaussian() / 8);
                        }
                        x.setInvulnerable(false);
                    }
                }
            });
        });
    }

    @SubscribeEvent
    public void pickupBurnedItem(PlayerEvent.ItemPickupEvent event) {
        event.getStack().removeChildTag("type");
    }

    /*
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
    }*/
}
