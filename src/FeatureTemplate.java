/**
 * @file:   FeatureTemplate.java
 * @author: Thang Do
 * 
 * @description:
 *          Use Integral Image to calculate features
 *          Can apply to all types of filters
 * 
 * @note:
 *          2017-09-29: file created
 *          2017-10-01: test passed
 */
public class FeatureTemplate {

    public int width, height;
    public int[][] color;

    public FeatureTemplate(int[][] color) {
        this.height = color.length;
        this.width = color[0].length;
        this.color = new int[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(color[i], 0, this.color[i], 0, width);
        }
    }

    public double getSumPrefixRec(int[][] integral, double x, double y) {
        int i = (int) Math.floor(x);
        int j = (int) Math.floor(y);
        
        double sum = integral[i][j];
        sum += (integral[i + 1][j] - integral[i][j]) * (x - i);
        sum += (integral[i][j + 1] - integral[i][j]) * (y - j);
        sum += (integral[i + 1][j + 1] - integral[i + 1][j] - integral[i][j + 1] + integral[i][j]) * (x - i) * (y - j);
        return sum;
    }

    public double getSumRec(int[][] integral, double x1, double y1, double x2, double y2) {
        return getSumPrefixRec(integral, x2, y2)
                - getSumPrefixRec(integral, x2, y1)
                - getSumPrefixRec(integral, x1, y2)
                + getSumPrefixRec(integral, x1, y1);
    }

    public double getFeatureValue(int[][] integral, double x1, double y1, double x2, double y2) {
        double sum = 0;
        x1--;
        y1--;
        double lenH = x2 - x1;
        double lenW = y2 - y1;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sum += color[i][j] * getSumRec(integral, 
                        x1 + (i * lenH) / height, y1 + (j * lenW) / width, 
                        x1 + ((i + 1) * lenH) / height, y1 + ((j + 1) * lenW) / width);
            }
        }
        return sum;
    }

}