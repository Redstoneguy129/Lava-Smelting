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
        super((EntityType<? extends ItemEntity>) LavaSmelting.ITEM_ENTITY, world);
    }

    public NewItemEntity(World world, double posX, double posY, double posZ, ItemStack item) {
        this(world, posX, posY, posZ);
        this.setItem(item);
        this.lifespan = (item.getItem() == null ? 6000 : item.getEntityLifespan(world));
    }

    public NewItemEntity(World world, double posX, double posY, double posZ) {
        this((EntityType<Entity>) LavaSmelting.ITEM_ENTITY, world);
        this.setPosition(posX, posY, posZ);
        this.rotationYaw = this.rand.nextFloat() * 360.0F;
        this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
