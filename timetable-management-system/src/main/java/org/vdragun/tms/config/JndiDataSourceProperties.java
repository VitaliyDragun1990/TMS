package org.vdragun.tms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Vitaliy Dragun
 *
 */
@Component
@ConfigurationProperties(prefix = "jndi.datasource")
public class JndiDataSourceProperties {

    private String implicitCachingEnabled;
    private String password;
    private String user;
    private String driverClassName;
    private String factory;
    private String jdbcUrl;
    private String idleTimeout;
    private String maximumPoolSize;
    private String minimumIdle;
    private String resourceName;
    private String resourceType;

    public String getImplicitCachingEnabled() {
        return implicitCachingEnabled;
    }

    public void setImplicitCachingEnabled(String implicitCachingEnabled) {
        this.implicitCachingEnabled = implicitCachingEnabled;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(String idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public String getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(String maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public String getMinimumIdle() {
        return minimumIdle;
    }

    public void setMinimumIdle(String minimumIdle) {
        this.minimumIdle = minimumIdle;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public String toString() {
        return "JndiDataSourceProperties [implicitCachingEnabled=" + implicitCachingEnabled + ", password=" + password
                + ", user=" + user + ", driverClassName=" + driverClassName + ", factory=" + factory + ", jdbcUrl="
                + jdbcUrl + ", idleTimeout=" + idleTimeout + ", maximumPoolSize=" + maximumPoolSize + ", minimumIdle="
                + minimumIdle + ", resourceName=" + resourceName + ", resourceType=" + resourceType + "]";
    }

}
