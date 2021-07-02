package service;

import entity.mvc.Token;
import entity.mvc.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author aeolus
 * @program SSMDemo
 * @description
 * @date 2021-05-08 10:10:47
 */
@Service
public class TokenManager {
    @Autowired
    private RedisService redisService;


    //  token格式为：id_typeCode_UUID
    public Token createToken(User user) {
//        String raw = user.getName() + user.getPassword();
//        String uuid = UUID.nameUUIDFromBytes(raw.getBytes())
//                .toString().replace("-", "");
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String tokenValue = user.getId() + "_" + user.getRole() + "_" + uuid;
        Token token = new Token(user.getId(), user.getRole(), uuid, tokenValue);
        redisService.set(user.getId() + ":" + user.getRole(), tokenValue);
        return token;
    }

    public boolean checkToken(String tokenValue) {
        if (tokenValue == null || tokenValue.length() == 0) {
            return false;
        }

        String[] param = tokenValue.split("_");
        if (param.length != 3) {
            return false;
        }
        String keyName = param[0] + ":" + param[1];
        if (redisService.hasKey(keyName)) {
            String tokenFromRedis = redisService.get(keyName);
            return tokenFromRedis.equals(tokenValue);
        }
        return false;
    }

    public void deleteToken(User user) {
        redisService.delete(user.getId() + ":" + user.getRole());
    }
}
