package fr.miage.bank.security;

import fr.miage.bank.entity.User;
import fr.miage.bank.repository.UserRepository;
import fr.miage.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class UserPermissionEvaluator implements TargetedPermissionEvaluator {

    private final UserRepository userRepository;

    @Override
    public String getTargetType(){
        return User.class.getSimpleName();
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission){
        throw new UnsupportedOperationException("Not supported by this PermissionEvaluator: " + UserPermissionEvaluator.class);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

        boolean authorized = false;

        String perm = permission.toString();

        User springUser = userRepository.findByEmail(authentication.getName()).get();
        User user = userRepository.findById(targetId.toString()).get();

        switch (perm) {
            case "MANAGE_USER" :
                authorized = springUser.getId().equals(user.getId());
                break;

            default:
                break;
        }
        return authorized;
    }
}
