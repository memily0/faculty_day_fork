package com.example.library.repository

import com.example.library.entity.Reader
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query

// ============================================================================
// ЗАДАНИЕ ФИНАЛ: Создать интерфейс ReaderRepository
// ============================================================================
// ИНСТРУКЦИЯ:
// 2. Создай интерфейс

interface ReaderRepository : JpaRepository<Reader, Long> {

    @Query("""
        select distinct r
        from Reader r
        left join fetch r.books b
        left join fetch b.author
        left join fetch b.genre
    """)
    fun findAllWithBooksFetched(): List<Reader>
}

