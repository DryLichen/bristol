PImage img;

void setup(){
  // run once
  size(1000, 1000);
  background(240, 210, 220);
  img = loadImage("Toyokawa.jpg");
}

// Click within the image to change 
// the value of the rectangle
void draw() {
  if (key == 'c') {
    fill(random(255), random(255), random(255));
  }
  
  keyPressed() {
    
  }
  
  if (key == 'b') {
    if (key == '0') {
      if (mousePressed == true) {
        ellipse(mouseX, mouseY, 30, 30);
      } 
    }
  }
 
  
  //image(img, 0, 0);
}
