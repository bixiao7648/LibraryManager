package aspect;

import com.orhanobut.logger.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 18-8-7.
 */

@Aspect
public class AspectTest {

    private Map<String, Integer> mCountMap = new HashMap<>();

    @Before("execution(* com..**(..))")
    public void onActivityMethodBefore2(JoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toString();
        int count = mCountMap.getOrDefault(key, 0);
        if (count > 20) {
            return;
        }

        Logger.i(key + "\n" + Arrays.toString(joinPoint.getArgs()));

        mCountMap.put(key, count + 1);
    }
}
