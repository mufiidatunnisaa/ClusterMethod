/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;

import kmeans.Point;

/**
 *
 * @author ASUS
 */
public class Individu {
    private Point[] listkromosom;
    private double fitness;
    private double probabilitasRoulate;
    
    public Individu(int jmlKromosom){
        listkromosom = new Point[jmlKromosom];
        for(int i=0;i<jmlKromosom;i++){
            listkromosom[i] = new Point(0.0,0.0);
        }
        fitness = 0;
        probabilitasRoulate = 0;
    }

    public Point[] getListkromosom() {
        return listkromosom;
    }

    public void setListkromosom(Point[] listkromosom) {
        this.listkromosom = listkromosom;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
    public void setKromosom(int index, Point point){
        this.listkromosom[index] = point;
    }
    
    public Point getKromosom(int index){
        return this.listkromosom[index];
    }

    public double getProbabilitasRoulate() {
        return probabilitasRoulate;
    }

    public void setProbabilitasRoulate(double probabilitasRoulate) {
        this.probabilitasRoulate = probabilitasRoulate;
    }
    
    
}
