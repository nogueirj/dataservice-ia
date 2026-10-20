package br.com.faculdadedonaduzzi.dataservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "analise_peixes")
public class FishAnalysisEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "nome", length = 300)
    private String nome;

    @Column(name = "avaliacao_modelo",  length = 25)
    private String avaliacao;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getNome() {
        return this.nome;
    }
    public void setAvaliacao(String avaliacao) {
        this.avaliacao = avaliacao;
    }
    public String getAvaliacao() {
        return this.avaliacao;
    }
}
