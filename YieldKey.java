public class YieldKey {
  public final int years;
  public final double coupon;
  public final double face;
  public final double price;

  public YieldKey(double coupon, int years, double face, double price) {
    this.coupon = coupon;
    this.years = years;
    this.face = face;
    this.price = price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (!(o instanceof YieldKey)){
      return false;
    } else {
      YieldKey key = (YieldKey) o;
      return (coupon == key.coupon && years == key.years && face == key.face && price == key.price);
    }
  }

  @Override
  public int hashCode() {
    int hash = Long.hashCode(Double.doubleToLongBits(((coupon * 31 + years) * 31 + face) * 31 + price));
    return hash;
  }
}
