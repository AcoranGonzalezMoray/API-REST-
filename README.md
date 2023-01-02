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
- Como observamos la salida compone los datos necesarios:
  - Dimension = departureTime (Corresponde por el parametro pasado por la peticion)
  - Filtros   = null (No se han aplicado filtros)
  - Bin       = 1 (Por defecto si no se especifica siempre es 1)
  - lenY      = 5819811 (Vuelos totales que corresponden a la dimension y filtro aplicado y por tanto, es la sumatoria de los valores repartidos en el EjeY)
  - Valores_Dimension_Eje_x = [...] (Lista de valores de la dimension pasada)
  - Valores_Histogtrama_Eje_y = [...] (Contador de vuelos para cada valor de la dimension)
  
### 2. Dimension con Bin
#### Ejemplo: ```http://localhost:8080/Flight/departureTime?bin=6``` donde le preguntamos por el departureTime de todos los vuelos de la base de datos separados por un bin (devuelve por rangos).

#### Salida: 
```
{"status":"SUCCESS","data":{"Dimension":"departureTime","Filtros":"null","Bin":"6","lenY":"5819811",
"Valores_Dimension_Eje_x":["00:00-06:00","06:00-12:00","12:00-18:00","18:00-23:59"],
"Valores_Histogtrama_Eje_y":[317128,2142633,2103489,1256561]}}
```

### 3. Dimension con Filtro
#### Ejemplo: ```http://localhost:8080/Flight/departureTime?Filtros=dayOfWeek=FRIDAY,MONDAY;cancelled=true``` donde le preguntamos por el departureTime de todos aquellos vuelos de la base de datos que se hayan realizado un Viernes o un Lunes y que además estos hayan sido cancelados;

#### Salida: 
```
{"status":"SUCCESS","data":{"Dimension":"departureTime","Filtros":"dayOfWeek:FRIDAY,MONDAY;cancelled:true",
"Bin":"1","lenY":"37810","Valores_Dimension_Eje_x":["00:00-01:00","01:00-02:00","02:00-03:00","03:00-04:00",
"04:00-05:00","05:00-06:00","06:00-07:00","07:00-08:00","08:00-09:00","09:00-10:00","10:00-11:00","11:00-12:00",
"12:00-13:00","13:00-14:00","14:00-15:00","15:00-16:00","16:00-17:00","17:00-18:00","18:00-19:00","19:00-20:00",
"20:00-21:00","21:00-22:00","22:00-23:00","23:00-23:59"],
"Valores_Histogtrama_Eje_y":[36497,4,2,0,2,48,87,60,55,35,48,44,50,89,93,85,107,124,83,80,70,80,44,23]}}
```
- Tenga en cuenta que para especificar un Filtro se utiliza la expresión ```Filtros={parametro}={valor}``` si requiere de mas de un valor deben ir acompañado de una `,` por ejemplo ```Filtros={parametro}={valor1,valor2}```, si requiere de más parametros debe separarlos con `;`  por ejemplo ```Filtros={parametro}={valor1,valor2};{parametro}={valor}``` . Aquí tiene algunos ejemplos funcionales:
  - 1. ```localhost:8080/Flight/arrivalTime?Filtros=diverted=true```
  - 2. ```localhost:8080/Flight/arrivalDelay?Filtros=dayOfWeek=FRIDAY,MONDAY```
  - 3. ```localhost:8080/Flight/distance?Filtros=dayOfWeek=FRIDAY,MONDAY;cancelled=false;diverted=true```

### 4. Dimension con Filtro y Bin
#### Ejemplo: ```http://localhost:8080/Flight/departureDelay?Filtros=dayOfWeek=WEDNESDAY,MONDAY;diverted=true&&bin=200``` donde le preguntamos por el departureDelay de todos los vuelos de la base de datos que se hayan realizado un Miercoles o un Lunes y que además estos hayan sido diverted, luego le especificamos un bin de 200 para una salida más amable (rangos de 200)

#### Salida: 
```
{"status":"SUCCESS","data":{"Dimension":"departureDelay","Filtros":"dayOfWeek:WEDNESDAY,MONDAY;diverted:true"
,"Bin":"200","lenY":"4341",
"Valores_Dimension_Eje_x":["(-19)-(181)","(181)-(381)","(381)-(581)","(581)-(781)","(781)-(981)","(981)-(1030)"],
"Valores_Histogtrama_Eje_y":[4201,125,12,1,1,1]}}
```

