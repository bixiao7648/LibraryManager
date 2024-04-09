package com.example.librarymanager.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.test.platform.app.InstrumentationRegistry
import com.example.librarymanager.BookApplication
import com.example.librarymanager.BookRepository
import com.example.librarymanager.db.BookInfoDatabase
import io.mockk.*
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.Thread.sleep
import java.util.concurrent.Executors

@RunWith(RobolectricTestRunner::class)
class EditViewModelTest {

    private val context = mockk<Context>()
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val database = BookInfoDatabase.getInstance(appContext)
    private val toast = mockk<Toast>()
    private val viewModel = EditViewModel()
    private var toUpdateId = -1

    @Before
    fun setUp() {
        mockkObject(BookApplication)
        every { BookApplication.getContext() } returns context
        mockkObject(BookInfoDatabase)
        every { BookInfoDatabase.getInstance(any()) } returns database
        mockkStatic(Toast::class)
        every { Toast.makeText(any(), any<String>(), any()) } returns toast
        every { Toast.makeText(any(), any<Int>(), any()) } returns toast
        every { toast.show() } just runs
        Dispatchers.setMain(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
    }

    @After
    fun tearUp() {
        Dispatchers.resetMain()
    }

    @Test
    fun test() {
        val ioJob = CoroutineScope(Dispatchers.IO).launch {
            // add an example BookInfo
            val title = arrayOf("title1", "title2")
            val author = arrayOf("author1", "author2")
            val publishYear = arrayOf(2024, 2023)
            val isbn = arrayOf("1234", "2345")
            BookRepository.getInstance().addNewBook(title[0], author[0], publishYear[0], isbn[0]) {
                assertEquals(it[0].title, title[0])
                assertEquals(it[0].author, author[0])
                assertEquals(it[0].publishYear, publishYear[0])
                assertEquals(it[0].isbn, isbn[0])

                it[0].id?.run { toUpdateId = this }
            }
            sleep(1000)

            // update the existing data
            viewModel.title = title[1]
            viewModel.author = author[1]
            viewModel.publishYear = publishYear[1].toString()
            viewModel.isbn = isbn[1]
            viewModel.updateInfo(appContext, toUpdateId)

            // check if the data is changed
            BookRepository.getInstance().showBooks("") {
                assertEquals(it[0].title, title[1])
                assertEquals(it[0].author, author[1])
                assertEquals(it[0].publishYear, publishYear[1])
                assertEquals(it[0].isbn, isbn[1])
            }
        }
        runBlocking {
            ioJob.join()
        }
    }
}