package com.xy.gears.valve.dispatcher;

import com.xy.gears.valve.dispatcher.service.JsonServiceRegistry;
import com.xy.gears.valve.resources.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Saurabh on 26-06-2016.
 */
public class JsonDispatcher extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(JsonDispatcher.class);
    private static JsonServiceRegistry registry;

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        LOGGER.info("executing service method...");

        String paramStr = req.getParameter(registry.PARAM);
        String urlMapping = ((HttpServletRequest) req).getRequestURI();
        Object returnVal = null;
        try {
            returnVal = registry.execute(urlMapping, paramStr);
        } catch (ServiceException e) {
            LOGGER.info("Exception occurred while processing service: " + urlMapping);
            throw new ServletException(e);
        }

        PrintWriter out = res.getWriter();
        res.setContentType("application/json");
        if (null != returnVal) {
            LOGGER.info("returning response");
            try {
                out.write(registry.getJson(returnVal));
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.info("writing sample text");
            out.write("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }
        out.flush();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        registry = JsonServiceRegistry.getInstance();
        try {
            registry.scan(getServletConfig().getInitParameter(Resources.REQUEST_HANDLER_PACKAGE));
        } catch (ServiceException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}
