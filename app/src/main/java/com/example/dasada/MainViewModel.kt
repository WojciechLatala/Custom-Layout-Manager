package com.example.dasada

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dasada.model.CustomLayoutAdapterState
import com.example.dasada.model.ListItemViewModel
import com.example.dasada.repository.ItemProvider
import java.util.Collections

class MainViewModel : ViewModel() {

    private val itemProvider = ItemProvider() // created manually for simplicity, preferably DI using HILT/Dagger/Koin etc.

    private val _listItems = MutableLiveData(itemProvider.getItems(23))
    val listItems: LiveData<List<ListItemViewModel>> = _listItems

    private val _scrollTargetPageIndex = MutableLiveData(0)
    val scrollTargetPageIndex: LiveData<Int> = _scrollTargetPageIndex

    private val _rowCount = MutableLiveData(DEFAULT_ROW_COUNT)
    private val _columnCount = MutableLiveData(DEFAULT_COLUMN_COUNT)
    private val _rtlEnabled = MutableLiveData(false)

    val customLayoutAdapterState = MediatorLiveData<CustomLayoutAdapterState>().apply {
        addSource(_rowCount) { value = updateCustomLayoutAdapterState(newRowCount = it) }
        addSource(_columnCount) { value = updateCustomLayoutAdapterState(newColumnCount = it) }
        addSource(_rtlEnabled) { value = updateCustomLayoutAdapterState(newRtlEnabled = it) }
    }

    private fun updateCustomLayoutAdapterState(
        newRowCount: Int = (_rowCount.value ?: DEFAULT_ROW_COUNT),
        newColumnCount: Int = (_columnCount.value ?: DEFAULT_COLUMN_COUNT),
        newRtlEnabled: Boolean = (_rtlEnabled.value ?: false)
    ): CustomLayoutAdapterState = CustomLayoutAdapterState(columnCount = newColumnCount, rowsCount = newRowCount, rtlEnabled = newRtlEnabled)

    fun addItem() {
        _listItems.postValue(itemProvider.addItem(listItems.value ?: emptyList()))
    }

    fun removeItem() {
        listItems.value?.let {
            if (it.isNotEmpty())
                _listItems.postValue(
                    it.minus(it.random()) // remove random item to show items animation better
                )
        }
    }

    fun addScrollTargetIndex() {
        _scrollTargetPageIndex.postValue((scrollTargetPageIndex.value ?: 0) + 1)
    }

    fun subtractScrollTargetIndex() {
        _scrollTargetPageIndex.postValue((scrollTargetPageIndex.value ?: 0) - 1)
    }

    fun updateItemPosition(fromPosition: Int, toPosition: Int) {
        val currentList = listItems.value?.toMutableList() ?: return
        Collections.swap(currentList, fromPosition, toPosition)
        _listItems.postValue(currentList)
    }

    fun addRow() = _rowCount.postValue((_rowCount.value ?: DEFAULT_ROW_COUNT) + 1)
    fun subtractRow() = _rowCount.postValue((_rowCount.value ?: DEFAULT_ROW_COUNT) - 1)

    fun addColumn() = _columnCount.postValue((_columnCount.value ?: DEFAULT_COLUMN_COUNT) + 1)
    fun subtractColumn() = _columnCount.postValue((_columnCount.value ?: DEFAULT_COLUMN_COUNT) - 1)

    fun switchRtl(enabled: Boolean) = _rtlEnabled.postValue(enabled)

    private companion object {
        const val DEFAULT_ROW_COUNT = 2
        const val DEFAULT_COLUMN_COUNT = 5
    }
}
