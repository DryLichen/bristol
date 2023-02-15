package edu.uob;

import java.math.BigDecimal;

public class Triangle extends TwoDimensionalShape implements MultiVariantShape{
  private int a;
  private int b;
  private int c;
  private TriangleVariant variant;
  private static int count;

  public Triangle(int a, int b, int c) {
    setA(a);
    setB(b);
    setC(c);
    setVariant();
    count++;
  }

  public void setA(int a) {
    this.a = a;
  }

  public void setB(int b) {
    this.b = b;
  }

  public void setC(int c) {
    this.c = c;
  }

  public void setVariant() {
    // sort three sides
    sort();

    // illegal input
    if (a <= 0) {
      variant = TriangleVariant.ILLEGAL;
    } else if (c - a > b) {
      // impossible three sides
      variant = TriangleVariant.IMPOSSIBLE;
    } else if (c - a == b) {
      variant = TriangleVariant.FLAT;
    } else if (a == c) {
      variant = TriangleVariant.EQUILATERAL;
    } else if (a == b || b == c) {
      variant = TriangleVariant.ISOSCELES;
    } else if (BigDecimal.valueOf(a).pow(2).add(BigDecimal.valueOf(b).pow(2)).equals(BigDecimal.valueOf(c).pow(2))) {
      variant = TriangleVariant.RIGHT;
    } else {
      variant = TriangleVariant.SCALENE;
    }
  }


  public int getA() {
    return a;
  }

  public int getB() {
    return b;
  }

  public int getC() {
    return c;
  }

  public TriangleVariant getVariant() {
    return variant;
  }

  public static int getCount() {
    return count;
  }

  @Override
  public String toString() {
    return "This is a Triangle with sides of length " +
            a + ", " + b + ", " + c + " and " +
            super.toString();
  }

  /**
   * @return the longest side value
   */
  public int getLongestSide() {
    sort();
    return c;
  }

  // sort the three sides in ascending order
  private void sort() {
    int temp = 0;

    if (a > b) {
      temp = a;
      a = b;
      b = temp;
    }

    if (b > c) {
      temp = b;
      b = c;
      c = temp;
    }

    if (a > b) {
      temp = a;
      a = b;
      b = temp;
    }
  }

  // TODO implement me!
  public double calculateArea() {
    double s = ((double)a + (double)b + (double)c) / 2;
    double mul = s * (s - (double)a) * (s - (double)b) * (s - (double)c);
    double sqrt = Math.sqrt(mul);

    return sqrt;
  }

  // TODO implement me!
  public int calculatePerimeterLength() {
    int perimeter = a + b + c;
    return perimeter;
  }
}
