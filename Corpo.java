package Implementazione;
import java.util.LinkedList;

public class Corpo {
    private LinkedList<Simbolo> simboli;

    public Corpo(LinkedList<Simbolo> simboli) {
        this.simboli = simboli;
    }

    // Costruttore con i simboli
    public Corpo(Simbolo... simboli) {
        this.simboli = new LinkedList<Simbolo>();
        for (Simbolo s : simboli) {
            this.simboli.add(s);
        }
    }

    // Costruttore con le stringhe
    /**
     * Costruisce il corpo di una produzione passando come argomenti i singoli
     * simboli (terminali o non) sotto forma di stringhe separate.
     * Se i simboli non fanno parte dell'alfabeto restituisce l'eccezione
     * IllegalArgumentException
     * 
     * @param grammatica
     * @param stringhe
     * @throws IllegalArgumentException
     * 
     */
    public Corpo(Grammatica grammatica, String... stringhe) throws IllegalArgumentException {
        this.simboli = new LinkedList<Simbolo>();
        for (String s : stringhe) {
            Simbolo simbolo = grammatica.getTermSeEsiste(s);
            // se s non è un terminale
            if (simbolo == null)
                try {
                    simbolo = grammatica.getNonTermSeEsiste(s);
                } catch (Exception e) {
                    // se s non ha la struttura adatta per essere un non terminale
                    throw new IllegalArgumentException("Almeno uno dei simboli indicati non esiste, " + e.getMessage());
                }
            // se s non è neanche non terminale
            if (simbolo == null)
                throw new IllegalArgumentException("Almeno uno dei simboli indicati non esiste: " + s);
            simboli.add(simbolo);
        }
    }

    public LinkedList<Simbolo> getSimboli() {
        return simboli;
    }

    public void setSimboli(LinkedList<Simbolo> simboli) {
        this.simboli = simboli;
    }

    @Override
    public String toString() {
        String result = "";
        for (Simbolo s : this.simboli) {
            result += s;
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((simboli == null) ? 0 : simboli.hashCode());
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
        Corpo other = (Corpo) obj;
        if (simboli == null) {
            if (other.simboli != null)
                return false;
        } else if (!simboli.equals(other.simboli))
            return false;
        return true;
    }

    public String[] toStrings() {
        String[] result = new String[this.simboli.size()];
        int i = 0;
        for (Simbolo simbolo : this.simboli) {
            result[i] = simbolo.toString();
            i++;   
        }

        return result;
    }
}
