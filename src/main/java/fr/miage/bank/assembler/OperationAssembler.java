package fr.miage.bank.assembler;

import fr.miage.bank.controller.AccountController;
import fr.miage.bank.controller.OperationController;
import fr.miage.bank.entity.Operation;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OperationAssembler implements RepresentationModelAssembler<Operation, EntityModel<Operation>> {
    @Override
    public EntityModel<Operation> toModel(Operation entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(AccountController.class)
                        .getOneAccountById(entity.getCompteCreditor().getOwner().getId(), entity.getCompteCreditor().getIBAN())).withRel("account"));

    }

    @Override
    public CollectionModel<EntityModel<Operation>> toCollectionModel(Iterable<? extends Operation> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
