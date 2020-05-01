package me.cameronwhyte.mods.lavasmelting;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class NewItemEntity extends ItemEntity {

    public NewItemEntity(World world) {
        super((EntityType<? extends ItemEntity>) LavaSmelting.ITEM_ENTITY, world);
    }

    public NewItemEntity(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
        this.setItem(stack);
        stack.getItem();
        this.lifespan = stack.getEntityLifespan(worldIn);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
