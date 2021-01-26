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
    printResults(calc.CalcPrice(0.10, 5, 1000, 0.15), 832.3922451, 0.0);
    printResults(calc.CalcPrice(0.15, 5, 1000, 0.15), 1000.0000000, 0.0);
    printResults(calc.CalcPrice(0.10, 5, 1000, 0.08), 1079.8542007, 0.0);

    printResults(calc.CalcPrice(0.0, 10, 500, 0.10), 192.7716447, 0.0);
    printResults(calc.CalcPrice(0.10, 10, 500, 0.0), 1000.0000000, 0.0);
    printResults(calc.CalcPrice(0.10, 0, 500, 0.10), 500.0000000, 0.0); // check 0 year edge case
    printResults(calc.CalcPrice(0.10, 10, 0.0, 0.10), 0.0000000, 0.0);
    endTime = System.currentTimeMillis();
    timeDiff(startTime, endTime);

    System.out.println("=====================  Calc Yield  =====================");
    // coupon, years, face, price
    startTime = System.currentTimeMillis();

    printResults(calc.CalcYield(0.10, 5, 1000, 832.4), 0.1499974, 0.00001);
    printResults(calc.CalcYield(0.10, 5, 1000, 1000), 0.1000000, 0.00001);
    printResults(calc.CalcYield(0.10, 5, 1000, 1079.85), 0.080010, 0.00001);

    printResults(calc.CalcYield(0.0, 10, 500, 192.7716447), 0.10, 0.00001);
    printResults(calc.CalcYield(0.10, 10, 500, 1000.0), 0.0, 0.00001);
    printResults(calc.CalcYield(0.10, 0, 500, 500.0), 0.0, 0.00001); // ?
    endTime = System.currentTimeMillis();
    timeDiff(startTime, endTime);


    System.out.println("======================= Automate =======================");
    randomizedTests(5000, calc);
    // calc.CalcYield(0.078680, 7, 48912.364471, 978454.949250);
    // System.out.println(calc.CalcPrice(0.10, 5, 1000, -1.15));

    // System.out.println(calc.CalcPrice(0.10, 5, 1000, -0.15));
    // System.out.println(calc.CalcYield(0.10, 5, 1000, 3089.580147859961));
    // System.out.println(calc.CalcPrice(0.10, 6, 1000, -0.15));
    // System.out.println(calc.CalcYield(0.10, 6, 1000, 3752.447232776425));
    // System.out.println(calc.CalcPrice(0.10, 6, 1000, -1.15));
    // System.out.println(calc.CalcPrice(0.10, 6, 1000, -0.8493001111279779));
    // System.out.println(calc.CalcYield(0.10, 6, 1000, 95425451.303315535));

    // System.out.println(calc.CalcYield(0.948123, 62, 441301.903208, 21122.239053));
    // System.out.println(calc.CalcPrice(0.948123, 62, 441301.903208, 19.80843));
    // System.out.println(calc.CalcPrice(0.948123, 62, 441301.903208, -2.0169683933461027));

    // System.out.println(calc.CalcYield(0.682333, 61, 989325.664565, 75064.401150));
    // System.out.println(calc.CalcYield(0.591907, 86, 66494.903672, 920862.257603));
    // System.out.println(calc.CalcYield(0.190328, 2, 130094.786234, 446515.333954));
  }

  public static void printResults(double result, double expected, double error) {
    if (Math.abs(result - expected) <= error) {
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

  public static void randomizedTests(int num, BondYieldCalculator calc) {
    long startTime;
    long endTime;
    Random rand = new Random();

    double coupon, face, rate, price;
    int years;
    boolean noErrors = true;

    System.out.println("5000 CalcPrice calls on random values");
    startTime = System.currentTimeMillis();
    for (int n = 0; n < num; n++) {
      coupon = rand.nextDouble();
      years = rand.nextInt(100);
      face = rand.nextDouble() * 1000000;
      rate = rand.nextDouble() * 5;
      calc.CalcPrice(coupon, years, face, rate);
      // System.out.println(calc.CalcPrice(coupon, years, face, rate));
    }
    endTime = System.currentTimeMillis();
    timeDiff(startTime, endTime);

    System.out.println("5000 CalcYield calls on random values");
    startTime = System.currentTimeMillis();
    for (int n = 0; n < num; n++) {
      coupon = rand.nextDouble();
      years = rand.nextInt(100) + 1;
      face = rand.nextDouble() * 1000000;
      price = rand.nextDouble() * 1000000;
      rate = calc.CalcYield(coupon, years, face, price);
      double p = calc.CalcPrice(coupon, years, face, rate);
      System.out.printf("calc.CalcYield(%f, %d, %f, %f);\n", coupon, years, face, price);
      // System.out.println(rate);
      // System.out.println("" + p + " " + price);
      noErrors = Math.abs(p - price) < 0.0000001 || Double.isNaN(rate);
      // System.out.println(n);
      // System.out.printf("calc.CalcYield(%f, %d, %f, %f);\n", coupon, years, face, price);
      if (!noErrors) {
        System.out.println(rate);
      }
    }
    endTime = System.currentTimeMillis();
    timeDiff(startTime, endTime);
  }
}
