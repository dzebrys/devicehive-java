package com.devicehive.websockets.handlers.annotations;


import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Qualifier
@Inherited
public @interface WebsocketController {

}
