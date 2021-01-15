import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Driver {
  public static void main(String[] args) {
    long startTime;
    long endTime;

    System.out.println("Chimera - Bond Yield Calculator\n");
    BondYieldCalculator calc = new BondYieldCalculator();

    System.out.println("=====================  Calc Price  =====================");
    // coupon, years, face, rate
    startTime = System.currentTimeMillis();
    printResults(calc.CalcPrice(0.10, 5, 1000, 0.15), 832.3922451);
    printResults(calc.CalcPrice(0.15, 5, 1000, 0.15), 1000.0000000);
    printResults(calc.CalcPrice(0.10, 5, 1000, 0.08), 1079.8542007);

    printResults(calc.CalcPrice(0.0, 10, 500, 0.10), 192.7716447);
    printResults(calc.CalcPrice(0.10, 10, 500, 0.0), 1000.0000000);
    printResults(calc.CalcPrice(0.10, 0, 500, 0.10), 500.0000000);
    printResults(calc.CalcPrice(0.10, 10, 0.0, 0.10), 0.0000000);
    endTime = System.currentTimeMillis();
    timeDiff(startTime, endTime);

    System.out.println("=====================  Calc Yield  =====================");
    // coupon, years, face, price
    // printResults(calc.CalcYield(0.10, 5, 1000, 832.4), 0.1499974);
    // printResults(calc.CalcYield(0.10, 5, 1000, 1000), 0.1000000);
    // printResults(calc.CalcYield(0.10, 5, 1000, 1079.85), 0.080010);
    HashMap<List, HashMap<Integer, Double>> memo = new HashMap<List, HashMap<Integer, Double>>();
    HashMap<Integer, Double> yearPriceMap = new HashMap<Integer, Double>();
    memo.put(Arrays.asList(0.10, 1000.0, 0.15), yearPriceMap);
    System.out.println(memo.containsKey(Arrays.asList(0.10, 1000.0, 0.15)));
    HashMap<Integer, Double> temp = memo.get(Arrays.asList(0.10, 1000.0, 0.15));
    System.out.println(temp);
    temp.put(0, 1000.0);
    System.out.println(memo.get(Arrays.asList(0.10, 1000.0, 0.15)).get(0));
  }

  public static void printResults(double result, double expected) {
    if (result == expected) {
      System.out.print("O\t");
    } else{
      System.out.print("X\t");
    }
    BigDecimal res = new BigDecimal(result);
    BigDecimal exp = new BigDecimal(expected);
    System.out.printf("Result: %13s\tExpected: %13s\n", res.setScale(7, RoundingMode.HALF_UP), exp.setScale(7, RoundingMode.HALF_UP));
  }

  public static void timeDiff(long startTime, long endTime) {
    System.out.printf("Execution time: %dms\n\n", endTime - startTime);
  }
}
