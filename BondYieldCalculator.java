import java.util.*;
import java.lang.Math;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BondYieldCalculator {
  private static final int DECIMAL_ACCURACY = 7;
  private HashMap<List, HashMap<Integer, Double>> memo;
  public BondYieldCalculator() {
    // face -> coupon -> rate -> price
    // HashMap<double, HashMap<double, <HashMap<double, HashMap<int, double>>>>> memo = new HashMap<>();
    memo = new HashMap<List, HashMap<Integer, Double>>();
  }

  // http://www.columbia.edu/~ks20/FE-Notes/4700-07-Notes-bonds.pdf
  // https://financeformulas.net/Yield_to_Maturity.html
  // http://phillipmfeldman.org/Python/roots/find_roots.html#:~:text=Abstract,the%20Newton%2DRaphson%20method).
  // https://www.sciencedirect.com/science/article/pii/S0893965903901194#:~:text=Standard%20text%20books%20in%20numerical,the%20secant%20method%20becomes%20linear.
  public double CalcYield(double coupon, int years, double face, double price) {
    // BigDecimal yield = new BigDecimal((calcCoupon(coupon, face) + (face - price) / years) / ((face + price) / 2));
    // yield = yield.setScale(DECIMAL_ACCURACY, RoundingMode.HALF_UP);
    // return yield.doubleValue();
    double guessA = Double.MAX_VALUE;
    double guessB = Double.MIN_VALUE;
    double guessC = 0;

    double priceA = calcPriceHelper(coupon, years, face, guessA);
    double priceB = 0;

    while (Math.abs(price - priceA) > 0.0000001) {
      priceB = calcPriceHelper(coupon, years, face, guessB);
      guessC = guessA - priceA * (guessB - guessA) / (priceB - priceA);
      priceA = calcPriceHelper(coupon, years, face, guessA);
    }

    BigDecimal yield = new BigDecimal(priceA);
    yield = yield.setScale(DECIMAL_ACCURACY, RoundingMode.HALF_UP);
    return yield.doubleValue();
  }


  public double CalcPrice(double coupon, int years, double face, double rate) {
    double rawDouble = calcPriceHelper(coupon, years, face, rate);
    BigDecimal price = new BigDecimal(rawDouble);
    price = price.setScale(DECIMAL_ACCURACY, RoundingMode.HALF_UP);

    return price.doubleValue();
  }

  private double calcPriceHelper(double coupon, int years, double face, double rate) {
    if (!memo.containsKey(Arrays.asList(coupon, face, rate))) {
      HashMap<Integer, Double> newRecord = new HashMap<Integer, Double>();
      memo.put(Arrays.asList(coupon, face, rate), newRecord);
    }
    HashMap<Integer, Double> record = memo.get(Arrays.asList(coupon, face, rate));
    if (!record.containsKey(years)) {
      double principalPayment = calcPrincipalPayment(years, face, rate);
      double totalCouponPayment = calcTotalCouponPayment(coupon, years, face, rate);
      record.put(years, principalPayment + totalCouponPayment);
    }

    return record.get(years);
  }

  private double calcPrincipalPayment(int years, double face, double rate) {
    double discount = calcDiscount(years, rate);

    return face / discount;
  }

  private double calcTotalCouponPayment(double coupon, int years, double face, double rate) {
    double total = 0;
    double couponPayment = calcCoupon(coupon, face);

    for (int year = 1; year <= years; year++) {
      total += couponPayment / calcDiscount(year, rate);
    }

    return total;
  }

  private double calcDiscount(int year, double rate) {
    return Math.pow((1.0 + rate), year);
  }

  private double calcCoupon(double coupon, double face) {
    return coupon * face;
  }
}
