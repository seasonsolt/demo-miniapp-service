package com.example.demominiapp.e2e;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class ProbeRepository {

    private final JdbcTemplate jdbcTemplate;

    ProbeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    ProbeResponse save(ProbeResponse probe) {
        jdbcTemplate.update("""
                        MERGE INTO e2e_probe (probe_id, probe_value, updated_by, updated_at)
                        KEY (probe_id)
                        VALUES (?, ?, ?, ?)
                        """,
                probe.probeId(),
                probe.value(),
                probe.updatedBy(),
                probe.updatedAt());
        return probe;
    }

    Optional<ProbeResponse> findById(String probeId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                            SELECT probe_id, probe_value, updated_by, updated_at
                            FROM e2e_probe
                            WHERE probe_id = ?
                            """,
                    (rs, rowNum) -> new ProbeResponse(
                            rs.getString("probe_id"),
                            rs.getString("probe_value"),
                            rs.getString("updated_by"),
                            rs.getString("updated_at")
                    ),
                    probeId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    boolean deleteById(String probeId) {
        return jdbcTemplate.update("DELETE FROM e2e_probe WHERE probe_id = ?", probeId) > 0;
    }

    int deleteByPrefix(String probeIdPrefix) {
        String escapedPrefix = probeIdPrefix
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return jdbcTemplate.update(
                "DELETE FROM e2e_probe WHERE probe_id LIKE ? ESCAPE '!'",
                escapedPrefix + "%");
    }
}
