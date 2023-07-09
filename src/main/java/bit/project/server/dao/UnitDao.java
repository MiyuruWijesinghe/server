package bit.project.server.dao;

import bit.project.server.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitDao extends JpaRepository<Unit,Integer> {
}
