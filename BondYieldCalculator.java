import java.util.*;
import java.lang.Math;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BondYieldCalculator {
  private static final int DECIMAL_ACCURACY = 7;
  private static final double ACCURACY = 0.0000001;

  /*
   * Memo to keep track of the value of coupon payments
   * made up until a specified year.
   * Values here can then be added to the value of
   * principal payments to get the price of a bond.
   * key: CouponKey(cF, rate)
   * val: HashMap<year, price>
   */
  private HashMap<CouponKey, HashMap<Integer, Double>> couponMemo;

  /*
   * Memo to keep track of yields based on coupon, years
   * face value, and price.
   * key: YieldKey(coupon, years, face, price)
   * val: yield
   */
  private HashMap<YieldKey, Double> yieldMemo;

  /**
  * Sole constructor. Takes no params.
  */
  public BondYieldCalculator() {
    couponMemo = new HashMap<CouponKey, HashMap<Integer, Double>>();
    yieldMemo = new HashMap<YieldKey, Double>();
  }

  /**
  * Calculates the yield of a bond using the bisection method given
  * the coupon rate, the years to maturity, the face value, and
  * the price of the bond.
  * @param  coupon coupon rate
  * @param  years  number of years to maturity
  * @param  face   face value
  * @param  price  price of bond
  * @return        yield of bond
  */
  public double CalcYield(double coupon, int years, double face, double price) {
    YieldKey key = new YieldKey(coupon, years, face, price);
    /* Check if combination seen before and if not, calculate result */
    if (!yieldMemo.containsKey(key)) {
      yieldMemo.put(key, yieldBisection(coupon, years, face, price));
    }

    return yieldMemo.get(key);
  }

  /**
  * Use bisection method to find the yield.
  * @param  coupon coupon rate
  * @param  years  number of years to maturity
  * @param  face   face value
  * @param  price  price of bond
  * @return        yield of bond
  */
  private double yieldBisection(double coupon, int years, double face, double price) {
    /* Special case year = 0: return 0.0 */
    if (years == 0) { return 0.0; }

    /* guess for what r might be */
    double guessA = 5.0; /* r most likely will not be this high or higher */
    double guessB = -1.0 + ACCURACY; /* rate = -1 is asymptotic and would give divide by 0 */
    double guessC = 0.0;

    double priceA = CalcPrice(coupon, years, face, guessA) - price;
    double priceB = CalcPrice(coupon, years, face, guessB) - price;
    double priceC;

    int direction = 1;

    /*
    * Bisection requires f(a) and f(b) to have different signs.
    * If the two have different signs, it will not work.
    * If same sign, try to check other side of asymptote.
    * Odd order will not work.
    */
    if (priceA * priceB > 0) {
      if (years % 2 == 0) {
        guessA = -5.0;
        guessB = -1.0 - ACCURACY;       /* -1.0 will never have a value */
        direction = -1;
        priceA = CalcPrice(coupon, years, face, guessA) - price;
        priceB = CalcPrice(coupon, years, face, guessB) - price;
      } else {
        return Double.NaN;
      }
    }

    if(Math.abs(priceA) < ACCURACY) {
      return guessA;
    } else if (Math.abs(priceB) < ACCURACY) {
      return guessB;
    }

    guessC = (guessA + guessB) / 2;                /* get mid point */
    priceC = CalcPrice(coupon, years, face, guessC) - price;

    int counter = 0;
    while (Math.abs(priceC) >= ACCURACY ) {
      if (Double.isNaN(priceB)) {
        guessB += ACCURACY * direction;
        priceB = CalcPrice(coupon, years, face, guessB) - price;
      } else {
        if (priceA * priceC < 0) {
          guessB = guessC;
          priceB = priceC;
        } else if (priceB * priceC < 0) {
          guessA = guessC;
          priceA = priceC;
        }
      }
      guessC = (guessA + guessB) / 2;                /* get mid point */
      priceC = CalcPrice(coupon, years, face, guessC) - price;
      counter++;
    }

    return guessC;
  }

  /**
  * Calls CalcYield and returns formatted version of the result.
  * @param  coupon coupon rate
  * @param  years  number of years to maturity
  * @param  face   face value
  * @param  price  price of bond
  * @return        formatted bond yield
  */
  public String prettyCalcYield(double coupon, int years, double face, double price) {
    double yield = CalcYield(coupon, years, face, price);
    if (Double.isNaN(yield)) {
      return "Combination of parameters resulted in a calculation that produced a number too large for current machine to handle";
    }
    return stringify(yield, DECIMAL_ACCURACY);
  }

  /**
  * Calculates the price of a bond given the coupon rate, years to maturity
  * face value, and the discount rate. Accurate to 10e-7.
  * (C / (1 + r)) + (C / (1 + r)^2) + ... + (C / (1 + r)^N) + (F / (1 + r)^N)
  * @param  coupon coupon rate
  * @param  years  number of years to maturity
  * @param  face   face value
  * @param  rate   discount rate
  * @return        price of bond
  */
  public double CalcPrice(double coupon, int years, double face, double rate) {
    /* Special case when years = 0: return the face value */
    if (years == 0) { return face; }

    double totalCouponPayment = calcTotalCouponPaymentValue(coupon, years, face, rate);
    double principalPaymentValue = calcPrincipalPaymentValue(years, face, rate);
    return totalCouponPayment + principalPaymentValue;
  }

  /**
  * Calls CalcPrice and returns a formatted string of result.
  * @param  coupon coupon rate
  * @param  years  number of years to maturity
  * @param  face   face value
  * @param  rate   discount rate
  * @return        formatted bond price
  */
  public String prettyCalcPrice(double coupon, int years, double face, double rate) {
    double price = CalcPrice(coupon, years, face, rate);
    if (Double.isNaN(price)) {
      return "Combination of parameters resulted in a calculation that produced a number too large for current machine to handle";
    }

    return stringify(price, DECIMAL_ACCURACY);
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
    double cf = calcCF(coupon, face);
    CouponKey key = new CouponKey(cf, rate);

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
     * Check if total coupon payment up until years was previously calculated.
     * If not, calculate it by taking the total for previous year and
     * then adding to that the coupon payment for current year.
     * Base case of year = 0, total paid = 0 added into the record on creation.
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

  public double roundValue(double value, int precision) {
    BigDecimal val = new BigDecimal(value);
    val = val.setScale(DECIMAL_ACCURACY, RoundingMode.HALF_UP);
    return val.doubleValue();
  }

  public String prettyValue(double value, int precision) {
    return String.format("%.7f", value);
  }

  public String stringify(double value, int precision) {
    double val = roundValue(value, precision);
    return prettyValue(val, precision);
  }
}
