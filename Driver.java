import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Driver {
  public static void main(String[] args) {
    System.out.println("Chimera - Bond Yield Calculator\n");
    BondYieldCalculator calc = new BondYieldCalculator();

    System.out.println("=====================  Calc Price  =====================");
    // coupon, years, face, rate
    printResults(calc.CalcPrice(0.10, 5, 1000, 0.15), 832.3922451);
    printResults(calc.CalcPrice(0.15, 5, 1000, 0.15), 1000.0000000);
    printResults(calc.CalcPrice(0.10, 5, 1000, 0.08), 1079.8542007);

    printResults(calc.CalcPrice(0.0, 10, 500, 0.10), 192.7716447);
    printResults(calc.CalcPrice(0.10, 10, 500, 0.0), 1000.0000000);
    printResults(calc.CalcPrice(0.10, 0, 500, 0.10), 500.0000000);
    printResults(calc.CalcPrice(0.10, 10, 0.0, 0.10), 0.0000000);


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

}
