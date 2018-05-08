
import java.io.*;
import java.util.*;

public class Main {

    static final String pathProject = ".";
    static final String pathData = ".." + File.separator + ".." + File.separator
            + "__data" + File.separator + "yi_qing";

    static final String pathTrain = pathData + File.separator + "train";
    static final String pathTrain_0 = pathTrain + File.separator + "rot_0";
    //static final String pathTrain_0 = pathTrain + File.separator + "rot_random";

    static final String pathTest = pathData + File.separator + "test";
    static final String pathTest_0 = pathTest + File.separator + "rot_0";
    //static final String pathTest_0 = pathTest + File.separator + "rot_random";

    static final String pathInput = pathProject + File.separator + "inputs";
    static final String pathOutput = pathProject + File.separator + "outputs";
    static final String pathWeakClass = pathOutput + File.separator + "20180322_cga_2.wk";

    static final String face = File.separator + "face";
    static final String non_face = File.separator + "non-face";

    static final int NUM_FEATURE = 107219;
    static final int NUM_FEATURE_SEL = 5000;
    static final int NUM_TYPE = 5;

    static final int NUM_WEAKCLASS = 200;

    static final double EPS = 1e-9;

    static Random rnd = new Random();

    static ArrayList<int[][]> lstImgPos = null;
    static ArrayList<int[][]> lstImgNeg = null;

    static ArrayList<int[][]> lstIntPos = null;
    static ArrayList<int[][]> lstIntNeg = null;

    static ArrayList<int[][]> lstType = null;
    static IndexObj[] lstIdxObj = null;

    public static void main(String[] args) {
        //testSample();
        //trainProcess();
        testProcess();
    }

    public static void testProcess() {
        lstImgPos = new ArrayList<>();
        lstImgPos.addAll(ImageProcessing.readInputImage(pathTest_0 + face));
        System.out.println(lstImgPos.size());

        lstImgNeg = new ArrayList<>();
        lstImgNeg.addAll(ImageProcessing.readInputImage(pathTest_0 + non_face));
        System.out.println(lstImgNeg.size());

        prepareIntegralImage();
        prepareFeatureIndex();

        WeakClass[] arrWeakClass = DataUtils.loadWeakClass(pathWeakClass);
        /*
        for (int len = 60; len <= 100; len += 10) {
            System.out.println("Length: " + len);
            double rate = 0.2;
            while (rate < 0.7) {
                testAdaBoost(arrWeakClass, len, rate);
                rate += 0.01;
            }
            System.out.println("");
        }
         */
        double rate = 0.2;
        while (rate < 0.7) {
            testAdaBoost(arrWeakClass, arrWeakClass.length, rate);
            rate += 0.01;
        }
    }

    public static void trainProcess() {
        lstImgPos = new ArrayList<>();
        lstImgPos.addAll(ImageProcessing.readInputImage(pathTrain_0 + face));
        System.out.println(lstImgPos.size());

        lstImgNeg = new ArrayList<>();
        lstImgNeg.addAll(ImageProcessing.readInputImage(pathTrain_0 + non_face));
        System.out.println(lstImgNeg.size());

        prepareIntegralImage();
        prepareFeatureIndex();

        WeakClass[] wk = algorithmAdaboost();
        DataUtils.saveArrWeakClass(wk, pathWeakClass);
    }

    public static void testAdaBoost(WeakClass[] arrWeakClass, int len, double rate) {
        int num_pos = lstImgPos.size();
        int num_neg = lstImgNeg.size();

        int TP = 0;
        for (int imgIdx = 0; imgIdx < num_pos; imgIdx++) {
            double sumAlpha = 0.;
            double alpha = 0.;
            for (int wkIdx = 0; wkIdx < len; wkIdx++) {
                Feature ft = arrWeakClass[wkIdx].fi;
                int idx1 = ft.getIdx1();
                int idx2 = ft.getIdx2();

                double[] x = {getFeatureValueSingle(lstIntPos.get(imgIdx), idx1),
                    getFeatureValueSingle(lstIntPos.get(imgIdx), idx2)};

                double x_mag2 = MatrixUtils.dot_product(x, x);
                double gauss_p = findGaussDist(x, x_mag2, ft.getS_p(),
                        ft.getLambda_p(), ft.getS_inf_p(), ft.getS_0_p());
                double gauss_n = findGaussDist(x, x_mag2, ft.getS_n(),
                        ft.getLambda_n(), ft.getS_inf_n(), ft.getS_0_n());

                sumAlpha += arrWeakClass[wkIdx].alpha;
                if (gauss_p > gauss_n) {
                    alpha += arrWeakClass[wkIdx].alpha;
                }
            }

            if (alpha >= sumAlpha * rate) {
                TP++;
            }
        }

        int TN = 0;
        for (int imgIdx = 0; imgIdx < num_neg; imgIdx++) {
            double sumAlpha = 0.;
            double alpha = 0.;
            for (int wkIdx = 0; wkIdx < len; wkIdx++) {
                Feature ft = arrWeakClass[wkIdx].fi;
                int idx1 = ft.getIdx1();
                int idx2 = ft.getIdx2();

                double[] x = {getFeatureValueSingle(lstIntNeg.get(imgIdx), idx1),
                    getFeatureValueSingle(lstIntNeg.get(imgIdx), idx2)};

                double x_mag2 = MatrixUtils.dot_product(x, x);
                double gauss_p = findGaussDist(x, x_mag2, ft.getS_p(),
                        ft.getLambda_p(), ft.getS_inf_p(), ft.getS_0_p());
                double gauss_n = findGaussDist(x, x_mag2, ft.getS_n(),
                        ft.getLambda_n(), ft.getS_inf_n(), ft.getS_0_n());

                sumAlpha += arrWeakClass[wkIdx].alpha;
                if (gauss_p > gauss_n) {
                    alpha += arrWeakClass[wkIdx].alpha;
                }
            }

            if (alpha < sumAlpha * rate) {
                TN++;
            }
        }

        double TPR = 100. * TP / num_pos;
        double FPR = 100. * (1 - 1. * TN / num_neg);
        System.out.printf("%.3f %.3f %.3f\n", rate, TPR, FPR);

        /*
        //System.out.printf("Detection Rate: %.3f\n", TPR);
        //System.out.printf("False Postive:  %.3f\n", FPR);
        double err = 100. - 100.*(TP + TN) / (num_pos + num_neg);
        System.out.printf("%4d %.3f\n", len, err);
         */
    }

    public static double findGaussDist(double[] x, double x_mag2, double[][] s,
            double[] lambda, double[] s_inf, double[] s_0) {
        double[] dist = new double[s.length];
        for (int j = 0; j < s.length; j++) {
            dist[j] = MatrixUtils.dot_product(x, s[j])
                    - s_inf[j]
                    - 0.5 * x_mag2 * s_0[j];
        }

        double res = 1.;
        for (int j = 0; j < s.length; j++) {
            double p = -dist[j] * dist[j] / (2 * lambda[j]);
            double den = Math.sqrt(2 * Math.PI * lambda[j]);
            res *= Math.pow(Math.E, p) / den;
        }

        return res;
    }

    public static WeakClass[] algorithmAdaboost() {
        ArrayList<WeakClass> lstWeakClass = new ArrayList<>();

        try {
            PrintWriter writer = new PrintWriter("20180321_cga_2.txt", "UTF-8");

            writer.println("BEGIN: algorithmAdaboost");
            System.out.println("BEGIN: algorithmAdaboost");
            int num_pos = lstImgPos.size();
            int num_neg = lstImgNeg.size();

            double[] w_p = new double[num_pos];
            double[] w_n = new double[num_neg];
            for (int i = 0; i < num_pos; i++) {
                w_p[i] = 0.5 / num_pos;
            }

            for (int i = 0; i < num_neg; i++) {
                w_n[i] = 0.5 / num_neg;
            }

            for (int wkIdx = 0; wkIdx < NUM_WEAKCLASS; wkIdx++) {
                long tStart = new Date().getTime();
                writer.printf("WeakClass: %d\n", wkIdx);
                System.out.printf("WeakClass: %d\n", wkIdx);

                double sumWeight = 0.;
                for (int i = 0; i < num_pos; i++) {
                    sumWeight += w_p[i];
                }

                for (int i = 0; i < num_neg; i++) {
                    sumWeight += w_n[i];
                }

                for (int i = 0; i < num_pos; i++) {
                    w_p[i] /= sumWeight;
                }

                for (int i = 0; i < num_neg; i++) {
                    w_n[i] /= sumWeight;
                }

                Feature best = getFeatureIndex(w_p, w_n);
                writer.printf("Index: %d %d\n", best.getIdx1(), best.getIdx1());
                writer.printf("Error: %.6f\n", best.getMinError());
                writer.printf("Cnt: %d %d\n", best.getCnt_p(), best.getCnt_n());
                best.printIndex();
                best.printError();
                best.printCnt();

                double error = best.getMinError();
                if (0.5 - error < EPS || Math.abs(error - 0.5) < EPS) {
                    continue;
                }

                double beta = error / (1 - error);
                double alpha = Math.log(1 / beta);
                writer.println("Alpha: " + alpha);
                System.out.println("Alpha: " + alpha);

                // save new weak class
                lstWeakClass.add(new WeakClass(best, alpha));

                for (int imgIdx = 0; imgIdx < num_pos; imgIdx++) {
                    int e_i = best.getHt_p()[imgIdx] == 1 ? 0 : 1;
                    w_p[imgIdx] *= Math.pow(beta, 1 - e_i);
                }

                for (int imgIdx = 0; imgIdx < num_neg; imgIdx++) {
                    int e_i = best.getHt_n()[imgIdx] == 0 ? 0 : 1;
                    w_n[imgIdx] *= Math.pow(beta, 1 - e_i);
                }

                best.setHt_p(null);
                best.setHt_n(null);

                long tEnd = new Date().getTime();
                writer.printf("Time: %.3f\n\n", (tEnd - tStart) / 1000.);
                System.out.printf("Time: %.3f\n\n", (tEnd - tStart) / 1000.);
            }
            
            writer.println("END:   algorithmAdaboost");
            System.out.println("END:   algorithmAdaboost");
            writer.printf("Num of WeakClas: %d\n", lstWeakClass.size());
            System.out.printf("Num of WeakClas: %d\n", lstWeakClass.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return DataUtils.arrayListToArray(lstWeakClass);
    }

    public static Feature getFeatureIndex(double[] w_p, double[] w_n) {
        int num_pos = lstImgPos.size();
        int num_neg = lstImgNeg.size();

        Double minErr1 = Double.MAX_VALUE;
        int minIdx1 = -1;
        double[][] x1_p = null;
        double[][] x1_n = null;

        int[] rndIdx = generateIndex(NUM_FEATURE, NUM_FEATURE_SEL);
        for (int _x = 0; _x < rndIdx.length; _x++) {
            int idx1 = rndIdx[_x];

            double[] t_p = getFeatureValue(lstIntPos, idx1);
            double[] t_n = getFeatureValue(lstIntNeg, idx1);
            double[][] x_p = new double[num_pos][1];
            double[][] x_n = new double[num_neg][1];
            for (int i = 0; i < num_pos; i++) {
                x_p[i][0] = t_p[i];
            }

            for (int i = 0; i < num_neg; i++) {
                x_n[i][0] = t_n[i];
            }

            CGAUtils cga_p = new CGAUtils(x_p, w_p);
            CGAUtils cga_n = new CGAUtils(x_n, w_n);

            double err = 0.;
            for (int i = 0; i < num_pos; i++) {
                double gauss_p = cga_p.findGaussDist(i, cga_p);
                double gauss_n = cga_p.findGaussDist(i, cga_n);

                if (gauss_p < gauss_n) {
                    err += w_p[i];
                }
            }

            for (int i = 0; i < num_neg; i++) {
                double gauss_p = cga_n.findGaussDist(i, cga_p);
                double gauss_n = cga_n.findGaussDist(i, cga_n);

                if (gauss_n < gauss_p) {
                    err += w_n[i];
                }
            }

            if (err < minErr1) {
                minErr1 = err;
                minIdx1 = idx1;

                x1_p = x_p;
                x1_n = x_n;
            }
        }

        Double minErr = Double.MAX_VALUE;
        Feature best = new Feature(minIdx1, -1);
        double[][] dfk_p = new double[num_pos][2];
        double[][] dfk_n = new double[num_neg][2];
        for (int i = 0; i < num_pos; i++) {
            dfk_p[i][0] = x1_p[i][0];
            dfk_n[i][0] = x1_n[i][0];
        }

        rndIdx = generateIndex(NUM_FEATURE, NUM_FEATURE_SEL);
        for (int _x = 0; _x < rndIdx.length; _x++) {
            int idx2 = rndIdx[_x];
            if (idx2 == minIdx1)
                continue;

            double[] t_p = getFeatureValue(lstIntPos, idx2);
            double[] t_n = getFeatureValue(lstIntNeg, idx2);
            for (int i = 0; i < num_pos; i++) {
                dfk_p[i][1] = t_p[i];
            }

            for (int i = 0; i < num_neg; i++) {
                dfk_n[i][1] = t_n[i];
            }

            CGAUtils cga_p = new CGAUtils(dfk_p, w_p);
            CGAUtils cga_n = new CGAUtils(dfk_n, w_n);

            int cnt_p = 0;
            int[] ht_p = new int[num_pos];
            double err = 0.;
            for (int i = 0; i < num_pos; i++) {
                double gauss_p = cga_p.findGaussDist(i, cga_p);
                double gauss_n = cga_p.findGaussDist(i, cga_n);

                ht_p[i] = 1;
                if (gauss_p < gauss_n) {
                    ht_p[i] = 0;
                    err += w_p[i];
                    cnt_p++;
                }
            }

            int cnt_n = 0;
            int[] ht_n = new int[num_neg];
            for (int i = 0; i < num_neg; i++) {
                double gauss_p = cga_n.findGaussDist(i, cga_p);
                double gauss_n = cga_n.findGaussDist(i, cga_n);

                ht_n[i] = 0;
                if (gauss_n < gauss_p) {
                    ht_n[i] = 1;
                    err += w_n[i];
                    cnt_n++;
                }
            }

            if (err < minErr) {
                minErr = err;

                best.setCnt_p(cnt_p);
                best.setCnt_n(cnt_n);
                best.setIdx2(idx2);

                best.setMinError(minErr);
                best.setHt_p(ht_p);
                best.setHt_n(ht_n);

                best.setLambda_p(cga_p.getLambda());
                best.setS_p(cga_p.getS());
                best.setS_inf_p(cga_p.getS_inf());
                best.setS_0_p(cga_p.getS_0());

                best.setLambda_n(cga_n.getLambda());
                best.setS_n(cga_n.getS());
                best.setS_inf_n(cga_n.getS_inf());
                best.setS_0_n(cga_n.getS_0());
            }
        }

        return best;
    }

    public static double[] getFeatureValue(ArrayList<int[][]> lstInt, int idx) {
        int t = lstIdxObj[idx].getType() - 1;
        int x = lstIdxObj[idx].getX();
        int y = lstIdxObj[idx].getY();
        int h = lstIdxObj[idx].getH();
        int w = lstIdxObj[idx].getW();

        double[] ftValue = new double[lstInt.size()];
        FeatureTemplate ftemp = new FeatureTemplate(lstType.get(t));
        for (int i = 0; i < lstInt.size(); i++) {
            ftValue[i] = ftemp.getFeatureValue(
                    lstInt.get(i), x, y, x + h - 1, y + w - 1);
        }

        return ftValue;
    }

    public static double getFeatureValueSingle(int[][] imgInt, int idx) {
        int t = lstIdxObj[idx].getType() - 1;
        int x = lstIdxObj[idx].getX();
        int y = lstIdxObj[idx].getY();
        int h = lstIdxObj[idx].getH();
        int w = lstIdxObj[idx].getW();

        FeatureTemplate ftemp = new FeatureTemplate(lstType.get(t));
        return ftemp.getFeatureValue(
                imgInt, x, y, x + h - 1, y + w - 1);
    }

    public static int[] generateIndex(int total, int sel) {
        int[] arrIndex = new int[sel];
        for (int i = 0; i < sel; i++) {
            int idx = rnd.nextInt(total);

            arrIndex[i] = idx;
        }

        return arrIndex;
    }

    public static void prepareIntegralImage() {
        lstIntPos = new ArrayList<>();
        for (int[][] img : lstImgPos) {
            lstIntPos.add(ImageProcessing.getIntegralImage(img));
        }

        lstIntNeg = new ArrayList<>();
        for (int[][] img : lstImgNeg) {
            lstIntNeg.add(ImageProcessing.getIntegralImage(img));
        }
    }

    public static void prepareFeatureIndex() {
        lstType = DataUtils.readTypePattern(pathInput, NUM_TYPE);
        lstIdxObj = DataUtils.readIndex(pathInput, NUM_FEATURE);
    }

}
