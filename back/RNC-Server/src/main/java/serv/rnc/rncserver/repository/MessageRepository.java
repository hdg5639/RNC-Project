package serv.rnc.rncserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import serv.rnc.rncserver.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
