package Implementazione.Parser;

import java.util.TreeSet;

import Implementazione.Domain.Terminale;

public class Follow extends TreeSet<Terminale> {
    public Follow(Follow Follow) {
        addAll(Follow);
    }

    public Follow() {
    }

}
