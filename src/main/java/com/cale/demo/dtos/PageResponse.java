package com.cale.demo.dtos;

import java.util.List;

public class PageResponse<T> {
    private List<T> contenido;
    private int paginaActual;
    private int tamanioPagina;
    private Long totalElementos;
    private int totalPaginas;

    public PageResponse() {

    }

    public PageResponse(List<T> contenido, int paginaActual, int tamanioPagina, Long totalElementos, int totalPaginas) {
        this.contenido = contenido;
        this.paginaActual = paginaActual;
        this.tamanioPagina = tamanioPagina;
        this.totalElementos = totalElementos;
        this.totalPaginas = totalPaginas;
    }

    public List<T> getContenido() {
        return contenido;
    }

    public void setContenido(List<T> contenido) {
        this.contenido = contenido;
    }

    public int getPaginaActual() {
        return paginaActual;
    }

    public void setPaginaActual(int paginaActual) {
        this.paginaActual = paginaActual;
    }

    public int getTamanioPagina() {
        return tamanioPagina;
    }

    public void setTamanioPagina(int tamanioPagina) {
        this.tamanioPagina = tamanioPagina;
    }

    public Long getTotalElementos() {
        return totalElementos;
    }

    public void setTotalElementos(Long totalElementos) {
        this.totalElementos = totalElementos;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(int totalPaginas) {
        this.totalPaginas = totalPaginas;
    }
}
