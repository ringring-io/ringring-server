package com.zirgoo.server.config;

import java.util.Properties;

/**
 * Created by kosztope on 24/01/14.
 */
public interface ConfigManager {

    Properties getProperties();

    String getServerHost();
    int getServerPort();

    int getSslPort();
    boolean getSslEnabled();
    String getSslKeystorePath();
    String getSslKeystorePassword();
    String getSslKeymanagerPassword();

    String getDatabaseDriver();
    String getDatabaseUrl();
    String getDatabaseUser();
    String getDatabasePassword();

    String getSipDomain();
    String getSipContext();

    String getSmtpHost();
    int getSmtpPort();
    String getSmtpAuth();
    String getSmtpUser();
    String getSmtpPassword();
    String getSmtpFrom();
    String getSmtpRealname();

    String getActivationCodeSubject();
    String getActivationCodeBody();

    int getInvitationLimit();
    String getInvitationSubject();
    String getInvitationBody();

    String getRenewActivationCodeSubject();
    String getRenewActivationCodeBody();

    int getActivationCodeLength();
}
