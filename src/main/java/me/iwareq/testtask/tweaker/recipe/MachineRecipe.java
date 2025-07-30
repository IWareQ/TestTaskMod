package me.iwareq.testtask.tweaker.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author IWareQ
 */
@Getter
@AllArgsConstructor
public abstract class MachineRecipe {
    protected final int operationTime;
    protected final double energyPerTick;
}
