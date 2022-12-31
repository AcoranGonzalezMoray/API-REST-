package model;

import java.util.ArrayList;
import java.util.List;

public class Schema {
    public String Dimension;
    public String Filtros;
    public String Bin="1";
    public String lenY;
    public List<String> Valores_Dimension_Eje_x = new ArrayList<String>();
    public List<Integer> Valores_Histogtrama_Eje_y = new ArrayList<Integer>();

    public Schema(String Dimension, String Filtros, String Bin) {
        this.Dimension = Dimension;
        this.Filtros = Filtros.replace("=", ":");
        this.Bin = Bin;
        this.lenY="";
    }

    public String getBin() {
        return Bin;
    }

    public void setBin(String Bin) {
        this.Bin = Bin;
    }

    public String getDimension() {
        return Dimension;
    }

    public String getFiltros() {
        return Filtros;
    }

    public List<String> getValores_Dimension_Eje_x() {
        return Valores_Dimension_Eje_x;
    }

    public List<Integer> getValores_Histogtrama_Eje_y() {
        return Valores_Histogtrama_Eje_y;
    }

    public void addEje_x(String str) {
        this.Valores_Dimension_Eje_x.add(str);
    }

    public void addEje_y(int ent) {
        this.Valores_Histogtrama_Eje_y.add(ent);
    }

    public String getLenY() {
        return lenY;
    }

    public void setLenY(String lenY) {
        this.lenY = lenY;
    }
    
}
