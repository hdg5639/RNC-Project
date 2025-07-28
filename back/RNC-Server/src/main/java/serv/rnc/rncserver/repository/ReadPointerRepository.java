package serv.rnc.rncserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import serv.rnc.rncserver.entity.readPointer.ReadPointer;
import serv.rnc.rncserver.entity.readPointer.ReadPointerId;

@Repository
public interface ReadPointerRepository extends JpaRepository<ReadPointer, ReadPointerId> {
}
