package hello.springjpa.webapp.test;

public class Car{
    static {
        System.out.println("static { 클래스 초기화 블럭 }");
    }

    {
        System.out.println("{ 인스턴스 초기화 블럭 }");
    }

    public Car(){
        System.out.println("생성자");
    }

    public static void main(String args[]){
        System.out.println("Car car = new Car(); ");
        Car car = new Car();

        System.out.println("Car car2 = new Car();");
        Car car2 = new Car();
    }
}