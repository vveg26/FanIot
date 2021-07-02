package entity.mvc;

/**
 * @author aeolus
 * @program SSMDemo
 * @description token
 * @date 2021-05-08 09:43:06
 */
public class Token {
    private String typeCode;  // 4种类型
    private int id;
    private String uuid;
    private String tokenValue;

    public Token() {
    }

    public Token(int id, String uuid, String tokenValue) {
        this.id = id;
        this.uuid = uuid;
        this.tokenValue = tokenValue;
    }

    public Token(int id, String typeCode, String uuid, String tokenValue) {
        this.typeCode = typeCode;
        this.id = id;
        this.uuid = uuid;
        this.tokenValue = tokenValue;
    }

    @Override
    public String toString() {
        return "Token{" +
                "typeCode='" + typeCode + '\'' +
                ", id=" + id +
                ", uuid='" + uuid + '\'' +
                ", tokenValue='" + tokenValue + '\'' +
                '}';
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}
