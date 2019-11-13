package example;

import java.awt.Color;
import java.util.Random;
import javax.swing.JFrame;

import cartesian.coordinate.CCPoint;
import cartesian.coordinate.CCPolygon;
import cartesian.coordinate.CCSystem;
import cartesian.coordinate.CCLine;
import ga.Individu;
import ga.Populasi;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import kmeans.Cluster;
import kmeans.Data;
import kmeans.Point;

public class Main extends JFrame {
    private static final long serialVersionUID = 1L;
    static FileInputStream fstream;
    static DataInputStream in;
    static BufferedReader br;
    static Integer k;
    static ArrayList<Data> listData;
    static Cluster[] listCluster;
    static Point[] listCentroid;
    static Scanner sc;
    
    static Double xMax = 0.0;
    static Double yMax = 0.0;
    static Integer jarak = 1;
    static Integer generasi, jmlPopulasi;
    
    static Populasi populasi, hasilSeleksi;
    Main() throws FileNotFoundException, IOException, InterruptedException {
        super("Viewer");
        setTitle("Viewer");
        
        setVisible(true);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
//        
//        double[] x = new double[]{-50,100,0};
//        double[] y = new double[]{-50,-50,3};
//       CCPolygon ccp = new CCPolygon(x, y);
//        
//        s.add(new CCLine(1.0, 0.0, Color.blue));
//        s.add(new CCLine(-1.0, 10.0, Color.magenta));
//        s.add(new CCLine(1.0, 5.0, Color.red));
//        s.add(new CCLine(-1.0, 5.0, Color.cyan));
//        s.add(new CCLine(-1.0, 15.0, Color.yellow));
//        s.add(new CCLine(1.0, -5.0, Color.green));
//        s.add(new CCLine(1.0, 0.0, 5.0, Color.orange));
//        s.add(new CCLine(0.0, 1.0, 5.0, Color.pink));
//        s.add(ccp);
        
        /*
        Random r = new Random();
        for (int i = 0; i < 300; i++) {
            double a = r.nextDouble();
            double b = r.nextDouble();
            double c = r.nextDouble();
            if (r.nextBoolean()) a = -a;
            if (r.nextBoolean()) b = -b;
            if (r.nextBoolean()) c = -c;
            
            int cint = r.nextInt(13);
            Color color;
            
            switch (cint) {
            case 0: color = Color.black; break;
            case 1: color = Color.blue; break;
            case 2: color = Color.cyan; break;
            case 3: color = Color.darkGray; break;
            case 4: color = Color.gray; break;
            case 5: color = Color.green; break;
            case 6: color = Color.lightGray; break;
            case 7: color = Color.magenta; break;
            case 8: color = Color.orange; break;
            case 9: color = Color.pink; break;
            case 10: color = Color.red; break;
            case 11: color = Color.white; break;
            default: color = Color.yellow;
            }
            
            s.add(new CCLine(a,b,c,color));
        }
        */
         init();
         readTxt();
         setXMaxAndYMax();
         setKValue();
         CCSystem s = new CCSystem(0.0, 0.0, 150.0, 200.0);
         add(s);
         initCluster();
         implementKMeans(s);
    }
    
    private static void initCluster(){
        listCluster = new Cluster[k];
        for(int i=0;i<k;i++){
            listCluster[i] = new Cluster(i);
        }
    }
    
    private static void setXMaxAndYMax(){
        for(int i=0;i<listData.size();i++){
            if(xMax < listData.get(i).getPoint().getX()){
                xMax = listData.get(i).getPoint().getX();
            }
            
            if(yMax < listData.get(i).getPoint().getY()){
                yMax = listData.get(i).getPoint().getY();
            }
        }
    }
    
    private static void init() throws FileNotFoundException{
       fstream = new FileInputStream("ruspini.txt");
       in = new DataInputStream(fstream);  
       br = new BufferedReader(new InputStreamReader(in));
       sc = new Scanner(System.in);
       listData = new ArrayList<>();
    }
    
    private static void readTxt() throws IOException{
        String strLine;
        String dataTemp[];
        int i = 1;
        while((strLine = br.readLine()) != null){
            if(i == 1){
                
            }
            else{
                dataTemp = strLine.split("\t");
                Point point = new Point(Double.parseDouble(dataTemp[0]),Double.parseDouble(dataTemp[1]));
                listData.add(new Data(i,point));
            }
            i++;
        }
    }
    
    private static void setKValue(){
        System.out.print("Masukkan nilai K :");
        k = sc.nextInt();
        System.out.print("Masukkan jumlah generasi :");
        generasi = sc.nextInt();
        System.out.print("Masukkan jumlah individu/populasi :");
        jmlPopulasi = sc.nextInt();
    }
        
    private static void clearCluster(){
        for(int i=0;i<listCluster.length;i++){
            listCluster[i].getListData().clear();
        }
    }
    
    private static void setView(CCSystem s){
        s.clear();
        for(int i=0;i<k;i++){
            s.add(new CCPoint(listCentroid[i].getX(), listCentroid[i].getY(), Color.RED, new BasicStroke(1f)));
        }
        for(int i=0;i<listData.size();i++){
            Data data = listData.get(i);
            Paint paint = Color.BLACK;
            switch(data.getCluster() % 5){
                case 0 : paint = Color.BLUE;
                        break;
                case 1 : paint = Color.CYAN;
                        break;
                case 2 : paint = Color.YELLOW;
                          break;
                case 3 : paint = Color.GREEN;
                        break;
                case 4 : paint = Color.magenta;
            }
            s.add(new CCPoint(data.getPoint().getX(), data.getPoint().getY(), paint, new BasicStroke(1f)));
        }
    }
    
    private static void setNewCentroid(){
        Point[] listNewCentroid = new Point[k];
        boolean flag = true;
        for(int i=0;i<k;i++){
            listNewCentroid[i] = countNewCentroidPosition(listCluster[i]);
        }
        
        for(int i=0; i<k ; i++){
            if(listCentroid[i].getX() != listNewCentroid[i].getX() 
                && listCentroid[i].getY() != listNewCentroid[i].getY()){
                flag = false;
            }
            listCentroid[i] = listNewCentroid[i];
        }
        if(flag){
            jarak = 0;
        }
    }
    
    private static Point countNewCentroidPosition(Cluster cluster){
       Double rata2x = 0.0;
       Double rata2y = 0.0;
       Point point;
       for(int i=0 ; i< cluster.getListData().size() ; i++){
           rata2x += cluster.getData(i).getPoint().getX();
           rata2y += cluster.getData(i).getPoint().getY();
       }
       if(cluster.getListData().size() == 0){
            point = new Point(-50.0, -50.0);
       }
       else{
           rata2x = rata2x/cluster.getListData().size();
           rata2y = rata2y/cluster.getListData().size();
           point = new Point(rata2x, rata2y);
       }
       return point;
    }
    

        
    private static void setClustertoListCluster(Data data){
        for(int i=0 ; i<listCluster.length ; i++){
            if(data.getCluster() == listCluster[i].getCluster()){
                listCluster[i].addData(data);
            }
        }
    }
    
    private static void countJarak(Data dataTes, Double[] allJarak){
        for(int i=0 ; i<k ; i++){
            allJarak[i] = Math.sqrt(Math.pow((listCentroid[i].getX() - dataTes.getPoint().getX()), 2) 
                                    + Math.pow((listCentroid[i].getY() - dataTes.getPoint().getY()), 2));
        }
    }
    
    private static Integer getClusterMinimum(Double[] allJarak){
        Double min = Double.MAX_VALUE;
        Integer cluster = 0;
        for(int i=0 ; i < allJarak.length; i++){
            if(min > allJarak[i]){
                min = allJarak[i];
                cluster = i;
            }
        }
        return cluster;
    }
    
    private static void setCluster(){
        for(int i=0 ; i<listCluster.length ; i++){
            listCluster[i].setCluster(i);
        }
        for(int i=0 ; i<listData.size() ; i++){
            Double allJarak[] = new Double[k];
            countJarak(listData.get(i), allJarak);
            Integer clusterJarakTerdekat = getClusterMinimum(allJarak);
            listData.get(i).setCluster(clusterJarakTerdekat);
            setClustertoListCluster(listData.get(i));
        }
    }
   private static void implementKMeans(CCSystem s) throws InterruptedException{
        createRandomCentroid();
        //Thread.sleep(10000);
        while(jarak == 1){
            setCluster();
            setView(s);
            Thread.sleep(10000);
            setNewCentroid();
            clearCluster();
        }
        System.out.println("Selesai");
    }
    
    public static void main(String[] args) throws IOException, FileNotFoundException, InterruptedException {
        new Main();
    }
    
        
    private static void createRandomCentroid(){
        listCentroid = new Point[k];
        for(int i=1; i<=k ; i++){
            listCentroid[i-1] = new Point(0.0,0.0);
        }
//        Random rand = new Random();
//        for(int i=0 ; i<listCentroid.length ; i++){
//            Integer xTemp = rand.nextInt(xMax.intValue());
//            Integer yTemp = rand.nextInt(yMax.intValue());
//            if(xTemp < 0){
//                xTemp *= -1;
//            }
//            if(yTemp < 0){
//                yTemp *= -1;
//            }
//            Double x = xTemp.doubleValue();
//            Double y = yTemp.doubleValue();
//            listCentroid[i].setX(x);
//            listCentroid[i].setY(y);
//        }
        
        listCentroid = GAImplementation();
    }
    
    private static Point[] GAImplementation(){
        populasi = new Populasi(jmlPopulasi,k);
        setRandomGen();
      
        for(int i=0;i<generasi;i++){
            populasi = countFitness(populasi);
            hasilSeleksi = roulateSelection();
            crossOverSelection();
            mutationSelection();
            elitism();
        }
        
        return populasi.getIndividu(0).getListkromosom();
    }
    
    private static void elitism(){
        Populasi populasiRanking = new Populasi((jmlPopulasi*2),k);
      //  System.out.println("juml indi :" + populasiRanking.getListIndividu().length);
        for(int i=0;i<jmlPopulasi;i++){
            populasiRanking.setIndividu(i, populasi.getIndividu(i));
        }
        int index = 0;
        for(int i=jmlPopulasi;i<jmlPopulasi*2;i++){
            populasiRanking.setIndividu(i, hasilSeleksi.getIndividu(index));
            index++;
        }
        
        populasiRanking = countFitness(populasiRanking);
//                System.out.println("-----populasi---------");
//        for(int i=0;i<jmlPopulasi*2;i++){
//            System.out.println(populasiRanking.getIndividu(i).getFitness());
//        }
        populasiRanking = sortIndividu(populasiRanking);
//        System.out.println("-----populasi---------");
//        for(int i=0;i<jmlPopulasi*2;i++){
//            System.out.println(populasiRanking.getIndividu(i).getFitness());
//        }
        for(int i=0;i<jmlPopulasi;i++){
            populasi.setIndividu(i, populasiRanking.getIndividu(i));
        }
    }
    
    private static Populasi sortIndividu(Populasi populasi){
        for(int i=0;i<populasi.getListIndividu().length - 1;i++){
            
            for(int j=0;j<populasi.getListIndividu().length - 1;j++){
                Individu individu1 = populasi.getIndividu(j);
                Individu individu2 = populasi.getIndividu(j+1);
                
                if(individu1.getFitness() < individu2.getFitness()){
                    //System.out.println("sort fitness : " + individu1.getFitness() + " ," + individu2.getFitness());
                    Individu temp = new Individu(k);
                        temp.setFitness(individu1.getFitness());
                        temp.setProbabilitasRoulate(individu1.getProbabilitasRoulate());
                        temp.setListkromosom(individu1.getListkromosom());
                    
                    populasi.setIndividu(j, individu2);
                    populasi.setIndividu(j+1, temp);
                    //System.out.println("sort hasil fitness : " + populasi.getIndividu(i).getFitness() + " ," + populasi.getIndividu(j).getFitness());
                }
                
            }
        }
        return populasi;
    }
    
    private static void mutationSelection(){
        double probMutation = 0.1;
        for(int i=0;i<jmlPopulasi;i++){
            double prob = Math.random();
            if(prob< probMutation){
                mutation(i);
            }
        }
    }
    
    private static void mutation(int index){
        Random rand = new Random();
        Individu individu = hasilSeleksi.getIndividu(index);
        double geserValue = 2;
        double sign = Math.random();
        if(sign < 0.5){
            geserValue *= -1;
        }
        int indexGeser = rand.nextInt(individu.getListkromosom().length * 2);
        int state = indexGeser/2;
        if(indexGeser % 2 == 0){
            double xLama = individu.getKromosom(state).getX();
            individu.getKromosom(state).setX(xLama + geserValue);
        }
        else{
            double yLama = individu.getKromosom(state).getY();
            individu.getKromosom(state).setY(yLama + geserValue);
        }
    }
    
    private static void crossOverSelection(){
        double probCrossOver = 0.8;
        for(int i=0;i<jmlPopulasi/2;i++){
            double prob = Math.random();
            if(prob < probCrossOver){
                crossOver(i,i+1);
            }
        }
    }
    
    private static void crossOver(int index1, int index2){
        Random rand = new Random();
        Individu individu1 = hasilSeleksi.getIndividu(index1);
        Individu individu2 = hasilSeleksi.getIndividu(index2);
        int batas = hasilSeleksi.getIndividu(0).getListkromosom().length * 2;
        int batasKiri = rand.nextInt(batas)-2;
        if(batasKiri < 0){
            batasKiri = 0;
        }
        int batasKanan = rand.nextInt(batas - batasKiri) + batasKiri + 1;
        if(batasKanan > (batas-1) ){
            batasKanan = batas-1;
        }
//        System.out.println("batas kiri, batas kanan" + batasKiri + ", " + batasKanan);
//        System.out.println("individu1");
//        for(int i=0;i<individu1.getListkromosom().length;i++){
//            System.out.println("X : " + individu1.getKromosom(i).getX());
//            System.out.println("Y : " + individu1.getKromosom(i).getY());
//        }
        
//        System.out.println("individu2");
//        for(int i=0;i<individu1.getListkromosom().length;i++){
//            System.out.println("X : " + individu2.getKromosom(i).getX());
//            System.out.println("Y : " + individu2.getKromosom(i).getY());
//        }
//        
        for(int i=batasKiri;i<=batasKanan;i++){
            int index = i/2;
            if(i%2 == 0){
                double temp = individu1.getKromosom(index).getX();
                individu1.getKromosom(index).setX(individu2.getKromosom(index).getX());
                individu2.getKromosom(index).setX(temp);
            }
            else{
                double temp = individu1.getKromosom(index).getY();
                individu1.getKromosom(index).setY(individu2.getKromosom(index).getY());
                individu2.getKromosom(index).setY(temp);
            }
        }
        
//        System.out.println("individu1after");
//        for(int i=0;i<individu1.getListkromosom().length;i++){
//            System.out.println("X : " + individu1.getKromosom(i).getX());
//            System.out.println("Y : " + individu1.getKromosom(i).getY());
//        }
//        
//        System.out.println("individu2after");
//        for(int i=0;i<individu1.getListkromosom().length;i++){
//            System.out.println("X : " + individu2.getKromosom(i).getX());
//            System.out.println("Y : " + individu2.getKromosom(i).getY());
//        }
    }
    
    private static Populasi countFitness(Populasi populasi){
        
        //System.out.println("--------fitness--------");
        for(int i=0;i<populasi.getListIndividu().length;i++){
           Individu individu = populasi.getIndividu(i);
           double fitness = 0;
           for(int j=0;j<individu.getListkromosom().length;j++){
               Point kromosom = individu.getKromosom(j);
               fitness += getMinJarak(kromosom);
           }
           individu.setFitness(1/fitness);
          // System.out.println(individu.getFitness());
        }
        return populasi;
    }
    
    private static Populasi roulateSelection(){
        int jmlIndividu = populasi.getListIndividu().length;
        Populasi seleksi = new Populasi(jmlIndividu, k);
        double total = 0;
        Random rand = new Random();
        
        for(int i=0;i<jmlIndividu;i++){
            total += populasi.getIndividu(i).getFitness();
        }
        
        for(int i=0;i<jmlIndividu;i++){
            Individu individu = populasi.getIndividu(i);
            double probabilitas = individu.getFitness()/total * 100;
            individu.setProbabilitasRoulate(probabilitas);
        }
        
        for(int i=0;i<jmlIndividu;i++){
            double probabilitas = rand.nextInt(100);
            if(probabilitas < 0){
                probabilitas *= -1;
            }
            //System.out.println("prob : " + probabilitas);
            double state = 0;
            
            for(int j=0;j<jmlIndividu;j++){
                Individu individu = populasi.getIndividu(j);
                if(probabilitas <= (state + individu.getProbabilitasRoulate()) ){
                    individu.setProbabilitasRoulate(state + individu.getProbabilitasRoulate());
                    seleksi.setIndividu(i, individu);
              //      System.out.println("set-----------------" + j);
                //     System.out.println("probabilitas roulate -" + i + " :" + individu.getProbabilitasRoulate());
                    break;
                }
                else{
                    state += individu.getProbabilitasRoulate();
                }
            }
           // System.out.println("probabilitas roulate -" + i + " :" + seleksi.getIndividu(i).getProbabilitasRoulate());
        }
        return seleksi;
    }
    
    private static double getMinJarak(Point point){
        double jarak = Double.MAX_VALUE;
        for(int i=0;i<listData.size();i++){
            Point data = listData.get(i).getPoint();
            double temp = Math.sqrt(Math.pow(point.getX() - data.getX(), 2) + Math.pow( point.getY() - data.getY() , 2));
            if(jarak > temp){
                jarak = temp;
            }
        }
        return jarak;
    } 
    
    private static void setRandomGen(){
        Random rand = new Random();
        //System.out.println("---------kromosom-----------");
        for(int i=0;i<populasi.getListIndividu().length;i++){
            Individu individu = populasi.getIndividu(i);
            
            for(int j=0;j<individu.getListkromosom().length;j++){
                Integer xTemp = rand.nextInt(xMax.intValue());
                Integer yTemp = rand.nextInt(yMax.intValue());
                if(xTemp < 0){
                    xTemp *= -1;
                }
                if(yTemp < 0){
                    yTemp *= -1;
                }
                Double x = xTemp.doubleValue();
                Double y = yTemp.doubleValue();
                
                individu.setKromosom(j, new Point(x,y));
            }
        }
    }
}