
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class DataUtils {

    public static IndexObj[] readIndex(String path, int num_feature) {
        String pathLookUpTable = path + File.separator + "lookUpTable.txt";
        File file = new File(pathLookUpTable);

        IndexObj[] arrIdxObj = new IndexObj[num_feature];
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            for (int i = 0; i < num_feature; i++) {
                String line = br.readLine();
                Scanner sc = new Scanner(line);
                byte type = sc.nextByte();
                byte x = sc.nextByte();
                byte y = sc.nextByte();
                byte h = sc.nextByte();
                byte w = sc.nextByte();

                IndexObj idxObj = new IndexObj(type, x, y, h, w);
                arrIdxObj[i] = idxObj;
            }
            br.close();
        } catch (IOException e) {

        }

        return arrIdxObj;
    }

    public static ArrayList<int[][]> readTypePattern(String path, int num_type) {
        ArrayList<int[][]> lst = new ArrayList<>();

        for (int type = 1; type <= num_type; type++) {
            String pathType = path + File.separator + "type" + type + ".txt";
            File file = new File(pathType);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();

                Scanner sc = new Scanner(line);
                int H = sc.nextInt();
                int W = sc.nextInt();

                int[][] pattern = new int[H][W];
                for (int i = 0; i < H; i++) {
                    line = br.readLine();
                    sc = new Scanner(line);
                    for (int j = 0; j < W; j++) {
                        pattern[i][j] = sc.nextInt();
                    }
                }
                lst.add(pattern);
                br.close();
            } catch (IOException e) {
            }
        }

        return lst;
    }

    public static WeakClass[] arrayListToArray(ArrayList<WeakClass> lst) {
        WeakClass[] arr = new WeakClass[lst.size()];

        for (int idx = 0; idx < lst.size(); idx++) {
            arr[idx] = lst.get(idx);
        }

        return arr;
    }

    public static void saveArrWeakClass(WeakClass[] arr, String path) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(path));
            try (ObjectOutputStream out = new ObjectOutputStream(fos)) {
                out.writeObject(arr);

                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static WeakClass[] loadWeakClass(String path) {
        WeakClass[] arr = null;

        try {
            FileInputStream fis = new FileInputStream(new File(path));
            ObjectInputStream in = new ObjectInputStream(fis);
            arr = (WeakClass[]) in.readObject();

            in.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return arr;
    }
    
    public static void saveData1D(String pathFile, double[] arr) {
        try {
            PrintWriter printWriter = new PrintWriter(new File(pathFile));
            for (int i = 0; i < arr.length; i++) {
                printWriter.printf("%.6f\n", arr[i]);
            }
            
        } catch (FileNotFoundException e) {
            
        }
    }
    
    public static void saveData2D(String pathFile, double[][] arr) {
        try {
            PrintWriter printWriter = new PrintWriter(new File(pathFile));
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    printWriter.printf("%.6f ", arr[i][j]);
                }
                printWriter.println();
            }
            
        } catch (FileNotFoundException e) {
            
        }
    }
}
