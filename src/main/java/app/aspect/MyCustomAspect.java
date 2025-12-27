package app.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

// This class is Aspect

@Aspect
@Component
public class MyCustomAspect {

    // This method is Advice
    // This expression @Before("bean(indexController)) is Pointcut Expression

//    @Before("execution(* com.app.service.*.*(..))")
//    public void logBeforeMethod() {
//
//    }

    @Before("within(app.web.controller.UserController)")
    public void logMessageWithin() {
        System.out.println("Hello UserController!");
    }

    @Before("bean(userServiceImpl)")
    public void logMessageBean() {
        System.out.println("Hello UserServiceImpl!");
    }

    @Before("@annotation(app.aspect.VeryImportant)")
    public void logMessageAnnotation() {
        System.out.println("Hello annotation!");
    }













}
