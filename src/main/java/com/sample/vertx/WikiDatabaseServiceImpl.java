package com.sample.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.ext.jdbc.JDBCClient;
import io.vertx.rxjava.ext.sql.SQLConnection;
import rx.Single;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;

/**
 * Created by adi on 15/06/17.
 */
public class WikiDatabaseServiceImpl implements WikiDatabaseService{


    private final HashMap<SqlQuery, String> sqlQueries;
    private final JDBCClient dbClient;

    WikiDatabaseServiceImpl(io.vertx.ext.jdbc.JDBCClient dbClient, HashMap<SqlQuery, String> sqlQueries, Handler<AsyncResult<WikiDatabaseService>> readyHandler)
    {
        this.dbClient = new JDBCClient(dbClient);
        this.sqlQueries = sqlQueries;

        getConnection().flatMap(conn -> conn.rxExecute(sqlQueries.get(SqlQuery.CREATE_PAGES_TABLE)))
                .map(v -> this)
                .subscribe(RxHelper.toSubscriber(readyHandler));

    }

    private Single<SQLConnection> getConnection() {
        return dbClient.rxGetConnection().flatMap(conn -> {
            Single<SQLConnection> connectionSingle = Single.just(conn); // <1>
            return connectionSingle.doOnUnsubscribe(conn::close); // <2>
        });
    }


    @Override
    public WikiDatabaseService fetchAllPages(Handler<AsyncResult<JsonArray>> resultHandler) {

        getConnection().flatMap(conn -> conn.rxQuery(sqlQueries.get(SqlQuery.ALL_PAGES)))
                .flatMapObservable(res -> {
                    List<JsonArray> results = res.getResults();
                    return rx.Observable.from(results);
                })
                .map(json -> json.getString(0))
                .sorted()
                .collect(JsonArray::new, JsonArray::add)
                .subscribe(RxHelper.toSubscriber(resultHandler));

        return this;
    }

    @Override
    public WikiDatabaseService createPage(String title, String markdown, Handler<AsyncResult<Void>> resultHandler) {
        getConnection()
                .flatMap(conn -> conn.rxUpdateWithParams(sqlQueries.get(SqlQuery.CREATE_PAGE), new JsonArray().add(title).add(markdown)))
                .map(res -> (Void) null)
                .subscribe(RxHelper.toSubscriber(resultHandler));
        return this;
    }
}
