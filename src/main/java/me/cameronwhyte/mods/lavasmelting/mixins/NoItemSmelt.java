package me.cameronwhyte.mods.lavasmelting.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;
import java.util.Random;

@Mixin(ItemEntity.class)
public abstract class NoItemSmelt extends Entity {

    @Shadow public abstract ItemStack getStack();

    @Shadow public abstract void setStack(ItemStack stack);

    private final Random random = new Random();

    public NoItemSmelt(EntityType<?> type, World world) {
        super(type, world);
    }

    public void baseTick() {
        if(this.isInvulnerable() && this.isInLava()) {
            this.setVelocity(this.random.nextGaussian() / 8, .15, this.random.nextGaussian() / 8);
        }
        Optional<SmeltingRecipe> canBeSmelted = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(this.getStack()), this.world);
        canBeSmelted.ifPresent(furnaceRecipe -> {
            if(this.isInLava() || this.isOnFire()) {
                this.extinguish();
                this.setInvulnerable(true);
                ItemStack output = furnaceRecipe.getOutput();
                output.setCount(this.getStack().getCount());
                this.setStack(output);
            }
        });
        super.baseTick();
    }
}
