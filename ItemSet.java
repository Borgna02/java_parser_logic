package Implementazione;

import java.util.LinkedHashSet;
import java.util.LinkedList;

public class ItemSet extends LinkedHashSet<ItemSLR> {

    public ItemSet() {
        super();
    }

    public ItemSet(ItemSLR item) {
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

    public ItemSet getItemsBySimboloPuntato(Simbolo simboloPuntato) {
        ItemSet items = new ItemSet();
        for (ItemSLR item : this) {
            if (item.getSimboloPuntato().equals(simboloPuntato)) {
                items.add(item);
            }
        }
        return items;
    }

}
