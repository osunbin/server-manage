package com.bin.sm.extension.registry.interceptor;

import com.bin.sm.extension.registry.entity.Contract;
import com.bin.sm.extension.registry.entity.MethodInfo;
import com.bin.sm.extension.registry.entity.ParamInfo;
import com.bin.sm.extension.registry.entity.ServerInfo;
import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpringCloudMappingRegistryInterceptor extends AbstractInterceptor {


    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {

        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        if (context.getArguments() == null || context.getArguments().length < 3) {
            return context;
        }
        Contract contract = new Contract();
        for (Object object : context.getArguments()) {
            if (object instanceof RequestMappingInfo) {
                RequestMappingInfo requestMappingInfo = (RequestMappingInfo) object;
                contract.setUrl((String) requestMappingInfo.getPatternsCondition().getPatterns().toArray()[0]);
            }
            if (object instanceof Method) {
                Method method = (Method) object;
                MethodInfo methodInfo = new MethodInfo();
                methodInfo.setName(method.getName());
                fillParamInfo(method, methodInfo);
                fillReturnInfo(method, methodInfo);
                contract.setInterfaceName(method.getDeclaringClass().getName());
                contract.setMethodInfoList(new ArrayList<>());
                contract.getMethodInfoList().add(methodInfo);
                contract.setServiceKey(contract.getInterfaceName() + "," + method.getName());
            }
        }
        contract.setServiceType("spring cloud");

        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setContractList(new ArrayList<>());
        serverInfo.getContractList().add(contract);


//        serverInfo.setApplicationName(BootArgsIndexer.getAppName());
//        serverInfo.setGroupName(serviceMeta.getApplication());
//        serverInfo.setVersion(serviceMeta.getVersion());
//        serverInfo.setEnvironment(serviceMeta.getEnvironment());
//        serverInfo.setZone(serviceMeta.getZone());
//        serverInfo.setProject(serviceMeta.getProject());
//        serverInfo.setInstanceId(BootArgsIndexer.getInstanceId());
// TODO
        return context;
    }

    /**
     * Save method information
     *
     * @param methodName The name of the method
     * @param interfaceClass Interface information
     * @param contract Contract Information
     */
    public void fillMethodInfo(String methodName, Class<?> interfaceClass, Contract contract) {
        List<String> methodNames = Arrays.asList(methodName.split(","));
        Method[] methods = interfaceClass.getMethods();
        for (Method method : methods) {
            if (!method.isDefault() && methodNames.contains(method.getName())) {
                MethodInfo methodInfo = new MethodInfo();
                methodInfo.setName(method.getName());
                fillParamInfo(method, methodInfo);
                fillReturnInfo(method, methodInfo);
                contract.getMethodInfoList().add(methodInfo);
            }
        }
    }

    public void fillReturnInfo(Method method, MethodInfo methodInfo) {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.setParamType(method.getReturnType().getTypeName());
        methodInfo.setReturnInfo(paramInfo);
    }

    /**
     * Save the parameter information
     *
     * @param method method
     * @param methodInfo Method information storage class
     */
    public void fillParamInfo(Method method, MethodInfo methodInfo) {
        if (method.getParameters() == null) {
            return;
        }
        List<ParamInfo> paramInfoList = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            ParamInfo paramInfo = new ParamInfo();
            paramInfo.setParamType(parameter.getType().getTypeName());
            paramInfo.setParamName(parameter.getName());
            paramInfoList.add(paramInfo);
        }
        methodInfo.setParamInfoList(paramInfoList);
    }


}
