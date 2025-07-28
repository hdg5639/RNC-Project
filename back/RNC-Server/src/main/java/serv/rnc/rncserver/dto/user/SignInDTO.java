package serv.rnc.rncserver.dto.user;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SignInDTO {
    private String username;
    private String email;
    private String token;
}
