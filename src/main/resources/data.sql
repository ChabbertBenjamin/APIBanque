INSERT INTO account (lastname, firstname, birthdate, country, no_passeport, no_tel, secret, IBAN) VALUES
    ('Chabbert', 'Benjamin', '1998-06-13', 'France', '2', '0600000000','1234','546784651184');

INSERT INTO cart (id, code, crypto, freeze, localisation, plafond, contact_less, virtual, account_id) VALUES
    (1, 1234, 123, false, false, 500, true, false, '1');

INSERT INTO operation (date, time, text, amount, taux, IBAN_creditor, name_creditor, category, country, compte_owner_id) VALUES
    (CURRENT_DATE(), Current_Timestamp(), 'virement noel', 30, 1, '123', 'Thomas', 'Noel', 'France', '1');