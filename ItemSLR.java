package Implementazione;

public class ItemSLR {

    private Produzione produzione;
    private int indicePuntatore;

    public ItemSLR(Produzione produzione, int indPuntatore) {
        this.produzione = produzione;
        this.indicePuntatore = indPuntatore;
    }

    public int getIndicePuntatore() {
        return indicePuntatore;
    }

    public Simbolo getSimboloPuntato() {
        if (indicePuntatore < this.produzione.getCorpo().size()) {
            return this.produzione.getCorpo().get(indicePuntatore);
        } else {
            return null;
        }
    }

    public Produzione getProduzione() {
        return produzione;
    }

    public boolean shiftPuntatore() {
        if (indicePuntatore == this.produzione.getCorpo().size()) {
            return false;
        } else {
            indicePuntatore++;
            return true;
        }
    }

    @Override
    public String toString() {
        String result = this.produzione.getTesta() + " -> ";
        if (indicePuntatore >= this.produzione.getCorpo().size())
            return result + this.produzione.getCorpo() + ".";
        for (int i = 0; i < this.produzione.getCorpo().size(); i++) {
            if (i == this.indicePuntatore) {
                result += ".";
            }
            result += this.produzione.getCorpo().get(i);
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((produzione == null) ? 0 : produzione.hashCode());
        result = prime * result + indicePuntatore;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItemSLR other = (ItemSLR) obj;
        if (produzione == null) {
            if (other.produzione != null)
                return false;
        } else if (!produzione.equals(other.produzione))
            return false;
        if (indicePuntatore != other.indicePuntatore)
            return false;
        return true;
    }

}
