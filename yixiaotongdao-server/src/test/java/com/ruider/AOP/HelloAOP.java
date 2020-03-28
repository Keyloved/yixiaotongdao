package com.ruider.AOP;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

import org.springframework.stereotype.Component;

@Aspect
@Component
public class HelloAOP {
    private final static String POINT_CUT = "execution(public * com.ruider.AOP ..* .*(..))";

    @Pointcut(POINT_CUT)
    public void point(){}

    @Before("point()")
    public void before(){
        System.out.println("执行print方法前");
    }

    @After("point()")
    public void after(){
        System.out.println("执行print方法后");
    }

    @AfterReturning("point()")
    public void afterReturning(){
        System.out.println("执行print方法正常结束");
    }

    @AfterThrowing("point()")
    public void afterThrowing(){
        System.out.println("执行print方法出现异常");
    }
}
