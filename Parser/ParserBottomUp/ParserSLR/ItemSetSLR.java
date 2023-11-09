package Implementazione.Parser.ParserBottomUp.ParserSLR;

import java.util.LinkedHashSet;

import Implementazione.Domain.Simbolo;

public class ItemSetSLR extends LinkedHashSet<ItemSLR> {

    public ItemSetSLR() {
        super();
    }

    public ItemSetSLR(ItemSLR item) {
        this.add(item);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    public LinkedHashSet<Simbolo> getSimboliPuntati() {
        LinkedHashSet<Simbolo> result = new LinkedHashSet<>();
        for (ItemSLR item : this) {
            if (item.getSimboloPuntato() != null) {
                result.add(item.getSimboloPuntato());
            }
        }
        return result;
    }

    public ItemSetSLR getItemsBySimboloPuntato(Simbolo simboloPuntato) {
        ItemSetSLR items = new ItemSetSLR();
        for (ItemSLR item : this) {
            if (item.getSimboloPuntato().equals(simboloPuntato)) {
                items.add(item);
            }
        }
        return items;
    }

    public ItemSetSLR getItemsByPuntatoreAllaFine() {
        ItemSetSLR items = new ItemSetSLR();
        for (ItemSLR item : this) {
            if(item.getSimboloPuntato() == null || item.getSimboloPuntato().toString().equals("eps")) {
                items.add(item);
            }
        }
        return items;
    }

}
