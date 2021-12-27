package com.example.todoapp.fragment.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.example.todoapp.R
import com.example.todoapp.SharedViewModel
import com.example.todoapp.data.models.ToDoData
import com.example.todoapp.data.viewmodel.ToDoViewModel
import com.example.todoapp.fragment.list.adapter.ListAdapter
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.fragment_list.view.*

class ListFragment : Fragment() ,SearchView.OnQueryTextListener{


    private val adapter: ListAdapter by lazy { ListAdapter() }
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()


    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
             searchThroughtDatabase(query)
         }
            return true


    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let {
               searchThroughtDatabase(newText)
           }
           return true
    }

    private fun searchThroughtDatabase(query:String)
    {
        val searchQuery="%$query%"

        mToDoViewModel.searchDatabase(searchQuery).observe(this, Observer {
            list->
            list?.let {
                adapter.setData(it)
            }
        })


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        setupRecyclerview(view)

        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer { data ->

            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)

            mSharedViewModel.emptyData.observe(viewLifecycleOwner, Observer {
                showEmptyDatabseViews(it)

            })
        })

        view.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)

        }


        setHasOptionsMenu(true)
        return view
    }

    private fun showEmptyDatabseViews(emptyData: Boolean) {
        if (emptyData) {
            view?.no_data_image?.visibility = View.VISIBLE
            view?.no_data_text_view?.visibility = View.VISIBLE
        } else {
            view?.no_data_image?.visibility = View.INVISIBLE
            view?.no_data_text_view?.visibility = View.INVISIBLE
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.list_fragment_menu, menu)
        val seach:MenuItem =menu.findItem(R.id.menu_search)
        val searchView :SearchView?=seach.actionView as? SearchView
        searchView?.isSubmitButtonEnabled=true
        searchView?.setOnCloseListener (object :SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                if (true)
                {
                     Toast.makeText(requireContext(),"Aradıgını bula bildin mi",Toast.LENGTH_SHORT).show()
                }
            return false
            }

        })

        searchView?.setOnQueryTextListener(this)




    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_all -> confirmRemoval()
            R.id.menu_priority_higt-> mToDoViewModel.sortByHighPriority.observe(this, Observer {
                it-> adapter.setData(it)
            })
            R.id.menu_priority_low-> mToDoViewModel.sortByLowPriority.observe(this, Observer {
                    it-> adapter.setData(it)
            })
        }

        return super.onOptionsItemSelected(item)
    }

    private fun confirmRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mToDoViewModel.deleteAll()
            Toast.makeText(
                requireContext(),
                "Successfully Removed Everything ",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete Everything")
        builder.setMessage("Are you sure you want to remove Everything ?")
        builder.create().show()
    }

    private fun setupRecyclerview(view: View) {
        val recyclerView = view.recylerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        recyclerView.itemAnimator=LandingAnimator().apply {
            addDuration=300
        }
        swipeToDelete(recyclerView)

    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]
                mToDoViewModel.deleteItem(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                restoredDeletedData(viewHolder.itemView, deletedItem, viewHolder.adapterPosition)

            }

        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoredDeletedData(view: View, deletedItem: ToDoData, postion: Int) {
        val snackbar = Snackbar.make(
            view, "Deleted '${deletedItem.title}' ",
            Snackbar.LENGTH_LONG

        )
        snackbar.setAction("Undo")
        {
            mToDoViewModel.insertData(deletedItem)
            adapter.notifyItemChanged(postion)
        }
        snackbar.show()
    }



}