import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Poly {

    public static double lagrangeInterpolationAtZero(List<int[]> points) {
        double result = 0.0;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            double xi = points.get(i)[0];
            double yi = points.get(i)[1];
            double term = 1.0;

            for (int j = 0; j < n; j++) {
                if (j == i) continue;
                double xj = points.get(j)[0];
                term *= (0.0 - xj) / (xi - xj);
            }

            result += yi * term;
        }
        return result;
    }

    public static void generateCombinations(List<int[]> arr, int k, int start,
                                            List<int[]> current,
                                            List<List<int[]>> allCombos) {
        if (current.size() == k) {
            allCombos.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < arr.size(); i++) {
            current.add(arr.get(i));
            generateCombinations(arr, k, i + 1, current, allCombos);
            current.remove(current.size() - 1);
        }
    }

    public static double roundTo6(double val) {
        return Math.round(val * 1e6) / 1e6;
    }

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("testcase2.json"));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }
            reader.close();
            String json = jsonBuilder.toString();

            int n = Integer.parseInt(json.replaceAll(".*\"n\"\\s*:\\s*(\\d+).*", "$1"));
            int k = Integer.parseInt(json.replaceAll(".*\"k\"\\s*:\\s*(\\d+).*", "$1"));

            List<int[]> points = new ArrayList<>();

            String[] objects = json.split("\\},");
            for (String obj : objects) {
                obj = obj.replaceAll("[{}\\[\\]\"]", "").trim();

                if (obj.contains("keys")) continue;  

                if (obj.contains("base") && obj.contains("value")) {
                    String[] tokens = obj.split(",");

                    int x = Integer.parseInt(tokens[0].split(":")[0].trim());

                    int base = Integer.parseInt(tokens[0].split(":")[2].trim());

                    String encodedValue = tokens[1].split(":")[1].trim();

                    int y = new BigInteger(encodedValue, base).intValue();

                    points.add(new int[]{x, y});
                }
            }

            System.out.println("n = " + n + ", k = " + k);
            System.out.print("Key-Value Pairs: ");
            for (int[] p : points) {
                System.out.print("(" + p[0] + "," + p[1] + ") ");
            }
            System.out.println("\n");

            // all combinations of k
            List<List<int[]>> allCombinations = new ArrayList<>();
            generateCombinations(points, k, 0, new ArrayList<>(), allCombinations);

            // interpolation and frequency of results
            Map<Double, Integer> frequencyMap = new HashMap<>();

            System.out.println("Trying all combinations of k=" + k + " points:");
            for (List<int[]> combo : allCombinations) {
                System.out.print("Combo: ");
                for (int[] p : combo) {
                    System.out.print("(" + p[0] + "," + p[1] + ") ");
                }
                double secretCandidate = lagrangeInterpolationAtZero(combo);
                double roundedValue = roundTo6(secretCandidate);

                frequencyMap.put(roundedValue, frequencyMap.getOrDefault(roundedValue, 0) + 1);
                System.out.println("→ f(0) = " + secretCandidate);
            }

            //most frequent candidate of c
            double bestC = 0.0;
            int maxFrequency = 0;
            for (Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
                if (entry.getValue() > maxFrequency) {
                    maxFrequency = entry.getValue();
                    bestC = entry.getKey();
                }
            }

            System.out.println("\n✅ Most probable secret (constant term 'c') is: "
                    + bestC + " (appeared " + maxFrequency + " times)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


