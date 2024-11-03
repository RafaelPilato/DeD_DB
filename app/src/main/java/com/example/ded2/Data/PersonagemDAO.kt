package com.example.ded2.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.ded2.PersonagemBD

@Dao
interface PersonagemDAO {

    @Insert
    suspend fun insert(personagem: PersonagemBD)

    @Query("SELECT * FROM personagem")
    suspend fun getAllPersonagens(): List<PersonagemBD>

    @Delete
    suspend fun delete(personagem: PersonagemBD)

    @Query("UPDATE personagem SET nome = :novoNome WHERE id = :id")
    suspend fun updateNome(id: Int, novoNome: String)

    @Query("SELECT * FROM personagem WHERE id = :id")
    suspend fun getPersonagemById(id: Int): PersonagemBD?
}