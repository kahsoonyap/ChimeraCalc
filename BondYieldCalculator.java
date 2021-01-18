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

  /**
  * Calculates the price of a bond given the coupon rate, years to maturity
  * face value, and the discount rate. Accurate to 10e-7.
  * (C / (1 + r)) + (C / (1 + r)^2) + ... + (C / (1 + r)^N) + (F / (1 + r)^N)
  * @param  coupon coupon rate
  * @param  years  number of years to maturity
  * @param  face   face value
  * @param  rate   discount rate
  * @return        price
  */
  public double CalcPrice(double coupon, int years, double face, double rate) {
    double rawDouble = calcPriceHelper(coupon, years, face, rate);
    BigDecimal price = new BigDecimal(rawDouble);
    price = price.setScale(DECIMAL_ACCURACY, RoundingMode.HALF_UP);

    return price.doubleValue();
  }

  private double calcPriceHelper(double coupon, int years, double face, double rate) {
    /* Special case when years = 0: return the face value */
    if (years == 0) { return face; }

    double totalCouponPayment = calcTotalCouponPaymentValue(coupon, years, face, rate);
    double principalPaymentValue = calcPrincipalPaymentValue(years, face, rate);

    return totalCouponPayment + principalPaymentValue;
  }

  /**
  * Calculates the value of a bond's principal payment given the
  * years to maturity, the face value, and the discount rate.
  * F / (1 + r)^N
  * @param  years number of years to maturity
  * @param  face  face value
  * @param  rate  discount rate
  * @return       value of the bond's principal payment
  */
  private double calcPrincipalPaymentValue(int years, double face, double rate) {
    double discount = calcValueModifier(years, rate);

    return face / discount;
  }

  /**
  * Calculates the value of the total coupon payments made for a bond
  * given the coupon rate, the number of years to maturity,
  * the face value, and the discount rate.
  * (C / (1 + r)) + (C / (1 + r)^2) + ... + (C / (1 + r)^N)
  * @param  coupon coupon rate
  * @param  years  number of years to maturity
  * @param  face   face value
  * @param  rate   discount rate
  * @return        value of coupon payment total
  */
  private double calcTotalCouponPaymentValue(double coupon, int years, double face, double rate) {
    List key = Arrays.asList(coupon, face, rate);
    double cf = calcCF(coupon, face);

    /*
     * Check if combination of coupon, face value, and rate
     * have already been seen. If not, create an entry into
     * the memo with said combination.
     */
    if (!couponMemo.containsKey(key)) {
      HashMap newRecord = new HashMap<Integer, Double>();
      newRecord.put(0, 0.0);            /* Base case for later calculations */
      couponMemo.put(key, newRecord);
    }

    HashMap record = couponMemo.get(key);
    return calcCouponPaymentValue(cf, years, rate, record);
  }

  /**
  * Calculates the value of coupon payments up to a certain year
  * given the coupon rate, the current year, the face value,
  * and the discount rate. Does this with recursive calls to get
  * the values of previous years before updating the memo.
  * (C / (1 + r)) + (C / (1 + r)^2) + ... + (C / (1 + r)^n)
  * @param  coupon coupon rate
  * @param  years  target year
  * @param  face   face value
  * @param  rate   discount rate
  * @return        value coupon payments up until target year
  */
  private double calcCouponPaymentValue(double cf, int years, double rate, HashMap<Integer, Double> memo) {
    /*
     * Check if current year coupon payment was previously calculated.
     * If not, calculate
     */
    if (!memo.containsKey(years)) {
      double previousCouponPayment = calcCouponPaymentValue(cf, years - 1, rate, memo);
      double currentCouponPayment = previousCouponPayment + cf / calcValueModifier(years, rate);
      memo.put(years, currentCouponPayment);
    }

    return memo.get(years);
  }

  /**
  * Calculates the modifier of coupon and principal payments
  * C / (1 + r)^n
  * @param  year  target year
  * @param  rate  discount rate
  * @return       value modifier for a given year
  */
  private double calcValueModifier(int year, double rate) {
    return Math.pow((1.0 + rate), year);
  }

  /**
  * Calculates the coupon payment before discounted to present value
  * C = cF
  * @param  coupon coupon rate
  * @param  face   face value
  * @return        coupon payment
  */
  private double calcCF(double coupon, double face) {
    return coupon * face;
  }
}
