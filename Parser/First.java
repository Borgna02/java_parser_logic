package Implementazione.Parser;

import java.util.TreeSet;

import Implementazione.Domain.Terminale;

public class First extends TreeSet<Terminale> {

    public First(First first) {
        addAll(first);
    }

    public First() {
    }
    
}
