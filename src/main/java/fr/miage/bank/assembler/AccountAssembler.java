package fr.miage.bank.assembler;

import fr.miage.bank.controller.AccountController;
import fr.miage.bank.controller.CartController;
import fr.miage.bank.controller.OperationController;
import fr.miage.bank.entity.Account;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountAssembler implements RepresentationModelAssembler<Account, EntityModel<Account>> {
    @Override
    public EntityModel<Account> toModel(Account entity) {
        String userId = entity.getOwner().getId();
        return EntityModel.of(entity,
                linkTo(methodOn(AccountController.class)
                        .getOneAccountById(userId, entity.getIBAN())).withSelfRel(),
                linkTo(methodOn(AccountController.class)
                        .getAllAccountsByUserId(userId)).withRel("collection"),
                linkTo(methodOn(CartController.class)
                        .getAllCartsByAccountId(entity.getIBAN(),userId)).withRel("cartes"),
                linkTo(methodOn(OperationController.class)
                        .getAllOperationsByAccountId(entity.getIBAN(),userId)).withRel("operations"));
    }

    @Override
    public CollectionModel<EntityModel<Account>> toCollectionModel(Iterable<? extends Account> entities) {
        List<EntityModel<Account>> accountModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());

        EntityModel<Account> firstAccount = accountModel.get(0);

        return CollectionModel.of(accountModel,
                linkTo(methodOn(AccountController.class)
                        .getAllAccountsByUserId(firstAccount.getContent().getOwner().getId())).withSelfRel());
    }
}

