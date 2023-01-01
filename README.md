# Web service que expone una API REST para consultar estadísticas de vuelos.
## Nota Importante
- Una vez descargado/clonado el código, debe extraer ```flights.rar``` luego puede ejecutar el proyecto para activar la ```Web Service```
## Comportamiento
- La API debe exponer recursos (según la metodología REST) para obtener histogramas
serializados en formato JSON.
- En la petición se tendría que especificar, como parámetro inline o como parámetro de
query:
  - Dimensión por la que se está realizando el histograma (p.e. hora de llegada, o
retrasos). 
  - Filtros que se han aplicado en el cálculo del histograma (p.e. día de la semana).
  - Tamaño del bin (p.e. 6 horas cuando la dimensión es la hora de llegada)
- El esquema del histograma debe tener como mínimo los siguientes campos:
  - Dimensión
  - Filtros
  - Valores de la dimensión (eje X)
  - Valores del histograma (eje Y)

## Instrucciones de Uso
- Una vez ejecutado el código podrá realizar peticiones a travez del puerto 8080 (Puede especificar otro puerto en el archivo ```Kata7.java```)
### 1. Dimension
- Cuando se requiera realizar una petición como mínimo le debemos indicar la dimension por el cual pregunta para así devolver los datos correcto para formar un histograma.
#### Ejemplo: ```http://localhost:8080/Flight/departureTime``` donde le preguntamos por el departureTime de todos los vuelos de la base de datos.
#### Salida: 
```
{"status":"SUCCESS","data":{"Dimension":"departureTime","Filtros":"null","Bin":"1","lenY":"5819811","Valores_Dimension_Eje_x":
["00:00-01:00","01:00-02:00","02:00-03:00","03:00-04:00","04:00-05:00","05:00-06:00","06:00-07:00","07:00-08:00","08:00-09:00",
"09:00-10:00","10:00-11:00","11:00-12:00","12:00-13:00","13:00-14:00","14:00-15:00","15:00-16:00","16:00-17:00","17:00-18:00",
"18:00-19:00","19:00-20:00","20:00-21:00","21:00-22:00","22:00-23:00","23:00-23:59"],
"Valores_Histogtrama_Eje_y":[140701,6348,1603,925,4831,162720,358783,342039,379840,338775,360981,362215,348348,354289,341436,
347554,338163,373699,326684,326360,256331,191700,111645,43841]}}
```
- Como observamos la salida compone los datos necesarios

### 2. Dimension con Bin
### 3. Dimension con Filtro
### 4. Dimension con Filtro y Bin
