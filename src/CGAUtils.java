
import org.jblas.*;

public class CGAUtils {

    private double[][] x = null;
    private double[] x_mag2 = null;
    private double sum2;
    private double sum4;

    private double[] f_inf = null;
    private double[] f_0 = null;
    private double[][] f = null;

    private double[][] A = null;

    private double[] lambda = null;
    private double[][] s = null;
    private double[] s_inf = null;
    private double[] s_0 = null;


    public CGAUtils(double[][] x, double[] w) {
        this.x = new double[x.length][x[0].length];
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {            
                this.x[i][j] = x[i][j];
            }
        }

    	this.x_mag2 = find_x_mag2(this.x);
    	this.sum2 = find_sum2(this.x_mag2, w);
    	this.sum4 = find_sum4(this.x_mag2, w);

    	this.f_inf = find_f_inf(this.x, this.x_mag2, this.sum2, this.sum4, w);
    	this.f_0 = find_f_0(this.x, this.x_mag2, this.sum2, this.sum4, w);
    	this.f = find_f(this.x, this.x_mag2, this.f_inf, this.f_0, w);

    	this.A = find_A(this.f, w);

    	ComplexDoubleMatrix[] dummy = Eigen.eigenvectors(new DoubleMatrix(this.A));
        double[][] tmp = dummy[1].real().toArray2();
        this.lambda = new double[tmp.length];
        for (int i = 0; i < tmp.length; i++)
            lambda[i] = tmp[i][i];
        
        this.s = dummy[0].real().transpose().toArray2();

        this.s_inf = new double[this.s.length];
        this.s_0 = new double[this.s.length];
        for (int i = 0; i < this.s.length; i++) {
            this.s_inf[i] = MatrixUtils.dot_product(this.f_inf, this.s[i]);
            this.s_0[i] = 2 * MatrixUtils.dot_product(this.f_0, this.s[i]);
        }
    }

    private double[] find_x_mag2(double[][] x) {
        double[] x_mag2 = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            x_mag2[i] = MatrixUtils.dot_product(x[i], x[i]);
        }

        return x_mag2;
    }

    private double find_sum2(double[] x_mag2, double[] w) {
        double res = 0.;
        for (int i = 0; i < x_mag2.length; i++) {
            res += x_mag2[i] * w[i];
        }

        return res;
    }

    private double find_sum4(double[] x_mag2, double[] w) {
        double res = 0.;
        for (int i = 0; i < x_mag2.length; i++) {
            res += x_mag2[i] * x_mag2[i] * w[i];
        }

        return res;
    }

    private double[] find_f_inf(double[][] x, double[] x_mag2, double sum2, double sum4, double[] w) {
        int n = x.length;
        double sW = 0.;
        for (int i = 0; i < w.length; i++)
            sW += w[i];

        double[] tmp1 = new double[x[0].length];        
        for (int i = 0; i < n; i++) {
            tmp1 = MatrixUtils.add(tmp1, MatrixUtils.multiply_scalar(x[i], w[i]));
        }
        tmp1 = MatrixUtils.multiply_scalar(tmp1, -sum4);

        double[] tmp2 = new double[x[0].length];
        for (int i = 0; i < n; i++) {
            tmp2 = MatrixUtils.add(tmp2, MatrixUtils.multiply_scalar(x[i], w[i] * x_mag2[i]));
        }
        tmp2 = MatrixUtils.multiply_scalar(tmp2, sum2);

        tmp1 = MatrixUtils.add(tmp1, tmp2);

        double den = sum2 * sum2 - sW * sum4;
        tmp1 = MatrixUtils.multiply_scalar(tmp1, 1 / den);

        return tmp1;
    }

    private double[] find_f_0(double[][] x, double[] x_mag2, double sum2, double sum4, double[] w) {
        int n = x.length;
        double sW = 0.;
        for (int i = 0; i < w.length; i++)
            sW += w[i];

        double[] tmp1 = new double[x[0].length];        
        for (int i = 0; i < n; i++) {
            tmp1 = MatrixUtils.add(tmp1, MatrixUtils.multiply_scalar(x[i], w[i]));
        }
        tmp1 = MatrixUtils.multiply_scalar(tmp1, sum2);

        double[] tmp2 = new double[x[0].length];        
        for (int i = 0; i < n; i++) {
            tmp2 = MatrixUtils.add(tmp2, MatrixUtils.multiply_scalar(x[i], x_mag2[i] * w[i]));

        }
        tmp2 = MatrixUtils.multiply_scalar(tmp2, -sW);

        tmp1 = MatrixUtils.add(tmp1, tmp2);
        double den = sum2 * sum2 - sW * sum4;

        tmp1 = MatrixUtils.multiply_scalar(tmp1, 1 / den);
        return tmp1;
    }

    private double[][] find_f(double[][] x, double[] x_mag2, double[] f_inf, double[] f_0, double[] w) {
        double[][] f = new double[x.length][x[0].length];

        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                f[i][j] = x[i][j] - f_inf[j] - f_0[j] * x_mag2[i];
            }
        }

        return f;
    }

    private double[][] find_A(double[][] f, double[] w) {
        int n = f.length;
        double[][] A = new double[f[0].length][f[0].length];

        for (int i = 0; i < n; i++) {
            double[][] tmp = MatrixUtils.multiply_mat_scalar(
                    MatrixUtils.multiply_mat(f[i], f[i]), w[i]);
            A = MatrixUtils.add_mat(A, tmp);
        }

        return A;
    }

    public double findGaussDist(int idx, CGAUtils obj) {
    	double[] dist = new double[obj.getS().length];
    	for (int j = 0; j < obj.getS().length; j++) {
    		dist[j] = MatrixUtils.dot_product(this.x[idx], obj.getS()[j])
                        - obj.getS_inf()[j]
                        - 0.5 * this.x_mag2[idx] * obj.getS_0()[j];
    	}

    	double res = 1.;
    	for (int j = 0; j < obj.getS().length; j++) {
    		double p = -dist[j] * dist[j] / (2 * obj.getLambda()[j]);
    		double den = Math.sqrt(2 * Math.PI * obj.getLambda()[j]);
                //double den = 1.;
    		res *= Math.pow(Math.E, p) / den;
    	}

    	return res;
    }

    public double[][] getX() {
        return x;
    }

    public double[] getLambda() {
        return lambda;
    }

    public double[][] getS() {
        return s;
    }

    public double[] getS_inf() {
        return s_inf;
    }

    public double[] getS_0() {
        return s_0;
    }

    
}
