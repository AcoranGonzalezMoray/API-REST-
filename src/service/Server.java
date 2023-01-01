package service;


import static spark.Spark.get;
import com.google.gson.Gson;
import java.io.File;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import model.Flight;
import model.FlightIterable;
import model.SqliteFlightBD;
import static spark.Spark.port;
import model.Histogram;
import model.Schema;


public class Server {
    public List<Flight> object = new ArrayList<Flight>();
    public int puerto=80;
    
    public Server(int puerto) {
        this.puerto=puerto;
    }

    public void start() throws SQLException {
        port(this.puerto);
        allFlight();
        
        get("/Flight", (req,res)->{
            res.type("application/json");
            return new Gson().toJson(
            new StandardResponse(StatusResponse.SUCCESS,new Gson().toJson(this.object)));
        });
        

        
        
        get("/Flight/:Dimension", (req,res)->{
            if((req.queryParams("Filtros")==null) && (req.queryParams("bin")==null)){//DIMENSION (de hora en hora|no minuto a minuto)
                Schema schema = new Schema(req.params(":Dimension"),"null", "1");
                Schema histogramSchema = createBin(Dimension(req.params(":Dimension"),null), schema, "1");
                return new Gson().toJson(
                new StandardResponse(StatusResponse.SUCCESS,new Gson().toJsonTree(histogramSchema)));
            }
            
            if((req.queryParams("Filtros")!=null) && (req.queryParams("bin")==null)){//DIMENSION - FILTRO
                Schema schema = new Schema(req.params(":Dimension"), req.queryParams("Filtros"), "1");
                Schema histogramSchema = createBin(Dimension(req.params(":Dimension"),req.queryParams("Filtros")), schema, "1");
                return new Gson().toJson(
                new StandardResponse(StatusResponse.SUCCESS,new Gson().toJsonTree(histogramSchema)));
            }
            
            if((req.queryParams("Filtros")==null) && (req.queryParams("bin")!=null)){//DIMENSION - BIN  
                Schema schema = new Schema(req.params(":Dimension"), "null", req.queryParams("bin"));
                Schema histogramSchema = createBin(Dimension(req.params(":Dimension"), null), schema, req.queryParams("bin"));
                return new Gson().toJson(
                new StandardResponse(StatusResponse.SUCCESS,new Gson().toJsonTree(histogramSchema)));
            }
            
            
            //DIMENSION - FILTRO - BIN
            Schema schema = new Schema(req.params(":Dimension"), req.queryParams("Filtros"), req.queryParams("bin"));
            Schema histogramSchema = createBin(Dimension(req.params(":Dimension"), req.queryParams("Filtros")), schema, req.queryParams("bin"));
            return new Gson().toJson(
            new StandardResponse(StatusResponse.SUCCESS,new Gson().toJsonTree(histogramSchema)));
        });  
    }
    
    
    
    private void allFlight() throws SQLException{
        FlightIterable F = new SqliteFlightBD(new File("flights.db"));
        for (Flight flight :F.flights()) this.object.add(flight);
      
    }

    private Histogram Dimension(String dim, String filter) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        Histogram histogram= new Histogram();
        String param=null;
        String value=null;
        if(filter!=null){
            param=filter.split("=")[0];
            value=filter.split("=")[1];
        }
        
        switch (dim) {
            case "departureTime":
                for (Flight flight : object) {
                    if(param!=null){
                        if(filtro(flight, filter)) histogram.increment(String.valueOf(flight.departureTime)); 
                    } else histogram.increment(String.valueOf(flight.departureTime));
                }
                break;
            case "dayOfWeek":
                for (Flight flight : object) {
                    if(param!=null){
                        if(filtro(flight, filter)) histogram.increment(String.valueOf(flight.dayOfWeek));
                    }else histogram.increment(String.valueOf(flight.dayOfWeek));
                }
                break;
            case "departureDelay":
                for (Flight flight : object) {
                    if(param!=null){
                        if(filtro(flight, filter)) histogram.increment(String.valueOf(flight.departureDelay));
                    }else histogram.increment(String.valueOf(flight.departureDelay));
                }
                break;
            case "arrivalDelay":
                for (Flight flight : object) {
                    if(param!=null){
                        if(filtro(flight, filter)) histogram.increment(String.valueOf(flight.arrivalDelay));
                    }else histogram.increment(String.valueOf(flight.arrivalDelay));
                }
                break;
            case "duration":
                for (Flight flight : object) {
                    if(param!=null){
                        if(filtro(flight, filter)) histogram.increment(String.valueOf(flight.duration));
                    }else histogram.increment(String.valueOf(flight.duration));
                }
                break;
            case "distance":
                for (Flight flight : object) {
                    if(param!=null){
                        if(filtro(flight, filter))  histogram.increment(String.valueOf(flight.distance));
                    }else histogram.increment(String.valueOf(flight.distance));
                }
                break;
            case "cancelled":
                for (Flight flight : object) {
                    if(param!=null){
                        if(filtro(flight, filter)) histogram.increment(String.valueOf(flight.cancelled));
                    }else histogram.increment(String.valueOf(flight.cancelled));
                }
                break;     
            case "diverted":
                for (Flight flight : object) {
                    if(param!=null){
                        if(filtro(flight, filter)) histogram.increment(String.valueOf(flight.diverted));
                    }else histogram.increment(String.valueOf(flight.diverted));
                }
            break;   
            case "arrivalTime":
                for (Flight flight : object) {
                    if(param!=null){
                        if(filtro(flight, filter))  histogram.increment(String.valueOf(flight.arrivalTime));   
                    }else histogram.increment(String.valueOf(flight.arrivalTime));
                } 
                break;                
            default:
                throw new AssertionError();
        }
        return histogram;
    }
   
    
    public boolean filtro(Flight flight, String filtro) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        String all=filtro;
        String[] oneCutAll=all.split(";");
        int condiciones=0;
        for (String string : oneCutAll) {
            String param=string.split("=")[0];
            String value=string.split("=")[1];
            String[] ofValue=value.split(",");
             
            for (String Value : ofValue) {
                if(flight.getClass().getField(param).get(flight).toString().compareTo(Value)==0){
                    condiciones++;
                }
            }  
        }
        
        if(condiciones==oneCutAll.length) return true;
        else return false;
    }
    
    //ordenar  map con /:bin/
    private Schema createBin(Histogram<String> histogram, Schema schema, String bin) throws ParseException{
        if(schema.getDimension().compareTo("departureTime")==0 ||schema.getDimension().compareTo("arrivalTime")==0){
            String a,b;int min=0;int max=Integer.parseInt(bin);int tmp=0;String c="";
            TreeMap<String, Integer> histogramSorted2 = new TreeMap<>(histogram.getMap());
            for (int i = 1; i <=24; i++) {
                a=getTwoDecimals(min)+":00";
                b=getTwoDecimals(max)+":00";
                if(max>=24){c=getTwoDecimals(23)+":59";schema.addEje_x(a+"-"+c);}
                else schema.addEje_x(a+"-"+b);
                int count=0;
                for( String key : histogramSorted2.keySet())if(Comprobar(key,a,b))count+=histogram.get(key); 
                schema.addEje_y(count);tmp+=count;min=max;max+=Integer.parseInt(bin);  
                if(min>=(24))break;
            }
            schema.setLenY(String.valueOf(tmp));    
        }
        else if(schema.getDimension().compareTo("distance")==0//Problema con ultima iteracion
                ||schema.getDimension().compareTo("arrivalDelay")==0
                ||schema.getDimension().compareTo("departureDelay")==0
                ||schema.getDimension().compareTo("duration")==0){
            String a,b;int min=0;int max=Integer.parseInt(bin);int tmp=0;String c="";
            TreeMap<String, Integer> histogramSorted = new TreeMap<>(new OrdenarAsc());
            histogramSorted.putAll(histogram.getMap());
            int MAX = maxMap(histogram);
            min=minMap(histogram);
            max+=min;
            for (int i = 0; i <=(MAX+MAX); i++) {
                a=String.valueOf(min);
                b=String.valueOf(max);
                if(max>=MAX){c=String.valueOf(MAX);schema.addEje_x("("+a+")"+"-("+c+")");b+=1;}
                else schema.addEje_x("("+a+")"+"-("+b+")");
                int count=0;
                for( String key : histogramSorted.keySet())if(ComprobarINT(key,a,b))count+=histogram.get(key); 
                schema.addEje_y(count);tmp+=count;min=max;max+=Integer.parseInt(bin);  
                if(min>=MAX)break;
            }
            
            
            
            schema.setLenY(String.valueOf(tmp));     
        }else{
            TreeMap<String, Integer> histogramSorted = new TreeMap<>(histogram.getMap());
            int tmp=0;
            for( String key :  histogramSorted.keySet()){
                schema.addEje_x(key);
                schema.addEje_y(histogram.get(key));
                tmp+=histogram.get(key);
            }
            schema.setLenY(String.valueOf(tmp));      
        }

        return schema;      
    }
    
    public boolean Comprobar(String dActual, String dInicial, String dFinal) throws ParseException {
      Date actual = new SimpleDateFormat("HH:mm").parse(dActual);
      Date inicial = new SimpleDateFormat("HH:mm").parse(dInicial);
      Date finall = new SimpleDateFormat("HH:mm").parse(dFinal);
      //if(actual.after(inicial) && actual.before(finall))return true;  
      if ((inicial.compareTo(actual) <= 0) && (finall.compareTo(actual) > 0))return true;
      else return false;
    }
    public boolean ComprobarINT(String dActual, String dInicial, String dFinal) throws ParseException {
        int I=Integer.parseInt(dInicial);
        int A=Integer.parseInt(dActual);
        int F=Integer.parseInt(dFinal);
      if((A>=I)&&(A<F))return true;
      else return false;
    } 
    private LocalTime formatToInstant(String timeInString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeInString, formatter);
    }
    private static String getTwoDecimals(int value){
      DecimalFormat df = new DecimalFormat("00"); 
      return df.format(value);
    }
    
    public class OrdenarAsc implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
             return Integer.parseInt(o1) - Integer.parseInt(o2);
        }
    }
    
    public int maxMap(Histogram<String> h){
        int max=0;
        for (String v : h.keySet()) if(Integer.parseInt(v)>max)max=Integer.parseInt(v);
        return max;
    }
    public int minMap(Histogram<String> h){
        int min=10000;
        for (String v : h.keySet()) if(Integer.parseInt(v)<min)min=Integer.parseInt(v);
        return min;
    }
}
