package com.fadlyas07.fadencecalc.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.fadlyas07.fadencecalc.domain.repository.HistoryDatabase
import com.fadlyas07.fadencecalc.ui.screens.calculator.CalculatorViewModel
import com.fadlyas07.fadencecalc.ui.screens.history.HistoryViewModel


class HistoryViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    private val historyDb by lazy {
        Room.databaseBuilder(
            context = application,
            klass = HistoryDatabase::class.java,
            name = "history.db"
        ).build()
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryViewModel(historyDb.dao) as T
    }
}

class CalculatorViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CalculatorViewModel(application) as T
    }
}