package com.snowk.blog.api.settings;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface BlogSettingsRepository extends JpaRepository<BlogSettings,Long> {
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select s from BlogSettings s where s.id = 1") Optional<BlogSettings> findForUpdate();
}
