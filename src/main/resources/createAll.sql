CREATE TABLE IF NOT EXISTS patogeno (
  id int auto_increment NOT NULL ,
  tipo VARCHAR(255) NOT NULL ,
  cantidadDeEspecies int NOT NULL,
  PRIMARY KEY (id)
);