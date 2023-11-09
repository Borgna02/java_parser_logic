package Implementazione.Domain;
public class Produzione {
    NonTerminale testa;
    Corpo corpo;

    public Produzione(NonTerminale testa, Corpo corpo) throws IllegalArgumentException {
        if(testa == null) throw new IllegalArgumentException("Il non terminale indicato come testa non esiste");
        this.testa = testa;
        this.corpo = corpo;
    }
    
    public Produzione(Grammatica grammatica, String string, Corpo corpo) throws IllegalArgumentException {
        NonTerminale testa = grammatica.getNonTermSeEsiste(string);
        if(testa == null) throw new IllegalArgumentException("Il non terminale indicato come testa non esiste");
        this.testa = testa;
        this.corpo = corpo;
    }

    public NonTerminale getTesta() {
        return testa;
    }

    public void setTesta(NonTerminale testa) {
        this.testa = testa;
    }

    public Corpo getCorpo() {
        return corpo;
    }

    public void setCorpo(Corpo corpo) {
        this.corpo = corpo;
    }

    @Override
    public String toString() {
        return this.testa + " -> " + this.corpo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((testa == null) ? 0 : testa.hashCode());
        result = prime * result + ((corpo == null) ? 0 : corpo.hashCode());
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
        Produzione other = (Produzione) obj;
        if (testa == null) {
            if (other.testa != null)
                return false;
        } else if (!testa.equals(other.testa))
            return false;
        if (corpo == null) {
            if (other.corpo != null)
                return false;
        } else if (!corpo.equals(other.corpo))
            return false;
        return true;
    }
}