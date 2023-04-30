import java.util.HashSet;
import java.util.Set;

public class demo {
    
    public static void main(String[] args) {
        

        Set<Dog> set = new HashSet<>();
        Dog dog1 = new Dog();
        Dog dog2 = new Dog();
        set.add(dog1);
        set.add(dog2);

        Animal animal = dog1;
        Dog dog = dog2;
        Animal animal2 = new Dog();

        System.out.println(set.contains(animal)); // true
        System.out.println(set.contains(dog));    // true
        System.out.println(set.contains(animal2));

    }
}

class Animal {}
class Dog extends Animal {}
