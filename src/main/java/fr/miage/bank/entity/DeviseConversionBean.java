package fr.miage.bank.entity;

import java.math.BigDecimal;

public class DeviseConversionBean {

    private Long id;
    private String source;
    private String cible;
    private BigDecimal tauxConversion;
    private BigDecimal quantite;
    private BigDecimal total;
    private int port;

    public DeviseConversionBean() {
    }

    public DeviseConversionBean(Long id, String source, String cible, BigDecimal taux, BigDecimal qte,
                                BigDecimal total, int port) {
        super();
        this.id = id;
        this.source = source;
        this.cible = cible;
        this.tauxConversion = taux;
        this.quantite = qte;
        this.total = total;
        this.port = port;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCible() {
        return cible;
    }

    public void setCible(String cible) {
        this.cible = cible;
    }

    public BigDecimal getTauxConversion() {
        return tauxConversion;
    }

    public void setTauxConversion(BigDecimal tauxConversion) {
        this.tauxConversion = tauxConversion;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}