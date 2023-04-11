-- Data preparation
INSERT INTO lottery (name, create_date, award, ballot_price, ballot_unit, state)
VALUES ('Weekly Lottery', '2023-02-20 00:00:00', '1000 USD', 500, 'DOLLAR', 'ACTIVE');

INSERT INTO lottery (name, create_date, award, ballot_price, ballot_unit, state)
VALUES ('Daily Lottery', '2023-02-21 00:00:00', '500 USD', 100, 'DOLLAR', 'ACTIVE');

INSERT INTO lottery (name, create_date, award, ballot_price, ballot_unit, state)
VALUES ('Monthly Lottery', '2023-02-01 00:00:00', '5000 USD', 1000, 'DOLLAR', 'ACTIVE');

INSERT INTO participant (name, ssn, lottery_id, registration_date)
VALUES ('John Smith', '123-45-6789', 1, '2023-02-22 09:00:00');

INSERT INTO participant (name, ssn, lottery_id, registration_date)
VALUES ('Jane Doe', '987-65-4321', 2, '2023-02-22 10:00:00');

INSERT INTO participant (name, ssn, lottery_id, registration_date)
VALUES ('Bob Johnson', '555-55-5555', 3, '2023-02-22 11:00:00');

INSERT INTO submission (participant_id, number_of_ballots, date)
VALUES (1, 2, '2023-02-22 09:00:00');

INSERT INTO submission (participant_id, number_of_ballots, date)
VALUES (2, 1, '2023-02-22 10:00:00');

INSERT INTO submission (participant_id, number_of_ballots, date)
VALUES (3, 3, '2023-02-22 11:00:00');

INSERT INTO ballot (submission_id, code)
VALUES (1, 'ABC123');

INSERT INTO ballot (submission_id, code)
VALUES (2, 'XYZ789');

INSERT INTO ballot (submission_id, code)
VALUES (3, 'DEF456');

INSERT INTO winner (ballot_id, date)
VALUES (2, '2023-02-22 10:00:00')
