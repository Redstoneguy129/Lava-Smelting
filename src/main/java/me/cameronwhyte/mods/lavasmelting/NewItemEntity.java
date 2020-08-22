package me.cameronwhyte.mods.lavasmelting;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class NewItemEntity extends ItemEntity {

    public NewItemEntity(EntityType<Entity> type, World world) {
        super(EntityType.ITEM, world);
    }

    public NewItemEntity(World world, double posX, double posY, double posZ, ItemStack item) {
        super(world, posX, posY, posZ, item);
    }

    @Override
    public void tick() {
        super.tick();
        System.out.println("Hello");
    }



    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
