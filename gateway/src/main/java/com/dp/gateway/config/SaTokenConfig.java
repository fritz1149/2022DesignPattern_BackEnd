package com.dp.gateway.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SaTokenConfig {
    // 需要跨域的地址从配置中读取
//    @Value("#{'${allow-origins}'.split(',')}")
    @Value("${allow-origins}")
    String ExternalOrigins;
    /**
     * 注册 [Sa-Token全局过滤器]
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 指定 [拦截路由]
                .addInclude("/**")
                // 指定 [放行路由]
//                .addExclude("/favicon.ico")
                // 指定[认证函数]: 每次请求执行
                .setBeforeAuth(r->{
                    // 跨域，从saToken官网提供的教程里抄的
                    SaHolder.getResponse()
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", ExternalOrigins)
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "*")
                            .setHeader("Access-Control-Allow-Credentials", "true")
                    ;
                })
                .setAuth(obj -> {
                    SaRouter.match("/account/logout").check(r -> StpUtil.checkLogin());
                    SaRouter.match("/ws/**").check(r -> {
                        String token = SaHolder.getRequest().getHeader("Sec-WebSocket-Protocol");
                        // websocket需要在返回报文同样加一个协议的头
                        SaHolder.getResponse().setHeader("Sec-WebSocket-Protocol", token);

                        Object userId = StpUtil.getLoginIdByToken(token);
                        if(userId == null)
                            throw NotLoginException.newInstance("未登录", "-2", token);
                        if(!userId.toString().equals(SaHolder.getRequest().getRequestPath().substring(4)))
                            throw NotLoginException.newInstance("token所属id与欲建立连接的id不同", "-2", token);
                    });
                    SaRouter.match("/**").
                            notMatch("/**/swagger-ui/**").notMatch("/**/swagger-resources/**").notMatch("/**/v2/**").
                            notMatch("/account/**").notMatch("/ws/**").check(r -> StpUtil.checkLogin());
                })
                // 指定[异常处理函数]：每次[认证函数]发生异常时执行此函数
                .setError(e -> {
                    if(e.getClass() == NotLoginException.class) {
                        System.out.println("登录检测未通过");
                        return new SaResult(401, e.getMessage(), null);
                    }
                    return SaResult.error("未处理的拦截类型，请殴打后端同学");
                })
                ;
    }
}
