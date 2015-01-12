package com.ctrip.infosec.riskverify.aop;

import com.ctrip.infosec.common.model.RiskFact;
import com.ctrip.infosec.sars.monitor.SarsMonitorContext;
import com.ctrip.infosec.sars.monitor.counters.CounterRepository;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.Executor;

/**
 * Created by zhangsx on 2015/1/8.
 */
public class RiskVerifyAdvice implements MethodInterceptor {
    private final Log logger = LogFactory.getLog(RiskVerifyAdvice.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String serviceName = invocation.getThis().getClass().getSimpleName();
        String operationName = invocation.getMethod().getName();
        String eventPoint = ((RiskFact)invocation.getArguments()[0]).getEventPoint();
        String fact = ((String)invocation.getArguments()[1]);
        // invoke
        boolean fault = false;
        StopWatch clock = new StopWatch();
        clock.start();
        try {
            return invocation.proceed();
        } catch (Exception ex) {
            fault = true;
            throw ex;
        } catch (Throwable t) {
            fault = true;
            throw t;
        } finally {
            clock.stop();
            long handlingTime = clock.getTime();
            CounterRepository.increaseCounter(eventPoint + "." + fact, handlingTime, fault);
            // operationPrefix
//            String operationPrefix = SarsMonitorContext.getOperationPrefix();
//            if (StringUtils.isNotBlank(operationPrefix)) {
//                CounterRepository.increaseCounter("[" + operationPrefix + "] " + serviceName + "." + operationName, handlingTime, fault);
//            }
            // logger
            if (!fault) {
                if (handlingTime < SarsMonitorContext.WARN_VALUE) {
                    logger.info(SarsMonitorContext.getLogPrefix() + "invoke " + serviceName + "." + operationName + ", usage=" + handlingTime + "ms");
                } else {
                    logger.warn(SarsMonitorContext.getLogPrefix() + "invoke " + serviceName + "." + operationName + ", usage=" + handlingTime + "ms");
                }
            } else {
                logger.warn(SarsMonitorContext.getLogPrefix() + "invoke " + serviceName + "." + operationName + ", fault.");
            }
        }
    }
}
