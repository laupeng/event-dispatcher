package rest;

import com.ctrip.infosec.common.model.RiskFact;
import com.ctrip.infosec.common.model.RiskResult;
import com.ctrip.infosec.configs.event.Channel;
import static com.ctrip.infosec.configs.utils.Utils.JSON;
import handlerImpl.Handler;
import manager.Receiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zhangsx on 2015/2/3.
 */
@Controller
public class Restful implements Receiver {

    @Autowired
    private Handler handler;

    /**
     * 风控审核（针对外部PD、只返回finalResult）
     *
     * @param fact
     * @return
     */
    @RequestMapping(value = "/riskverify", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RiskResult> riskverify(@RequestBody RiskFact fact) {
        RiskResult result = handler.verify(Channel.REST, fact);
        return new ResponseEntity<RiskResult>(result, HttpStatus.OK);
    }

    /**
     * 执行规则验证（针对支付风控、返回finalResult以及results）
     *
     * @param factTxt
     * @return
     */
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RiskFact> execute(@RequestBody String factTxt) {
        RiskFact fact = JSON.parseObject(factTxt, RiskFact.class);
        fact = handler.execute(Channel.REST, fact);
        fact.eventBody.clear();
        fact.ext.clear();
        return new ResponseEntity<RiskFact>(fact, HttpStatus.OK);
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public @ResponseBody
    String checkHealth() {
        return "hello!";
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void restart() {

    }

}
