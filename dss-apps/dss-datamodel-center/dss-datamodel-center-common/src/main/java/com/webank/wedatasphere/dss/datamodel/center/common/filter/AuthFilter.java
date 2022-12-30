package com.webank.wedatasphere.dss.datamodel.center.common.filter;


import com.webank.wedatasphere.dss.datamodel.center.common.context.DataModelAuthentication;
import com.webank.wedatasphere.dss.datamodel.center.common.context.DataModelSecurityContextHolder;
import com.webank.wedatasphere.dss.datamodel.center.common.service.AuthenticationClientStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class AuthFilter implements Filter, AuthenticationClientStrategy {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        DataModelAuthentication dataModelAuthentication = new DataModelAuthentication();
        dataModelAuthentication.setUser(getStrategyUser((HttpServletRequest) servletRequest));
        DataModelSecurityContextHolder.getContext().setDataModelAuthentication(dataModelAuthentication);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
