package serv.rnc.rncserver.dto.user;

import lombok.*;
import serv.rnc.rncserver.enums.UserRole;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDTO {
    private String email;
    private String username;
    private String password;
    private UserRole role;
}
