package de.cerus.twitchbot.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.intellij.lang.annotations.Language;
import org.sqlite.SQLiteDataSource;

public class SqliteService {

    private final Connection connection;

    public SqliteService(final File file) throws SQLException {
        final SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + file.getPath());

        this.connection = dataSource.getConnection();
    }

    public int update(@Language("SQL") final String query, final Object... params) throws SQLException {
        final PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }

        return preparedStatement.executeUpdate();
    }

    public ResultSet execute(@Language("SQL") final String query, final Object... params) throws SQLException {
        final PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }

        return preparedStatement.executeQuery();
    }

}
