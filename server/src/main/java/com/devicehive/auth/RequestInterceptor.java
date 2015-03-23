package com.devicehive.auth;

import com.devicehive.exceptions.HiveException;
import com.devicehive.model.AccessKey;
import com.devicehive.model.AccessKeyPermission;
import com.devicehive.model.User;
import com.devicehive.model.enums.UserStatus;
import com.devicehive.util.ThreadLocalVariablesKeeper;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Set;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;


public class RequestInterceptor implements MethodInterceptor {

    private static Logger logger = LoggerFactory.getLogger(RequestInterceptor.class);


    private HiveSecurityContext hiveSecurityContext;

    @Autowired
    public void setHiveSecurityContext(HiveSecurityContext hiveSecurityContext) {
        this.hiveSecurityContext = hiveSecurityContext;
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            HivePrincipal principal = hiveSecurityContext.getHivePrincipal();
            User user = principal.getUser();
            if (user != null && user.getStatus() != UserStatus.ACTIVE) {
                throw new HiveException(UNAUTHORIZED.getReasonPhrase(), UNAUTHORIZED.getStatusCode());
            }
            AccessKey key = principal.getKey();
            if (key == null) {
                return methodInvocation.proceed();
            }
            if (key.getUser() == null || !key.getUser().getStatus().equals(UserStatus.ACTIVE)) {
                throw new HiveException(UNAUTHORIZED.getReasonPhrase(), UNAUTHORIZED.getStatusCode());
            }
            Timestamp expirationDate = key.getExpirationDate();
            if (expirationDate != null && expirationDate.before(new Timestamp(System.currentTimeMillis()))) {
                throw new HiveException(UNAUTHORIZED.getReasonPhrase(), UNAUTHORIZED.getStatusCode());
            }
            Method method = methodInvocation.getMethod();
            AllowedKeyAction allowedActionAnnotation = method.getAnnotation(AllowedKeyAction.class);
            Set<AccessKeyPermission>
                filtered =
                CheckPermissionsHelper.filterPermissions(key.getPermissions(), allowedActionAnnotation.action(),
                                                         hiveSecurityContext.getClientInetAddress(),
                                                         hiveSecurityContext.getOrigin());
            if (filtered.isEmpty()) {
                throw new HiveException(UNAUTHORIZED.getReasonPhrase(), UNAUTHORIZED.getStatusCode());
            }
            key.setPermissions(filtered);
            return methodInvocation.proceed();
        } finally {
            ThreadLocalVariablesKeeper.clean();
        }
    }


}
