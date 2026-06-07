INSERT INTO member (id, first_name, last_name, email, phone, level, joined_date, active) VALUES
                                                                                             (1, 'Anna', 'Müller', 'anna.mueller@example.com', '+41 79 111 22 33', 'INTERMEDIATE', '2023-03-15', true),
                                                                                             (2, 'Tobias', 'Keller', 'tobias.keller@example.com', '+41 79 222 33 44', 'BEGINNER', '2024-01-10', true),
                                                                                             (3, 'Sabine', 'Braun', 'sabine.braun@example.com', '+41 79 333 44 55', 'ADVANCED', '2022-06-01', true),
                                                                                             (4, 'Marco', 'Huber', 'marco.huber@example.com', '+41 79 444 55 66', 'BEGINNER', '2024-03-20', true),
                                                                                             (5, 'Lisa', 'Weber', 'lisa.weber@example.com', '+41 79 555 66 77', 'INTERMEDIATE', '2023-09-05', false);

INSERT INTO training_plan (id, name, description, level, created_date) VALUES
                                                                                                      (1, '5km Einsteiger-Plan', 'Perfekt für absolute Neulinge. Aufbau von 0 auf 5km in 8 Wochen.', 'BEGINNER', '2024-01-01'),
                                                                                                      (2, 'Halbmarathon Vorbereitung', '12-wöchiger Aufbauplan für den ersten Halbmarathon.', 'INTERMEDIATE', '2024-01-01'),
                                                                                                      (3, 'Marathon Fortgeschrittene', 'Intensiver 16-wöchiger Marathonplan mit Intervalltraining.', 'ADVANCED', '2024-01-01');

INSERT INTO plan_assignment (id, member_id, training_plan_id, start_date, end_date, completed) VALUES
                                                                                                   (1, 1, 2, '2024-02-01', '2024-04-25', false),
                                                                                                   (2, 2, 1, '2024-03-01', '2024-04-26', false),
                                                                                                   (3, 3, 3, '2024-01-15', '2024-05-13', false),
                                                                                                   (4, 4, 1, '2024-04-01', '2024-05-27', false);