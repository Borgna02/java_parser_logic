package Implementazione.Domain;
public class Terminale implements Simbolo{
    private String terminale;

    public Terminale(String terminale) {
        this.terminale = terminale;
    }

    public String getTerminale() {
        return terminale;
    }

    public void setTerminale(String terminale) {
        this.terminale = terminale;
    }

    @Override
    public String toString() {
        return this.terminale;
    }

    @Override
    public boolean equals(Object obj) {
        return this.terminale.toString().equals(obj.toString());
    }
    

}
