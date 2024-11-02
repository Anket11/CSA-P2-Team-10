package CPU;


public class Condition_Code extends Register{

    private boolean cc;

    public boolean getBoolean(){
        return cc;
    }

    public void set(boolean value) {
        this.cc = value;
        setValue(value? 1:0);
    }

    public boolean get() {
        return this.cc;
    }
    public Condition_Code(boolean value) {super(value? 1:0,2,"CC");
        cc=value;
    }



}
