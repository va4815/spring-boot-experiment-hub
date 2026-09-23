package com.va4815.bearerjwtwithrefresh.repository;

import com.va4815.bearerjwtwithrefresh.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(byte[] tokenHash);

    @Query(nativeQuery = true, value = "select t.* from refresh_token t where t.token_hash = :tokenHash")
    Optional<RefreshToken> findByTokenHashForUpdate(@Param("tokenHash") byte[] tokenHash);

    @Query(nativeQuery = true, value = "delete from refresh_token t where t.token_hash = :tokenHash")
    void deleteRefreshToken(@Param("tokenHash") byte[] tokenHash);
}
