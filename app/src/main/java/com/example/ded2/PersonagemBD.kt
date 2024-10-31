package com.example.ded2

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "personagem")

class PersonagemBD {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var nome: String = ""
    var raca: String = ""

}