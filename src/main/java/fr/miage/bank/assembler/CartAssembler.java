package fr.miage.bank.assembler;

import fr.miage.bank.controller.AccountController;
import fr.miage.bank.controller.CartController;
import fr.miage.bank.controller.PaymentController;
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
        String userId = entity.getAccount().getOwner().getId();
        return EntityModel.of(entity,
                linkTo(methodOn(CartController.class)
                        .getOneCartByIdAndAccountId(entity.getAccount().getIBAN(), entity.getId(),userId)).withSelfRel(),
                linkTo(methodOn(AccountController.class)
                        .getOneAccountById(userId, entity.getAccount().getIBAN())).withRel("account"),
                linkTo(methodOn(PaymentController.class)
                        .getAllPaiementsByCarteId(userId, entity.getAccount().getIBAN(),entity.getId())).withRel("paiement"));
    }

    @Override
    public CollectionModel<EntityModel<Cart>> toCollectionModel(Iterable<? extends Cart> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
