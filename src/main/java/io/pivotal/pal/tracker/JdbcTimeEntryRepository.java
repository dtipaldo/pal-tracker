package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private JdbcTemplate jdbcTemplate;
    private RowMapper<TimeEntry> rowMapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong(1),
            rs.getLong(2),
            rs.getLong(3),
            rs.getDate(4).toLocalDate(),
            rs.getInt(5)
    );

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement sql = connection.prepareStatement(
                    "INSERT INTO time_entries (project_id, user_id, date, hours) " +
                            "VALUES (?, ?, ?, ?)",
                    RETURN_GENERATED_KEYS
            );
            sql.setLong(1, timeEntry.getProjectId());
            sql.setLong(2, timeEntry.getUserId());
            sql.setDate(3, Date.valueOf(timeEntry.getDate()));
            sql.setInt(4, timeEntry.getHours());
            return sql;
        }, generatedKeyHolder);
        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        return jdbcTemplate.query("SELECT * FROM time_entries WHERE id = ?", new Object[]{timeEntryId}, rs -> rs.next() ? rowMapper.mapRow(rs, 1) : null);
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query("SELECT * FROM time_entries",
                rowMapper
        );
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        jdbcTemplate.update(connection -> {
            PreparedStatement sql = connection.prepareStatement(
                    "UPDATE time_entries SET project_id = ?, user_id = ?, date = ?, hours = ? WHERE id = ?"
            );
            sql.setLong(1, timeEntry.getProjectId());
            sql.setLong(2, timeEntry.getUserId());
            sql.setDate(3, Date.valueOf(timeEntry.getDate()));
            sql.setInt(4, timeEntry.getHours());
            sql.setLong(5, id);
            return sql;
        });
        return find(id);
    }

    @Override
    public void delete(long timeEntryId) {
        jdbcTemplate.update("DELETE FROM time_entries WHERE id = ?", timeEntryId);
    }
}
