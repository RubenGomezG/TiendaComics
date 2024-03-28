
/*CREATE TABLE tomos(
                      cod_tomo INTEGER PRIMARY KEY,
                      num_volumen INTEGER NOT NULL,
                      descripcion VARCHAR(300) DEFAULT NULL,
                      CONSTRAINT tomo_articulo FOREIGN KEY(cod_tomo) REFERENCES articulos(cod_articulo) ON UPDATE CASCADE ON DELETE CASCADE
);*/

/* CREATE TABLE merchandising (cod_merch INTEGER PRIMARY KEY,
                            tipo ENUM ('Ropa', 'Accesorio', 'Poster', 'Figura', 'Otro') NOT NULL,
                            coleccion VARCHAR(20) NOT NULL,
                            talla VARCHAR(10),
                            CONSTRAINT merch_articulo FOREIGN KEY(cod_merch) REFERENCES articulos(cod_articulo) ON UPDATE CASCADE ON DELETE CASCADE
);*/

/*CREATE TABLE ventas(
                       cod_ticket INTEGER NOT NULL,
                       cod_articulo INTEGER NOT NULL,
                       fecha_hora_venta DATETIME NOT NULL DEFAULT NOW(),
                       cantidad INTEGER NOT NULL DEFAULT 1,
                       CONSTRAINT ventas_pk PRIMARY KEY(cod_ticket,cod_articulo),
                       CONSTRAINT articulo_fk FOREIGN KEY(cod_articulo) REFERENCES articulos(cod_articulo) ON UPDATE CASCADE ON DELETE CASCADE,
                       CONSTRAINT ticket_fk FOREIGN KEY(cod_ticket) REFERENCES tickets(cod_ticket) ON UPDATE CASCADE ON DELETE CASCADE
);*/

select articulos.cod_articulo as 'CódigoArtículo', articulos.nombre as 'Artículo', tomos.num_volumen as 'Volumen', articulos.precio as 'Precio', articulos.stock as 'Cantidad', ifnull(tomos.descripcion,'') as 'Descripción'
from articulos inner join tomos
                          on articulos.cod_articulo = tomos.cod_tomo;

select articulos.cod_articulo as 'CódigoArtículo', articulos.nombre as 'Artículo', articulos.precio as 'Precio', articulos.stock as 'Cantidad', ifnull(juegos_mesa.descripcion,'') as 'Descripción'
from articulos inner join juegos_mesa
                          on articulos.cod_articulo = juegos_mesa.cod_juego;

select articulos.cod_articulo as 'CódigoArtículo', articulos.nombre as 'Artículo', articulos.precio as 'Precio', articulos.stock as 'Cantidad', merchandising.tipo as 'Tipo', ifnull(merchandising.coleccion,'') as 'Colección', ifnull(merchandising.talla,'') as 'Talla'
from articulos inner join merchandising
                          on articulos.cod_articulo = merchandising.cod_merch;

drop procedure if exists consultaArticulo;
DELIMITER $$
create procedure consultaArticulo()
begin

select articulos.cod_articulo as 'CódigoArtículo', articulos.nombre as 'Artículo', tomos.num_volumen as 'Volumen', articulos.precio as 'Precio', articulos.stock as 'Cantidad', ifnull(tomos.descripcion,'') as 'Descripción'
from articulos inner join tomos
on articulos.cod_articulo = tomos.cod_tomo;

select articulos.cod_articulo as 'CódigoArtículo', articulos.nombre as 'Artículo', articulos.precio as 'Precio', articulos.stock as 'Cantidad', ifnull(juegos_mesa.descripcion,'') as 'Descripción'
from articulos inner join juegos_mesa
on articulos.cod_articulo = juegos_mesa.cod_juego;

select articulos.cod_articulo as 'CódigoArtículo', articulos.nombre as 'Artículo', articulos.precio as 'Precio', articulos.stock as 'Cantidad', merchandising.tipo as 'Tipo', ifnull(merchandising.coleccion,'') as 'Colección', ifnull(merchandising.talla,'') as 'Talla', ifnull(tomos.descripcion,'') as 'Descripción'
from articulos inner join merchandising
on articulos.cod_articulo = merchandising.cod_merch;

select sum(articulos.precio * ventas.cantidad) from tickets inner join ventas inner join articulos where cod_ticket = (select max(cod_ticket) from tickets);

DROP FUNCTION IF EXISTS buscarTicket(ticket)
select sum(articulos.precio * ventas.cantidad) from tickets inner join ventas inner join articulos where cod_ticket = ticket;

end
$$
DELIMITER ;
call consultaArticulo;


select * from articulos;
select * from merchandising;
select * from juegos_mesa;
select * from tomos;

select * from tickets;
select * from ventas;

select * from articulos a
                  left join tomos t on t.cod_tomo = a.cod_articulo
                  left join juegos_mesa j on j.cod_juego = a.cod_articulo
                  left join merchandising m on m.cod_merch = a.cod_articulo;

select a.cod_articulo, a.nombre, a.precio, a.stock, ifnull(ifnull(t.descripcion, j.descripcion), null) as 'descripcion', t.num_volumen, m.coleccion, m.tipo, m.talla
from articulos a
         left join tomos t on t.cod_tomo = a.cod_articulo
         left join juegos_mesa j on j.cod_juego = a.cod_articulo
         left join merchandising m on m.cod_merch = a.cod_articulo;

select * from articulos LEFT join tomos on articulos.cod_articulo = tomos.cod_tomo LEFT join juegos_mesa on articulos.cod_articulo = juegos_mesa.cod_juego LEFT join merchandising on articulos.cod_articulo = merchandising.cod_merch inner join ventas on articulos.cod_articulo = ventas.cod_articulo inner join tickets on tickets.cod_ticket = ventas.cod_ticket  WHERE articulos.nombre LIKE 'O%';

select * from articulos LEFT join tomos on articulos.cod_articulo = tomos.cod_tomo LEFT join juegos_mesa on articulos.cod_articulo = juegos_mesa.cod_juego LEFT join merchandising on articulos.cod_articulo = merchandising.cod_merch  WHERE articulos.nombre LIKE "O%";