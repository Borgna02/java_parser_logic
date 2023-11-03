package Implementazione;
import java.util.LinkedHashSet;

public class Grammatica {
    private LinkedHashSet<NonTerminale> nonTerminali;
    private LinkedHashSet<Terminale> terminali;
    private LinkedHashSet<Produzione> produzioni;
    private NonTerminale partenza;

    public NonTerminale getPartenza() {
        return this.partenza;
    }

    public void setPartenza(NonTerminale nonTerminale) {
        this.partenza = nonTerminale;

    }

    public Grammatica() {
        this.produzioni = new LinkedHashSet<Produzione>();
    }

    public LinkedHashSet<NonTerminale> getNonTerminali() {
        return nonTerminali;
    }

    public NonTerminale getNonTermSeEsiste(String nonTerminale) {
        for (NonTerminale obj : this.nonTerminali) {
            if (obj.equals(new NonTerminale(nonTerminale)))
                return obj;
        }
        return null;
    }

    public void setNonTerminali(LinkedHashSet<NonTerminale> nonTerminali) {
        this.nonTerminali = nonTerminali;
    }

    public void setNonTerminali(NonTerminale... nonTerminali) {
        this.nonTerminali = new LinkedHashSet<NonTerminale>();
        for (NonTerminale nonTerminale : nonTerminali) {
            this.nonTerminali.add(nonTerminale);
        }
    }

    public void setNonTerminali(String... strings) throws IllegalArgumentException {
        this.nonTerminali = new LinkedHashSet<NonTerminale>();
        for (String string : strings) {
            try {
                this.nonTerminali.add(new NonTerminale(string));
            } catch (IllegalArgumentException e) {
                throw e;
            }
        }
    }

    public LinkedHashSet<Terminale> getTerminali() {
        return terminali;
    }

    public Terminale getTermSeEsiste(String terminale) {
        for (Terminale obj : this.terminali) {
            if (obj.equals(new Terminale(terminale))) {
                return obj;
            }
        }

        return null;
    }

    public void setTerminali(LinkedHashSet<Terminale> terminali) {
        this.terminali = terminali;
    }

    public void setTerminali(Terminale... terminali) {
        this.terminali = new LinkedHashSet<Terminale>();
        for (Terminale terminale : terminali) {
            this.terminali.add(terminale);
        }
    }

    public void setTerminali(String... strings) {
        this.terminali = new LinkedHashSet<Terminale>();
        for (String string : strings) {
            this.terminali.add(new Terminale(string));
        }
    }

    public LinkedHashSet<Produzione> getProduzioni() {
        return produzioni;
    }

    public LinkedHashSet<Produzione> getProduzioniByTesta(String string) throws IllegalArgumentException {
        LinkedHashSet<Produzione> result = new LinkedHashSet<Produzione>();
        try {
            NonTerminale testa = new NonTerminale(string);
            for (Produzione produzione : this.produzioni) {
                if (produzione.getTesta().equals(testa)) {
                    result.add(produzione);
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }
        ;

        return result;
    }

    public LinkedHashSet<Produzione> getProduzioniByTesta(Simbolo simbolo) {

        LinkedHashSet<Produzione> result = new LinkedHashSet<Produzione>();
        if (simbolo instanceof NonTerminale) {
            for (Produzione produzione : this.produzioni) {
                if (produzione.getTesta().equals(simbolo)) {
                    result.add(produzione);
                }
            }
        }
        return result;
    }

    public LinkedHashSet<Produzione> getProduzioniByTestaNonRicorsive(Simbolo simbolo) {
        LinkedHashSet<Produzione> result = new LinkedHashSet<Produzione>();
        if (simbolo instanceof NonTerminale) {
            for (Produzione produzione : this.produzioni) {
                if (produzione.getTesta().equals(simbolo)
                        && !produzione.getTesta().equals(produzione.getCorpo().getSimboli().get(0))) {
                    result.add(produzione);
                }
            }
        }
        return result;
    }

    public LinkedHashSet<Produzione> getProduzioniIfCorpoContains(Simbolo simbolo) {
        LinkedHashSet<Produzione> result = new LinkedHashSet<Produzione>();
        for (Produzione produzione : this.produzioni) {
            if (produzione.getCorpo().getSimboli().contains(simbolo)) {
                result.add(produzione);
            }
        }
        return result;
    }

    public void setProduzioni(LinkedHashSet<Produzione> produzioni) {
        this.produzioni = produzioni;
    }

    public void addProduzione(Produzione produzione) {
        this.produzioni.add(produzione);
    }

    public void addProduzione(String testa, Corpo corpo) {
        this.produzioni.add(new Produzione(this.getNonTermSeEsiste(testa), corpo));
    }

    public String toStringProduzioni() {
        String result = "";
        int i = 0;
        for (Produzione produzione : this.produzioni) {
            result += i + ": " + produzione.toString() + "\n";
            i++;
        }
        return result;
    }

    @Override
    public String toString() {
        return "Terminali: " + this.terminali + "\nNon terminali: " + this.nonTerminali + "\nSimboli di partenza: "
                + this.partenza + "\nProduzioni: \n" + this.toStringProduzioni();
    }
}
