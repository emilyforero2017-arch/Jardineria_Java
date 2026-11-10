package com.jardineria.domain.entity;

public class gamaproducto {
    private String gama;
    private String descripcionTexto;
    private String descripcionHtml;
    private String imagen;

    public gamaproducto() {
    }

    public gamaproducto(String gama, String descripcionTexto,
                        String descripcionHtml, String imagen) {
        this.gama = gama;
        this.descripcionTexto = descripcionTexto;
        this.descripcionHtml = descripcionHtml;
        this.imagen = imagen;
    }

    public String getGama() {
        return gama;
    }

    public void setGama(String gama) {
        this.gama = gama;
    }

    public String getDescripcionTexto() {
        return descripcionTexto;
    }

    public void setDescripcionTexto(String descripcionTexto) {
        this.descripcionTexto = descripcionTexto;
    }

    public String getDescripcionHtml() {
        return descripcionHtml;
    }

    public void setDescripcionHtml(String descripcionHtml) {
        this.descripcionHtml = descripcionHtml;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }


}
