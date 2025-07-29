package me.iwareq.testtask.tweaker.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author IWareQ
 */
@Getter
@AllArgsConstructor
public abstract class MachineRecipe {
    private final int operationTime;
    private final double energyPerTick;
}
