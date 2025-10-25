package io.github.taoganio.jdbc.provider.spring.boot.creator;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class HikariDataSourceCreator implements DataSourceCreator {

    private final HikariDataSource hikariDataSource;
    private boolean copyDataSource = true;

    public HikariDataSourceCreator() {
        this(null);
    }

    public HikariDataSourceCreator(HikariDataSource hikariDataSource) {
        if (hikariDataSource == null) {
            this.hikariDataSource = new HikariDataSource();
            this.copyDataSource = false;
        } else {
            this.hikariDataSource = hikariDataSource;
        }
    }

    public void setCopyDataSource(boolean copyDataSource) {
        this.copyDataSource = copyDataSource;
    }

    @Override
    public DataSource create(DataSourceDefinition definition) throws Exception {
        HikariDataSource hikariDataSource = this.hikariDataSource;
        if (copyDataSource) {
            hikariDataSource = new HikariDataSource();
            this.hikariDataSource.copyStateTo(hikariDataSource);
        }
        DataSourceUtils.setHikariDataSourceProperty(hikariDataSource, definition, true);
        return hikariDataSource;
    }
}

