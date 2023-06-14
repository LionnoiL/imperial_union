CREATE TABLE users (
  user_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_name varchar(45) NOT NULL UNIQUE,
  password varchar(64) NOT NULL,
  role varchar(45) NOT NULL
);

INSERT INTO users (user_name, password, role)
VALUES ('admin', '$2a$12$6MScoIJF1i38dSZXePsdqeX3w4NWPSS5f9QqG0T2vGFBn2ZcpkRkG', 'ROLE_ADMIN');

INSERT INTO users (user_name, password, role)
VALUES ('user', '$2a$12$6MScoIJF1i38dSZXePsdqeFb/P8SYxVeCCoIbhsYpDdAikgzEK.NG', 'ROLE_USER');
