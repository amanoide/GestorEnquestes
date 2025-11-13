package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Representa un clúster: centroide y lista de miembros.
 */
public class Kluster {
    private double[] centroid;
    private List<double[]> members;

    public Kluster(int dim) {
        this.centroid = new double[dim];
        this.members = new ArrayList<>();
    }

    public Kluster(double[] centroid) {
        this.centroid = Arrays.copyOf(centroid, centroid.length);
        this.members = new ArrayList<>();
    }

    public double[] getCentroid() { return centroid; }
    public void setCentroid(double[] c) { this.centroid = Arrays.copyOf(c, c.length); }
    public List<double[]> getMembers() { return members; }
    public void addMember(double[] x) { members.add(x); }
    public void clearMembers() { members.clear(); }
    public int size() { return members.size(); }

    /** Recalcula el centroide como la media de los miembros. */
    public boolean recomputeCentroid(double tol) {
        if (members.isEmpty()) return false;
        int dim = centroid.length;
        double[] mean = new double[dim];
        for (double[] m : members) {
            for (int i = 0; i < dim; i++) mean[i] += m[i];
        }
        for (int i = 0; i < dim; i++) mean[i] /= members.size();
        boolean changed = false;
        for (int i = 0; i < dim; i++) if (Math.abs(mean[i] - centroid[i]) > tol) { changed = true; break; }
        this.centroid = mean;
        return changed;
    }

    @Override
    public String toString() { return "Kluster{centroid=" + Arrays.toString(centroid) + ", size=" + members.size() + "}"; }
}
