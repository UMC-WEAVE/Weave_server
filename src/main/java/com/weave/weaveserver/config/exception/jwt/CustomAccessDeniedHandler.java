package com.weave.weaveserver.config.exception.jwt;

import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler {
    @Component
    public class CustomAuthenticationEntryPoint implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
            ExceptionCode exceptionCode;
            exceptionCode = ExceptionCode.PERMISSION_DENIED;
            setResponse(response, exceptionCode);
        }

        private void setResponse(HttpServletResponse response, ExceptionCode exceptionCode) throws IOException {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            JSONObject responseJson = new JSONObject();
            responseJson.put("timeStamp", exceptionCode.getTimeStamp());
            responseJson.put("isSuccess",exceptionCode.isSuccess());
            responseJson.put("status",exceptionCode.getStatus());
            responseJson.put("message", exceptionCode.getMessage());

            response.getWriter().print(responseJson);
        }
    }
}
