package owl.owlapi;


public class Log {
    private long now;
    private long prec;

    public Log() {
        this.now = System.currentTimeMillis();
        this.prec = 0L;
    }


    public void write(String msg) {
        this.prec = this.now;
        this.now = System.currentTimeMillis();


        System.out.println("<time: " + (this.now - this.prec) + " msec> " + msg + "\n");
    }
}


