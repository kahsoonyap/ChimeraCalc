public class CouponKey {
  public final double cF;
  public final double rate;

  public CouponKey(double cF, double rate) {
    this.cF = cF;
    this.rate = rate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (!(o instanceof CouponKey)){
      return false;
    } else {
      CouponKey key = (CouponKey) o;
      return (cF == key.cF && rate == key.rate);
    }
  }

  @Override
  public int hashCode() {
    int hash = Long.hashCode(Double.doubleToLongBits(cF * 31 + rate));
    return hash;
  }
}
