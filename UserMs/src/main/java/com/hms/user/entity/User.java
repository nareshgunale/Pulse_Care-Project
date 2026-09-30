package com.hms.user.entity;

import com.hms.user.constant.Roles;
import com.hms.user.dto.UserDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity()
@Table(
        name = "user_detail",
        indexes = {
                @Index(name = "idx_user_email",      columnList = "email",    unique = true),
                @Index(name = "idx_user_role",       columnList = "role"),
                @Index(name = "idx_user_profile_id", columnList = "profileId")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Email
    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Roles role;
    private Long profileId;

    @OneToOne(mappedBy = "user")
    private ForgotPassword forgotPassword;

    public UserDTO toDTO() {
        return new UserDTO(this.id, this.name, this.email, this.password, this.role, this.profileId,null);
    }
}
