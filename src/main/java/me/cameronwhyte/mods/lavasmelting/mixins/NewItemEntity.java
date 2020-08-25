package me.cameronwhyte.mods.lavasmelting.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;
import java.util.Random;

@Mixin(ItemEntity.class)
public abstract class NewItemEntity extends Entity {

    @Shadow public abstract ItemStack getItem();

    @Shadow public abstract void setItem(ItemStack stack);

    private final Random random = new Random();

    public NewItemEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public void baseTick() {
        if(this.isInvulnerable() && this.isInLava()) {
            this.setMotion(this.random.nextGaussian() / 8, .15, this.random.nextGaussian() / 8);
        }
        Optional<FurnaceRecipe> canBeSmelted = this.world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(this.getItem()), this.world);
        canBeSmelted.ifPresent(furnaceRecipe -> {
            if(this.isInLava() || this.isBurning()) {
                this.extinguish();
                this.setInvulnerable(true);
                ItemStack output = furnaceRecipe.getRecipeOutput();
                output.setCount(this.getItem().getCount());
                this.setItem(output);
            }
        });
        super.baseTick();
    }
}
