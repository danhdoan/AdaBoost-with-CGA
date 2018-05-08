
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class ImageProcessing {

    public static int[][] readImage(String pathFile) {
        int[][] img = null;
        try {
            if (pathFile.endsWith(".png") || pathFile.endsWith(".jpg")) {
                BufferedImage buffImg = ImageIO.read(new File(pathFile));
                img = bufferedImageToArray(buffImg);
            } else if (pathFile.endsWith(".pgm")) {
                img = readPGMImage(pathFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return img;
    }

    public static int[][] readPGMImage(String pathFile) {
        int[][] img = null;
        try {
            Scanner infile = new Scanner(new FileReader(pathFile));
            String filetype = infile.nextLine();
            /*
            if (!filetype.equalsIgnoreCase("p2")) {
                System.out.println("[readPGM]Cannot load the image type of "+filetype);
                return null;
            }
             */
            int W = infile.nextInt();
            int H = infile.nextInt();
            int maxValue = infile.nextInt();
            img = new int[H+1][W+1];
            for (int r = 1; r <= H; r++) {
                for (int c = 1; c <= W; c++) {
                    int b = infile.nextInt();
                    img[r][c] = (int) (b * 255.0 / maxValue);
                }
            }

            infile.close();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
        }

        return img;
    }

    public static int[][] bufferedImageToArray(BufferedImage buffImg) {
        int W = buffImg.getWidth();
        int H = buffImg.getHeight();
        int[][] img = new int[H+1][W+1];
        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {
                img[i+1][j+1] = buffImg.getRGB(j, i);
            }
        }
        return img;
    }

    public static BufferedImage arrayToBufferedImage(int[][] img) {
        int W = img[0].length;
        int H = img.length;
        BufferedImage buffImg = new BufferedImage(W-1, H-1, BufferedImage.TYPE_INT_RGB);
        for (int i = 1; i < H; i++) {
            for (int j = 1; j < W; j++) {
                int x = img[i][j];
                buffImg.setRGB(j-1, i-1, (255 << 24) | (x << 16) |
                        (x << 8) | x);
            }
        }

        return buffImg;
    }

    public static int[][] toGrayScale(int[][] img) {
        for (int i = 0; i < img.length; i++) {
            for (int j = 0; j < img[0].length; j++) {
                int rgb = img[i][j];
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int gray = (r + g + b) / 3;
                img[i][j] = gray;
            }
        }
        return img;
    }

    public static BufferedImage rotateBufferedImage(BufferedImage bufferedImage, double degree) {
        int W = bufferedImage.getWidth();
        int H = bufferedImage.getHeight();
        BufferedImage out = new BufferedImage(W, H, bufferedImage.getType());

        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.rotate(Math.toRadians(degree), W / 2, H / 2);
        g.drawImage(bufferedImage, null, 0, 0);
        return out;
    }

    public static BufferedImage scaleBufferdImage(BufferedImage buffImg, int dWidth, int dHeight) {
        BufferedImage out = null;
        if (buffImg != null) {
            out = new BufferedImage(dWidth, dHeight, buffImg.getType());
            Graphics2D graphics2D = out.createGraphics();
            graphics2D.drawImage(buffImg, 0, 0, dWidth, dHeight, null);
            graphics2D.dispose();
        }
        return out;
    }

    public static void writeImage(String pathFile, int[][] img) {
        if (pathFile.endsWith(".png") || pathFile.endsWith(".jpg")) {
            String ext = pathFile.substring(pathFile.length() - 3);
            BufferedImage buffImg = arrayToBufferedImage(img);
            try {
                ImageIO.write(buffImg, ext, new File(pathFile));
            } catch (Exception e) {

            }
        } else if (pathFile.endsWith(".pgm")) {
            writePGMImage(pathFile, img);
        }
    }

    public static void writePGMImage(String pathFile, int[][] img) {
        try {
            FileWriter fstream = new FileWriter(pathFile);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("P2\n" + img[0].length + " " + img.length + "\n255\n");
            for (int i = 0; i < img.length; i++) {
                for (int j = 0; j < img[0].length; j++) {
                    out.write(img[i][j] + " ");
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage getBufferedImage(String pathFile) {
        BufferedImage buffImg = null;
        try {
            buffImg = ImageIO.read(new File(pathFile));
        } catch (Exception e) {
        }
        return buffImg;
    }

    public static int[][] normalizeImage(int[][] img) {
        int[] histo = new int[256];
        for (int i = 0; i < histo.length; i++) {
            histo[i] = 0;
        }

        int[] sum = new int[256];
        for (int i = 0; i < img.length; i++) {
            for (int j = 0; j < img[0].length; j++) {
                histo[img[i][j]]++;
            }
        }

        sum[0] = histo[0];
        for (int i = 1; i < histo.length; i++) {
            sum[i] = sum[i - 1] + histo[i];
        }

        int area = img.length * img[0].length;

        int[][] outImg = new int[img.length][img[0].length];
        for (int i = 0; i < outImg.length; i++) {
            for (int j = 0; j < outImg[0].length; j++) {
                outImg[i][j] = (int) (1. * 255 / area * sum[img[i][j]]);
            }
        }

        return outImg;
    }
    
    public static int[][] getIntegralImage(int[][] img) {
        int W = img[0].length;
        int H = img.length;
        int[][] dp = new int[H+1][W+1];
        
        for (int i = 1; i < H; i++){
            for (int j = 1; j < W; j++) {
                dp[i][j] = img[i][j] + dp[i-1][j] + dp[i][j-1] - dp[i-1][j-1];
            }
        }
            
        return dp;
    }

    public void printImage(int[][] img) {
        for (int r = 0; r < img.length; r++) {
            for (int c = 0; c < img[0].length; c++) {
                System.out.printf("%4d", img[r][c]);
            }
            System.out.println("");
        }
        System.out.println("");
    }
    
    public static ArrayList<int[][]> readInputImage(String path) {
        ArrayList<int[][]> lst = new ArrayList<>();
        
        File fd_face = new File(path);
        File[] files_face = fd_face.listFiles();
        
        for (File f : files_face) {
            if (f.isFile()) {
                String fileName = f.getName();
                int[][] img = readImage(path
                        + File.separator + fileName);
                
                img = toGrayScale(img);
                img = normalizeImage(img);
                lst.add(img);
            }
        }

        return lst;
    }
}
