package io.github.taoganio.jdbc.provider;

import io.github.taoganio.datasource.provider.ReadWriteScopeRouter;

import java.util.List;
import java.util.Map;

public class RouterConfig {

    private Map<String, List<String>> datasourceRouter;
    private List<ReadWriteScopeRouter> readwriteRouter;

    public Map<String, List<String>> getDatasourceRouter() {
        return datasourceRouter;
    }

    public void setDatasourceRouter(Map<String, List<String>> datasourceRouter) {
        this.datasourceRouter = datasourceRouter;
    }

    public List<ReadWriteScopeRouter> getReadwriteRouter() {
        return readwriteRouter;
    }

    public void setReadwriteRouter(List<ReadWriteScopeRouter> readwriteRouter) {
        this.readwriteRouter = readwriteRouter;
    }

}