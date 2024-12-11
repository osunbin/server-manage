package com.bin.sm.toolkit.singleflight;

public class SingleFlightResult<V> {

    public  enum ResultTyep {
        EXEC("exec"),
        WAIT("wait"),
        ERROR("error");
        public String type;

        ResultTyep(String type) {
            this.type = type;
        }
    }

    private V val;
    private ResultTyep type;
    private Throwable throwable;


    SingleFlightResult(V v, ResultTyep type) {
        this.val = v;
        this.type = type;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public static  <V> SingleFlightResult<V> ofExec(V v) {
        return new SingleFlightResult(v, ResultTyep.EXEC);
    }

    public static  <V> SingleFlightResult<V> ofWait(V v) {
        return new SingleFlightResult(v, ResultTyep.WAIT);
    }

    public static  <V> SingleFlightResult<V> ofError(Throwable throwable) {
        SingleFlightResult<V> result = new SingleFlightResult<>(null, ResultTyep.WAIT);
        result.setThrowable(throwable);
        return result;
    }

}