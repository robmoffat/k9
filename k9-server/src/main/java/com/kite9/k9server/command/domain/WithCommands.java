package com.kite9.k9server.command.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.kite9.k9server.command.Command;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WithCommands {

	Class<? extends Command>[] value();
}
