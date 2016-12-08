package org.example.bootstrap;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.TenantMode;
import com.avaje.ebean.config.dbplatform.h2.H2Platform;

public class BootstrapEbean {

  private final ServerConfig config = new ServerConfig();

  public BootstrapEbean() {

    config.getMigrationConfig().setRunMigration(true);
    config.getMigrationConfig().setDbUsername("sa");
    config.getMigrationConfig().setDbPassword("");

    config.setH2ProductionMode(true);
    //config.setDdlGenerate(false);
    //config.setDdlRun(false);
    config.setTenantMode(TenantMode.DB);
    config.setDatabasePlatform(new H2Platform());
    config.setCurrentTenantProvider(new CurrentTenant());
    config.setTenantDataSourceProvider(new DataSourceProvider(config));
  }

  public EbeanServer create() {
    return EbeanServerFactory.create(config);
  }
}
