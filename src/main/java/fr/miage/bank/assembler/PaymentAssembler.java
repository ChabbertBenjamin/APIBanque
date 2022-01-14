package fr.miage.bank.assembler;

import fr.miage.bank.controller.CartController;
import fr.miage.bank.controller.PaymentController;
import fr.miage.bank.entity.Payment;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaymentAssembler implements RepresentationModelAssembler<Payment, EntityModel<Payment>> {

    @Override
    public EntityModel<Payment> toModel(Payment entity) {
        String userId = entity.getCart().getAccount().getOwner().getId();
        String iban = entity.getCart().getAccount().getIBAN();
        String cartId = entity.getCart().getId();

        return EntityModel.of(entity,
                linkTo(methodOn(PaymentController.class)
                        .getOnePaiementById(userId, iban, cartId, entity.getId())).withSelfRel(),
                linkTo(methodOn(CartController.class)
                        .getOneCartByIdAndAccountId(userId, iban, cartId)).withRel("carte"));
    }

    @Override
    public CollectionModel<EntityModel<Payment>> toCollectionModel(Iterable<? extends Payment> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
