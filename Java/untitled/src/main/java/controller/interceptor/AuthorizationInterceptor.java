package controller.interceptor;

import dao.UserDao;
import entity.mvc.Result;
import entity.mvc.Role;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import service.TokenManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @author aeolus
 * @program SSMDemo
 * @description
 * @date 2021-05-08 17:32:03
 */

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private UserDao userDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        String tokenValue = request.getHeader("tokenValue");

        if (tokenValue == null){
            return true;
        }

        if (method.getAnnotation(Developer.class) != null
                && method.getAnnotation(Staff.class) != null){
            if (tokenManager.checkToken(tokenValue)
                    && (Role.STAFF.equals(tokenValue.split("_")[1])
                            || Role.DEVELOPER.equals(tokenValue.split("_")[1]))){
                return true;
            }
            // 拦截
            interceptor("使用者或开发者", response);
            return false;
        } else if (method.getAnnotation(Admin.class) != null) {
            if (tokenManager.checkToken(tokenValue)
                    && Role.ADMIN.equals(tokenValue.split("_")[1])){
                return true;
            }
            // 拦截
            interceptor("管理员", response);
            return false;
        } else if (method.getAnnotation(Device.class) != null) {
            if (tokenManager.checkToken(tokenValue)
                    && Role.DEVICE.equals(tokenValue.split("_")[1])){
                return true;
            }
            // 拦截
            interceptor("设备", response);
            return false;
        } else if (method.getAnnotation(Developer.class) != null) {
            if (tokenManager.checkToken(tokenValue)
                    && Role.DEVELOPER.equals(tokenValue.split("_")[1])){
                return true;
            }
            // 拦截
            interceptor("开发者", response);
            return false;
        } else if (method.getAnnotation(Staff.class) != null) {
            // 为staff
            if (tokenManager.checkToken(tokenValue)
                    && Role.STAFF.equals(tokenValue.split("_")[1])){
                return true;
            }
            // 拦截
            interceptor("使用者", response);
            return false;
        }

        return true;

    }

    private void interceptor(String msg, HttpServletResponse response) {
        Result result = new Result();
        result.setMsg("非" + msg +"，禁止操作");
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Type", "application/json; charset=UTF-8");
            out = response.getWriter();
            out.write(JSONObject.fromObject(result).toString());
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
}
