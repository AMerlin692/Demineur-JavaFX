package fr.AMerlin692.Demineur;

public class Case {

    private boolean mine;
    private boolean revelee;
    private boolean drapeau;
    private int minesVoisines;

    public boolean estMine() { return mine; }
    public void setMine(boolean mine) { this.mine = mine; }

    public boolean estRevelee() { return revelee; }
    public void setRevelee(boolean revelee) { this.revelee = revelee; }

    public boolean aDrapeau() { return drapeau; }
    public void basculerDrapeau() { this.drapeau = !this.drapeau; }

    public int getMinesVoisines() { return minesVoisines; }
    public void setMinesVoisines(int n) { this.minesVoisines = n; }
}
