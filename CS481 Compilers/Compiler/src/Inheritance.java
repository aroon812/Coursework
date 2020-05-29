
/*
 * Output should be:
 * 5
 * 100
 * 10
 * 5
 * 50  (100 if dynamic typing implemented)
 * 0
 */

class Inheritance {
    public static void main(String[] a){
       System.out.println(new Run().test());
    }
 }
 
 /** 
  * A basic class with an instance variable, setter, and getter.
  */
 class Foo {
    int fooVal;
    
    public int set(int a) {
       fooVal = a;
       return a;
    }
    
    public int get() {
       return fooVal;
    }
 }
 
 /**
  * SubFoo adds another instance variable to the one inherited from Foo.
  * It inherits the set() method (which will set fooVal), but overwrites
  * get() with a version that returns 2 * fooVal so that we can distinguish
  * between it and the get() in our superclass.
  */
 
 class SubFoo extends Foo {
    int subFooVal;
    
    public int setMyVar(int a) {
       subFooVal = a;
       return a;
    }
    
    public int get() {
       return 2 * fooVal;
    }
    
    public int getMyVar() {
       return subFooVal;
    }
 }
 
 class Run {
    
    public int test() {
       Foo foo;
       SubFoo subfoo;
       int dummy;
       
       foo = new Foo();
       subfoo = new SubFoo();
       
       // Make sure the basic setters and getters work as
       // expected.
       
       dummy = foo.set(5);
       dummy = subfoo.set(50);
       dummy = subfoo.setMyVar(10);
       
       System.out.println(foo.get());         // 5
       System.out.println(subfoo.get());      // 100
       System.out.println(subfoo.getMyVar()); // 10
       
       // Now test polymorphism.  In a basic (static typing only)
       // implementation, printFooVar will treat every object 
       // passed to it as a Foo instance, even if it's really a
       // SubFoo.
       
       dummy = this.printFooVar(foo);         // 5
       dummy = this.printFooVar(subfoo);      // 50 -- uses wrong get()
       
       return 0;
    }
    
    /**
     * Since parameter is of type Foo, the default implementation will
     * generate code to invoke Foo$get() instead of determining at 
     * runtime which get() is appropriate (Foo$get() or SubFoo$get()).
     */
    public int printFooVar(Foo obj) {
       int val;
       val = obj.get();
       System.out.println(val);
       return 0;
    }
 }