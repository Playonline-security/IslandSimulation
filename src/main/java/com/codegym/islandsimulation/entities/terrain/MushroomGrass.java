package com.codegym.islandsimulation.entities.terrain;

import com.codegym.islandsimulation.entities.Organism;

public class MushroomGrass extends Organism {
    public MushroomGrass() {
        super("🍄‍🟫", 2.0); // Peso de un hongo hierba.
    }

    @Override
    public boolean isPlant() {
        return false;
    }
}