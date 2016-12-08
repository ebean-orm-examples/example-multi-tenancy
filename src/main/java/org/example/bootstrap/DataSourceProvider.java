package org.example.bootstrap;

import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.TenantDataSourceProvider;
import org.avaje.datasource.DataSourceConfig;
import org.avaje.datasource.DataSourcePool;
import org.avaje.datasource.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

class DataSourceProvider implements TenantDataSourceProvider {

  private static final Logger log = LoggerFactory.getLogger(DataSourceProvider.class);

  private final ConcurrentHashMap<Object, DataSource> cache = new ConcurrentHashMap<>();

  private final ServerConfig serverConfig;

  DataSourceProvider(ServerConfig serverConfig) {
    this.serverConfig = serverConfig;
  }

  @Override
  public DataSource dataSource(Object tenantId) {

    if (tenantId == null) {
      tenantId = "default";
    }
    return getOrCreate(tenantId);
  }

  private DataSource getOrCreate(Object tenantId) {
    return cache.computeIfAbsent(tenantId, s -> createAndMigrateDataSource(tenantId));
  }

  @Override
  public void shutdown(boolean deregisterDriver) {

    log.info("Shutdown all DataSources");
    ConcurrentHashMap.KeySetView<Object, DataSource> keys = cache.keySet();
    keys.forEach(tenantId -> shutdownDataSource(cache.remove(tenantId), deregisterDriver));
  }

  private void shutdownDataSource(DataSource dataSource, boolean deregisterDriver) {
    if (dataSource instanceof DataSourcePool) {
      ((DataSourcePool)dataSource).shutdown(deregisterDriver);
    }
  }

  private DataSource createAndMigrateDataSource(Object tenantId) {

    DataSource dataSource = createDataSource(tenantId);
    return serverConfig.runDbMigration(dataSource);
  }

  private DataSource createDataSource(Object tenantId) {

    log.info("Create DataSource for tenantId:{}", tenantId);

    DataSourceConfig config = new DataSourceConfig();
    config.setDriver("org.h2.Driver");
    config.setUrl("jdbc:h2:./db/database_"+tenantId);
    config.setUsername("sa");
    config.setPassword("");

    return new Factory().createPool(tenantId.toString(), config);
  }
}
