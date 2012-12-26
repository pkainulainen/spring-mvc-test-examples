package net.petrikainulainen.spring.testmvc.user.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Petri Kainulainen
 */
public class UserDTO {

    private String username;

    private SecurityRole role;

    public UserDTO(String username, SecurityRole role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public SecurityRole getRole() {
        return role;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
