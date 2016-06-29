package com.xy.gears.valve.dispatcher.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.xy.gears.valve.dispatcher.JsonService;
import com.xy.gears.valve.dispatcher.ServiceException;
import com.xy.gears.valve.resources.Resources;
import com.xy.gears.valve.resources.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurabh on 27-06-2016.
 */
public class JsonServiceRegistry implements ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(JsonServiceRegistry.class);

    private static JsonServiceRegistry instace;
    //    private final ObjectMapper mapper;
    public final String URL_PREFIX = "/abc/json";
    public final String PARAM = "param";
    private final Map<String, Target> urlMapRegistry;

    private JsonServiceRegistry() {
        urlMapRegistry = new HashMap();
        //mapper = new ObjectMapper();
    }

    public static JsonServiceRegistry getInstance() {
        if (null == instace) {
            instace = new JsonServiceRegistry();
        }
        return instace;
    }

    public String getJson(Object object) throws ServiceException {
        try {
            String json = Resources.getJson(object); //mapper.writeValueAsString(object);
            return json;
        } catch (JsonProcessingException e) {
            throw new ServiceException(e);
        }
    }

    public Object execute(String urlMappingStr, String paramVal) throws ServiceException {
        Object returnVal = null;

        urlMappingStr = urlMappingStr.replace(URL_PREFIX, "");
        LOGGER.info("Service requested: " + urlMappingStr);
        LOGGER.info("Param passed: " + paramVal);
        Target target = urlMapRegistry.get(urlMappingStr);
        LOGGER.info("service will handed over: " + target);
        try {
            Object handler = target.getClazz().newInstance();
            Class[] params = target.getMethod().getParameterTypes();
            Object param = null;
            for (int i = 0; i < params.length; i++) {
                Class paramClazz = Class.forName(params[i].getName());
                param = Resources.mapObject(paramVal, paramClazz); // mapper.readValue(paramVal, paramClazz);
            }
            if (null != param) {
                returnVal = target.getMethod().invoke(handler, param);
            } else {
                returnVal = target.getMethod().invoke(handler);
            }
            LOGGER.info("value returned: " + returnVal);
        } catch (InstantiationException e) {
            LOGGER.trace("exception occured while creating handler: " + target);
            throw new ServiceException(e);
        } catch (IllegalAccessException e) {
            LOGGER.trace("exception occured while creating handler: " + target);
            throw new ServiceException(e);
        } catch (InvocationTargetException e) {
            LOGGER.trace("exception occured while calling handler method: " + target);
            throw new ServiceException(e);
        } catch (ClassNotFoundException e) {
            LOGGER.trace("exception occured while handling method param: " + target);
            throw new ServiceException(e);
        } catch (JsonParseException e) {
            LOGGER.trace("exception occured while handling method param: " + target);
            throw new ServiceException(e);
        } catch (JsonMappingException e) {
            LOGGER.trace("exception occured while handling method param: " + target);
            throw new ServiceException(e);
        } catch (IOException e) {
            LOGGER.trace("exception occured while handling method param: " + target);
            throw new ServiceException(e);
        }

        return returnVal;
    }

    public void scan(String packageName) throws ServiceException {
        LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        LOGGER.info("Registering handler classes...");
        try {
            Class[] classes = Util.getClasses(packageName);
            for (int i = 0; i < classes.length; i++) {
                LOGGER.info("Scanning class: " + classes[i].getSimpleName());
                Method[] methods = classes[i].getMethods();
                for (int j = 0; j < methods.length; j++) {
                    LOGGER.info("Scanning methd: " + methods[j].getName());
                    JsonService jsonService = methods[j]
                            .getAnnotation(JsonService.class);
                    if (null != jsonService) {
                        String urlMapping = jsonService.mapping();
                        register(urlMapping, classes[i], methods[j]);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            LOGGER.info("Exception occured while resolving handler: " + e);
            throw new ServiceException(e);
        } catch (IOException e) {
            LOGGER.info("Exception occured while resolving handler: " + e);
            throw new ServiceException(e);
        }
        LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    public void register(String url, Class clazz, Method method) {
        Target target = new Target(clazz, method);
        urlMapRegistry.put(url, target);
    }

    class Target {
        private Class clazz;
        private Method method;

        public Target(Class clazz, Method method) {
            this.clazz = clazz;
            this.method = method;
        }

        public Class getClazz() {
            return clazz;
        }

        public Method getMethod() {
            return method;
        }

        @Override
        public String toString() {
            return "Target{" +
                    "clazz=" + clazz +
                    ", method=" + method +
                    '}';
        }
    }
}
