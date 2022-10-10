module com.cloud.cloudclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires org.slf4j;
    requires java.logging;
    requires java.naming;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires bson4jackson;
    requires java.sql;
    requires com.google.common;
    requires jfxanimation;
    requires io.netty.transport;
    requires io.netty.common;
    requires io.netty.codec;
    requires io.netty.handler;
    requires io.netty.buffer;

    opens com.cloud.cloudclient to javafx.fxml;
    exports com.cloud.cloudclient;
    exports com.cloud.cloudclient.entity;
    exports com.cloud.cloudclient.fxcontrollers;
    opens com.cloud.cloudclient.fxcontrollers to javafx.fxml;
    exports com.cloud.cloudclient.view;
    opens com.cloud.cloudclient.view to javafx.fxml;
    exports com.cloud.common.entity;
    exports com.cloud.cloudclient.utils;
    opens com.cloud.cloudclient.utils to javafx.fxml;
    exports com.cloud.cloudclient.view.enums;
    opens com.cloud.cloudclient.view.enums to javafx.fxml;
}