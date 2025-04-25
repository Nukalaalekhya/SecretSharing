import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
import org.json.JSONObject;

public class SecretSharing {

    public static void main(String[] args) throws Exception {
        // Load both test cases
        JSONObject testCase1 = new JSONObject(Files.readString(Paths.get("testcase1.json")));
        JSONObject testCase2 = new JSONObject(Files.readString(Paths.get("testcase2.json")));

        BigInteger secret1 = findSecret(testCase1);
        BigInteger secret2 = findSecret(testCase2);

        System.out.println("Secret 1: " + secret1);
        System.out.println("Secret 2: " + secret2);
    }

    private static BigInteger findSecret(JSONObject testCase) {
        JSONObject keys = testCase.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        List<BigInteger> xList = new ArrayList<>();
        List<BigInteger> yList = new ArrayList<>();

        // Collect first k points
        int count = 0;
        for (String key : testCase.keySet()) {
            if (key.equals("keys")) continue;

            int x = Integer.parseInt(key);
            JSONObject point = testCase.getJSONObject(key);
            int base = point.getInt("base");
            String value = point.getString("value");
            BigInteger y = new BigInteger(value, base);

            xList.add(BigInteger.valueOf(x));
            yList.add(y);

            count++;
            if (count >= k) break;
        }

        // Perform Lagrange interpolation at x = 0 to find constant term
        return lagrangeInterpolation(BigInteger.ZERO, xList, yList);
    }

    // Lagrange interpolation to evaluate f(x) at a given point (here x = 0)
    private static BigInteger lagrangeInterpolation(BigInteger x, List<BigInteger> xi, List<BigInteger> yi) {
        BigInteger result = BigInteger.ZERO;
        int k = xi.size();

        for (int i = 0; i < k; i++) {
            BigInteger term = yi.get(i);
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger numerator = x.subtract(xi.get(j));
                    BigInteger denominator = xi.get(i).subtract(xi.get(j));
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            result = result.add(term);
        }

        return result;
    }
}
