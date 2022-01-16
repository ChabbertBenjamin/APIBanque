--INSERT INTO Role (id, name)VALUES
--   (1, 'ROLE_USER'),
--   (2, 'ROLE_ADMIN');

INSERT INTO user (id, lastname, firstname, birthdate, email, password,country, no_passport, no_tel) VALUES
    ('1','Chabbert', 'Benjamin', '1998-06-13', 'benjamin@chabbert.fr', '$argon2id$v=19$m=4096,t=3,p=1$HuEg1ZJsyu9B1CYwzkzlJw$BOZcZFCWVfLtSBBFCGrltr/j5X9IvwzLI66QTENXS0s','France', '2', '0600000000'), --password=1234
    ('2','Test', 'Thomas',  '1997-08-20', 'thomas@test.fr', '$argon2id$v=19$m=4096,t=3,p=1$dnClZco7cvp82T5tn6BwZg$ghgspBxo2+yLiVcVB8FltcgL1KgmfkDcKAEDtpaB4ps','France', '3', '0600000011'); --password=test

--INSERT INTO User_Roles  (user_id, roles_id)VALUES
--    ('1', 1);

INSERT INTO account (secret, IBAN, country ,solde,user_id) VALUES
    ('1234','546784651184','France',50.0,1),
    ('0123','546784651183','France',500.0,2),
    ('4567','546784651182','USA',110.0,2);

INSERT INTO cart (code, crypto, freeze, localisation, plafond, contact_less, virtual, account_IBAN,num,date_expiry) VALUES
    (1234, 123, false, false, 500, true, false,'546784651184','1234567891234567','2022-01-01'),
    (4567, 456, false, false, 500, true, false,'546784651183','1234567891234568','2022-01-01');

INSERT INTO operation (date, text, amount, taux, creditor_account_iban,debitor_account_iban, category, country) VALUES
    (Current_Timestamp(), 'payement', 30, 1, '546784651184', '546784651183', 'vente', 'France'),
    (Current_Timestamp(), 'remboursement', 30, 1, '546784651183','546784651184','achat', 'France');

INSERT INTO payment (id, cart_id, amount, country, crebitor_account_iban, date) VALUES
    ('1', '1', 10, 'France', '546784651183',Current_Timestamp());