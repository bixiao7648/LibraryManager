package com.example.librarymanager

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.librarymanager.db.BookInfo
import com.example.librarymanager.db.BookInfoDatabase
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.Thread.sleep
import java.util.concurrent.Executors

@RunWith(RobolectricTestRunner::class)
class BookRepositoryTest {

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private var database = Room.databaseBuilder(
        appContext,
        BookInfoDatabase::class.java,
        "test_book_database"
    ).build()
    private var toDeleteId = -1
    private var toUpdateId = -1
    private val title = arrayOf("title1", "title2")
    private val author = arrayOf("author1", "author2")
    private val publishYear = arrayOf(2024, 2023)
    private val isbn = arrayOf("1234", "2345")

    @Before
    fun setUp() {
        mockkObject(BookApplication)
        every { BookApplication.getContext() } returns appContext
        mockkObject(BookInfoDatabase)
        every { BookInfoDatabase.getInstance(any()) } returns database
        Dispatchers.setMain(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
    }

    @After
    fun tearUp() {
        Dispatchers.resetMain()
    }

    @Test
    fun test_add_new_book() {
        val ioJob = CoroutineScope(Dispatchers.IO).launch {
            BookRepository.getInstance().addNewBook(title[0], author[0], publishYear[0], isbn[0]) {}
            BookRepository.getInstance().addNewBook(title[1], author[1], publishYear[1], isbn[1]) {
                assertEquals(it[0].title, title[0])
                assertEquals(it[0].author, author[0])
                assertEquals(it[0].publishYear, publishYear[0])
                assertEquals(it[0].isbn, isbn[0])

                assertEquals(it[1].title, title[1])
                assertEquals(it[1].author, author[1])
                assertEquals(it[1].publishYear, publishYear[1])
                assertEquals(it[1].isbn, isbn[1])
            }
            sleep(1000)
        }
        runBlocking {
            ioJob.join()
        }
    }

    @Test
    fun test_delete_book_info() {
        BookRepository.resetSingleton()
        val ioJob = CoroutineScope(Dispatchers.IO).launch {
            BookRepository.getInstance().addNewBook(title[0], author[0], publishYear[0], isbn[0]) {}
            BookRepository.getInstance().addNewBook(title[1], author[1], publishYear[1], isbn[1]) {
                it[0].id?.run { toDeleteId = this }
            }
            sleep(1000)

            BookRepository.getInstance().deleteBookInfo(toDeleteId) {
                assertEquals(it[0].title, title[1])
                assertEquals(it[0].author, author[1])
                assertEquals(it[0].publishYear, publishYear[1])
                assertEquals(it[0].isbn, isbn[1])
            }
            sleep(1000)
        }
        runBlocking {
            ioJob.join()
        }
    }

    @Test
    fun test_update_book_info() {
        BookRepository.resetSingleton()
        val ioJob = CoroutineScope(Dispatchers.IO).launch {
            BookRepository.getInstance().addNewBook(title[0], author[0], publishYear[0], isbn[0]) {}
            BookRepository.getInstance().addNewBook(title[1], author[1], publishYear[1], isbn[1]) {
                it[1].id?.run { toUpdateId = this }
            }
            sleep(1000)

            BookInfo().let {
                it.id = toUpdateId
                it.title = title[0]
                it.author = author[0]
                it.publishYear = publishYear[0]
                it.isbn = isbn[0]
                BookRepository.getInstance().updateBookInfo(it) { books ->
                    assertEquals(books[1].title, title[0])
                    assertEquals(books[1].author, author[0])
                    assertEquals(books[1].publishYear, publishYear[0])
                    assertEquals(books[1].isbn, isbn[0])
                }
            }
            sleep(1000)
        }
        runBlocking {
            ioJob.join()
        }
    }

    @Test
    fun test_show_books() {
        BookRepository.resetSingleton()
        val ioJob = CoroutineScope(Dispatchers.IO).launch {
            BookRepository.getInstance().addNewBook(title[0], author[0], publishYear[0], isbn[0]) {}
            BookRepository.getInstance().addNewBook(title[1], author[1], publishYear[1], isbn[1]) {}
            sleep(1000)

            BookRepository.getInstance().showBooks("12") {
                assertEquals(it[0].title, title[0])
                assertEquals(it[0].author, author[0])
                assertEquals(it[0].publishYear, publishYear[0])
                assertEquals(it[0].isbn, isbn[0])
            }
            sleep(1000)
        }
        runBlocking {
            ioJob.join()
        }
    }
}