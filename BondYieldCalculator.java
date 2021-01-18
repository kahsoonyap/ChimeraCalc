import java.util.*;
import java.lang.Math;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BondYieldCalculator {
  private static final int DECIMAL_ACCURACY = 7;
  private static final double ACCURACY = 0.0000001;
  // key: list(CF, rate)
  // val: HashMap<year, price>
  private HashMap<List, HashMap<Integer, Double>> couponMemo;
  public BondYieldCalculator() {
    couponMemo = new HashMap<List, HashMap<Integer, Double>>();
  }

  // http://www.columbia.edu/~ks20/FE-Notes/4700-07-Notes-bonds.pdf
  // https://financeformulas.net/Yield_to_Maturity.html
  // http://phillipmfeldman.org/Python/roots/find_roots.html#:~:text=Abstract,the%20Newton%2DRaphson%20method).
  // https://www.sciencedirect.com/science/article/pii/S0893965903901194#:~:text=Standard%20text%20books%20in%20numerical,the%20secant%20method%20becomes%20linear.
  // why guess is 0+
  // https://www.investopedia.com/ask/answers/062315/what-does-negative-bond-yield-mean.asp#:~:text=Since%20the%20YTM%20calculation%20incorporates,sufficiently%20outweigh%20the%20initial%20investment.
  public double CalcYield(double coupon, int years, double face, double price) {
    if (years == 0) {
      return 0.0;
    }
    double guessA = 5.0;
    double guessB = 0.0;
    double guessC = 0.0;
    double priceA;
    double priceB;
    double priceC;
    do {
      priceA = calcPriceHelper(coupon, years, face, guessA) - price;
      priceB = calcPriceHelper(coupon, years, face, guessB) - price;

      guessC = (guessA + guessB) / 2;
      priceC = calcPriceHelper(coupon, years, face, guessC) - price;

      if (Math.abs(priceA) < ACCURACY) {
        return guessA;
      } else if (Math.abs(priceB) < ACCURACY) {
        return guessB;
      } else {
        if (priceA * priceC < 0) {
          guessB = guessC;
        } else if (priceB * priceC < 0) {
          guessA = guessC;
        }
      }
    } while (Math.abs(priceC) >= ACCURACY);

    return guessC;
  }


  public double CalcPrice(double coupon, int years, double face, double rate) {
    double rawDouble = calcPriceHelper(coupon, years, face, rate);
    BigDecimal price = new BigDecimal(rawDouble);
    price = price.setScale(DECIMAL_ACCURACY, RoundingMode.HALF_UP);

    return price.doubleValue();
  }

  private double calcPriceHelper(double coupon, int years, double face, double rate) {
    if (years == 0) {
      return face;
    }
    double principalPayment = calcPrincipalPayment(years, face, rate);
    double totalCouponPayment = calcTotalCouponPayment(coupon, years, face, rate);
    return principalPayment + totalCouponPayment;
  }

  private double calcPrincipalPayment(int years, double face, double rate) {
    double discount = calcDiscount(years, rate);

    return face / discount;
  }

  private double calcTotalCouponPayment(double coupon, int years, double face, double rate) {
    List key = Arrays.asList(coupon, face, rate);
    double cf = calcCF(coupon, face);

    if (!couponMemo.containsKey(key)) {
      HashMap newRecord = new HashMap<Integer, Double>();
      newRecord.put(0, 0.0);
      couponMemo.put(key, newRecord);
    }

    HashMap record = couponMemo.get(key);
    return calcCouponPayment(cf, years, rate, record);
  }

  private double calcCouponPayment(double cf, int years, double rate, HashMap<Integer, Double> memo) {
    if (!memo.containsKey(years)) {
      double previousCouponPayment = calcCouponPayment(cf, years - 1, rate, memo);
      double currentCouponPayment = previousCouponPayment + cf / calcDiscount(years, rate);
      memo.put(years, currentCouponPayment);
    }
    return memo.get(years);
  }

  private double calcDiscount(int year, double rate) {
    return Math.pow((1.0 + rate), year);
  }

  private double calcDiscountHelper(int year, double rate, HashMap<Integer, Double> memo) {
    if (!memo.containsKey(year)) {
      double previousDiscount = calcDiscountHelper(year - 1, rate, memo);
      double currentDiscount = previousDiscount * (1 + rate);
      memo.put(year, currentDiscount);
    }
    return memo.get(year);
  }

  private double calcCF(double coupon, double face) {
    return coupon * face;
  }
}
