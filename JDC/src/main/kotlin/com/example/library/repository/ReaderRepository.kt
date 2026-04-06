package com.example.library.repository

import com.example.library.entity.Reader
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// ============================================================================
// ЗАДАНИЕ ФИНАЛ: Создать интерфейс ReaderRepository
// ============================================================================
// ИНСТРУКЦИЯ:
// 2. Создай интерфейс

@Repository
interface ReaderRepository : JpaRepository<Reader, Long>


