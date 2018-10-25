package devlab.app.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

//@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {


    @Id
    @Column(name = "user")
    @NotNull
    private String userName;

    @Column(name = "password")
    @NotNull
    private String password;

    @NotNull
    private int enabled = 1;

}
