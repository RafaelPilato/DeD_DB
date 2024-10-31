package com.example.ded2

import org.example.controlePersonagem.personagem

class PersonagemController (
    var Personagem : personagem
){

    fun getPontosDisponiveis() : Int{
        return this.Personagem.pontos
    }
    fun setAtributo(valor: Int, indiceHabilidade: Int){
        if(this.Personagem.validacaoValorHabilidade(valor))
            throw IllegalArgumentException("Valor deve estar entre 8 e 15")
        else if (this.Personagem.descontarValorHabilidade(valor, indiceHabilidade))
            throw  IllegalArgumentException("Pontos insuficientes")
    }
}