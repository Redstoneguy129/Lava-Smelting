package me.cameronwhyte.mods.lavasmelting.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Mixin(ItemEntity.class)
public abstract class NoItemSmelt extends Entity {


    @Shadow public abstract ItemStack getStack();

    private boolean debounce = false;
    private Random random = new Random();

    public NoItemSmelt(EntityType<?> type, World world) {
        super(type, world);
    }

    public void baseTick() {
        if(!debounce) {
            if(!this.getStack().getOrCreateTag().contains("type")) {
                if(isInLava() || isOnFire()) {
                    debounce = true;
                    Optional<SmeltingRecipe> maysmelt = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new BasicInventory(this.getStack()), this.world);
                    if(maysmelt.isPresent()) {
                        ItemEntity smelted = new ItemEntity(world, this.getPos().x, this.getPos().y, this.getPos().z, maysmelt.get().getOutput());
                        smelted.getStack().setCount(this.getStack().getCount());
                        smelted.getStack().getOrCreateTag().putString("type", "lavasmelting");
                        this.world.spawnEntity(smelted);
                    }
                }
            }
            if(this.getStack().getOrCreateTag().contains("type")) {
                if(Objects.requireNonNull(this.getStack().getOrCreateTag().get("type")).asString().equals("lavasmelting")) {
                    if(isInLava() || isOnFire()) {
                        this.extinguish();
                        this.setInvulnerable(true);
                        this.setVelocity(random.nextGaussian() / 8, .15, random.nextGaussian() / 8);
                    }
                }
            }
        }
        super.baseTick();
    }

}
