import java.lang.Math;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BondYieldCalculator {
  private static final int DECIMAL_ACCURACY = 7;
  public BondYieldCalculator() {
  }

  public double CalcPrice(double coupon, int years, double face, double rate) {
    double principalPayment = calcPrincipalPayment(years, face, rate);
    double totalCouponPayment = calcTotalCouponPayment(coupon, years, face, rate);
    BigDecimal price = new BigDecimal(principalPayment + totalCouponPayment);
    price = price.setScale(DECIMAL_ACCURACY, RoundingMode.HALF_UP);

    return price.doubleValue();
  }

  public double CalcYield(double coupon, int years, double face, double price) {
    return 0.0;
  }



  private double calcDiscount(int year, double rate) {
    return Math.pow((1.0 + rate), year);
  }

  private double calcCoupon(double coupon, double face) {
    return coupon * face;
  }

  private double calcTotalCouponPayment(double coupon, int years, double face, double rate) {
    double total = 0;
    double couponPayment = calcCoupon(coupon, face);

    for (int year = 1; year <= years; year++) {
      total += couponPayment / calcDiscount(year, rate);
    }

    return total;
  }

  private double calcPrincipalPayment(int years, double face, double rate) {
    double discount = calcDiscount(years, rate);

    return face / discount;
  }

}
