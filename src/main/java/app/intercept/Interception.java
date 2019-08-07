package app.intercept;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 *
 * @author OAK
 *
 */
@Component
@Aspect
@EnableAspectJAutoProxy(exposeProxy = true)
public class Interception {

    public AtomicLong total = new AtomicLong(0);

    Logger logger = LoggerFactory.getLogger(Interception.class);

    @Pointcut("execution(* app.schema.*.*(..))")
    private void pointCutMethodService(){

    }

    @Around("pointCutMethodService()")
    public  Object doAroundService(ProceedingJoinPoint pjp) throws Throwable{
        Instant instant = Instant.now();
        if(pjp !=null){
            Object obj = pjp.proceed();
            Long millis = Instant.now().minus(instant.getMillis()).getMillis();
            Long sumMillis = total.addAndGet(millis);
            Object target = pjp.getTarget();
            if(pjp.getSignature() != null && pjp.getSignature().toString().indexOf("performRequestAsync")!= -1){
                logger.debug("调用Service方法：{}，参数：{}，执行耗时：{}纳秒，耗时：{}毫秒, 总耗时：{}毫秒",
                        pjp.getSignature().toString(), Arrays.toString(pjp.getArgs()), millis*1000000, millis, sumMillis);
            }
            return obj;
        }
       return null;
    }
}