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
}
