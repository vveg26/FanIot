package entity.mvc;

/**
 * @author aeolus
 * @program SSMDemo
 * @description
 * @date 2021-04-26 11:00:41
 */
public class Result {
    private int status = 1;
    private String msg;
    private Object data;

    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
