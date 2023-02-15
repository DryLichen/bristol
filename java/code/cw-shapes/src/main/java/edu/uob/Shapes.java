package edu.uob;

public class Shapes {

  // TODO use this class as then entry point; play around with your shapes, etc
  public static void main(String[] args) {
    // longest side
//    Triangle testTriangle1 = new Triangle(1, 2, 3);
//    int longestSide1 = testTriangle1.getLongestSide();
//    System.out.println("The longest side of the triangle is " + longestSide1);
//
//    Triangle testTriangle2 = new Triangle(4, 7, 9);
//    int longestSide2 = testTriangle2.getLongestSide();
//    System.out.println("The longest side of the triangle is " + longestSide2);

    // Polymorphism
//    TwoDimensionalShape shape = null;
//    shape = new Triangle();
//    System.out.println(shape.toString());
//    shape = new Rectangle(3, 4);
//    System.out.println(shape.toString());
//    shape = new Circle(10);
//    System.out.println(shape.toString());

    // color
//    Triangle triangle4 = new Triangle(5, 5, 5);
//    triangle4.setColour(Colour.GREEN);
//    System.out.println(triangle4.toString());
    // variant
//    System.out.println("Variant: " + triangle4.getVariant());

    // areas and perimeter
    Triangle triangle1 = new Triangle(3, 3, 3);
    double area = triangle1.calculateArea();
    System.out.println("area: " + area);
    int perimeterLength = triangle1.calculatePerimeterLength();
    System.out.println("perimeter: " + perimeterLength);

    // interface
    if(triangle1 instanceof MultiVariantShape) {
      System.out.println("This shape has multiple variants");
    } else {
      System.out.println("This shape has only one variant");
    }

    // Arrays
    TwoDimensionalShape[] shapes = new TwoDimensionalShape[100];
    for (int i = 0; i < 100; i++) {
      shapes[i] = getOneShape();
    }

    int countTri = 0;
    for (TwoDimensionalShape shape : shapes) {
      System.out.println("\n" + shape.toString());
      if (shape instanceof Triangle) {
        System.out.println("and the variant is: " + ((Triangle) shape).getVariant());
      }

      if (shape instanceof Triangle) {
        countTri++;
      }
    }

    System.out.println("Number of triangles by using for loop: " + countTri);
    System.out.println("Number of triangles by using static variable: " + Triangle.getCount());
  }

  private static TwoDimensionalShape getOneShape() {
    double random = Math.random();
    if (random < 0.33) {
      return new Circle(10);
    } else if (random < 0.66) {
      return new Triangle(3, 4, 5);
    } else {
      return new Rectangle(2, 4);
    }
  }
}
