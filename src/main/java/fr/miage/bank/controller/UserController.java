package fr.miage.bank.controller;

import fr.miage.bank.assembler.UserAssembler;
import fr.miage.bank.entity.User;
import fr.miage.bank.input.UserInput;
import fr.miage.bank.repository.UserRepository;
import fr.miage.bank.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ReflectionUtils;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RestController
@ExposesResourceFor(User.class)
@RequestMapping(value = "/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserAssembler assembler;
    private final UserValidator validator;
    private final PasswordEncoder passwordEncoder;


    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        Iterable<User> allUsers = userRepository.findAll();
        return ResponseEntity.ok(assembler.toCollectionModel(allUsers));
    }

    @GetMapping(value = "/{userId}")
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getOneUserById(@PathVariable("userId") String id){
        return Optional.of(userRepository.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    @Transactional
    public ResponseEntity<?> saveUser(@RequestBody @Valid UserInput user){
        User user2save = new User(
                UUID.randomUUID().toString(),
                user.getLastname(),
                user.getFirstname(),
                user.getBirthdate(),
                user.getCountry(),
                user.getNoPassport(),
                user.getNoTel(),
                user.getEmail(),
                passwordEncoder.encode(user.getPassword())
        );

        User saved = userRepository.save(user2save);

        URI location = linkTo(UserController.class).slash(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(value = "/{userId}")
    @Transactional
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable("userId") String userId){
        Optional<User> body = Optional.ofNullable(user);

        if(!body.isPresent()){
            return ResponseEntity.badRequest().build();
        }

        if(!userRepository.existsById(userId)){
            return ResponseEntity.notFound().build();
        }

        user.setId(userId);
        User result = userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{userId}")
    @Transactional
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> updateUserPartial(@PathVariable("userId") String userId,
                                               @RequestBody Map<Object, Object> fields){

        Optional<User> body = userRepository.findById(userId);

        if(body.isPresent()){
            User user = body.get();

            fields.forEach((f,v) -> {
                Field field = ReflectionUtils.findField(User.class, f.toString());
                field.setAccessible(true);

                if(field.getType() == Date.class){
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);
                    try {
                        ReflectionUtils.setField(field, user, formatter.parse(v.toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else{
                    ReflectionUtils.setField(field, user, v);
                }
            });

            validator.validate(new UserInput(user.getLastname(), user.getFirstname(), user.getEmail(),user.getPassword(),user.getBirthdate()
                 ,user.getCountry(),user.getNoPassport(),user.getNoTel()));
            user.setId(userId);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}
