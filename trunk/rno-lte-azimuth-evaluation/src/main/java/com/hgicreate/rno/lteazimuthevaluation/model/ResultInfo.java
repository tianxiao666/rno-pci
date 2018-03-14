package com.hgicreate.rno.lteazimuthevaluation.model;

public class ResultInfo {

    private boolean flag = false;
    private String msg;
    private Object attach;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getAttach() {
        return attach;
    }

    public void setAttach(Object attach) {
        this.attach = attach;
    }

    @Override
    public String toString() {
        return "ResultInfo [flag=" + flag + ", msg=" + msg + ", attach="
                + attach + "]";
    }

}
