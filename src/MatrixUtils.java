public class MatrixUtils {
    public static double dot_product(double[] u, double[] v) {
        double res = 0.;
        for (int i = 0; i < u.length; i++) {
            res += u[i] * v[i];
        }
        
        return res;
    }
   
    public static double[] add(double[] u, double[] v) {
        double[] res = new double[u.length];
        
        for (int i = 0; i < u.length; i++) {
            res[i] = u[i] + v[i];
        }
        
        return res;
    }
    
    public static double[] subtract(double[] u, double[] v) {
        double[] res = new double[u.length];
        
        for (int i = 0; i < u.length; i++) {
            res[i] = u[i] - v[i];
        }
        
        return res;
    }
    
    public static double[] multiply_scalar(double[] u, double scalar) {
        double[] res = new double[u.length];
        for (int i = 0; i < u.length; i++) {
            res[i] = u[i] * scalar;
        }
        
        return res;
    }
    
    public static double[][] multiply_mat(double[] u, double[] v) {
        double[][] res = new double[u.length][v.length];
        int N = u.length;
        int M = v.length;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                res[i][j] = u[i] * v[j];
            }
        }
        
        return res;
    }
    
    public static double[][] multiply_mat_scalar(double[][] a, double scalar) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                a[i][j] *= scalar;
            }
        }
        return a;
    }
    
    public static double[][] add_mat(double[][] u, double[][] v) {
        double[][] res = new double[u.length][u[0].length];
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[0].length; j++) {
                res[i][j] = u[i][j] + v[i][j];
            }
        }
        
        return res;
    }
    
    public static double[][] initial_mat(int R, int C) {
        double[][] res = new double[R][C];
        for (int i = 0; i < R; i++) {
            for (int j = 0; j < C; j++) {
                res[i][j] = 0.;
            }
        }
        
        return res;
    }
}
