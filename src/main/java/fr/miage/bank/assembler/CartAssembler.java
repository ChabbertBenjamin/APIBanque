package fr.miage.bank.assembler;

import fr.miage.bank.controller.AccountController;
import fr.miage.bank.controller.CartController;
import fr.miage.bank.entity.Cart;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CartAssembler implements RepresentationModelAssembler<Cart, EntityModel<Cart>> {
    @Override
    public EntityModel<Cart> toModel(Cart entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(CartController.class)
                        .getOneCarteByIdAndAccountId(entity.getAccount().getId(), entity.getId())).withSelfRel(),
                linkTo(methodOn(AccountController.class)
                        .getOneAccountById(entity.getAccount().getId())).withRel("account"));
    }

    @Override
    public CollectionModel<EntityModel<Cart>> toCollectionModel(Iterable<? extends Cart> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
