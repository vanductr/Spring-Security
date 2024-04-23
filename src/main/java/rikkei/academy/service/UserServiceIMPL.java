package rikkei.academy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rikkei.academy.model.dto.request.FormLogin;
import rikkei.academy.model.dto.request.FormRegister;
import rikkei.academy.model.dto.response.JWTResponse;
import rikkei.academy.model.entity.Role;
import rikkei.academy.model.entity.RoleName;
import rikkei.academy.model.entity.User;
import rikkei.academy.repository.IRoleRepository;
import rikkei.academy.repository.IUserRepository;
import rikkei.academy.security.jwt.JWTProvider;
import rikkei.academy.security.principle.UserDetailsCustom;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class UserServiceIMPL implements IUserService{
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public boolean register(FormRegister formRegister) {
        User user = User.builder()
                .email(formRegister.getEmail())
                .username(formRegister.getUsername())
                .fullName(formRegister.getFullName())
                .password(passwordEncoder.encode(formRegister.getPassword()))
                .status(true)
                .build();

        if (formRegister.getRoles() != null && !formRegister.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            formRegister.getRoles().forEach(
                    r -> {
                        switch (r) {
                            case "ADMIN":
                                roles.add(roleRepository.findByRoleName(RoleName.ROLE_ADMIN).orElseThrow(() -> new NoSuchElementException("role not found")));
                            case "MANAGER":
                                roles.add(roleRepository.findByRoleName(RoleName.ROLE_MANAGER).orElseThrow(() -> new NoSuchElementException("role not found")));
                            case "USER":
                                roles.add(roleRepository.findByRoleName(RoleName.ROLE_USER).orElseThrow(() -> new NoSuchElementException("role not found")));
                            default:
                                roles.add(roleRepository.findByRoleName(RoleName.ROLE_USER).orElseThrow(() -> new NoSuchElementException("role not found")));
                        }
                    }
            );
            user.setRoleSet(roles);

        } else {
            // Mặc định là User
            Set<Role> roleSet = new HashSet<>();
            roleSet.add(roleRepository.findByRoleName(RoleName.ROLE_USER).orElseThrow(() -> new NoSuchElementException("role not found")));
            user.setRoleSet(roleSet);
        }
        userRepository.save(user);
        return true;
    }

    @Override
    public JWTResponse login(FormLogin formLogin) {
        // Xác thực thông qua username và password.
        Authentication authentication = null;
        try {
            authentication = manager.authenticate(new UsernamePasswordAuthenticationToken(formLogin.getUsername(), formLogin.getPassword()));
        } catch (AuthenticationException e) {
            throw new  RuntimeException("username hoặc password không chính xác");
        }
        UserDetailsCustom detailsCustom = (UserDetailsCustom) authentication.getPrincipal();

        String accessToken = jwtProvider.generateAccessToken(detailsCustom);

        return JWTResponse.builder()
                .email(detailsCustom.getEmail())
                .fullName(detailsCustom.getFullName())
                .roleSet(detailsCustom.getAuthorities())
                .status(detailsCustom.isStatus())
                .accessToken(accessToken)
                .build();
    }
}
