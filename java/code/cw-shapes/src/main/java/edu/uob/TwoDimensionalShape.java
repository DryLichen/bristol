package edu.uob;

abstract class TwoDimensionalShape {
  private Colour colour;

  public TwoDimensionalShape() {}

  public Colour getColour() {
    return colour;
  }

  public void setColour(Colour colour) {
    this.colour = colour;
  }

  @Override
  public String toString() {
    return  "Colour=" + colour;
  }

  abstract double calculateArea();

  abstract int calculatePerimeterLength();
}
