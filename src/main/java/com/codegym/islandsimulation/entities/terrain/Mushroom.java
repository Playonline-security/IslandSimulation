package com.codegym.islandsimulation.entities.terrain;

import com.codegym.islandsimulation.entities.Organism;

public class Mushroom extends Organism {
    public Mushroom() {
        super("🍄", 5.0);
    }

    @Override
    public boolean isPlant() {
        return false;
    }
}