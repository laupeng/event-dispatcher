package com.ctrip.infosec.riskverify.orderIndex;

import com.ctrip.cmessaging.client.IAsyncConsumer;
import com.ctrip.cmessaging.client.IMessage;
import com.ctrip.cmessaging.client.content.AckMode;
import com.ctrip.cmessaging.client.event.IConsumerCallbackEventHandler;
import com.ctrip.cmessaging.client.exception.IllegalExchangeName;
import com.ctrip.cmessaging.client.exception.IllegalTopic;
import com.ctrip.cmessaging.client.impl.Config;
import com.ctrip.cmessaging.client.impl.ConsumerFactory;
import com.ctrip.infosec.common.model.RiskFact;
import com.ctrip.infosec.riskverify.biz.RiskVerifyBiz;
import com.ctrip.infosec.sars.monitor.util.Utils;
import com.ctrip.infosec.sars.util.GlobalConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangsx on 2015/1/29.
 */
public abstract class IOrderIndex {
    @Autowired
    private RiskVerifyBiz biz;
    private String identifier;
    private String subject;
    private String exchange;
    private String cp;
    private String fact;
    private int batchSize;
    private int consumeMaxThreadSize;
    private IAsyncConsumer consumer;

    public IOrderIndex(String identifier, String subject, String exchange, String cp, String fact) {
        this(identifier, subject, exchange, cp, fact, 20, 5);
    }

    /**
     * FWS:http://ws.config.framework.fws.qa.nt.ctripcorp.com/Configws/ServiceConfig/ConfigInfoes/Get/
     * UAT:http://ws.config.framework.uat.qa.nt.ctripcorp.com/Configws/ServiceConfig/ConfigInfoes/Get/
     * PRD:http://ws.config.framework.sh.ctripcorp.com/Configws/ServiceConfig/ConfigInfoes/Get/
     */
    public IOrderIndex(String identifier, String subject, String exchange, String cp, String fact,
                       int batchSize, int consumeMaxThreadSize) {
        this.identifier = identifier;
        this.subject = subject;
        this.exchange = exchange;
        this.cp = cp;
        this.fact = fact;
        this.batchSize = batchSize;
        this.consumeMaxThreadSize = consumeMaxThreadSize;
//        System.out.println(GlobalConfig.getString("CMessageUrl"));
        Config.setConfigWsUri(GlobalConfig.getString("CMessageUrl"));
//        Config.setConfigWsUri("http://ws.config.framework.sh.ctripcorp.com/Configws/ServiceConfig/ConfigInfoes/Get/");

        Config.setAppId("100000557");
    }

    public IAsyncConsumer setConsumer() {
        IAsyncConsumer consumer = null;
        try {
            consumer = ConsumerFactory.instance.createConsumerAsAsync(identifier, subject, exchange);
        } catch (IllegalTopic illegalTopic) {
            illegalTopic.printStackTrace();
        } catch (IllegalExchangeName illegalExchangeName) {
            illegalExchangeName.printStackTrace();
        }
        return consumer;
    }

    public void handleMessage(IMessage message) {
        try {

            Map map = Utils.JSON.parseObject(new String(message.getBody(), Charset.forName("utf-8")), Map.class);
//            Map map = Utils.JSON.parseObject(new String(message.getBody(), Charset.forName("utf-8")), Utils.JSON.constructCollectionType(Map.class,));
            List<Map> subjects = (List<Map>) map.get("Subjects");
            for (Map item : subjects) {
                RiskFact req = new RiskFact();
                req.setEventPoint(cp);
                Map _map = new HashMap();
                for (Object o : item.keySet()) {
                    String key = o.toString();
                    String first = key.substring(0, 1).toLowerCase();
                    String rest = key.substring(1, key.length());
                    _map.put(first + rest, item.get(key));
                }
                req.setEventBody(_map);
//                System.out.println(Utils.JSON.toJSONString(req));
                biz.exe(req, fact);
            }
        } catch (Throwable throwable) {

        } finally {
            message.setAcks(AckMode.Ack);
            message.dispose();
        }
    }

    @PostConstruct
    public void init() {
        consumer = setConsumer();
        if (consumer != null) {
            consumer.addConsumerCallbackEventHandler(new IConsumerCallbackEventHandler() {
                @Override
                public void callback(IMessage iMessage) throws Exception {
                    handleMessage(iMessage);
                }
            });
            consumer.setBatchSize(batchSize);
            consumer.ConsumeAsync(consumeMaxThreadSize, false);
        }
    }

    public void stop() {
        if (consumer != null) {
            consumer.stop();
        }
    }

    @Deprecated
    public void assembleAndSend(byte[] body) {
//        Config.setConfigWsUri("http://ws.config.framework.sh.ctripcorp.com/Configws/ServiceConfig/ConfigInfoes/Get/");
//        Config.setAppId("100000557");
        Map map = Utils.JSON.parseObject(new String(body, Charset.forName("utf-8")), Map.class);
        List<Map> subjects = (List<Map>) map.get("Subjects");
        for (Map item : subjects) {
            RiskFact req = new RiskFact();
            req.setEventPoint(cp);
            req.setEventBody(item);
            biz.exe(req, fact);
        }
    }
}