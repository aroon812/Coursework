class Loop {
    public static void main(String[] a){
        System.out.println((new Loopy()).run(10));
    }
}

class Loopy {
    public int run(int var) {
        int a;
        a = 0;
        while (a < var) {
            a = a + 1;
        }
        return a;
    }
}
