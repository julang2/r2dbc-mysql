/*
 * Copyright 2018-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.miku.r2dbc.mysql;

import com.zaxxer.hikari.HikariDataSource;
import io.r2dbc.spi.test.TestKit;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Duration;
import java.util.Optional;

/**
 * An implementation of {@link TestKit}.
 */
class MySqlTestKit implements TestKit<String> {

    private final MySqlConnectionFactory connectionFactory;

    private final JdbcTemplate jdbcOperations;

    MySqlTestKit() {
        MySqlConnectionConfiguration configuration = IntegrationTestSupport.configuration(false, null);

        this.connectionFactory = MySqlConnectionFactory.from(configuration);
        this.jdbcOperations = jdbc(configuration);
    }

    @Override
    public MySqlConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    @Override
    public JdbcTemplate getJdbcOperations() {
        return jdbcOperations;
    }

    @Override
    public String getCreateTableWithAutogeneratedKey() {
        return "CREATE TABLE test (id INT PRIMARY KEY AUTO_INCREMENT, value INT)";
    }

    @Override
    public String getInsertIntoWithAutogeneratedKey() {
        return "INSERT INTO test VALUES (DEFAULT,100)";
    }

    @Override
    public String getIdentifier(int index) {
        return "v" + index;
    }

    @Override
    public String getPlaceholder(int index) {
        return "?v" + index;
    }

    @Override
    public String clobType() {
        return "TEXT";
    }

    private static JdbcTemplate jdbc(MySqlConnectionConfiguration configuration) {
        HikariDataSource source = new HikariDataSource();

        source.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s", configuration.getDomain(), configuration.getPort(), configuration.getDatabase()));
        source.setUsername(configuration.getUser());
        source.setPassword(Optional.ofNullable(configuration.getPassword()).map(Object::toString).orElse(null));
        source.setMaximumPoolSize(1);
        source.setConnectionTimeout(Optional.ofNullable(configuration.getConnectTimeout()).map(Duration::toMillis).orElse(0L));

        return new JdbcTemplate(source);
    }
}
