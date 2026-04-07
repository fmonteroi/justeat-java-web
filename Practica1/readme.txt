Alumno: Francisco Montero Sánchez

Como ya comentamos por correo, me surgieron varias dudas a lo largo del desarrollo de la práctica. Si esta
entrega fuera única no importaría pero, al ser la siguiente (y quizás la última también) continuación de esta,
aclaro las dudas que me han surgido y la decisión que se tomó para resolverlas a continuación:

Nota: las separo por importancia porque hay muchas.

- Dudas/problemas mayores:

1 - Modificación script listar restaurantes: Esta era mi duda principal, no sé si podía hacer esto ya que era modificar mucho
la base que nos diste. Basicamente en vez de dibujar los restaurantes en busqueda.html uso el autocomplete y mando un get
con parametros a listado.html que contiene la calle, una vez alli el script filtra restaurantes y dibuja los que sean con
las características correspondientes.

Nota: No he borrado la parte de código que no se usa, pero he dejado un comentario que indica la parte que no uso, está
protegido con una clausula if, asi que no deberia dar problemas.


2 - Errores iniciales script.js: El script que nos diste marcaba varios errores en el phoenix code. El error de que la i
no estaba definida lo arreglé. Busqué y chatgpt decía que los errores de jshint eran de estilo y no hacia falta arreglarlos,
asi que los dejé sin arreglar.


3 - Distribución js: He dejado index.js con el script que nos diste de iniSesion y por separado en script.js todo
lo demás. No sabía si separarlo en 1 script por página o meterlo todo junto. He respetado los scripts que nos
diste y lo he dejado en 2 partes: Index.js solo para el login y script.js con todo lo demás.

Nota: Para poder utilizar el script.js en varios lados, lo he modificado ya que daba fallos en las páginas en
las que se ejecutaba un bloque de JS sobre un elemento que no existía. (un if antes de hacer cada cosa)


4 - Robustez de la contraseña: En el enunciado decía que la contraseña al menos contendrá 8 dígitos entre los que se
incluye un número, una mayúscula, una minúscula y un carácter alfanumérico. Se ha modificado el script de registro para
que se cumpla con eso, antes solo controlaba al menos 8 letras y números.

Nota: también se modificó para que valide campo por campo, no todos de golpe (nada más hacer click en un campo y dejarlo vacio,
el resto de campos mostraban que eran obligatorios).


5 - Platos precargados: en editar se cargan por js todos los datos pero no los platos. De momento están fijos en el html,
no se si debería haber hecho platos para cada restaurante posible también.




- Dudas/problemas menores:


1 - Cesta en editar_restaurante: Creo que podría quitarse la cesta de la compra de editar pero como está 
en el enunciado y en el ejemplo que nos proporcionaste la he dejado.


2 - Navegación: Se dijo en clase que la página principal era el iniciar sesión y se le llamó index. Se decidió
que el boton < vuelva hacia la página anterior y el logo de justeat lleve al index, es decir al login. La única
posibilidad de llegar a búsqueda es darle a atrás desde el index. Considero que deberia ir todo a busqueda dandole
al logo de justeat pero las indicaciones decían que el index era el login.


3 - Dimensiones: Cuando el navegador se hace muy estrecho se rompe la interfaz en algunas pantallas 
(Ej: detalles_cume.html). Como se dijo que no nos preocupemos por el responsive, eso no se ha controlado.


4 - Descripción platos: Si la descripción de los platos es muy larga se rompe la interfaz, no se ha controlado
asumiendo que se controlará la longitud de la misma en el futuro a la hora de manejar el añadir platos.


5 - Categorías: Las tarjetas de categorías que se mostraban en la página de búsqueda y listado las he cambiado 
para que coincidan con las categorías que usamos en el editar restaurante.


6 - Ojo en la contraseña: En el script de js que nos diste no se controlaba el ojo en la contraseña, lo he dejado
en el login ya que en ese script si está controlado. Iba a añadir esa funcionalidad al script de registro también
pero, para ser fiel al script que nos diste no lo hice y dejé el ojo solo en login.


7 - Error en index/registro: En el js que nos diste del iniciar sesión saltaba el error con un recuadro arriba, en el de
registro no hace eso. No lo he añadido para respetar el original igual que con el ojo.


8 - Lo + top: en el ejemplo del enunciado salía el mensaje de "9 establecimientos coinciden con lo + top", lo he adaptado
para que salga igual pero en principio con destacados, no sé si eso había que quitarlo.


9 - Restaurantes de listado: Como los cargo por js he dejado las cartas originales (que hice antes de meterme con el js)
comentadas por si las necesito en algun momento.


10 - Teléfono con 2 formatos: En detalle se ve el formato del numero de teléfono con el prefijo y en detalles sin el prefijo.
En el script que proporcionaste valida el telefono sin tener en cuenta el prefijo, supongo que en el futuro habrá que quitar
el prefijo de detalles también, ya que en la bd no se guardará el prefijo.

11 - Categorias en búsqueda: De momento hago que al hacer click en una categoria no lleve a ningún lado. No sé muy bien como
hacerlo ya que en el justeat original no salen las categorías en la página principal y sin meter una dirección no puedes
llevar al usuario a todos los restaurantes del mundo con esa categoría.





