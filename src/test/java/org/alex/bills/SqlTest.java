package org.alex.bills;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@SpringBootTest
class SqlTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    void initDataBase() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            clearDatabase(connection);
            Resource schema = resourceLoader.getResource("classpath:schema.sql");
            ScriptUtils.executeSqlScript(connection, schema);
        }
    }

    private void clearDatabase(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
        }
    }
}
