package com.example.dasada

import android.os.Bundle
import android.view.View.LAYOUT_DIRECTION_LTR
import android.view.View.LAYOUT_DIRECTION_RTL
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.dasada.model.CustomLayoutAdapterState
import com.example.dasada.recyclerview.ItemTouchHelperCallback
import com.example.dasada.recyclerview.ListItemAdapter
import com.example.dasada.recyclerview.layoutmanager.CustomLayoutManager
import com.example.dasada.recyclerview.layoutmanager.CustomSnapHelper

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels<MainViewModel>()

    private val listItemAdapter = ListItemAdapter()
    private var customLayoutManager: CustomLayoutManager? = null
    private var customSnapHelper: CustomSnapHelper? = null
    private val simpleCallback: ItemTouchHelper.SimpleCallback by lazy { ItemTouchHelperCallback(mainViewModel::updateItemPosition) }
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setupRecyclerView()
        observeViewModelChanges()
        setupUiElements()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            this.adapter = listItemAdapter
            ItemTouchHelper(simpleCallback).attachToRecyclerView(this)
        }
    }

    /**
     * Replaces layout manager and snap helper in RecyclerView. This should usually be only done once on RecyclerView creation, here it is called multiple times
     * just to facilitate showing [CustomLayoutManager]'s capabilities.
     */
    private fun updateLayoutManagerState(customLayoutAdapterState: CustomLayoutAdapterState) {
        recyclerView.layoutDirection = if (customLayoutAdapterState.rtlEnabled) LAYOUT_DIRECTION_RTL else LAYOUT_DIRECTION_LTR
        CustomLayoutManager(context = this, columns = customLayoutAdapterState.columnCount, rows = customLayoutAdapterState.rowsCount).also {
            customLayoutManager = it
            recyclerView.layoutManager = it
        }
        // refresh snap helper orientation
        customSnapHelper?.attachToRecyclerView(null) //detach old snap helper to avoid exception
        CustomSnapHelper().apply {
            this.attachToRecyclerView(recyclerView)
            customSnapHelper = this
        }
    }

    private fun observeViewModelChanges() {
        mainViewModel.listItems.observe(this) {
            val listCountBefore = listItemAdapter.itemCount
            listItemAdapter.setItems(it) {
                if (listCountBefore > listItemAdapter.itemCount)
                    customLayoutManager?.scrollIfNoItemsAreVisible()
            }
        }

        mainViewModel.scrollTargetPageIndex.observe(this) {
            findViewById<Button>(R.id.smooth_scroll).text = getString(R.string.smooth_scroll, it)
        }

        // values that need layout manager recreation
        mainViewModel.customLayoutAdapterState.observe(this) {
            findViewById<TextView>(R.id.columns_text).text = getString(R.string.columns_text, it.columnCount)
            findViewById<TextView>(R.id.rows_text).text = getString(R.string.rows_text, it.rowsCount)
            findViewById<SwitchCompat>(R.id.rtl_enabled).isChecked = it.rtlEnabled
            updateLayoutManagerState(customLayoutAdapterState = it)
        }
    }

    /**
     * This would usually be done by databinding library, but in this project it is kept as simple as possible.
     */
    private fun setupUiElements() {
        setupAddRemoveButtons()
        setupScrollToPageButtons()
        setupColumnAndRowButtons()
        setupRTLSwitch()
    }

    private fun setupAddRemoveButtons() {
        findViewById<Button>(R.id.add_button).apply {
            setOnClickListener {
                mainViewModel.addItem()
            }
        }

        findViewById<Button>(R.id.remove_button).apply {
            setOnClickListener {
                mainViewModel.removeItem()
            }
        }
    }

    private fun setupScrollToPageButtons() {
        findViewById<Button>(R.id.smooth_scroll).apply {
            setOnClickListener {
                mainViewModel.scrollTargetPageIndex.value?.let { index ->
                    customLayoutManager?.smoothScrollToPage(index)
                }
            }
        }

        findViewById<ImageButton>(R.id.add_scroll_index).apply {
            setOnClickListener {
                mainViewModel.addScrollTargetIndex()
            }
        }

        findViewById<ImageButton>(R.id.subtract_scroll_index).apply {
            setOnClickListener {
                mainViewModel.subtractScrollTargetIndex()
            }
        }
    }

    private fun setupColumnAndRowButtons() {
        findViewById<ImageButton>(R.id.rows_add).apply {
            setOnClickListener {
                mainViewModel.addRow()
            }
        }

        findViewById<ImageButton>(R.id.rows_subtract).apply {
            setOnClickListener {
                mainViewModel.subtractRow()
            }
        }

        findViewById<ImageButton>(R.id.columns_add).apply {
            setOnClickListener {
                mainViewModel.addColumn()
            }
        }

        findViewById<ImageButton>(R.id.columns_subtract).apply {
            setOnClickListener {
                mainViewModel.subtractColumn()
            }
        }
    }

    private fun setupRTLSwitch() {
        findViewById<SwitchCompat>(R.id.rtl_enabled).apply {
            setOnCheckedChangeListener { _, isChecked ->
                mainViewModel.switchRtl(isChecked)
            }
        }
    }
}
