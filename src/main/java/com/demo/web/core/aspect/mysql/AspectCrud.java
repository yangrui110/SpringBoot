package com.demo.web.core.aspect.mysql;

import com.demo.web.core.crud.centity.CEntity;
import com.demo.web.core.crud.centity.COrderBy;
import com.demo.web.core.xmlEntity.EntityMap;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/11 16:01
 */
@Component
@Aspect
public class AspectCrud {

    /***
     * 切面拦截mysql操作
     */
    @Around(value="execution(public * com.demo.web.core.crud.*.service.*.*(..))")
    public Object doCheck(ProceedingJoinPoint point) throws Throwable {
        Object[] args=point.getArgs();
        System.out.println("方法名："+point.getSignature().getName());
        String orign= (String) args[0];

        for (Object a:args) {
            if(a instanceof Map) {
                EntityMap.yanzhengMap((Map<String, Object>) a, orign);
            }else if(a instanceof List){
                if(((List) a).size()>0){
                    if(((List) a).get(0) instanceof CEntity) {
                        Map map = new HashMap();
                        ((List) a).forEach((k) -> {
                            map.put(((CEntity) k).getLeft(), "");
                        });
                        EntityMap.yanzhengMap((Map<String, Object>) map, orign);
                    }
                }
            }
        }
        //当是findAll方法是才会进行列属性转换
        if(args[1]==null)
            args[1]=new ArrayList<>();
        if("findAll".equals(point.getSignature().getName()))
            args[5] =EntityMap.readEntityToCondition(orign, (List<CEntity>) args[1], (List<COrderBy>) args[4]);
        args[0]=EntityMap.getTableName(orign);

        Object os= point.proceed(args);
        return os;
    }

    /***
     * 切面拦截sql操作
     * */


}
