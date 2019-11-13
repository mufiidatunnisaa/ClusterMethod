/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;

/**
 *
 * @author ASUS
 */
public class Populasi {
    private Individu[] listIndividu;
    
    public Populasi(Integer jmlIndividu, Integer jmlKromosom) {
        listIndividu = new Individu[jmlIndividu];
        for(int i=0;i<jmlIndividu;i++){
            listIndividu[i] = new Individu(jmlKromosom);
        }
    }
    
    public Individu getIndividu(int index){
        return listIndividu[index];
    }
    
    public void setIndividu(int index, Individu individu){
        this.listIndividu[index] = individu;
    }

    public Individu[] getListIndividu() {
        return listIndividu;
    }

    public void setListIndividu(Individu[] listIndividu) {
        this.listIndividu = listIndividu;
    }
    
    
}
