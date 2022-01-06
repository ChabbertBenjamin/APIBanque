INSERT INTO user (lastname, firstname, birthdate, email, password,country, no_passport, no_tel) VALUES
    ('Chabbert', 'Benjamin', '1998-06-13', 'benjamin@chabbert.fr', '1234','France', '2', '0600000000'),
    ('Test', 'Thomas',  '1997-08-20', 'thomas@test.fr', '1234','France', '3', '0600000011');
INSERT INTO account (secret, IBAN, country ,solde,user_id) VALUES
    ('1234','546784651184','France',50.0,1),
    ('0123','546784651183','France',500.0,2),
    ('4567','546784651182','USA',110.0,2);

INSERT INTO cart (code, crypto, freeze, localisation, plafond, contact_less, virtual, account_IBAN) VALUES
    (1234, 123, false, false, 500, true, false,'546784651184');

INSERT INTO operation (date, text, amount, taux, creditor_account_iban, name_creditor, category, country, cart_id) VALUES
    (Current_Timestamp(), 'virement noel', 30, 1, '546784651183', 'Thomas', 'Noel', 'France',1);