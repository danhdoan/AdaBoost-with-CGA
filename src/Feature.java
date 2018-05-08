
import java.io.Serializable;


public class Feature implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private int idx1;
    private int idx2;
    
    private int cnt_p;
    private int cnt_n;
    private int[] ht_p;
    private int[] ht_n;
    private double minError;
    
    private double[] lambda_p = null;
    private double[][] s_p = null;
    private double[] s_inf_p = null;
    private double[] s_0_p = null;
    
    private double[] lambda_n = null;
    private double[][] s_n = null;
    private double[] s_inf_n = null;
    private double[] s_0_n = null;
    
    public Feature(int idx1, int idx2) {
        this.idx1 = idx1;
        this.idx2 = idx2;
    }

    public int getIdx1() {
        return idx1;
    }

    public void setIdx1(int idx1) {
        this.idx1 = idx1;
    }

    public int getIdx2() {
        return idx2;
    }

    public void setIdx2(int idx2) {
        this.idx2 = idx2;
    }
            
    public void printIndex() {
        System.out.printf("Index: %d %d\n", this.idx1, this.idx2);
    }
    
    public void printError() {
        System.out.printf("Error: %.6f\n", this.minError);
    }

    public int getCnt_p() {
        return cnt_p;
    }

    public void setCnt_p(int cnt_p) {
        this.cnt_p = cnt_p;
    }

    public int getCnt_n() {
        return cnt_n;
    }

    public void setCnt_n(int cnt_n) {
        this.cnt_n = cnt_n;
    }
        
    public void printCnt() {
        System.out.printf("Cnt: %d %d\n", this.cnt_p, this.cnt_n);
    }

    public double getMinError() {
        return minError;
    }

    public void setMinError(double minError) {
        this.minError = minError;
    }

    public double[] getLambda_p() {
        return lambda_p;
    }

    public void setLambda_p(double[] lambda_p) {
        this.lambda_p = lambda_p;
    }

    public double[][] getS_p() {
        return s_p;
    }

    public void setS_p(double[][] s_p) {
        this.s_p = s_p;
    }

    public double[] getS_inf_p() {
        return s_inf_p;
    }

    public void setS_inf_p(double[] s_inf_p) {
        this.s_inf_p = s_inf_p;
    }

    public double[] getS_0_p() {
        return s_0_p;
    }

    public void setS_0_p(double[] s_0_p) {
        this.s_0_p = s_0_p;
    }

    public double[] getLambda_n() {
        return lambda_n;
    }

    public void setLambda_n(double[] lambda_n) {
        this.lambda_n = lambda_n;
    }

    public double[][] getS_n() {
        return s_n;
    }

    public void setS_n(double[][] s_n) {
        this.s_n = s_n;
    }

    public double[] getS_inf_n() {
        return s_inf_n;
    }

    public void setS_inf_n(double[] s_inf_n) {
        this.s_inf_n = s_inf_n;
    }

    public double[] getS_0_n() {
        return s_0_n;
    }

    public void setS_0_n(double[] s_0_n) {
        this.s_0_n = s_0_n;
    }

    public int[] getHt_p() {
        return ht_p;
    }

    public void setHt_p(int[] ht_p) {
        this.ht_p = ht_p;
    }

    public int[] getHt_n() {
        return ht_n;
    }

    public void setHt_n(int[] ht_n) {
        this.ht_n = ht_n;
    }
    
    
}
